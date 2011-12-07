package au.edu.qut.apromore.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import au.edu.qut.apromore.util.ApromoreBPMN2XPDLConverter;
import au.edu.qut.apromore.util.ApromoreConfig;
import de.hpi.bpmn2xpdl.XPDLPackage;
import org.apromore.portal.client.PortalService;
import org.apromore.portal.model.WriteAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewProcessOutputMsgType;
import org.apromore.portal.model.WriteProcessOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * this servlet directly talks with the view and responds to all ajax calls from the apromore plugin.
 *
 * @author Mehrad Seyed Sadegh
 */
public class ApromoreXpdlLinkServlet extends HttpServlet {

    private static final long serialVersionUID = -7357279951080137753L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApromoreXpdlLinkServlet.class);
    private static String SESSION_CODE_PARAMETER_NAME = "sessionCode";
    private static String PROCESS_VERSION_PARAMETER_NAME = "APProcessVersion";
    private static String PROCESS_NAME_PARAMETER_NAME = "APProcessName";
    private static String ANNOTATION_NAME_PARAMETER_NAME = "APAnnotationName";
    private static String NOTATIONS_ONLY_PARAMETER_NAME = "notationsOnly";

    private static String NAMESPACE_STRING = "xmlns=\"http://www.wfmc.org/2008/XPDL2.1\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
            + "xsi:schemaLocation=\"http://www.wfmc.org/2008/XPDL2.1 bpmnxpdl_31.xsd\"";

    ApromoreConfig config = null;

    @Autowired
    private PortalService portalService;

    /**
     * Init Servlet.
     *
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String data = req.getParameter("data");
        String action = req.getParameter("action");

        String sessionCode = req.getParameter(SESSION_CODE_PARAMETER_NAME);
        if ("Export".equals(action)) { // if its a save or a saveAs call
            String isSave = req.getParameter("isSave");
            String isNotationsOnly = req.getParameter(NOTATIONS_ONLY_PARAMETER_NAME);
            res.setContentType("application/json");
            try {
                config = new ApromoreConfig();
                ApromoreBPMN2XPDLConverter converter = new ApromoreBPMN2XPDLConverter();

                String xpdlData = "";

                String result = "";
                if (ApromoreConfig.TRUE.equalsIgnoreCase(isSave)) {
                    if (ApromoreConfig.TRUE.equalsIgnoreCase(isNotationsOnly)) {
                        xpdlData = converter.getNativeXPDLToSave(data, null);
                        xpdlData = fixHeaders(xpdlData);

                        WriteAnnotationOutputMsgType writeAnnotationResult = portalService.writeAnnotation(xpdlData, Integer.valueOf(sessionCode));

                        if (writeAnnotationResult.getResult().getCode() == 0) {
                            result = "{success: true, data: '" + sessionCode + "'}";
                            req.getSession().setAttribute(sessionCode, xpdlData);
                            res.setStatus(HttpStatus.SC_OK);
                        } else {
                            result = "{success: false, data: \"" + writeAnnotationResult.getResult().getMessage() + "\"}";
                            res.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                    } else {
                        String processVersion = req.getParameter(PROCESS_VERSION_PARAMETER_NAME).trim();
                        String processName = req.getParameter(PROCESS_NAME_PARAMETER_NAME).trim();

                        xpdlData = converter.getNativeXPDLToSave(data, processVersion);
                        xpdlData = fixHeaders(xpdlData);

                        logTheProcessInFile(xpdlData, processName, processVersion);

                        WriteProcessOutputMsgType writeProcessResult = portalService.writeProcess(xpdlData, Integer.valueOf(sessionCode), processName, processVersion);

                        if (writeProcessResult.getResult().getCode() == 0) {
                            result = "{success: true, data: '" + sessionCode + "'}";
                            req.getSession().setAttribute(sessionCode, xpdlData);
                            res.setStatus(HttpStatus.SC_OK);
                        } else {
                            result = "{success: false, data: \"" + writeProcessResult.getResult().getMessage() + "\"}";
                            res.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                    }

                } else if ("false".equalsIgnoreCase(isSave)) {
                    if (ApromoreConfig.TRUE.equalsIgnoreCase(isNotationsOnly)) {
                        String annotationName = req.getParameter(ANNOTATION_NAME_PARAMETER_NAME).trim();
                        xpdlData = converter.getNativeXPDLToSave(data, null);
                        xpdlData = fixHeaders(xpdlData);

                        WriteNewAnnotationOutputMsgType writeNewAnnotationResult = portalService.writeNewAnnotation(xpdlData, Integer.valueOf(sessionCode), annotationName);

                        if (writeNewAnnotationResult.getResult().getCode() == 0) {
                            result = "{success: true, data: '" + writeNewAnnotationResult.getEditSessionCode() + "'}";
                            req.getSession().setAttribute(sessionCode, xpdlData);
                            res.setStatus(HttpStatus.SC_OK);
                        } else {
                            result = "{success: false, data: \"" + writeNewAnnotationResult.getResult().getMessage() + "\"}";
                            res.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }

                    } else {
                        String processVersion = req.getParameter(PROCESS_VERSION_PARAMETER_NAME).trim();
                        String processName = req.getParameter(PROCESS_NAME_PARAMETER_NAME).trim();

                        xpdlData = converter.getNativeXPDLToSaveAs(data, processVersion, processName);
                        xpdlData = fixHeaders(xpdlData);

                        logTheProcessInFile(xpdlData, processName, processVersion);

                        WriteNewProcessOutputMsgType writeNewProcessResult = portalService.writeNewProcess(xpdlData, Integer.valueOf(sessionCode), processName, processVersion);

                        if (writeNewProcessResult.getResult().getCode() == 0) {
                            result = "{success: true, data: '" + writeNewProcessResult.getEditSessionCode() + "'}";
                            res.setStatus(HttpStatus.SC_OK);
                        } else {
                            result = "{success: false, data: \"" + writeNewProcessResult.getResult().getMessage() + "\"}";
                            res.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                    }
                } else {
                    res.setStatus(HttpStatus.SC_BAD_REQUEST);
                }

                res.getWriter().write(result);
                res.getWriter().flush();
                res.getWriter().close();

            } catch (Exception e) {
                LOGGER.error("Error in exporting XPDL" + e.getMessage());
                res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                res.setContentType("application/json");
                res.getWriter().write("{success: false, data: \"" + e.getMessage() + "\"}");
            }
        } else if ("Import".equals(action)) {
            res.setContentType("text/xml");
            try {
                ApromoreBPMN2XPDLConverter converter = new ApromoreBPMN2XPDLConverter();
                XPDLPackage xpdlModel = converter.getXPDLModel((String) req.getSession().getAttribute(sessionCode));
                res.setStatus(HttpStatus.SC_OK);
                res.getWriter().print(converter.getXPDL(xpdlModel));
                req.getSession().removeAttribute(sessionCode);//session will be always empty
                res.getWriter().flush();
                res.getWriter().close();
            } catch (Exception e) {
                LOGGER.error("Error in importing XPDL" + e.getMessage());
                res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                res.setContentType("application/json");
                res.getWriter().write("{success: false, data: \"" + e.getMessage() + "\"}");
            }

        } else {
            LOGGER.error("Bad Request from XPDL plugin");
            res.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * dummy logger for keeping the sent process to the portal. should be called after development stage.
     *
     * @param process
     * @param processName
     * @param processVersion
     */
    private void logTheProcessInFile(String process, String processName, String processVersion) {
        try {
            if ("no".equalsIgnoreCase(config.getProperty(ApromoreConfig.LOG_GENERATED_PROCESSESS_TO_FILE)))
                return;
        } catch (IOException e) {
            System.out.println("Log faild:" + e.getMessage());
        }

        FileWriter fr = null;
        try {
            fr = new FileWriter(new File(config.getProperty(ApromoreConfig.LOG_FOLDER) + processName + "_" + processVersion + "_" + System.currentTimeMillis() + ".xpdl"));
            fr.write(process);
        } catch (IOException e) {
            System.out.println("Log faild:" + e.getMessage());
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                System.out.println("Log faild:" + e.getMessage());
            }
        }

    }

    /**
     * adds required namespaces and <ProcessHeader/> to the process to prevent the canonizer from crashing
     *
     * @param xpdlData
     * @return
     */
    private String fixHeaders(String xpdlData) {
        xpdlData = xpdlData.replace("<Package Language=", "<Package " + NAMESPACE_STRING + " Language=");
        xpdlData = xpdlData.replace("<Activities>", "<ProcessHeader/><Activities>");//TODO: fix in model mapping
        return xpdlData;
    }

}
