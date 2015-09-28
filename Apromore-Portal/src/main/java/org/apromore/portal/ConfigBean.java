package org.apromore.portal;

public class ConfigBean {

    private String siteEditor;
    private String siteExternalHost;
    private int    siteExternalPort;
    private String versionNumber;
    private String versionBuildDate;

    public ConfigBean(String siteEditor, String siteExternalHost, int siteExternalPort, String versionNumber, String versionBuildDate) {
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
