package org.apromore.portal.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helps with debugging and seeing the data travel between services.
 */
public class StreamUtil {

    /**
     * Convert a InputStream to a String
     */
    public static String convertStreamToString(InputStream is) {
        return convert(is);
    }

    /**
     * Convert a DataHandler to a String
     */
    public static String convertStreamToString(DataHandler dh) {
        try {
            return convert(dh.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataHandler: " + e.toString();
        }
    }

    /**
     * Convert a DataHandler to a String
     */
    public static String convertStreamToString(DataSource ds) {
        try {
            return convert(ds.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataSource: " + e.toString();
        }
    }


    /* Does the work. */
    private static String convert(InputStream is) {
        try {
            if (is != null) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");
                } finally {
                    is.close();
                }
                return sb.toString();
            }
        } catch (IOException e) {
            return "error in readin the input streams: " + e.toString();
        }
        return "";
    }
}
