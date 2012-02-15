package au.edu.qut.apromore.server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.edu.qut.apromore.util.ApromoreBPMN2XPDLConverter;
import au.edu.qut.apromore.util.ApromoreConfig;
import de.hpi.bpmn2xpdl.XPDLPackage;
import de.hpi.bpmn2xpdl.XPDLPool;
import de.hpi.bpmn2xpdl.XPDLPools;
import org.apromore.portal.client.PortalService;
import org.apromore.portal.client.util.EditSessionHolder;
import org.apromore.portal.model.EditSessionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * this class is responds to newly opened oryx window while edit is selected, portal executes it through a GET call from a url.
 *
 * @author Mehrad Seyed Sadegh
 */
public class OpenFromApromoreServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFromApromoreServlet.class);
    private static final String NEW_PROCESS_TEMP_VERSION = "0.0";

    private static final long serialVersionUID = 8008133568793009069L;

    private static String SESSION_CODE_PARAMETER_NAME = "sessionCode";
    private static String PROCESS_VERSION_PARAMETER_NAME = "processVersion";
    private static String PROCESS_NAME_PARAMETER_NAME = "processName";
    private static String AUTOLAYOUT_PARAMETER_NAME = "autolayout";
    private static String NOTATIONS_ONLY_PARAMETER_NAME = "notationsOnly";

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


    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionCode = req.getParameter(SESSION_CODE_PARAMETER_NAME);
        String processVersion = "";
        String processName = "";
        String processSource = "";
        Map<String, String> additionalParameters = new HashMap<String, String>();

        LOGGER.debug("Entered ApromoreServlet with session code " + sessionCode);

        try {
            ApromoreConfig config = new ApromoreConfig();

            if (req.getSession().getAttribute(sessionCode) != null) {
                processSource = (String) req.getSession().getAttribute(sessionCode);
                processName = portalService.getProcessName(sessionCode);
                processVersion = portalService.getProcessVersion(sessionCode);
            } else {
                processSource = portalService.readNativeProcess(Integer.valueOf(sessionCode));

                if (getProcessType(processSource) == ProcessNativeLanguage.EPML) {
                    req.getSession().setAttribute(sessionCode, processSource);
                    processVersion = portalService.getProcessVersion(sessionCode);
                    processName = portalService.getProcessName(sessionCode);

                } else if (getProcessType(processSource) == ProcessNativeLanguage.XPDL) {
                    ApromoreBPMN2XPDLConverter converter = new ApromoreBPMN2XPDLConverter();
                    XPDLPackage xpdlModel = converter.getXPDLModel(processSource);
                    //if (processSource.indexOf("<Vendor>BizAgi") >= 0) {
                        //xpdlModel.setNeedsAutolayout(true);
                        //additionalParameters.put(AUTOLAYOUT_PARAMETER_NAME, xpdlModel.getNeedsAutolayout().toString());
                    //}

                    req.getSession().setAttribute(sessionCode, processSource);
                    processVersion = portalService.getProcessVersion(sessionCode);
                    processName = portalService.getProcessName(sessionCode);

                    if (xpdlModel.getRedefinableHeader() != null) {
                        if (!NEW_PROCESS_TEMP_VERSION.equals(xpdlModel.getRedefinableHeader().getVersion().getContent().trim()) && // check if version is not 0.0 and there are no pools
                                (xpdlModel.getPools() == null || xpdlModel.getPools().getPools() == null || xpdlModel.getPools().getPools().size() == 0)) {

                            XPDLPools xpdlPools = new XPDLPools();
                            xpdlModel.setPools(xpdlPools);
                            if (xpdlModel.getPools().getPools() == null || xpdlModel.getPools().getPools().size() == 0) {
                                XPDLPool pool = new XPDLPool();
                                pool.setBoundaryVisible(false);
                                pool.setMainPool(true);
                                xpdlPools.add(pool);
                            }
                            //xpdlModel.setNeedsAutolayout(true);
                        }
                    }
                }
            }

            String targetURL = "";
            if (getProcessType(processSource) == ProcessNativeLanguage.XPDL) {
                if (!ApromoreConfig.TRUE.equalsIgnoreCase(req.getParameter(NOTATIONS_ONLY_PARAMETER_NAME))) //TODO: externalize strings+URLs
                    targetURL = "/editor;bpmn?stencilset=/stencilsets/bpmn1.1/bpmn1.1.json&";
                else
                    targetURL = "/editor;APROMORE_bpmn_readonly?stencilset=/stencilsets/bpmn1.1_readonly/bpmn1.1.json&";
            } else if (getProcessType(processSource) == ProcessNativeLanguage.EPML) {
                targetURL = "/editor;epc?stencilset=/stencilsets/epc/epc.json&";
            }

            targetURL = req.getContextPath() + targetURL
                    + SESSION_CODE_PARAMETER_NAME + "=" + URLEncoder.encode(sessionCode, "UTF-8") + "&"
                    + PROCESS_NAME_PARAMETER_NAME + "=" + URLEncoder.encode(processName, "UTF-8") + "&"
                    + PROCESS_VERSION_PARAMETER_NAME + "=" + URLEncoder.encode(processVersion, "UTF-8") + "&"
                    + NOTATIONS_ONLY_PARAMETER_NAME + "=" + req.getParameter(NOTATIONS_ONLY_PARAMETER_NAME);

            String additionalParamsStr = "";
            for (String key : additionalParameters.keySet()) {
                additionalParamsStr += "&" + key + "=" + additionalParameters.get(key);
            }
            targetURL += additionalParamsStr;
            resp.sendRedirect(targetURL);

        } catch (Exception e) {
            LOGGER.error("Error in OpenFromApromoreServlet:" + e.getMessage());
            resp.getWriter().write("<body><b>ERROR in communicating with APROMORE:</b>" + e.getMessage() + "<br/><br/>"
                    + new ArrayList(Arrays.asList(e.getStackTrace())).toString() + "</body>"); //show a human readable exception
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }



    private ProcessNativeLanguage getProcessType(String processSource) { //TODO: Fix this, needs the process type to be sent by the service
        if (processSource.indexOf("epml") > 0 || processSource.indexOf("EPML") > 0 || processSource.indexOf("http://www.epml.de") > 0) {
            return ProcessNativeLanguage.EPML;
        } else {
            return ProcessNativeLanguage.XPDL;
        }
    }

}
