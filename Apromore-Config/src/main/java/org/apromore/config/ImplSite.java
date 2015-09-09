package org.apromore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by conforti on 10/08/15.
 */
public class ImplSite {

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
    private static final String SITE_EXTERNALHOST  = "site.externalhost";
    private static final String SITE_EXTERNALPORT  = "site.externalport";

    private String versionNumber;
    private String buildDate;
    private String host;
    private String port;
    private String editorDir;
    private String filestoreDir;
    private String siteManager;
    private String sitePortal;
    private String siteEditor;
    private String siteFilestore;
    private String externalhost;
    private String externalport;

    /**
     * The properties contained in {@link PROPERTY_FILE}.
     *
     * Don't access this directly; it's lazily initialized by accessing it via {@link getProperties}.
     */
    private Properties properties = null;

    /**
     * Lazily initialize {@link properties }
     *
     * @throws RuntimeException if the {@link PROPERTY_FILE} can't be found in the classpath
     */
    private Properties getProperties() {
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

    public String getBuildDate() {
        return getProperties().getProperty(VERSION_BUILD_DATE);
    }

    public String getEditorDir() {
        return getProperties().getProperty(EDITOR_DIR);
    }

    public String getFilestoreDir() {
        return getProperties().getProperty(FILESTORE_DIR);
    }

    public String getSiteManager() {
        return getProperties().getProperty(SITE_MANAGER);
    }

    public String getSitePortal() {
        return getProperties().getProperty(SITE_PORTAL);
    }

    public String getSiteEditor() {
        return getProperties().getProperty(SITE_EDITOR);
    }

    public String getSiteFilestore() {
        return getProperties().getProperty(SITE_FILESTORE);
    }

    public String getHost() {
        return getProperties().getProperty(SITE_HOST);
    }

    public String getPort() {
        return getProperties().getProperty(SITE_PORT);
    }

    public String getVersionNumber() {
        return getProperties().getProperty(VERSION_NUMBER);
    }

    public String getExternalhost() {
        return getProperties().getProperty(SITE_EXTERNALHOST);
    }

    public String getExternalportPort() {
        return getProperties().getProperty(SITE_EXTERNALPORT);
    }


    private void setProperties(Properties properties) {
        return;
    }

    // Public accessors

    public void setBuildDate(String buildDate) {
        return;
    }

    public void setEditorDir(String editorDir) {
        return;
    }

    public void setFilestoreDir(String filestoreDir) {
        return;
    }

    public void setSiteManager(String siteManager) {
        return;
    }

    public void setSitePortal(String sitePortal) {
        return;
    }

    public void setSiteEditor(String siteEditor) {
        return;
    }

    public void setSiteFilestore(String siteFilestore) {
        return;
    }

    public void setHost(String host) {
        return;
    }

    public void setPort(String port) {
        return;
    }

    public void setVersionNumber(String versionNumber) {
        return;
    }

    public void getExternalhost(String externalhost) {
        return;
    }

    public void getExternalport(String externalport) {
        return;
    }
}
