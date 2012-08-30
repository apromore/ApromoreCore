package au.edu.qut.apromore.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ApromoreConfig is responsible for reading APROMORE or ORYX configurations
 * from APROMORE.properties file.
 *
 * @author Mehrad Seyed Sadegh
 */
public class ApromoreConfig {

    private Properties properties;
    //public static String APROMORE_WSDL_LOCATION="APROMORE_WSDL_LOCATION";
    public static String LOG_GENERATED_PROCESSESS_TO_FILE = "LOG_GENERATED_PROCESSESS_TO_FILE";
    public static String LOG_FOLDER = "LOG_FOLDER";
    public static String TRUE = "true";
    //public static String FALSE="false";

    /**
     * initialise setting mapping
     *
     * @throws java.io.IOException
     */
    private void load() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("APROMORE.properties");
        properties = new Properties();
        properties.load(in);

    }

    /**
     * fetches the value for the setting stores in APROMORE.properties file.
     *
     * @param key
     * @return
     * @throws java.io.IOException
     */
    public String getProperty(String key) throws IOException {
        if (properties == null)
            load();
        return properties.getProperty(key);
    }

}
