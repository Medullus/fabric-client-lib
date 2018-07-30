package com.mhc.fabric.client;


import com.mhc.fabric.client.config.FabricConfig;
import com.mhc.fabric.client.models.ChaincodeInfo;
import com.mhc.fabric.client.models.SampleStore;
import com.mhc.fabric.client.models.SampleUser;
import com.mhc.fabric.client.utils.TestUtil;
import com.mhc.fabric.client.utils.chaincode.CCStub;
import com.mhc.fabric.client.utils.channel.ChannelUtils;
import com.mhc.fabric.client.utils.user.UserUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_NETWORKCONFIG;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_STORETABLENAME;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_TRANSACTIONWAITTIME;
import static java.lang.String.format;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class FabricITTest {

    FabricConfig fabricConfig;
    NetworkConfig networkConfig;
    SampleStore sampleStore;

    private final String TESTUSER = "testUsera";
    private final String TESTUSERPW = "secret";

    //TODO modify to your specific
    private final String FOO_CHAN = "foo";
    private final String CC_NAME = "example20";
    private final String CC_VER = "1";

    //TODO modify to your specific CC fcn and args, below is based off of example02 cc
    private final String FCN_INVOKE = "invoke";
    private final String[] ARGS_INVOKE = new String[]{"a", "b", "100"};
    private final String FCN_QUERY = "query";
    private final String[] ARGS_QUERY = new String[]{"b"};


    @Before
    public void begin() throws NetworkConfigurationException, IOException, InvalidArgumentException {
        setup();
    }

    @Test
    public void runIT() throws Exception {
        //register user
        Random random = new Random();//Use random users so that when we rerun tests, register wont throw error
        String randomUser = TESTUSER+random.nextInt();

        UserUtils userUtils = new UserUtils(fabricConfig, networkConfig, sampleStore);
        SampleUser testUser = userUtils.registerUser(randomUser, TESTUSERPW, "org1.department1");

        assertEquals(randomUser, testUser.getName());
        assertEquals(TESTUSERPW, testUser.getEnrollmentSecret());

        String channel = networkConfig.getChannelNames().iterator().next();
        assertEquals("foo", channel);

        assertTrue(testUser.isRegistered());

        Enrollment enrollment = userUtils.enrollUser(testUser);
        testUser.setEnrollment(enrollment);
        assertNotNull(enrollment);
        out(enrollment.getCert());

        assertTrue(testUser.isEnrolled());

        SampleUser userFromStore = userUtils.getMember(randomUser);
        assertNotNull(userFromStore);
        assertEquals(randomUser, userFromStore.getName());
        assertTrue(userFromStore.isEnrolled());

        //invoke cc

        CCStub ccStub = new CCStub(fabricConfig, networkConfig);
        HFClient client = UserUtils.getHFClient();

        client.setUserContext(testUser);

        ChaincodeInfo chaincodeInfo = new ChaincodeInfo(CC_NAME, CC_VER, FOO_CHAN);

        String txId = ccStub.invoke(client, FCN_INVOKE, ARGS_INVOKE , chaincodeInfo)
                .get(Integer.parseInt(fabricConfig.getProperty(MHC_FABRIC_TRANSACTIONWAITTIME)), TimeUnit.SECONDS);

        assertNotNull(txId);
        //get new client to stimlate thread
        //query cc
        client = UserUtils.getHFClient();
        client.setUserContext(testUser);

        String payload = ccStub.query(client, FCN_QUERY, ARGS_QUERY, chaincodeInfo);
        assertNotNull(payload);
        out("payload is "+payload);

        //blockwalker
        ChannelUtils channelUtils = ChannelUtils.getInstance();
        Channel channelBlock = client.getChannel("foo");
        channelUtils.blockWalker(client, channelBlock);
    }

    private void setup() throws IOException, NetworkConfigurationException, InvalidArgumentException {

        this.fabricConfig = TestUtil.getFabricConfig();

        Resource resource = new ClassPathResource(fabricConfig.getProperty(MHC_FABRIC_NETWORKCONFIG));
        this.networkConfig = NetworkConfig.fromJsonStream(resource.getInputStream());

        //TODO modify after repository package is complete
        File file = new File(fabricConfig.getProperty(MHC_FABRIC_STORETABLENAME));
        this.sampleStore = new SampleStore(file);
    }

    private void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();
    }

}
