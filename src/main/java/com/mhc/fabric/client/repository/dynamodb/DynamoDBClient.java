package com.mhc.fabric.client.repository.dynamodb;

import com.mhc.fabric.client.config.FabricConfig;
import com.mhc.fabric.client.repository.Repository;

public class DynamoDBClient implements Repository {

    private FabricConfig fabricConfig;

    @Override
    public void delete(String key) {

    }

    @Override
    public String update(String key, String value) {
        return null;
    }

    @Override
    public String retrieve(String key) {
        return null;
    }

    @Override
    public String create(String key, String value) {
        return null;
    }

    @Override
    public boolean hasTable(String tableName) {
        return false;
    }

    @Override
    public void createTable(String tableName) {

    }

}
