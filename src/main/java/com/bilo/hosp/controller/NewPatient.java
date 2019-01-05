/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bilo.hosp.controller;

import com.bilo.hosp.DB;
import com.google.common.io.CharStreams;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.ExtensionInstallationException;

/**
 *
 * @author StoneInside
 */
public class NewPatient extends HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NewPatient.class.getName());

    public NewPatient() {

    }

    private String taskNew(byte[] buff, DB linkDB) throws IOException {
        String result = "";
        String collectionName = System.getProperty("collectionForAddPatient");

        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collectionName);
        LOG.info("Set collection database: " + collectionName);
        LOG.warn("Start document generate");

        Document doc = null;
        JSONObject json =  new JSONObject(IOUtils.toString(buff, "UTF-8"));
        
        try {
            doc = Document.parse(json.toString());
            LOG.info("Document is generated: {}", doc);
           
            
            collection.insertOne(doc);           

            result = "Document '" + doc + "' insert in collection '" + collection + "'";
            LOG.info(result);
        } catch (MongoException ex) {
            LOG.error("ERROR {}", ex);
        }
        return result;
    }

    @Override
    public void service(Request rqst, Response rspns) throws Exception {
        long startTime = System.currentTimeMillis();
        DB dbLink = DB.getInstance();

        rspns.setCharacterEncoding("UTF-8");
        
        try {
            byte[] buff = null;
            try {
                buff = IOUtils.toByteArray(rqst.getInputStream());
            } catch (IOException ex) {
                LOG.error("ERROR {}", ex);
            }
            if ((buff == null) || (buff.length == 0)) {
                rqst.setAttribute("coderror", 400);
                rqst.setAttribute("texterror", "No request bytes.");
                LOG.error("ERROR", "No request bytes");
                rspns.sendError(400, "No request bytes.");
                return;
            }

            String result = taskNew(buff, dbLink);

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
