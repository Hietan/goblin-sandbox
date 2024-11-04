package org.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

/**
 * The `PropertiesLoader` class is responsible for loading and managing properties from a specified file.
 * It provides access to property values by key and throws an exception if a required property is missing.
 */
public class PropertiesLoader {
    private static final Logger logger = Logger.getLogger(PropertiesLoader.class.getName());

    private final Properties properties;

    /**
     * Constructs a `PropertiesLoader` by loading properties from the specified file path.
     * If the file cannot be loaded, an error is logged and the program exits.
     *
     * @param path the path of the properties file
     */
    public PropertiesLoader(Path path) {
        this.properties = new Properties();
        try (var input = Files.newInputStream(path)) {
            this.properties.load(input);
            logger.fine("Loaded properties file: " + path);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load properties file: " + path, e);
            exit(1);
        }
    }

    /**
     * Retrieves the value associated with the specified property key.
     * Throws an exception if the property key does not exist.
     *
     * @param key the property key
     * @return the value associated with the specified key
     * @throws IllegalArgumentException if the specified key does not exist in the properties
     */
    public String getProperty(String key) {
        String value = this.properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is missing.");
        }
        return value;
    }
}
