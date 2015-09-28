package org.apromore.filestore;

public class ConfigBean {

    String siteFilestore;
    String filestoreDir;

    public ConfigBean(String siteFilestore, String filestoreDir) {
        this.siteFilestore = siteFilestore;
        this.filestoreDir  = filestoreDir;
    }

    public String getSiteFilestore() { return siteFilestore; }
    public String getFilestoreDir()  { return filestoreDir;  }
}
