package org.example;

import org.example.utils.PropertiesLoader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Path configFilePath = Paths.get("src/main/resources/config.properties");

    public static void main(String[] args) {
        String domain;
        String port;
        try {
            PropertiesLoader configLoader = new PropertiesLoader(configFilePath);
            domain = configLoader.getProperty("server.domain");
            port = configLoader.getProperty("server.port");
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Error: ", e);
            return;
        }

        try {
            URL urlDocs = new URI("http://" + domain + ":" + port + "/swagger-ui/index.html").toURL();
            HttpURLConnection connection = (HttpURLConnection) urlDocs.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                logger.info("Success");
            }
            else {
                logger.severe("Error: " + responseCode);
            }
        }
        catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Error: URL Syntax Exception.", e);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Error: Unable to connect to the URL.", e);
        }
    }
}