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
        String result = "";
        String collForAdd = System.getProperty("collectionForAddPatient");
        JSONObject obj = new JSONObject(buff);

        String _id = obj.getString("_id");
        String t = IOUtils.toString(obj.getJSONObject("info").getJSONObject("data").getString("t").getBytes(), "UTF-8");

        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collForAdd);

//        Block<Document> printBlock = new Block<Document>() {
//            @Override
//            public void apply(final Document document) {
//                document.getString("name");
//            }
//        };
//
//        collection.find(eq("_id", new ObjectId(_id)))
//                .forEach(printBlock);
        Document targetPatient = collection.findOneAndUpdate(
                eq("_id", new ObjectId(_id)),
                set("info.data.t", t));
        if (targetPatient != null) {
            result = "Температура пациента '" + targetPatient.getString("name") + " " + targetPatient.getString("lastname") + "' установлена на " + t;
        }

        LOG.info(result);

        return result;
    }
}
