package com.mhc.fabric.client.repository;


/** Decide from MHC_FABRIC_DB property which instance of Repository to use. Choices should be [dynamo, cloudant, localfile]
 *  Based on the choice of persistance store, initialized that DB for CRUD operations
 *
 * **/
public class StoreRepository {

    private Repository repository;

    private static StoreRepository instance;

    private StoreRepository(Repository repository){

    }

    public static StoreRepository getRepo(Repository repository){
        if(instance != null){
            return instance;
        }else{
            instance = new StoreRepository(repository);
            return instance;
        }
    }

}
