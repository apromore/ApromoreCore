package au.edu.qut.apromore.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.edu.qut.apromore.util.ApromoreConfig;
import au.edu.qut.apromore.util.ApromoreEPMLToERDFConvertor;
import org.apache.commons.httpclient.HttpStatus;
import org.apromore.portal.client.PortalService;
import org.apromore.portal.model.WriteNewProcessOutputMsgType;
import org.apromore.portal.model.WriteProcessOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * this servlet directly talks with the view and responds to all ajax calls from the apromore plugin.
 *
 * @author <a href="mailto:mehrad1@gmail.com">Mehrad Seyed Sadegh</a>
 */
public class ApromoreEpmlLinkServlet extends HttpServlet {

    private static final long serialVersionUID = -7357279951080137753L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApromoreXpdlLinkServlet.class);
    private static final String SESSION_CODE_PARAMETER_NAME = "sessionCode";
    private static final String PROCESS_VERSION_PARAMETER_NAME = "APProcessVersion";
    private static final String PROCESS_NAME_PARAMETER_NAME = "APProcessName";

    @Autowired
    private PortalService portalService;

    /** Apromore Config. */
    protected ApromoreConfig config = null;


    /**
     * Init Servlet.
     * @param newConfig the servlet config so we can get some config elements.
     * @throws ServletException if the servlet wasn't created correctly.
     */
    public void init(final ServletConfig newConfig) throws ServletException {
        super.init(newConfig);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, newConfig.getServletContext());
    }

    /**
     * Handle the HTTP Post request.
     * @param req the http request
     * @param res the http response
     * @throws ServletException if there is something wrong with the servlet
     * @throws IOException if an communication fails
     */
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        String data = req.getParameter("data");
        String action = req.getParameter("action");

        String sessionCode = req.getParameter(SESSION_CODE_PARAMETER_NAME);

        if ("Export".equals(action)) {
            exportAction(req, res, data, sessionCode);

        } else if ("Import".equals(action)) {
            importAction(req, res, sessionCode);
        } else {
            LOGGER.error("Bad Request from EPML plugin");
            res.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
    }


    /**
     * Handle the HTTP Get request.
     * @param req the http request
     * @param res the http response
     * @throws ServletException if there is something wrong with the servlet
     * @throws IOException if an communication fails
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }


    /**
     * Change string ids with integers. replaceAll wont work due performance issues.
     * @param epmlSource the epml process source
     * @return the epml with corrected id's
     */
    public String fixOryxIds(String epmlSource) {
        Map<String, Integer> ids = new HashMap<String, Integer>();
        int nextId = 100;

        Pattern pattern = Pattern.compile("=\"oryx_[^\"]*\"");
        StringBuffer epmlBuffer = new StringBuffer(epmlSource);
        StringBuilder resultBuffer = new StringBuilder();
        Matcher matcher = pattern.matcher(epmlBuffer);
        int startIndex = 0;

        while (matcher.find()) {
            String oldId = matcher.group().substring(2, matcher.group().length() - 1);
            Integer newId = ids.get(oldId);
            if (newId == null) {
                newId = nextId;
                ids.put(oldId, nextId++);
            }
            resultBuffer.append(epmlBuffer.substring(startIndex, matcher.start() + 2));
            resultBuffer.append(newId);
            startIndex = matcher.end() - 1;
        }
        resultBuffer.append(epmlBuffer.substring(startIndex, epmlBuffer.length()));
        return resultBuffer.toString();
    }



    private void importAction(HttpServletRequest req, HttpServletResponse res, String sessionCode) throws IOException {
        res.setContentType("text/xml");
        try {
            ApromoreEPMLToERDFConvertor converter = new ApromoreEPMLToERDFConvertor();
            String epmlSource = (String) req.getSession().getAttribute(sessionCode);
            String eRDFData = converter.convert(getServletContext(), epmlSource);

            res.setStatus(HttpStatus.SC_OK);
            res.getWriter().print(eRDFData);
            req.getSession().removeAttribute(sessionCode);
            res.getWriter().flush();
            res.getWriter().close();
        } catch (Exception e) {
            LOGGER.error("Error in importing EPML:" + e.getMessage());
            res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json");
            res.getWriter().write("{success: false, data: \"" + e.getMessage() + "\"}");
        }
    }

    private void exportAction(HttpServletRequest req, HttpServletResponse res, String data, String sessionCode) throws IOException {
        String isSave = req.getParameter("isSave");
        try {
            config = new ApromoreConfig();
            String result = "";

            if (ApromoreConfig.TRUE.equalsIgnoreCase(isSave)) {
                String processVersion = req.getParameter(PROCESS_VERSION_PARAMETER_NAME).trim();
                String processName = req.getParameter(PROCESS_NAME_PARAMETER_NAME).trim();

                String epmlSourceToBeSent = fixOryxIds(data);
                WriteProcessOutputMsgType writeProcessResult = portalService.writeProcess(epmlSourceToBeSent, Integer.valueOf(sessionCode),
                        processName, processVersion);
                if (writeProcessResult.getResult().getCode() == 0) {
                    result = "{success: true, data: " + sessionCode + "}";
                    req.getSession().setAttribute(sessionCode, epmlSourceToBeSent);
                    res.setStatus(HttpStatus.SC_OK);
                } else {
                    result = "{success: false, data: \"" + writeProcessResult.getResult().getMessage() + "\"}";
                    res.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
                }
            } else if ("false".equalsIgnoreCase(isSave)) {
                String processVersion = req.getParameter(PROCESS_VERSION_PARAMETER_NAME).trim();
                String processName = req.getParameter(PROCESS_NAME_PARAMETER_NAME).trim();

                String epmlSourceToBeSent = fixOryxIds(data);
                WriteNewProcessOutputMsgType writeNewProcessResult = portalService.writeNewProcess(epmlSourceToBeSent,
                        Integer.valueOf(sessionCode), processName, processVersion);

                if (writeNewProcessResult.getResult().getCode() == 0) {
                    result = "{\"success\": true, \"data\": " + writeNewProcessResult.getEditSessionCode() + "}";
                    res.setStatus(HttpStatus.SC_OK);
                } else {
                    result = "{\"success\": false, \"data\": " + writeNewProcessResult.getResult().getMessage() + "}";
                    res.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
                }
            } else {
                res.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

            res.getWriter().write(result);
            res.getWriter().flush();
            res.getWriter().close();
        } catch (Exception e) {
            LOGGER.error("Error in EPML export:" + e.getMessage());
            res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json");
            res.getWriter().write("{success: false, data: \"" + e.getMessage() + "\"}");
        }
    }
}
