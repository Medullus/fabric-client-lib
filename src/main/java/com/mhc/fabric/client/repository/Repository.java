package com.mhc.fabric.client.repository;

public interface Repository {

    void delete(String key);
    String update(String key, String value);
    String retrieve(String key);
    String create(String key, String value);
    boolean hasTable(String tableName);
    void createTable(String tableName);

}
