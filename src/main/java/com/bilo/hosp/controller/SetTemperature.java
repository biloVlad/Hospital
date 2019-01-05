package com.bilo.hosp.controller;

import com.bilo.hosp.DB;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.bson.*;
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
public class SetTemperature extends HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SetTemperature.class.getName());

 public SetTemperature() {

    }   

    private String taskGetData(String buff, DB linkDB) throws IOException {
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
         if(targetPatient != null) {             
             result = "Температура пациента '" + targetPatient.getString("name") + " " + targetPatient.getString("lastname") + "' установлена на " + t;
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

            String result = taskGetData(buff, dbLink);
            
            //byte[] res = IOUtils.toByteArray(IOUtils.toString(result.getBytes()));
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
            // timing.afterCompletion(rqst, "ocsp");//ocsp
            long time = (System.currentTimeMillis() - startTime);
            LOG.warn("Time Taken 7 finish: " + time);
        }
    }

}
