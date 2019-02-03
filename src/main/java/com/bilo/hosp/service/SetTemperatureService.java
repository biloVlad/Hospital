package com.bilo.hosp.service;

import com.bilo.hosp.DB;
import com.bilo.hosp.controller.SetRoom;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetTemperatureService {

    private static final Logger LOG = LoggerFactory.getLogger(SetRoom.class.getName());

    public SetTemperatureService() {

    }

    public String task(String buff, DB linkDB) throws IOException {
        JSONObject result = new JSONObject();
        String collForAdd = System.getProperty("collectionForAddPatient");
        Document doc = Document.parse(buff);

        String _id = doc.getString("_id");
        String t = doc.getString("t");

        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collForAdd);

        Document targetPatient = collection.findOneAndUpdate(
                eq("_id", new ObjectId(_id)),
                set("info.data.t", t));
        if (targetPatient != null) {
            result.append("message", "Температура пациента '" + targetPatient.getString("name") + " " + targetPatient.getString("lastname") + "' установлена на " + t);
            result.append("status", "success");
        }

        LOG.info(result.toString());

        return result.toString();
    }
}
