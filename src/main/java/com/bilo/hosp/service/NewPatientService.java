package com.bilo.hosp.service;

import com.bilo.hosp.DB;
import com.bilo.hosp.controller.NewPatient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewPatientService {
     private static final Logger LOG = LoggerFactory.getLogger(NewPatient.class.getName());
     
     public NewPatientService() {
         
     }
     
    public String task(String buff, DB linkDB) {
        String result = "";
        String collectionName = System.getProperty("collectionForAddPatient");

        // Получение коллекции для работы с БД
        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collectionName);
        LOG.info("Set collection database: " + collectionName);

        JSONObject json = new JSONObject(buff); // Создание JSON объекта из полученой информации

        String pnum, pser;

        pnum = json.getJSONObject("document").getString("pnum");
        pser = json.getJSONObject("document").getString("pser");       

        if (collection.find(new Document("document",
                new Document("pnum", pnum)
                        .append("pser", pser)))
                .first() != null) {
            
            result = "Person with same documents already exists";
            LOG.info(result);
            
            return result;
        }

        Document doc = null;

        try {
            doc = Document.parse(json.toString()); // Создание документа из JSON данных
            LOG.info("Document is generated: {}", doc);

            collection.insertOne(doc);

            result = "Document '" + doc + "' insert in collection '" + collection + "'";
            LOG.info(result);
        } catch (MongoException ex) {
            LOG.error("ERROR {}", ex);
        }
        return result;
    }
}
