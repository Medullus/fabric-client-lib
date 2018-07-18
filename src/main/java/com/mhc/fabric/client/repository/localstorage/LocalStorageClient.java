package com.mhc.fabric.client.repository.localstorage;

import com.mhc.fabric.client.repository.Repository;

/** Table is in relation to a fileName, just store/search for the file in the current path(projects path)
 *
 *
 * **/
public class LocalStorageClient implements Repository {
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
