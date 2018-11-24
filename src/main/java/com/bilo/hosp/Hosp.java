package com.bilo.hosp;

import com.bilo.hosp.controller.*;
import java.io.IOException;
import java.util.Locale;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hosp {

    private static final Logger LOG = LoggerFactory.getLogger(Hosp.class.getName());
    private static Locale locale = new Locale("uk", "UA");

    public static void main(String[] args) throws Exception {
        //Загрузка конфига
        Config conf = Config.getInstance();

        //Получение базы данных
        LOG.warn("Main get to DB");
        DB dbLink = DB.getInstance();
       
        // Запуск HTTP сервера   
        HttpServer server = HttpServer.createSimpleServer("/hosp");
        final ServerConfiguration config = server.getServerConfiguration();
        
        // Подключение контроллеров
        config.addHttpHandler(new AddPatient(), "/hosp/patient/add");
        config.addHttpHandler(new GetPatientData(), "/hosp/patient/getData");
        config.addHttpHandler(new RelocatePatient(), "/hosp/patient/relocate");
        
        config.setJmxEnabled(true);
        try {
            server.start();
            Thread.currentThread().join();
        } catch (IOException e) {
            LOG.error("ERROR", e);
        }
    }

}
