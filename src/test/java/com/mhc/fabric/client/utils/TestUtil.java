package com.mhc.fabric.client.utils;

import com.mhc.fabric.client.config.FabricConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class TestUtil {

    public static FabricConfig getFabricConfig() throws IOException {
        FabricConfig configFromProp = new FabricConfig();

        Resource resource = new ClassPathResource("application.properties");
        Properties properties = new Properties();
        properties.load(resource.getInputStream());

        configFromProp.putAll(properties);

        return configFromProp;
    }
}
