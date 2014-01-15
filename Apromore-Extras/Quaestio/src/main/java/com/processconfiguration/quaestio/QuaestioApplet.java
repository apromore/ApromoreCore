package com.processconfiguration.quaestio;

// Java 2 Standard classes
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;

// Local classes
import com.processconfiguration.quaestio.Main;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

/**
 * Present the questionnaire editor as a browser applet.
 */
public class QuaestioApplet extends JApplet {

    public void init() {
        super.init();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                Main main;
                public void run() {
                    main = new Main() {
                        @Override
                        protected void browse(final URL url) throws Exception {
                            main.log("Browse " + url);
                            getAppletContext().showDocument(url, "target");
                            main.log("Browsed " + url);
                        }
                    };

                    try {
                        main.setEditorURL(new URL(getDocumentBase(), "../editor/"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    String cmap = getParameter("cmap_url");
                    main.log("Cmap URL: " + cmap);
                    if (cmap != null) {
                        try {
                            main.setLinkedCmap(new UrlCmap(cmap));
                        } catch (Exception e) {
                            showStatus("Unable to read configuration map URL " + cmap);
                            main.log("Exception in cmap URL");
                            e.printStackTrace();
                        }
                    }

                    main.log("Step 1");

                    String model = getParameter("model_url");
                    main.log("Model URL: " + model);
                    if (model != null) {
                        try {
                            main.setLinkedProcessModel(new UrlProcessModel(model));
                        } catch (Exception e) {
                            showStatus("Unable to read model URL " + model);
                            main.log("Exception in apromore model URL");
                            e.printStackTrace();
                        }
                    }

                    main.log("Step 2");

                    String qml = getParameter("qml_url");
                    main.log("QML URL: " + qml);
                    if (qml != null) {
                        try {
                            main.log("Step 2a");
                            main.openUrlQuestionnaireModel(qml);
                            main.log("Step 2b");
                        } catch (Exception e) {
                            showStatus("Unable to read questionnaire URL " + qml);
                            main.log("Exception in apromore QML URL");
                            e.printStackTrace();
                        }
                    }

                    main.log("Step 3");

                    /*String*/ model = getParameter("apromore_model");
                    if (model != null) {
                        try {
                            Pattern pattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)");
                            Matcher matcher = pattern.matcher(model);
                            if (!matcher.matches()) {
                                throw new Exception("Unable to parse apromore_model param: " + model);
                            }

                            int    processID = Integer.valueOf(matcher.group(1)).intValue();
                            String branch    = matcher.group(2);
                            double version   = Double.valueOf(matcher.group(3));

                            main.setLinkedProcessModel(new ApromoreProcessModel(processID, branch, version));

                        } catch (Exception e) {
                            showStatus("Unable to read model " + model);
                            main.log("Exception in apromore model");
                            e.printStackTrace();
                        }
                    }

                    /*String*/ cmap = getParameter("cmap");
                    if (cmap != null) {
                        try {
                            main.setLinkedCmap(new FileCmap(new File(cmap)));
                        } catch (Exception e) {
                            showStatus("Unable to read configuration map " + cmap);
                            main.log("Exception in cmap");
                            e.printStackTrace();
                        }
                    }

                    /*String*/ model = getParameter("model");
                    if (model != null) {
                        try {
                            main.setLinkedProcessModel(new FileProcessModel(new File(model)));
                        } catch (Exception e) {
                            showStatus("Unable to read model " + model);
                            main.log("Exception in model");
                            e.printStackTrace();
                        }
                    }

                    /*String*/ qml = getParameter("qml");
                    if (qml != null) {
                        try {
                            System.err.println("Opening QML file " + qml);
                            main.openQuestionnaireModel(new File(qml));
                        } catch (Exception e) {
                            showStatus("Unable to read questionnaire " + qml);
                            main.log("Exception in QML");
                            e.printStackTrace();
                        }
                    }

                    add(main.getJContentPane());
                    //setJMenuBar(main.getJJMenuBar());  // No menu bar when embedded in the Apromore portal web application

                    main.log("Exiting applet init");
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet, exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
