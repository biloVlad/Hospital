package com.bilo.hosp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    
    private static final Logger LOG = LoggerFactory.getLogger(Config.class.getName());
    private static volatile Config instance = null;
    
    private Config() {
        try {            
            LOG.info("Conf: load: acsk.properties = {}", System.getProperty("path.to.config"));
            InputStream file = new FileInputStream(new File(System.getProperty("path.to.config")));
            
            Properties props = new Properties();
            props.load(file);
            
            for (String key : props.stringPropertyNames()) {
                String value = (String) props.getProperty(key);
                
                LOG.warn("SYSPROPS:" + key + "=" + value);
                System.setProperty(key, value);                
            }
        } catch (Exception e) {
            LOG.error("ERROR:" + e);
            System.err.println("error" + e);
        }
    }
    
    public static Config getInstance() {
        Config localInstance = instance;
        if (localInstance == null) {
            synchronized (Config.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Config();
                }
            }
        }
        return localInstance;
    }
}
