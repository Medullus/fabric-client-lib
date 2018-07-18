package com.mhc.fabric.client.config;

import com.mhc.fabric.client.FabricClient;
import com.mhc.fabric.client.FabricClientImpl;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.mhc.fabric.client.config.FabricConfigParams.*;

@Configuration
@ConditionalOnClass(FabricClient.class)
@EnableConfigurationProperties(FabricProperties.class)
public class FabricAutoConfigure {

    @Autowired
    private FabricProperties fabricProperties;

    @Bean
    @ConditionalOnMissingBean
    public FabricConfig config(){
        String networkConfig = fabricProperties.getNetworkConfig() == null ? System.getProperty(MHC_FABRIC_NETWORKCONFIG) : fabricProperties.getNetworkConfig();
        String storeTableName = fabricProperties.getStoreTableName() == null ? System.getProperty(MHC_FABRIC_STORETABLENAME) : fabricProperties.getStoreTableName();
        String dynamodbId = fabricProperties.getDynamodbId() == null ? System.getProperty(MHC_FABRIC_DYNAMODBID) : fabricProperties.getDynamodbId();
        String dynamodbSecret = fabricProperties.getDynamodbSecret() == null ? System.getProperty(MHC_FABRIC_DYNAMODBSECRET) : fabricProperties.getDynamodbSecret();
        String orgAffiliation = fabricProperties.getOrgAffiliation() == null ? System.getProperty(MHC_FABRIC_ORGAFFILIATION) : fabricProperties.getOrgAffiliation();
        String maxEnrollment = fabricProperties.getMaxEnrollment() == null ? System.getProperty(MHC_FABRIC_MAXENROLLMENT) : fabricProperties.getMaxEnrollment();
        String proposalWaitTime = fabricProperties.getProposalWaitTime() == null ? System.getProperty(MHC_FABRIC_PROPOSALWAITTIME) : fabricProperties.getProposalWaitTime();
        String transactionWaitTime = fabricProperties.getTransactionWaitTime() == null ? System.getProperty(MHC_FABRIC_TRANSACTIONWAITTIME) : fabricProperties.getProposalWaitTime();
        String ccVer = fabricProperties.getCcVer() == null ? System.getProperty(MHC_FABRIC_CCVER) : fabricProperties.getCcVer();
        String ccName = fabricProperties.getCcName() == null ? System.getProperty(MHC_FABRIC_CCNAME) : fabricProperties.getCcName();


        FabricConfig fabricConfig = new FabricConfig();
        fabricConfig.put(MHC_FABRIC_NETWORKCONFIG, networkConfig);
        fabricConfig.put(MHC_FABRIC_STORETABLENAME, storeTableName);
        fabricConfig.put(MHC_FABRIC_DYNAMODBID, dynamodbId);
        fabricConfig.put(MHC_FABRIC_DYNAMODBSECRET, dynamodbSecret);
        fabricConfig.put(MHC_FABRIC_ORGAFFILIATION, orgAffiliation);
        fabricConfig.put(MHC_FABRIC_MAXENROLLMENT, maxEnrollment);
        fabricConfig.put(MHC_FABRIC_PROPOSALWAITTIME, proposalWaitTime);
        fabricConfig.put(MHC_FABRIC_TRANSACTIONWAITTIME, transactionWaitTime);
        fabricConfig.put(MHC_FABRIC_CCNAME, ccName);
        fabricConfig.put(MHC_FABRIC_CCVER, ccVer);

        return fabricConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public FabricClient fabricClient(FabricConfig fabricConfig) throws IOException, NetworkConfigurationException, InvalidArgumentException {
        FabricClient fabricClient = new FabricClientImpl(fabricConfig);

        return fabricClient;
    }

}
