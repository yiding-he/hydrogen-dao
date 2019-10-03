package com.hyd.dao.mate.util;

import com.hyd.dao.mate.MateConfiguration;
import org.junit.Test;

public class ConfigurationTest {

    private static final String PATH = "hydrogen-mate-config.xml";

    @Test
    public void saveConfiguration() throws Exception {
        MateConfiguration mateConfiguration = new MateConfiguration();
        mateConfiguration.setSrcPath("src/main/java");
        mateConfiguration.setPojoPackage("com.demo.entity");

        Configuration.saveConfiguration(mateConfiguration, PATH);
    }

    @Test
    public void readConfiguration() throws Exception {
        MateConfiguration c = Configuration.readConfiguration(PATH, MateConfiguration.class);
        System.out.println("c = " + c);
    }
}
