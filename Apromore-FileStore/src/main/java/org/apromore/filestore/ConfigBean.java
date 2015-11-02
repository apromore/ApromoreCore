package org.apromore.filestore;

// Third party packages
import org.slf4j.LoggerFactory;

public class ConfigBean {

    String siteFilestore;
    String filestoreDir;

    public ConfigBean(String siteFilestore, String filestoreDir) {

        LoggerFactory.getLogger(getClass()).info("Filestore configured with:" +
            " site.filestore=" + siteFilestore +
            " filestore.dir=" + filestoreDir);

        this.siteFilestore = siteFilestore;
        this.filestoreDir  = filestoreDir;
    }

    public String getSiteFilestore() { return siteFilestore; }
    public String getFilestoreDir()  { return filestoreDir;  }
}
