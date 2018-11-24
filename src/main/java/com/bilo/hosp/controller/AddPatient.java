/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bilo.hosp.controller;

import com.bilo.hosp.DB;
import com.google.common.io.CharStreams;
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

/**
 *
 * @author StoneInside
 */
public class AddPatient extends HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AddPatient.class.getName());
    //private Locale locale;

    public AddPatient() {

    }

//    public AddPatient(Locale locale) throws IOException {
//        this.locale = locale;
//    }

    private String taskAdd(String buff, DB linkDB) throws IOException {
        String result = "";
        String collForAdd = System.getProperty("collectionForAddPatient");
        JSONObject obj = new JSONObject(buff);
        String tmp1 = obj.getJSONObject("document").getString("pnum");

        byte[] tmp2 = obj.getString("name").getBytes();

        MongoCollection<Document> collection = linkDB.getDatabase().getCollection(collForAdd);
        LOG.info("Set collection database: " + collForAdd);
        LOG.warn("Start document generate");
        Document doc = new Document("name", IOUtils.toString(obj.getString("name").getBytes(), "UTF-8"))
                .append("surename", IOUtils.toString(obj.getString("surename").getBytes(), "UTF-8"))
                .append("lastname", IOUtils.toString(obj.getString("lastname").getBytes(), "UTF-8"))
                .append("org", IOUtils.toString(obj.getString("org").getBytes(), "UTF-8"))
                .append("orgunit", IOUtils.toString(obj.getString("orgunit").getBytes(), "UTF-8"))
                .append("room", IOUtils.toString(obj.getString("room").getBytes(), "UTF-8"))
                .append("document", new Document("pnum", IOUtils.toString(obj.getJSONObject("document").getString("pnum").getBytes(), "UTF-8"))
                        .append("pser", IOUtils.toString(obj.getJSONObject("document").getString("pser").getBytes(), "UTF-8")))
                .append("info", new Document("data", new Document("t", IOUtils.toString(obj.getJSONObject("info")
                        .getJSONObject("data")
                        .getString("t").getBytes(), "UTF-8")))
                        .append("medication", new Document("doctor", IOUtils.toString(obj.getJSONObject("info")
                                .getJSONObject("medication")
                                .getString("doctor").getBytes(), "UTF-8"))
                                .append("name", IOUtils.toString(obj.getJSONObject("info")
                                        .getJSONObject("medication")
                                        .getString("name").getBytes(), "UTF-8"))
                                .append("cod", IOUtils.toString(obj.getJSONObject("info")
                                        .getJSONObject("medication")
                                        .getString("cod").getBytes(), "UTF-8"))
                                .append("dosage", new Document("time", IOUtils.toString(obj.getJSONObject("info")
                                        .getJSONObject("medication")
                                        .getJSONObject("dosage")
                                        .getString("time").getBytes(), "UTF-8"))
                                        .append("count", IOUtils.toString(obj.getJSONObject("info")
                                                .getJSONObject("medication")
                                                .getJSONObject("dosage")
                                                .getString("count").getBytes(), "UTF-8")))));
        LOG.warn("Document is generated: {}", doc);
        
        LOG.warn("Insert document into {}", collForAdd);
        collection.insertOne(doc);
        
        result = "Document '" + doc + "' insert into collection '" + collection + "'";
        LOG.info(result);

        return result;
    }

    @Override
    public void service(Request rqst, Response rspns) throws Exception {
        long startTime = System.currentTimeMillis();
        LOG.warn("Get DB");
        DB dbLink = DB.getInstance();

        
        rspns.setCharacterEncoding("utf8");

        rqst.setAttribute("startTime", startTime);
        rqst.setAttribute("description", "Получение статуса сертификата");
        rqst.setAttribute("className", "AddPatient");
        rqst.setAttribute("methodName", "service");
        //System.out.println(IOUtils.toString(rqst.getInputStream()));
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

            //***************************
//            String ip = rqst.getHeader("X-Forwarded-For") == null ? rqst.getRemoteAddr() : rqst.getHeader("X-Forwarded-For");
//            String qstring = rqst.getQueryString() == null ? "" : rqst.getQueryString();
//            String serverip = rqst.getLocalAddr();
//            LOG.info("Info request: ip:{} queryString:{} serverIp:{}", ip, qstring, serverip);
            //***************************
            //long readRequestTime = (System.currentTimeMillis() - startTime);
            //LOG.warn("Time Taken 2 prepare: " + readRequestTime);
// ??????????????????????????????????????????????????????????????
//            OcspReqServiceBC service = new OcspReqServiceBC();
//            byte[] result = service.task(buf,
//                    locale,
//                    ids,
//                    startTime,
//                    ip,
//                    serverip,
//                    qstring,
//                    rqst);
//   
// ??????????????????????????????????????????????????????????????    
            String result = taskAdd(buff, dbLink);            
            byte[] res = IOUtils.toByteArray(IOUtils.toString(result.getBytes()));
            rspns.setHeader("Content-Type", "application/ocsp-response");
            rspns.setContentLength(res.length);
            rspns.getOutputStream().write(res);
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
