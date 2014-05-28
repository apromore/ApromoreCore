package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import com.processconfiguration.quaestio.ApromoreProcessModel;

/**
 * Execute the Cmapper as an applet.
 */
public class Applet extends JApplet {

    public void init() {
        super.init();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        Cmapper cmapper = new Cmapper();
                        //cmapper.setBpmn(new File("/Users/raboczi/Project/apromore/Apromore-Extras/bpmncmap/src/test/resources/2 Check-in.bpmn"));

                        /*
                        String model = getParameter("model_url");
                        if (model != null) {
                            cmapper.setBpmn(new File(model));
                        }
                        */

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
                                showStatus("Unable to read model " + model);
                                //log("Exception in apromore model");
                                e.printStackTrace();
                            }
                        }


                        setContentPane(Main.createUI(cmapper));

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
