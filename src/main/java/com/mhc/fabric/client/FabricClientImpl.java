package com.mhc.fabric.client;

import com.mhc.fabric.client.config.FabricConfig;
import com.mhc.fabric.client.models.ChaincodeInfo;
import com.mhc.fabric.client.models.SampleStore;
import com.mhc.fabric.client.models.SampleUser;
import com.mhc.fabric.client.models.UserStoreInfo;
import com.mhc.fabric.client.utils.chaincode.CCStub;
import com.mhc.fabric.client.utils.user.UserUtils;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_NETWORKCONFIG;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_ORGAFFILIATION;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_STORETABLENAME;

public class FabricClientImpl implements FabricClient {

    private static Logger logger = Logger.getLogger(FabricClientImpl.class);
    private final String QUERY = "QUERY_TYPE";
    private final String INVOKE = "INVOKE_TYPE";
    private final String AFFILIATION;

    private FabricConfig fabricConfig;
    private NetworkConfig networkConfig;
    private SampleStore sampleStore;
    private String org;

    public FabricClientImpl(FabricConfig fabricConfig) throws NetworkConfigurationException, IOException, InvalidArgumentException {
        this.fabricConfig = fabricConfig;
        setup();
        this.org = networkConfig.getClientOrganization().getName();
        this.AFFILIATION=fabricConfig.getProperty(MHC_FABRIC_ORGAFFILIATION);
    }

    @Override
    public String query(String caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo) throws IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, ProposalException, NetworkConfigurationException, ExecutionException, InterruptedException {
        return fcnHelper(caller, fcn, args, chaincodeInfo, QUERY);
    }

    @Override
    public String invoke(String caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo) throws TransactionException, InstantiationException, InvocationTargetException, NoSuchMethodException, InterruptedException, IllegalAccessException, InvalidArgumentException, ExecutionException, NetworkConfigurationException, CryptoException, ClassNotFoundException, ProposalException {
        return fcnHelper(caller, fcn, args, chaincodeInfo, INVOKE);
    }

    private String fcnHelper(String caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo, String type) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, InvalidArgumentException, NetworkConfigurationException, CryptoException, ClassNotFoundException, TransactionException, ProposalException, ExecutionException, InterruptedException {
        String str;
        try{
            hasMember(caller);
            HFClient hfClient = UserUtils.getHFClient();
            SampleUser invoker = sampleStore.getMember(caller, org);

            hfClient.setUserContext(invoker);
            CCStub ccStub = new CCStub(fabricConfig, networkConfig);

            if(type.equals(QUERY)){
                str = ccStub.query(hfClient, fcn, args, chaincodeInfo);
            }else{
                str = ccStub.invoke(hfClient, fcn, args, chaincodeInfo).get();
            }

        }catch(InterruptedException|ExecutionException |TransactionException |ProposalException |NetworkConfigurationException|InstantiationException|InvocationTargetException|NoSuchMethodException|IllegalAccessException|InvalidArgumentException|CryptoException|ClassNotFoundException e){
            logger.error(e);
            throw e;
        }
        return str;
    }

    @Override
    public SampleUser addMemberToNetwork(String name, String pw) throws org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, MalformedURLException, RegistrationException, EnrollmentException {
        SampleUser newUser;
        UserUtils userUtils = new UserUtils(fabricConfig, networkConfig, sampleStore);
        //register
        try {
            newUser = userUtils.registerUser(name, pw, AFFILIATION);

            newUser.setEnrollment(userUtils.enrollUser(newUser));

        } catch (org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException | EnrollmentException |MalformedURLException|RegistrationException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }

        return newUser;
    }

    @Override
    public boolean hasMember(String member) throws InvalidArgumentException {
        return hasMemberHelper(member);
    }

    @Override
    public NetworkConfig getNetworkConfig() {
        return this.networkConfig;
    }

    private boolean hasMemberHelper(String user) throws InvalidArgumentException {
        if(sampleStore.hasMember(user, org) && sampleStore.getMember(user, org).isEnrolled()){
            return true;
        }else{
            throw new InvalidArgumentException("User|Enrollment Not Found");
        }
    }

    private void setup() throws IOException, NetworkConfigurationException, InvalidArgumentException {
        Resource resource = new ClassPathResource(fabricConfig.getProperty(MHC_FABRIC_NETWORKCONFIG));
        try {

            this.networkConfig = NetworkConfig.fromJsonStream(resource.getInputStream());
            //TODO reimplement when repository package is complete

            File tempFile = File.createTempFile(fabricConfig.getProperty(MHC_FABRIC_STORETABLENAME), ".tmp");
            tempFile.deleteOnExit();
            this.sampleStore = new SampleStore(tempFile);

        }catch(IOException | InvalidArgumentException | NetworkConfigurationException e){
            logger.error("Network Config not found!!!! Critical!");
            logger.error(e);
            throw e;
        }
    }
}
