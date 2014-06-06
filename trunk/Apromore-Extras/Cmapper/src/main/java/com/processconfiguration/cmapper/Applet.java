package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.io.File;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import com.processconfiguration.quaestio.ApromoreProcessModel;

/**
 * Present the Cmapper as an applet.
 */
public class Applet extends JApplet {

    private static ResourceBundle bundle = ResourceBundle.getBundle("com.processconfiguration.cmapper.Applet");

    public void init() {
        super.init();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        Cmapper cmapper = new Cmapper();

                        // Check apromore_model parameter
                        String model = getParameter("apromore_model");
                        if (model != null) {
                            try {
                                Pattern pattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)");
                                Matcher matcher = pattern.matcher(model);
                                if (!matcher.matches()) {
                                    throw new Exception("Unable to parse apromore_model param: " + model);
                                }

                                int    processID = Integer.valueOf(matcher.group(1));
                                String branch    = matcher.group(2);
                                String version   = matcher.group(3);

                                cmapper.setBpmn(new ApromoreProcessModel(processID, branch, version, Applet.this));

                            } catch (Exception e) {
                                showStatus(bundle.getString("Unable_to_read_model") + model);
                                //log("Exception in apromore model");
                                e.printStackTrace();
                            }
                        }

                        setContentPane(new CmapperView(cmapper));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet, exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
