package org.example.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

/**
 * The `HttpClient` class is responsible for establishing an HTTP connection to a specified domain and port,
 * verifying connectivity by sending a GET request, and making HTTP POST requests with JSON data.
 */
public class HttpClient {
    private static final Logger logger = Logger.getLogger(HttpClient.class.getName());
    private static final Integer timeoutMs = 5000;

    private final String domain;
    private final Integer port;
    private final boolean useHttps;
    private final URI uriRoot;

    /**
     * Constructs an `HttpClient` with the specified domain, port, and HTTPS usage.
     *
     * @param domain   the domain of the server to connect to
     * @param port     the port number for the connection
     * @param useHttps true to use HTTPS, false to use HTTP
     */
    public HttpClient(String domain, Integer port, boolean useHttps) {
        this.domain = domain;
        this.port = port;
        this.useHttps = useHttps;
        this.uriRoot = this.buildUriRoot();
    }

    /**
     * Constructs an `HttpClient` with the specified domain and port, defaulting to HTTP.
     *
     * @param domain the domain of the server to connect to
     * @param port   the port number for the connection
     */
    public HttpClient(String domain, Integer port) {
        this(domain, port, false);
    }

    /**
     * Builds the root URI using the domain, port, and protocol.
     * The URI is constructed based on the specified protocol (HTTP or HTTPS).
     *
     * @return the root URI for the server
     */
    private URI buildUriRoot() {
        URI uri = null;
        try {
            String protocol = this.useHttps ? "https" : "http";
            uri =  new URI(protocol + "://" + this.domain + ":" + port + "/");
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Invalid URL format.", e);
            exit(1);
        }
        return uri;
    }

    /**
     * Checks if the client can successfully connect to the server.
     * Sends a GET request to the root URL and verifies the response code.
     * The connection is considered successful if the response code is in the range 200 to 499.
     *
     * @return true if the server responds with a status code between 200 and 499, false otherwise
     */
    public boolean isConnected() {
        try {
            HttpURLConnection connection = (HttpURLConnection) uriRoot.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);

            int responseCode = connection.getResponseCode();
            return 200 <= responseCode && responseCode < 500;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to connect to " + uriRoot, e);
            return false;
        }
    }

    /**
     * Sends a POST request with JSON data to the specified URI.
     * If the response code is 200, the JSON response is returned.
     *
     * @param uri  the URI to which the POST request is sent, relative to the root URI
     * @param json the JSON object to be sent as the POST request body
     * @return the JSON response from the server if the response code is 200, null otherwise
     */
    public JsonObject post(URI uri, JsonObject json) {
        try {
            URL url = uriRoot.resolve(uri).toURL();
            logger.info("POST request to: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));

            int responseCode = connection.getResponseCode();
            logger.info("Response code: " + responseCode);
            if (responseCode == 200) {
                return (JsonObject) JsonParser.parseReader(new InputStreamReader(connection.getInputStream()));
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to connect to " + uriRoot, e);
            return null;
        }
    }
}
