package com.mhc.fabric.client.models;

public class ChaincodeInfo {

    private String ccName;
    private String ccVersion;
    private String channelName;

    public ChaincodeInfo(String ccName, String ccVersion, String channelName) {
        this.ccName = ccName;
        this.ccVersion = ccVersion;
        this.channelName = channelName;
    }

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcVersion() {
        return ccVersion;
    }

    public void setCcVersion(String ccVersion) {
        this.ccVersion = ccVersion;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return "ChaincodeInfo{" +
                "ccName='" + ccName + '\'' +
                ", ccVersion='" + ccVersion + '\'' +
                ", channelName='" + channelName + '\'' +
                '}';
    }
}
