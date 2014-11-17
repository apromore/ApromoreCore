package servlet;

import de.hpi.bpmn2_0.animation.AnimationJSONBuilder;
import de.hpi.bpmn2_0.backtracking2.Backtracking;
import de.hpi.bpmn2_0.backtracking2.Node;
import de.hpi.bpmn2_0.backtracking2.State;
import de.hpi.bpmn2_0.backtracking2.StateElementStatus;
import org.json.JSONException;
import org.json.JSONObject;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.replay.AnimationLog;
import de.hpi.bpmn2_0.replay.BPMNDiagramHelper;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.ReplayTrace;
import de.hpi.bpmn2_0.replay.Replayer;
import de.hpi.bpmn2_0.replay.XTrace2;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
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
    private static final int THRESHOLD_SIZE = 1024 * 1024 * 50; // 50MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 200; // 200MB
    private static final int REQUEST_SIZE = 1024 * 1024 * 500; // 500MB

    /* (non-Javadoc)
      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out=null;
        HashMap<String, XLog> logMap;
        HashMap<String, String> colorMap;
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
        * logMap contains list of imported logs
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

        logMap = new HashMap<>();
        colorMap = new HashMap<>();
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
                            logMap.put(fileName, (XLog)logImporter.importFile(storeFile));

                            // color field must follow the log file
                            item = (FileItem) iter.next();
                            assert item.isFormField();
                            assert "color".equals(item.getFieldName());
                            LOGGER.info("Log color: " + item.getString());
                            colorMap.put(fileName, item.getString());
                    } else {
                        if (item.getFieldName().equals("json")) {
                            jsonData = item.getString();
                        }
                    }
            }            
            
            /*
            String log1File = "/editor/animation/repairExample.xes";
            //String logFilePath = getServletContext().getRealPath(log1File);
            URL url = getServletContext().getResource(log1File);
            OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
            XLog log;
            log = (XLog)logImporter.importFile(url.toURI());
            */
            
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
            params.setMinMatch(Double.valueOf(props.getProperty("MinMatchPercent")).doubleValue());
            params.setMaxMatch(Double.valueOf(props.getProperty("MaxMatchPercent")).doubleValue());
            params.setMaxDiffSeries(Integer.valueOf(props.getProperty("MaxDiffSeries")).intValue());
            params.setActivityMatchCost(Double.valueOf(props.getProperty("ActivityMatchCost")).doubleValue());
            params.setActivitySkipCost(Double.valueOf(props.getProperty("ActivitySkipCost")).doubleValue());
            params.setEventSkipCost(Double.valueOf(props.getProperty("EventSkipCost")).doubleValue());
            params.setNonActivityMoveCost(Double.valueOf(props.getProperty("NonActivityMoveCost")).doubleValue());
            params.setTraceChunkSize(Integer.valueOf(props.getProperty("TraceChunkSize")).intValue());
            params.setMaxNumberOfNodesVisited(Integer.valueOf(props.getProperty("MaxNumberOfNodesVisited")).intValue());
            params.setMaxActivitySkip(Double.valueOf(props.getProperty("MaxActivitySkipPercent")).doubleValue());
            params.setMaxNodeDistance(Integer.valueOf(props.getProperty("MaxNodeDistance")).intValue());
            params.setTimelineSlots(Integer.valueOf(props.getProperty("TimelineSlots")).intValue());
            params.setTotalEngineSeconds(Integer.valueOf(props.getProperty("TotalEngineSeconds")).intValue());
            params.setProgressCircleBarRadius(Integer.valueOf(props.getProperty("ProgressCircleBarRadius")).intValue());
            params.setSequenceTokenDiffThreshold(Integer.valueOf(props.getProperty("SequenceTokenDiffThreshold")).intValue());
            params.setBacktrackingDebug(props.getProperty("BacktrackingDebug"));
            
            Replayer replayer = new Replayer(bpmnDefinition, params);
            ArrayList<AnimationLog> replayedLogs = new ArrayList();
            AnimationLog animationLog; 
            if (replayer.isValidProcess()) {
                LOGGER.info("Process " + bpmnDefinition.getId() + " is valid");
                EncodeTraces.getEncodeTraces().read(logMap.values());
                for (String filename : logMap.keySet()) {
                    assert colorMap.containsKey(filename);
                    animationLog = replayer.replay(logMap.get(filename), colorMap.get(filename));
                    if (!animationLog.isEmpty()) {
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
            /*
            boolean hasAnimation = false;
            for (AnimationLog log : replayedLogs) {
                if (!log.isEmpty()) {
                    hasAnimation = true;
                    break;
                }
            }
            */
            if (replayedLogs.size() > 0) {
                out = res.getWriter();
                res.setContentType("text/html");  // Ext2JS's file upload requires this rather than "application/json"
                res.setStatus(200);
                
                //To be replaced
                AnimationJSONBuilder jsonBuilder = new AnimationJSONBuilder(replayedLogs, params);
                JSONObject json = jsonBuilder.parseLogCollection();
                json.put("success", true);  // Ext2JS's file upload requires this flag
                String string = json.toString();
                LOGGER.info(string);
                out.write(string);
            }
            else {
                out = res.getWriter();
                res.setContentType("text/html");
                res.setStatus(204);
                out.write("");
            }
            
            /*
            String sampleJsonFile = "/editor/animation/AnimationExample.json";
            String sampleFilePath = getServletContext().getRealPath(sampleJsonFile);
            File file = new File(sampleFilePath);
            byte[] bytes = getBytesFromFile(file);
            out.write(bytes);
            */        

        } catch (Exception e) {
            try {
                LOGGER.severe(e.toString());
                res.setStatus(500);
                res.setContentType("text/plain");
                PrintWriter writer = new PrintWriter(out);
                writer.println("Failed to generate animation JSON script " + e);
                e.printStackTrace(writer);
                e.printStackTrace();
                res.getWriter().write(e.toString());
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
