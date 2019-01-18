package com.bilo.hosp;

import com.bilo.hosp.controller.*;
import java.io.IOException;
import java.util.Locale;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.accesslog.AccessLogBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hosp {

    private static final Logger LOG = LoggerFactory.getLogger(Hosp.class.getName());

    public static void main(String[] args) throws Exception {
        //Загрузка конфига
        Config conf = Config.getInstance();

        //Получение link`a базы данных
        LOG.warn("Main get to DB");
        DB dbLink = DB.getInstance();

        // Запуск HTTP сервера   
        HttpServer server = HttpServer.createSimpleServer("/hosp");
        final ServerConfiguration config = server.getServerConfiguration();

        final AccessLogBuilder builder = new AccessLogBuilder(System.getProperty("pathToLog") + "/access.log");
        builder.instrument(config);

        // Подключение контроллеров
        config.addHttpHandler(new NewPatient(), "/hosp/patient/new"); // Добавление нового пациента
        config.addHttpHandler(new SetTemperature(), "/hosp/patient/setTemperature"); // Задать измеряемую температуру
        config.addHttpHandler(new SetRoom(), "/hosp/patient/setRoom"); // Задать палату пациента

        config.setJmxEnabled(true);
        try {
            server.start();
            Thread.currentThread().join();
        } catch (IOException e) {
            LOG.error("ERROR", e);
        } finally {
            server.shutdownNow();
        }
    }

}
