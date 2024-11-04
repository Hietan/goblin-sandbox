package org.example.utils;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

/**
 * The `HttpClient` class is responsible for establishing an HTTP connection to a specified domain and port,
 * and verifying connectivity by sending a GET request to the root path.
 */
public class HttpClient {
    private static final Logger logger = Logger.getLogger(HttpClient.class.getName());
    private static final Integer timeoutMs = 5000;

    private final String domain;
    private final Integer port;
    private final boolean useHttps;
    private final URL urlRoot;

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
        this.urlRoot = this.buildPathRoot();
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
     * Builds the root URL using the domain, port, and protocol.
     * The URL is constructed based on the specified protocol (HTTP or HTTPS).
     *
     * @return the root URL for the server
     */
    private URL buildPathRoot() {
        URL url = null;
        try {
            String protocol = this.useHttps ? "https" : "http";
            url =  new URI(protocol + "://" + this.domain + ":" + port + "/").toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            logger.log(Level.SEVERE, "Invalid URL format.", e);
            exit(1);
        }
        return url;
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
            HttpURLConnection connection = (HttpURLConnection) urlRoot.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);

            int responseCode = connection.getResponseCode();
            return 200 <= responseCode && responseCode < 500;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to connect to " + urlRoot, e);
            return false;
        }
    }
}
