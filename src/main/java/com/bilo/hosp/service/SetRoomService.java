
package com.bilo.hosp.service;

import com.bilo.hosp.DB;
import com.bilo.hosp.controller.SetRoom;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetRoomService {
    private static final Logger LOG = LoggerFactory.getLogger(SetRoom.class.getName());
    
    public SetRoomService() {
       
    }
    
    public String task(String buff, DB linkDB) {
        String result = "";
        String collForAdd = System.getProperty("collectionForAddPatient");
        JSONObject obj = new JSONObject(buff);

        String _id = obj.getString("_id");
        String room = obj.getString("room");

        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collForAdd); 

        Document targetPatient = collection.findOneAndUpdate(
                eq("_id", new ObjectId(_id)),
                set("room", room));
         if(targetPatient != null) {             
             result = "Палата пациента '" + targetPatient.getString("name") + " " + targetPatient.getString("lastname") + "' изменена на " + room;
         }
        
        LOG.info(result);        
        
        return result;
    }
}
