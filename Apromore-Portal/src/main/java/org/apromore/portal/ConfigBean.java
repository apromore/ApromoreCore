package org.apromore.portal;

// Third party packages
import org.slf4j.LoggerFactory;

public class ConfigBean {

    private String siteEditor;
    private String siteExternalHost;
    private int    siteExternalPort;
    private String versionNumber;
    private String versionBuildDate;

    public ConfigBean(String siteEditor, String siteExternalHost, int siteExternalPort, String versionNumber, String versionBuildDate) {

        LoggerFactory.getLogger(getClass()).info("Portal configured with:" +
            " site.editor=" + siteEditor +
            " site.externalHost=" + siteExternalHost +
            " site.externalPort=" + siteExternalPort +
            " version.number=" + versionNumber +
            " version.builddate=" + versionBuildDate);

        this.siteEditor       = siteEditor;
        this.siteExternalHost = siteExternalHost;
        this.siteExternalPort = siteExternalPort;
        this.versionNumber    = versionNumber;
        this.versionBuildDate = versionBuildDate;
    }

    public String getSiteEditor()       { return siteEditor; }
    public String getSiteExternalHost() { return siteExternalHost; }
    public int    getSiteExternalPort() { return siteExternalPort; }
    public String getVersionNumber()    { return versionNumber; }
    public String getVersionBuildDate() { return versionBuildDate; }
}
