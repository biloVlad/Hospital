
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
        JSONObject result = new JSONObject();
        String collForAdd = System.getProperty("collectionForAddPatient");
        Document doc = Document.parse(buff);

        String _id = doc.getString("_id");
        String room = doc.getString("room");

        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collForAdd); 

        Document targetPatient = collection.findOneAndUpdate(
                eq("_id", new ObjectId(_id)),
                set("room", room));
         if(targetPatient != null) {    
             result.append("message", "Палата пациента '" + targetPatient.getString("name") + " " + targetPatient.getString("lastname") + "' изменена на " + room);
             result.append("status", "success");             
         }
        
        LOG.info(result.toString());        
        
        return result.toString();
    }
}
