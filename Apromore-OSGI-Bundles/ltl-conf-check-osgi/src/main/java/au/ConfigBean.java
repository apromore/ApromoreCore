package au;

import org.slf4j.LoggerFactory;

/**
 * Created by armascer on 29/11/2017.
 */
public class ConfigBean {

    static private String downwardPath;

    public ConfigBean(String downwardPath) {
        LoggerFactory.getLogger(getClass()).info("Verification logic configured with: downward.path=" + downwardPath);
        this.downwardPath = downwardPath;
    }

    static public String getDownwardPath() {
        return downwardPath;
    }
}
