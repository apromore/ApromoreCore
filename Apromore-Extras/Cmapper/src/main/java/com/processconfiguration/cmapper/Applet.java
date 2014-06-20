package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

// Third party classes
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// Local classes
import com.processconfiguration.quaestio.ApromoreProcessModel;
import org.apromore.filestore.client.FileStoreService;

/**
 * Present the Cmapper as an applet.
 */
public class Applet extends JApplet {

    private static ResourceBundle bundle = ResourceBundle.getBundle("com.processconfiguration.cmapper.Applet");

    public void init() {
        super.init();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                // Location in the WebDAV repository from which we'll be reading and writing the cmap
                private final Cmapper cmapper = new Cmapper();
                private Cmap cmap = null;
                private FileStoreService fileStore;

                public void run() {
                    try {

                        // Obtain the proxy for the WebDAV repository
                        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/filestoreClientContext.xml");
                        fileStore = (FileStoreService) applicationContext.getAutowireCapableBeanFactory().getBean("fileStoreClientExternal");

                        // Check cmap_url parameter
                        try {
                            cmap = new DavCmap(new URI(getParameter("cmap_url")), fileStore);
                            cmapper.setCmap(cmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showStatus("Did not obtain existing Cmap: " + e);
                            cmap = null;
                        } 

                        // Check qml_url parameter
                        try {
                            cmapper.setQml(new DavQml(new URI(getParameter("qml_url")), fileStore));

                        } catch (Exception e) {
                            e.printStackTrace();
                            showStatus("Did not obtain existing questionnaire: " + e);
                        } 

                        // Check apromore_model parameter
                        final String model = getParameter("apromore_model");
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
                                e.printStackTrace();
                                showStatus(bundle.getString("Unable_to_read_model") + model);
                                //log("Exception in apromore model");
                            }
                        }

                        final JPanel contentPane = new JPanel(new BorderLayout());
                        setContentPane(contentPane);

                        contentPane.add(new CmapperView(cmapper), BorderLayout.CENTER);
                        contentPane.add(createButtonPanel(cmapper, contentPane), BorderLayout.SOUTH);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /**
                 * Create the panel of buttons that replace the menu options of the desktop version of the Cmapper.
                 *
                 * @param cmapper  the model manipulated by these controllers
                 * @param contentPane
                 * @return a panel containing "Load QML" and "Save C-map" buttons
                 */
                private JPanel createButtonPanel(final Cmapper cmapper, final JPanel contentPane) {
                    final JPanel buttonPanel = new JPanel(new FlowLayout());

                    // "Load QML" button downloads a questionnaire
                    JButton loadButton = new JButton(new AbstractAction(bundle.getString("Load_qml")) {
                        public void actionPerformed(ActionEvent event) {
                            showStatus(bundle.getString("Load_qml"));
                            try {
                                JFileChooser chooser = new JFileChooser(new DavFileSystemView(fileStore));
                                chooser.setDialogTitle(bundle.getString("Load_qml"));
                                chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("QML_questionnaire"), "qml"));
                                if (chooser.showOpenDialog(Applet.this) == JFileChooser.APPROVE_OPTION) {
                                    // Load the selected QML file
                                    URI uri = new URI(chooser.getSelectedFile().toURI().getRawPath());
                                    cmapper.setQml(new DavQml(uri, fileStore));

                                    JOptionPane.showMessageDialog(Applet.this, "Loaded questionnaire " + uri);
                                }
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(
                                    null,
                                    bundle.getString("Unable_to_read_qml") + e,
                                    bundle.getString("Error"),
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    });
                    loadButton.setToolTipText(bundle.getString("loadButton_tooltip"));
                    buttonPanel.add(loadButton);

                    // "Show QML" button pops out a cheat sheet for the questionnaire facts
                    final JButton showButton = new JButton(new AbstractAction(bundle.getString("Show_qml")) {
                        QmlFrame qmlFrame = null;
                        public void actionPerformed(ActionEvent event) {
                            showStatus(bundle.getString("Show_qml"));
                            if (qmlFrame != null) {
                                qmlFrame.dispose();
                            }
                            qmlFrame = new QmlFrame(cmapper.getQml());
                            qmlFrame.setVisible(true);
                        }
                    });
                    showButton.setEnabled(cmapper.isQmlSet());
                    showButton.setToolTipText(bundle.getString("showButton_tooltip"));
                    buttonPanel.add(showButton);

                    // "Show QML" button enabled/disabled whenever the cmapper model changes
                    cmapper.addObserver(new Observer() {
                        public void update(Observable observable, Object arg) {
                            showButton.setEnabled(((Cmapper) observable).isQmlSet());
                        }
                    });

                    // "Save C-map" button uploads the cmap file via WebDAV
                    JButton saveButton = new JButton(new AbstractAction(bundle.getString("Save_cmap")) {
                        public void actionPerformed(ActionEvent event) {
                            showStatus(bundle.getString("Save_cmap"));
                            try {
                                JFileChooser chooser = new JFileChooser(new DavFileSystemView(fileStore));
                                chooser.setDialogTitle(bundle.getString("Save_cmap"));
                                chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("Configuration_mapping"), "cmap"));
                                if (cmap != null) {
                                    chooser.setSelectedFile(new File(cmap.getURI().getPath()));
                                }
                                if (chooser.showSaveDialog(Applet.this) == JFileChooser.APPROVE_OPTION) {
                                    // Save the C-Map as the chosen filename
                                    URI uri = new URI("http", "admin:password", "localhost", 9000,
                                                      "/filestore/dav" + chooser.getSelectedFile().toString(),
                                                      null, null);
                                    cmap = new DavCmap(uri, fileStore);
                                    cmapper.save(cmap);
                                    JOptionPane.showMessageDialog(Applet.this, "Saved configuration mapping " + uri);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(
                                    null,
                                    bundle.getString("Unable_to_save_cmap") + e,
                                    bundle.getString("Error"),
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    });
                    saveButton.setToolTipText(bundle.getString("saveButton_tooltip"));
                    buttonPanel.add(saveButton);

                    return buttonPanel;
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet, exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
