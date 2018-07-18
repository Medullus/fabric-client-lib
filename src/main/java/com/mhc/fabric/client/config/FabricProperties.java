package com.mhc.fabric.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mhc.fabric")
public class FabricProperties {

    private String networkConfig;
    private String storeTableName;
    private String dynamodbId;
    private String dynamodbSecret;
    private String orgAffiliation;
    private String maxEnrollment;
    private String proposalWaitTime;
    private String transactionWaitTime;
    private String ccName;
    private String ccVer;

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcVer() {
        return ccVer;
    }

    public void setCcVer(String ccVer) {
        this.ccVer = ccVer;
    }

    public String getNetworkConfig() {
        return networkConfig;
    }

    public void setNetworkConfig(String networkConfig) {
        this.networkConfig = networkConfig;
    }

    public String getStoreTableName() {
        return storeTableName;
    }

    public void setStoreTableName(String storeTableName) {
        this.storeTableName = storeTableName;
    }

    public String getDynamodbId() {
        return dynamodbId;
    }

    public void setDynamodbId(String dynamodbId) {
        this.dynamodbId = dynamodbId;
    }

    public String getDynamodbSecret() {
        return dynamodbSecret;
    }

    public void setDynamodbSecret(String dynamodbSecret) {
        this.dynamodbSecret = dynamodbSecret;
    }

    public String getOrgAffiliation() {
        return orgAffiliation;
    }

    public void setOrgAffiliation(String orgAffiliation) {
        this.orgAffiliation = orgAffiliation;
    }

    public String getMaxEnrollment() {
        return maxEnrollment;
    }

    public void setMaxEnrollment(String maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }

    public String getProposalWaitTime() {
        return proposalWaitTime;
    }

    public void setProposalWaitTime(String proposalWaitTime) {
        this.proposalWaitTime = proposalWaitTime;
    }

    public String getTransactionWaitTime() {
        return transactionWaitTime;
    }

    public void setTransactionWaitTime(String transactionWaitTime) {
        this.transactionWaitTime = transactionWaitTime;
    }
}