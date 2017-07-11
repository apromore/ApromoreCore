package nl.rug.ds.bpm;

// Third party packages
import org.slf4j.LoggerFactory;

public class ConfigBean {

    static private String nusmvPath;

    public ConfigBean(String nusmvPath) {

        LoggerFactory.getLogger(getClass()).info("Verification logic configured with: nusmv.path=" + nusmvPath);

        this.nusmvPath = nusmvPath;
    }

    static public String getNuSMVPath() {
        return nusmvPath;
    }
}
