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

package org.apromore.portal.dialogController;

// Java 2 Standard packages
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import com.github.sardine.SardineFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Applet;
import org.zkoss.zul.Window;

// Local packages
import com.processconfiguration.ConfigurationMapping;
import com.processconfiguration.cmap.CMAP;

import org.apromore.filestore.client.FileStoreService;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.UserSessionManager;

import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TDocumentation;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * Invoke the Configuration Mapper UI to configure a process model version.
 */
public class CmapController extends BaseController {

    private static final String NATIVE_TYPE = "BPMN 2.0";
    private static final long serialVersionUID = 1L;
    private FileStoreService fileStore;
    private MainController mainC;
    private Window cmapW;

    /** The list of process versions to configure. */
    private Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions;

    /**
     * Sole constructor.
     *
     * @param mainC
     * @param selectedProcessVersions
     * @throws ConfigureException if <var>selectedProcessVersions</var> include a process which doesn't have
     *   a valid cmap and qml associated with it
     */
    public CmapController(MainController mainC, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions)
        throws ConfigureException {
        this.mainC = mainC;

        this.cmapW = (Window) Executions.createComponents("macros/cmap.zul", null, null);

        URL cmapURL = null;
        URL qmlURL  = null;

        // Obtain the proxy for the WebDAV repository
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(Sessions.getCurrent().getWebApp().getServletContext());
        fileStore = (FileStoreService) applicationContext.getAutowireCapableBeanFactory().getBean("fileStoreClient");

        // Look up JAXB context
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                              org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                              org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                              org.omg.spec.dd._20100524.di.ObjectFactory.class,
                                              com.processconfiguration.ObjectFactory.class,
                                              com.signavio.ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create JAXB context", e);
        }
        assert context != null;

        // Iterate through all selected models
        for (ProcessSummaryType process: selectedProcessVersions.keySet()) {
            for (VersionSummaryType version: selectedProcessVersions.get(process)) {

                String modelName = process.getName();

                // Look for the cmap URL in the C-BPMN document
                ExportFormatResultType exportFormatResult;
                try {
                    exportFormatResult = getService().exportFormat(
                        process.getId(),             // process ID
                        null,                        // process name
                        version.getName(),           // branch
                        version.getVersionNumber(),  // version number,
                        NATIVE_TYPE,                 // nativeType,
                        null,                        // annotation name,
                        false,                       // with annotations?
                        null,                        // owner
                        Collections.EMPTY_SET        // canoniser properties
                    );
                } catch (Exception e) {
                    System.err.println("Unable to export BPMN model: " + e.getMessage());
                    e.printStackTrace();
                    throw new ConfigureException("Unable to access " + modelName, e);
                }

                TDefinitions bpmn;
                try {
                    bpmn = ((JAXBElement<TDefinitions>) context.createUnmarshaller().unmarshal(new StreamSource(exportFormatResult.getNative().getInputStream()))).getValue();
                } catch (IOException e) {
                    throw new ConfigureException("Unable to read " + modelName, e);
                } catch (JAXBException e) {
                    throw new ConfigureException("Unable to parse " + modelName, e);
                }
                assert bpmn != null;

                String cmapURLString = findCmapURLString(bpmn);

                // Is there a cmap link at all?
                if (cmapURLString == null || cmapURLString.trim().isEmpty()) {
                    System.err.println("The model " + modelName + " lacks a link to a configuration mapping.");
                } else {
                    // Parse the cmap link into cmapURL
                    try {
                        FileStoreService fileStore = (FileStoreService) SpringUtil.getBean("fileStoreClient");
                        URI baseURI = fileStore.getBaseURI();
                        cmapURL = baseURI.resolve(cmapURLString).toURL();
                    } catch (MalformedURLException e) {
                        throw new ConfigureException("The model " + modelName +
                            " has a malformed link to its configuration mapping: \"" + cmapURLString + "\"", e);
                    }
                    assert cmapURL != null;

                    // Download and parse the cmap document
                    CMAP cmap;
                    try {
                        /*
                        cmap = (CMAP) JAXBContext.newInstance(com.processconfiguration.cmap.ObjectFactory.class)
                                                 .createUnmarshaller()
                                                 .unmarshal(new StreamSource(fileStore.getFile(cmapURL.toString())));
                        */
 
                        // Bypass the OSGi issues with FileStoreService, reading WebDAV directly
                        HttpURLConnection c = (HttpURLConnection) cmapURL.openConnection();
                        c.setRequestProperty("Authorization",
                            "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary("admin:password".getBytes("utf-8")));
                        c.setRequestMethod("GET");
                        c.connect();
                        System.err.println("Reponse code: " + c.getResponseCode());
                        System.err.println("Reponse message: " + c.getResponseMessage());
                        System.err.println("Content type: " + c.getContentType());
                        cmap = (CMAP) JAXBContext.newInstance(com.processconfiguration.cmap.ObjectFactory.class)
                                                 .createUnmarshaller()
                                                 .unmarshal(new StreamSource(c.getInputStream()));

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ConfigureException("Unable to read the configuration mapping from " + cmapURL, e);
                    }
                    assert cmap != null;

                    // Parse the qml link into qmlURL
                    if (cmap != null) {
                        System.err.println("QML field from Cmap: " + cmap.getQml());
                        try {
                            qmlURL = new URL(cmapURL, cmap.getQml());
                        } catch (MalformedURLException e) {
                            throw new ConfigureException("The cmap file " + cmapURLString +
                                " which " + modelName + " is linked to has an invalid questionnaire link: \"" +
                                cmap.getQml() + "\"", e);
                        }
                        System.err.println("QML URL from Cmap: " + qmlURL);
                    }
                }

                // Set the applet parameters
                Applet configureA = (Applet) this.cmapW.getFellow("cmapper");
                configureA.setParam("manager_endpoint", "http://" + config.getSiteExternalHost() + ":" + config.getSiteExternalPort() + "/" + config.getSiteManager() + "/services/manager");
                configureA.setParam("filestore_url", "http://" + config.getSiteExternalHost() + ":" + config.getSiteExternalPort() + "/" + config.getSiteFilestore());
                configureA.setParam("apromore_model", process.getId() + " " + version.getName() + " " + version.getVersionNumber());
                if (qmlURL != null) {
                    configureA.setParam("qml_url", qmlURL.toString());
                }
                if (cmapURL != null) {
                    configureA.setParam("cmap_url", cmapURL.toString());
                }

                UserType user = UserSessionManager.getCurrentUser();
                if (user != null) {
                    System.err.println("User was " + user.getUsername());
                    configureA.setParam("user", user.getUsername());
                } else {
                    System.err.println("User was null");
                }

                System.err.println("New params=" + configureA.getParams());

                this.cmapW.doModal();

                System.err.println("Entered Cmapper applet mode");
            }
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
