package com.mhc.fabric.client.models;

public class UserStoreInfo {

    private String id;

    public String getStateHex() {
        return stateHex;
    }

    public void setStateHex(String stateHex) {
        this.stateHex = stateHex;
    }

    private String stateHex;

    public UserStoreInfo(String id, String stateHex) {
        this.id = id;
        this.stateHex = stateHex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
