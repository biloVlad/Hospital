/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bilo.hosp.controller;

import com.bilo.hosp.DB;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import java.io.IOException;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author StoneInside
 */
public class RelocatePatient extends HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RelocatePatient.class.getName());

    public RelocatePatient() {

    }
 private String taskReloacate(String buff, DB linkDB) throws IOException {
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
 
    @Override
    public void service(Request rqst, Response rspns) throws Exception {
       long startTime = System.currentTimeMillis();

        LOG.warn("Get DB");
        DB dbLink = DB.getInstance();

        rspns.setCharacterEncoding("utf8");

        try {
            String buff = null;
            try {
                buff = IOUtils.toString(rqst.getInputStream());
            } catch (IOException ex) {
                LOG.error("ERROR {}", ex);
            }
            if ((buff == null) || (buff.length() == 0)) {
                rqst.setAttribute("coderror", 400);
                rqst.setAttribute("texterror", "No request bytes.");
                LOG.error("ERROR", "No request bytes");
                rspns.sendError(400, "No request bytes.");
                return;
            }

            String result = taskReloacate(buff, dbLink);            
           
            rspns.setHeader("Content-Type", "application/ocsp-response");
            rspns.setContentLength(result.length());                 
            rspns.getOutputStream().write(result.getBytes());
            rspns.flush();
        } catch (Exception ex) {
            LOG.error("ERROR {}", ex);
            String texterror = ex.getLocalizedMessage();
            rqst.setAttribute("coderror", 3000);
            rqst.setAttribute("texterror", texterror);
        } finally {            
            long time = (System.currentTimeMillis() - startTime);
            LOG.warn("Time Taken 7 finish: " + time);
        }
    }
}
