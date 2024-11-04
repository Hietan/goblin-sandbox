package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.utils.HttpClient;
import org.example.utils.PropertiesLoader;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Path configFilePath = Paths.get("src/main/resources/config.properties");

    public static void main(String[] args) throws URISyntaxException {
        String domain;
        int port;
        try {
            PropertiesLoader configLoader = new PropertiesLoader(configFilePath);
            domain = configLoader.getProperty("server.domain");
            port = Integer.parseInt(configLoader.getProperty("server.port"));
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Error: ", e);
            return;
        }

        HttpClient client = new HttpClient(domain, port);
        if (client.isConnected()) {
            logger.info("Connected to " + domain + ":" + port);
        } else {
            logger.severe("Failed to connect to " + domain + ":" + port);
        }

        URI postUri = new URI("/release/newVersions");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("groupId", "org.jgrapht");
        jsonObject.addProperty("artifactId", "jgrapht-core");
        jsonObject.addProperty("version", "1.5.0");
        JsonArray addedValues = new JsonArray();
        addedValues.add("CVE");
        addedValues.add("FRESHNESS");
        addedValues.add("POPULARITY_1_YEAR");
        jsonObject.add("addedValues", addedValues);

        JsonObject response = client.post(postUri, jsonObject);
        logger.info(response.toString());
    }
}