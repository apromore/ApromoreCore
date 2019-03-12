/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.editor.loganimation2;

// Java 2 Standard Edition
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;

// Java 2 Enterprise Edition
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Third party
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

// Local
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.replay.AnimationLog;
import de.hpi.bpmn2_0.replay.Optimizer;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.Replayer;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import org.apromore.service.loganimation.LogAnimationService;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;

/**
 * Create animation for a process diagram from event log
 * It should be accessible at: /bpmnanimation
 *
 * @author Bruce Nguyen (QUT)
 */
public class BPMNAnimationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BPMNAnimationServlet.class.getCanonicalName());
    
    private static final String UPLOAD_DIRECTORY = "upload";
    private static final int THRESHOLD_SIZE = 1024 * 1024 * 200; // 200MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 500; // 500MB
    private static final int REQUEST_SIZE = 1024 * 1024 * 500; // 500MB

    @Inject
    private LogAnimationService logAnimationService;

    @Override
    public void init() throws ServletException { 
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<LogAnimationService.Log> logs = new ArrayList<>();
        Set<XLog> xlogs = new HashSet<>();
        //Set<XLog> optimizedLogs = new HashSet<>();
        String jsonData = "";
        Definitions bpmnDefinition = null;
        
        if (!ServletFileUpload.isMultipartContent(req)) {
                res.getWriter().println("Does not support!");
                // if not, we stop here
                return;
        }
        
       /*
        * ------------------------------------------
        * Import event log files
        * logs variable contains list of imported logs
        * ------------------------------------------
        */ 
        // configures some settings
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(THRESHOLD_SIZE);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(REQUEST_SIZE);
        
        // constructs the directory path to store upload file
        String uploadPath = getServletContext().getRealPath("")
                + File.separator + UPLOAD_DIRECTORY;
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
                uploadDir.mkdir();
        } 

        try {
            // parses the request's content to extract file data
            List<FileItem> formItems = upload.parseRequest(req);
            Iterator iter = formItems.iterator();

            // iterates over form's fields
            while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();

                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = uploadPath + File.separator + fileName;
                            File storeFile = new File(filePath);

                            // saves the file on disk
                            item.write(storeFile);
                            LOGGER.info("Finish writing uploaded file to temp dir: " + filePath);

                            LOGGER.info("Start importing file: " + filePath);
                            item.getInputStream();
                            OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
                            XLog xlog = (XLog)logImporter.importFile(storeFile);

                            // color field must follow the log file
                            item = (FileItem) iter.next();
                            assert item.isFormField();
                            assert "color".equals(item.getFieldName());
                            LOGGER.info("Log color: " + item.getString());
                            String color = item.getString();

                            // Record the log
                            LogAnimationService.Log log = new LogAnimationService.Log();
                            log.fileName = fileName;
                            log.xlog     = xlog;
                            log.color    = item.getString();
                            logs.add(log);
                            xlogs.add(xlog);
                    } else {
                        if (item.getFieldName().equals("json")) {
                            jsonData = item.getString();
                        }
                    }
            }            
            
            // Convert the model from Signavio JSON format to a BPMN-formatted string
            StringWriter stringWriter = new StringWriter();
            JAXBContext.newInstance("de.hpi.bpmn2_0.model:de.hpi.bpmn2_0.model.extension.synergia", getClass().getClassLoader())
                       .createMarshaller()
                       .marshal(getBPMNfromJson(jsonData), stringWriter);

            // Use the log animation service to obtain the animation data
            String jsonAnimationData = logAnimationService.createAnimation(stringWriter.toString(), logs);
            
            // Write out the animation data as the servlet response
            PrintWriter out = res.getWriter();
            res.setContentType("text/html");  // Ext2JS's file upload requires this rather than "application/json"
            res.setStatus(200);
            out.write(jsonAnimationData);
            
        } catch (Exception e) {
            try {
                LOGGER.log(Level.SEVERE, "Failed to POST logs", e);
                String json = "{success:false, errors: {errormsg: '" +e.getMessage() + "'}}"; 
                res.setContentType("text/html; charset=UTF-8");
                res.getWriter().print(json);
            } catch (Exception el) {
                System.err.println("Original exception was:");
                e.printStackTrace();
                System.err.println("Exception in exception handler was:");
                el.printStackTrace();
            }
        }

    }

    private Definitions getBPMNfromJson(String jsonData) throws BpmnConverterException, JSONException {
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        return definitions;
    }
}
