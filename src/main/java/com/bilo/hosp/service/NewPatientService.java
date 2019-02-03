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
        JSONObject result = new JSONObject();
        String collectionName = System.getProperty("collectionForAddPatient");
        Document doc = Document.parse(buff);

        // Получение коллекции для работы с БД
        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collectionName);
        LOG.info("Set collection database: " + collectionName);       

        String pnum, pser;
        Document personDoc = (Document) doc.get("document");

        pnum = personDoc.getString("pnum");
        pser = personDoc.getString("pser");

        if (collection.find(new Document("document",
                new Document("pnum", pnum)
                        .append("pser", pser)))
                .first() != null) {
            result.append("message", "Person with same documents already exists");
            result.append("status", "error");

            LOG.info(result.toString());
            return result.toString();
        }

        try {
            LOG.info("Document is generated: {}", doc);

            collection.insertOne(doc);

            result.append("message", "Success insert");
            result.append("status", "success");
            LOG.info(result.toString());
        } catch (MongoException ex) {
            LOG.error("ERROR {}", ex);
        }
        return result.toString();
    }
}
