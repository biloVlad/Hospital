/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bilo.hosp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(Hosp.class.getName());
    private static volatile DB instance = null;
    private static volatile MongoDatabase database = null;
    
    private DB() {
        LOG.warn("Connecting to MongoDB:{}", System.getProperty("database"));
        
        MongoClient mongoClient = new MongoClient();
        database = mongoClient.getDatabase(System.getProperty("database"));
        
        LOG.info("Database set to: " + System.getProperty("database"));
    }

    public static DB getInstance() {
        DB localInstance = instance;
        if (localInstance == null) {
            synchronized (Config.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DB();
                }
            }
        }
        return localInstance;
    }
    
    public MongoDatabase getDatabase() {
        return database;
    }
}
