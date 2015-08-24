package org.apromore.config;

// Java 2 Standard packages
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Site {

    private static final String PROPERTY_FILE = "org/apromore/config/site.properties";

    // Property keys

    private static final String VERSION_NUMBER     = "version.number";
    private static final String VERSION_BUILD_DATE = "version.builddate";
    private static final String SITE_HOST          = "site.host";
    private static final String SITE_PORT          = "site.port";
    private static final String EDITOR_DIR         = "editor.dir";
    private static final String FILESTORE_DIR      = "filestore.dir";
    private static final String SITE_MANAGER       = "site.manager";
    private static final String SITE_PORTAL        = "site.portal";
    private static final String SITE_EDITOR        = "site.editor";
    private static final String SITE_FILESTORE     = "site.filestore";

    /**
     * The properties contained in {@link PROPERTY_FILE}.
     *
     * Don't access this directly; it's lazily initialized by accessing it via {@link getProperties}.
     */
    private static Properties properties = null;

    /**
     * Lazily initialize {@link properties }
     *
     * @throws RuntimeException if the {@link PROPERTY_FILE} can't be found in the classpath
     */
    private static Properties getProperties() {
        if (properties == null) {
            try {
                InputStream inputStream = Site.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
                Properties tempProperties = new Properties();
                tempProperties.load(inputStream);
                properties = tempProperties;  // only want to assign properties if loading succeeded
            } catch (IOException e) {
                throw new RuntimeException("Unable to obtain configuration properties from classpath " + PROPERTY_FILE, e);
            }
        }
        return properties;
    }

    // Public accessors

    public static String getBuildDate() {
        return getProperties().getProperty(VERSION_BUILD_DATE);
    }

    public static String getEditorDir() {
        return getProperties().getProperty(EDITOR_DIR);
    }

    public static String getFilestoreDir() {
        return getProperties().getProperty(FILESTORE_DIR);
    }

    public static String getSiteManager() {
        return getProperties().getProperty(SITE_MANAGER);
    }

    public static String getSitePortal() {
        return getProperties().getProperty(SITE_PORTAL);
    }

    public static String getSiteEditor() {
        return getProperties().getProperty(SITE_EDITOR);
    }

    public static String getSiteFilestore() {
        return getProperties().getProperty(SITE_FILESTORE);
    }


    public static String getHost() {
        return getProperties().getProperty(SITE_HOST);
    }

    public static String getPort() {
        return getProperties().getProperty(SITE_PORT);
    }

    public static String getVersionNumber() {
        return getProperties().getProperty(VERSION_NUMBER);
    }
}
