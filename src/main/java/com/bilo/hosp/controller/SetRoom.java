/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bilo.hosp.controller;

import com.bilo.hosp.DB;
import com.bilo.hosp.service.SetRoomService;
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
public class SetRoom extends HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SetRoom.class.getName());

    public SetRoom() {

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
            
            SetRoomService service = new SetRoomService();
            String result = service.task(buff, dbLink);            
           
            rspns.setHeader("Content-Type", "application/json");   
            rspns.getWriter().write(result);
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
