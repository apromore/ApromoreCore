/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.cmapper;

// Java 2 Standard Edition packages
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Applet;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

// Local packages
import com.processconfiguration.ConfigurationMapping;
import com.processconfiguration.cmap.CMAP;

import org.apromore.filestore.client.FileStoreService;
import org.apromore.helper.Version;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.ProcessService;

import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TDocumentation;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * A user interface to launch the Quaestio applet.
 */
public class CmapperPlugin extends DefaultPortalPlugin {

    private static final String NATIVE_TYPE = "BPMN 2.0";

    @Inject
    private ProcessService processService;

    private String managerEndpoint, filestoreURL;

    public CmapperPlugin(String siteExternalHost, int siteExternalPort, String siteFilestore, String siteManager) {
        managerEndpoint = "http://" + siteExternalHost + ":" + siteExternalPort + "/" + siteManager + "/services";
        filestoreURL    = "http://" + siteExternalHost + ":" + siteExternalPort + "/" + siteFilestore;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Define configuration mapping";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Design";
    }

/*
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());
        int count=0;
        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){

                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                TabListitem tabItem=null;
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        count++;
                        tabItem=(TabListitem)item;
                    }
                }
                if(count==1){
                    try {
                        HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                        new CmapController(this.mainC, processVersion);
                    } catch (ConfigureException e) {
                        Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    } catch (RuntimeException e) {
                        Messagebox.show("Unable to cmap model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }else{
                    this.mainC.displayMessage("Select only 1 process model to cmap.");
                }
            }
        }
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if (selectedProcessVersions.size() == 1) {
            try {
                new CmapController(this.mainC, selectedProcessVersions);
            } catch (ConfigureException e) {
                Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                LOGGER.log(Level.WARNING, "Unable to cmap model", e);
            } catch (RuntimeException e) {
                Messagebox.show("Unable to cmap model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                LOGGER.log(Level.WARNING, "Unable to cmap model", e);
            }
        } else {
            this.mainC.displayMessage("Select only 1 process model to cmap.");
        }
*/

    @Override
    public void execute(PortalContext portalContext) {
        try {
            Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = portalContext.getSelection().getSelectedProcessModelVersions();
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/cmap.zul", null, null);

            URL cmapURL = null;
            URL qmlURL  = null;

            // Obtain the proxy for the WebDAV repository
            FileStoreService fileStore = (FileStoreService) SpringUtil.getBean("fileStoreClient");

            // Look up JAXB context
            JAXBContext context = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                              org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                              org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                              org.omg.spec.dd._20100524.di.ObjectFactory.class,
                                              com.processconfiguration.ObjectFactory.class,
                                              com.signavio.ObjectFactory.class);
            assert context != null;

            // Iterate through all selected models
            for (ProcessSummaryType process: selectedProcessVersions.keySet()) {
                for (VersionSummaryType version: selectedProcessVersions.get(process)) {

                    String modelName = process.getName();

                    // Look for the cmap URL in the C-BPMN document
                    ExportFormatResultType exportFormatResult;
                    try {
                        exportFormatResult = processService.exportProcess(
                            null,                                     // process name
                            process.getId(),                          // process ID
                            version.getName(),                        // branch
                            new Version(version.getVersionNumber()),  // version number,
                            NATIVE_TYPE,                              // nativeType,
                            null,                                     // annotation name,
                            false,                                    // with annotations?
                            Collections.EMPTY_SET                     // canoniser properties
                        );
                    } catch (Exception e) {
                        System.err.println("Unable to export BPMN model: " + e.getMessage());
                        e.printStackTrace();
                        throw new Exception("Unable to access " + modelName, e);
                    }

                    TDefinitions bpmn;
                    try {
                        bpmn = ((JAXBElement<TDefinitions>) context.createUnmarshaller().unmarshal(new StreamSource(exportFormatResult.getNative().getInputStream()))).getValue();
                    } catch (IOException e) {
                        throw new Exception("Unable to read " + modelName, e);
                    } catch (JAXBException e) {
                        throw new Exception("Unable to parse " + modelName, e);
                    }
                    assert bpmn != null;

                    String cmapURLString = findCmapURLString(bpmn);

                    // Is there a cmap link at all?
                    if (cmapURLString == null || cmapURLString.trim().isEmpty()) {
                        throw new Exception("The model " + modelName + " lacks a link to a configuration mapping.");
                    }

                    // Parse the cmap link into cmapURL
                    try {
                        URI baseURI = fileStore.getBaseURI();
                        System.err.println("FileStoreClient.getBaseURI=" + baseURI);
                        cmapURL = baseURI.resolve(cmapURLString).toURL();
                    } catch (MalformedURLException e) {
                        throw new Exception("The model " + modelName +
                            " has a malformed link to its configuration mapping: \"" + cmapURLString + "\"", e);
                    }
                    assert cmapURL != null;

                    // Download and parse the cmap document
                    CMAP cmap;
                    try {
                        /*
                        // Obtain the proxy for the WebDAV repository
                        FileStoreService fileStore = (FileStoreService) SpringUtil.getBean("fileStoreClient");

                        System.out.println("AMARANTH");
                        System.out.println(org.apromore.portal.util.StreamUtil.convertStreamToString(fileStore.getFile(cmapURL.toString())));
                        System.out.println("/AMARANTH");
                    
                        // Deserialize a JAXB representation of the cmap document from the WebDAV repository
                        cmap = (CMAP) JAXBContext.newInstance(com.processconfiguration.cmap.ObjectFactory.class)
                                                 .createUnmarshaller()
                                                 .unmarshal(new StreamSource(fileStore.getFile(cmapURL.toString())));
                        */

                        // Bypass fileStore and its OSGi issues by going directly to WebDAV
                        HttpURLConnection c = (HttpURLConnection) cmapURL.openConnection();
                        c.setRequestMethod("GET");
                        c.setRequestProperty("Authorization",
                            "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary("admin:password".getBytes("utf-8")));
                        c.connect();
                        System.err.println("Reponse code: " + c.getResponseCode());
                        System.err.println("Reponse message: " + c.getResponseMessage());
                        System.err.println("Content type: " + c.getContentType());
                        cmap = (CMAP) JAXBContext.newInstance(com.processconfiguration.cmap.ObjectFactory.class)
                                                  .createUnmarshaller()
                                                  .unmarshal(new StreamSource(c.getInputStream()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Unable to read the configuration mapping from " + cmapURL, e);
                    }
                    assert cmap != null;

                    // Parse the qml link into qmlURL
                    System.err.println("QML field from Cmap: " + cmap.getQml());
                    try {
                        qmlURL = new URL(cmapURL, cmap.getQml());
                    } catch (MalformedURLException e) {
                        throw new Exception("The cmap file " + cmapURLString +
                            " which " + modelName + " is linked to has an invalid questionnaire link: \"" +
                            cmap.getQml() + "\"", e);
                    }
                    System.err.println("QML URL from Cmap: " + qmlURL);

                    // Set the applet parameters
                    Applet configureA = (Applet) window.getFellow("cmapper");
                    configureA.setParam("apromore_model", process.getId() + " " + version.getName() + " " + version.getVersionNumber());
                    if (qmlURL != null) {
                        configureA.setParam("qml_url", qmlURL.toString());
                    }
                    if (cmapURL != null) {
                        configureA.setParam("cmap_url", cmapURL.toString());
                    }
                    configureA.setParam("manager_endpoint", managerEndpoint);
                    configureA.setParam("filestore_url", filestoreURL);

                    UserType user = portalContext.getCurrentUser();
                    if (user != null) {
                        System.err.println("User was " + user.getUsername());
                        configureA.setParam("user", user.getUsername());
                    } else {
                        System.err.println("User was null");
                    }

                    System.err.println("New params=" + configureA.getParams());

                    window.doModal();

                    System.err.println("Entered Cmapper applet mode");
                }
            }
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Extract the C-MAP href from a C-BPMN document, if it exists.
     *
     * @param bpmn  a C-BPMN document
     * @return the CMAP URL hypertext reference, or <code>null</code> if absent
     */
    private String findCmapURLString(final TDefinitions bpmn) {
        for (JAXBElement<? extends TRootElement> root: bpmn.getRootElement()) {
            TExtensionElements extensionElements = root.getValue().getExtensionElements();
            if (extensionElements != null) {
                for (Object object: extensionElements.getAny()) {
                    if (object instanceof ConfigurationMapping) {
                        return ((ConfigurationMapping) object).getHref();
                    }
                }
            }
        }

        return null;  // No CMAP href is set
    }
}
