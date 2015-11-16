/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
//import org.apache.webdav.ui.WebdavSystemView;  // Jakarta Slide variation
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// Local classes
import com.processconfiguration.quaestio.ApromoreProcessModel;
import org.apromore.filestore.client.DavFileSystemView;
import org.apromore.filestore.client.FileStoreService;
import org.apromore.filestore.client.FileStoreServiceClient;

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
                        fileStore = new FileStoreServiceClient(new URI(getParameter("filestore_url")));

                        // Check cmap_url parameter
                        try {
                            System.err.println("Cmap_url=" + getParameter("cmap_url") + " uri=" + new URI(getParameter("cmap_url")));
                            cmap = new DavCmap(new URI(getParameter("cmap_url")), fileStore);
                            cmapper.setCmap(cmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showStatus("Did not obtain existing Cmap: " + e);
                            cmap = null;
                        } 

                        // Check qml_url parameter
                        try {
                            System.err.println("Qml_url=" + getParameter("qml_url") + " uri=" + new URI(getParameter("qml_url")));
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

                                URI    manager   = new URI(getParameter("manager_endpoint"));
                                int    processID = Integer.valueOf(matcher.group(1));
                                String branch    = matcher.group(2);
                                String version   = matcher.group(3);
                                String user      = getParameter("user");

                                cmapper.setModel(new ApromoreProcessModel(manager, processID, branch, version, Applet.this, user));

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
                                DavFileSystemView dfsv = new DavFileSystemView(fileStore);

                                /* Jakarta Slide variation
                                URI uri = fileStore.getBaseURI();
                                String server = uri.getScheme() + "://" + uri.getRawAuthority();
                                String path   = uri.getPath() + "/";
                                System.err.println("WebdavSystemView server=" + server + " path=" + path);
                                FileSystemView dfsv = new WebdavSystemView(server, path, "admin", "password");
                                */

                                System.err.println("File system view roots=" + dfsv.getRoots() + " default=" + dfsv.getDefaultDirectory());

                                JFileChooser chooser = new JFileChooser(dfsv);
                                chooser.setDialogTitle(bundle.getString("Load_qml"));
                                chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("QML_questionnaire"), "qml"));
                                if (chooser.showOpenDialog(Applet.this) == JFileChooser.APPROVE_OPTION) {
                                    // Load the selected QML file
                                    System.err.println("File chooser selected " + chooser.getSelectedFile());
                                    URI fileURI = chooser.getSelectedFile().toURI();
                                    //URI fileURI = new URI(chooser.getSelectedFile().toString());
                                    System.err.println("File chooser selection as URI " + fileURI);
                                    URI selectedURI = new URI(fileURI.getRawPath());
                                    System.err.println("File chooser selection as relative URI " + selectedURI);
                                    cmapper.setQml(new DavQml(selectedURI, fileStore));
                                    JOptionPane.showMessageDialog(Applet.this, "Loaded questionnaire " + chooser.getSelectedFile());
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
                                //JFileChooser chooser = new JFileChooser(new WebdavSystemView("http://localhost:9000", "/filestore/dav/", "admin", "password"));  // Jakarta Slide variation
                                chooser.setDialogTitle(bundle.getString("Save_cmap"));
                                chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("Configuration_mapping"), "cmap"));
                                if (cmap != null) {
                                    chooser.setSelectedFile(new File(cmap.getURI().getPath()));
                                }
                                if (chooser.showSaveDialog(Applet.this) == JFileChooser.APPROVE_OPTION) {
                                    // Save the C-Map as the chosen filename
                                    URI uri = new URI("/filestore/dav" + chooser.getSelectedFile().toURI().getRawPath());
                                    //URI uri = new URI(chooser.getSelectedFile().toString().replaceAll(" ", "%20"));  // Jakarta Slide variation
                                    cmap = new DavCmap(uri, fileStore);
                                    cmapper.save(cmap);
                                    cmapper.setCmap(cmap);
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

                    // "Link model" button writes the URL of this cmap into the C-BPMN
                    JButton linkButton = new JButton(new AbstractAction("Link") {
                        public void actionPerformed(ActionEvent event) {
                            showStatus("Link");
                            try {
                                cmapper.link();
                            } catch (Exception e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(
                                    null,
                                    "Unable to link from model to cmap: " + e,
                                    bundle.getString("Error"),
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    });
                    linkButton.setToolTipText("Insert link from the BPMN to this configuration map");
                    buttonPanel.add(linkButton);

                    return buttonPanel;
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet, exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
