/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package servlet;

import de.hpi.bpmn2_0.animation.AnimationJSONBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.replay.AnimationLog;
import de.hpi.bpmn2_0.replay.Optimizer;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.Replayer;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.signaturediscovery.encoding.EncodeTraces;

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

    /**
     * Log record.
     */
    private class Log {
        String fileName;
        XLog   xlog;
        String color;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out=null;
        List<Log> logs = new ArrayList<>();
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
                            Log log = new Log();
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
            
            /*
            * ------------------------------------------
            * Convert JSON map to BPMN objects
            * ------------------------------------------
            */         
            LOGGER.info("Then, convert JSON to BPMN map objects");
            if (!jsonData.equals("")) {
                bpmnDefinition = this.getBPMNfromJson(jsonData);
                LOGGER.info("BPMN Diagram Definition" + bpmnDefinition.toString());
            } else {
                LOGGER.info("JSON data sent to server is empty");
            }
            
            
            /*
            * ------------------------------------------
            * Optimize logs and process model
            * ------------------------------------------
            */
            Optimizer optimizer = new Optimizer();
            for (Log log : logs) {
                //optimizedLogs.add(optimizer.optimizeLog(log.xlog));
                log.xlog = optimizer.optimizeLog(log.xlog);
            }
            bpmnDefinition = optimizer.optimizeProcessModel(bpmnDefinition);

            
            
            /*
            * ------------------------------------------
            * Check BPMN diagram validity and replay log
            * ------------------------------------------
            */              
            
            //Reading backtracking properties for testing
            String propertyFile = "/editor/animation/properties.xml";
            InputStream is = getServletContext().getResourceAsStream(propertyFile);            
            Properties props = new Properties();            
            props.loadFromXML(is);
            ReplayParams params = new ReplayParams();
            params.setMaxCost(Double.valueOf(props.getProperty("MaxCost")).doubleValue());
            params.setMaxDepth(Integer.valueOf(props.getProperty("MaxDepth")).intValue());
            params.setMinMatchPercent(Double.valueOf(props.getProperty("MinMatchPercent")).doubleValue());
            params.setMaxMatchPercent(Double.valueOf(props.getProperty("MaxMatchPercent")).doubleValue());
            params.setMaxConsecutiveUnmatch(Integer.valueOf(props.getProperty("MaxConsecutiveUnmatch")).intValue());
            params.setActivityMatchCost(Double.valueOf(props.getProperty("ActivityMatchCost")).doubleValue());
            params.setActivitySkipCost(Double.valueOf(props.getProperty("ActivitySkipCost")).doubleValue());
            params.setEventSkipCost(Double.valueOf(props.getProperty("EventSkipCost")).doubleValue());
            params.setNonActivityMoveCost(Double.valueOf(props.getProperty("NonActivityMoveCost")).doubleValue());
            params.setTraceChunkSize(Integer.valueOf(props.getProperty("TraceChunkSize")).intValue());
            params.setMaxNumberOfNodesVisited(Integer.valueOf(props.getProperty("MaxNumberOfNodesVisited")).intValue());
            params.setMaxActivitySkipPercent(Double.valueOf(props.getProperty("MaxActivitySkipPercent")).doubleValue());
            params.setMaxNodeDistance(Integer.valueOf(props.getProperty("MaxNodeDistance")).intValue());
            params.setTimelineSlots(Integer.valueOf(props.getProperty("TimelineSlots")).intValue());
            params.setTotalEngineSeconds(Integer.valueOf(props.getProperty("TotalEngineSeconds")).intValue());
            params.setProgressCircleBarRadius(Integer.valueOf(props.getProperty("ProgressCircleBarRadius")).intValue());
            params.setSequenceTokenDiffThreshold(Integer.valueOf(props.getProperty("SequenceTokenDiffThreshold")).intValue());
            params.setMaxTimePerTrace(Long.valueOf(props.getProperty("MaxTimePerTrace")).longValue());
            params.setMaxTimeShortestPathExploration(Long.valueOf(props.getProperty("MaxTimeShortestPathExploration")).longValue());
            params.setExactTraceFitnessCalculation(props.getProperty("ExactTraceFitnessCalculation"));
            params.setBacktrackingDebug(props.getProperty("BacktrackingDebug"));
            params.setExploreShortestPathDebug(props.getProperty("ExploreShortestPathDebug"));     
            params.setCheckViciousCycle(props.getProperty("CheckViciousCycle"));
            params.setStartEventToFirstEventDuration(Integer.valueOf(props.getProperty("StartEventToFirstEventDuration")).intValue());
            params.setLastEventToEndEventDuration(Integer.valueOf(props.getProperty("LastEventToEndEventDuration")).intValue());            

            Replayer replayer = new Replayer(bpmnDefinition, params);
            ArrayList<AnimationLog> replayedLogs = new ArrayList();
            if (replayer.isValidProcess()) {
                LOGGER.info("Process " + bpmnDefinition.getId() + " is valid");
                EncodeTraces.getEncodeTraces().read(xlogs); //build a mapping from traceId to charstream
                for (Log log: logs) {

                    AnimationLog animationLog = replayer.replay(log.xlog, log.color);
                    //AnimationLog animationLog = replayer.replayWithMultiThreading(log.xlog, log.color);
                    if (animationLog !=null && !animationLog.isEmpty()) {
                        replayedLogs.add(animationLog);
                    }
                }

            } else {
                LOGGER.info(replayer.getProcessCheckingMsg());
            }
            
            /*
            * ------------------------------------------
            * Return Json animation
            * ------------------------------------------
            */
            LOGGER.info("Start sending back JSON animation script to browser");
            if (replayedLogs.size() > 0) {
                out = res.getWriter();
                res.setContentType("text/html");  // Ext2JS's file upload requires this rather than "application/json"
                res.setStatus(200);
                
                //To be replaced
                AnimationJSONBuilder jsonBuilder = new AnimationJSONBuilder(replayedLogs, replayer, params);
                JSONObject json = jsonBuilder.parseLogCollection();
                json.put("success", true);  // Ext2JS's file upload requires this flag
                String string = json.toString();
                //LOGGER.info(string);
                jsonBuilder.clear();
                
                out.write(string);
            }
            else {
                /*
                out = res.getWriter();
                res.setContentType("text/html");
                res.setStatus(204);
                out.write("");
                */
                String json = "{success:false, errors: {errormsg: '" + "No logs can be played." + "'}}"; 
                res.setContentType("text/html; charset=UTF-8");
                res.getWriter().print(json);                
            }
            
        } catch (Exception e) {
            try {
                LOGGER.severe(e.toString());
                /*
                res.setStatus(500);
                res.setContentType("text/plain");
                PrintWriter writer = new PrintWriter(out);
                writer.println("Failed to generate animation JSON script " + e);
                e.printStackTrace(writer);
                e.printStackTrace();
                res.getWriter().write(e.toString());
                */
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

    // Returns the contents of the file in a byte array.
    private static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
}
