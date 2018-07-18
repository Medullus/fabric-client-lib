package com.mhc.fabric.client;

import com.mhc.fabric.client.models.ChaincodeInfo;
import com.mhc.fabric.client.models.SampleUser;
import com.mhc.fabric.client.models.UserStoreInfo;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public interface FabricClient {

    /**
     * @param caller a member of the network
     * @param fcn   function call to chaincode
     * @param args  arguments for function call
     * @param chaincodeInfo channel, ccname, ccver information
     * @return payload
     *
     * **/
    String query(String caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo) throws IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, ProposalException, NetworkConfigurationException, ExecutionException, InterruptedException;

    /**
     * @param caller    a member of the network
     * @param fcn   chaincode function to invoke
     * @param args chaincode function args
     * @param chaincodeInfo channel, ccname, ccver package
     * **/
    String invoke(String caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo) throws TransactionException, InstantiationException, InvocationTargetException, NoSuchMethodException, InterruptedException, IllegalAccessException, InvalidArgumentException, ExecutionException, NetworkConfigurationException, CryptoException, ClassNotFoundException, ProposalException;

    /**@param name  name of user
     * @param pw    secret
     * @return true if successful
     *
     * **/
    SampleUser addMemberToNetwork(String name, String pw) throws org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, MalformedURLException, RegistrationException, EnrollmentException;

    /**
     * @param member member of network
     * @return true if member is registered
     * **/
    boolean hasMember(String member) throws InvalidArgumentException;


    /**
     * @return networkConfig
     * **/
    NetworkConfig getNetworkConfig();
}
