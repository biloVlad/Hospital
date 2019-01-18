package com.bilo.hosp.controller;

import com.bilo.hosp.DB;
import com.bilo.hosp.service.NewPatientService;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewPatient extends HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NewPatient.class.getName());

    public NewPatient() {

    }   

    @Override
    public void service(Request rqst, Response rspns) throws Exception {
        long startTime = System.currentTimeMillis();
        DB dbLink = DB.getInstance();

        rspns.setCharacterEncoding("UTF-8");

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
            
            NewPatientService service = new NewPatientService();
            
            String result = service.task(buff, dbLink);

            // ФОрмирование ответа
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
