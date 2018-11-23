/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bilo.hosp.controller;

import java.io.IOException;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author StoneInside
 */
public class RelocatePatient extends HttpHandler{
     private static final Logger LOG = LoggerFactory.getLogger(AddPatient.class.getName());
    private Locale locale;
    
    public RelocatePatient () {

    }

    public RelocatePatient(Locale locale) throws IOException {
        this.locale = locale;
    }
    
    @Override
    public void service(Request rqst, Response rspns) throws Exception {
        long startTime = System.currentTimeMillis();
        rspns.setCharacterEncoding("utf8");
        rqst.setAttribute("startTime", startTime);
        rqst.setAttribute("description", "Получение статуса сертификата");
        rqst.setAttribute("className", "AddPatient");
        rqst.setAttribute("methodName", "service");
        
        try {
            byte[] buf = null;
            try {
                buf = IOUtils.toByteArray(rqst.getInputStream());
            } catch (IOException ex) {
                LOG.error("ERROR {}", ex);
            }
            if ((buf == null) || (buf.length == 0)) {
                rqst.setAttribute("coderror", 400);
                rqst.setAttribute("texterror", "No request bytes.");
                LOG.error("ERROR", "No request bytes");
                rspns.sendError(400, "No request bytes.");
                return;
            }
            String ip = rqst.getHeader("X-Forwarded-For") == null ? rqst.getRemoteAddr() : rqst.getHeader("X-Forwarded-For");
            String qstring = rqst.getQueryString() == null ? "" : rqst.getQueryString();
            String serverip = rqst.getLocalAddr();

            long readRequestTime = (System.currentTimeMillis() - startTime);
            LOG.warn("Time Taken 2 prepare: " + readRequestTime);

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
            rspns.setHeader("Content-Type", "application/ocsp-response");
            rspns.setContentLength(buf.length);
            rspns.getOutputStream().write(buf);
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
