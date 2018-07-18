package com.mhc.fabric.client;

import com.mhc.fabric.client.config.FabricConfig;
import com.mhc.fabric.client.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_DYNAMODBID;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_DYNAMODBSECRET;
import static org.junit.Assert.assertEquals;


public class FabricConfigExample {
    /** Demonstration how host App will populate FabricConfig and retrieve properties from application.properties
     *
     *
     * **/
    FabricConfig fabricConfig;

    @Before
    public void setup() throws IOException {

        this.fabricConfig = getConfig();
    }

    @Test
    public void testApplicationProperties(){
        assertEquals("testid", fabricConfig.getProperty(MHC_FABRIC_DYNAMODBID));
        assertEquals("testsecret", fabricConfig.getProperty(MHC_FABRIC_DYNAMODBSECRET));
    }

    private FabricConfig getConfig() throws IOException {
        return TestUtil.getFabricConfig();
    }
}
