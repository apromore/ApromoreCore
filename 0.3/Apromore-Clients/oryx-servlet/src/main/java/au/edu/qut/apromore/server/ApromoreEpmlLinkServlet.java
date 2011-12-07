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

import org.apache.commons.httpclient.HttpStatus;

import au.edu.qut.apromore.util.ApromoreConfig;
import au.edu.qut.apromore.util.ApromoreEPMLToERDFConvertor;
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
 * @author Mehrad Seyed Sadegh
 */
public class ApromoreEpmlLinkServlet extends HttpServlet {

    private static final long serialVersionUID = -7357279951080137753L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApromoreXpdlLinkServlet.class);
    private static String SESSION_CODE_PARAMETER_NAME = "sessionCode";
    private static String PROCESS_VERSION_PARAMETER_NAME = "APProcessVersion";
    private static String PROCESS_NAME_PARAMETER_NAME = "APProcessName";
    private static String ANNOTATION_NAME_PARAMETER_NAME = "APAnnotationName";
    private static String NOTATIONS_ONLY_PARAMETER_NAME = "notationsOnly";

    protected ApromoreConfig config = null;

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

        if ("Export".equals(action)) {
            String isSave = req.getParameter("isSave");
            try {
                config = new ApromoreConfig();

                String epmlData = "";
                String result = "";

                if (ApromoreConfig.TRUE.equalsIgnoreCase(isSave)) {
                    String processVersion = req.getParameter(PROCESS_VERSION_PARAMETER_NAME).trim();
                    String processName = req.getParameter(PROCESS_NAME_PARAMETER_NAME).trim();

                    String epmlSourceToBeSent = fixOryxIds(data);
                    WriteProcessOutputMsgType writeProcessResult = portalService.writeProcess(epmlSourceToBeSent, Integer.valueOf(sessionCode), processName, processVersion);
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
                    WriteNewProcessOutputMsgType writeNewProcessResult = portalService.writeNewProcess(epmlSourceToBeSent, Integer.valueOf(sessionCode), processName, processVersion);

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

        } else if ("Import".equals(action)) {
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
        } else {
            LOGGER.error("Bad Request from EPML plugin");
            res.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    /**
     * change string ids with integers. replaceAll wont work due performance issues
     *
     * @param epmlSource
     * @return
     */
    public String fixOryxIds(String epmlSource) {
        Map<String, Integer> ids = new HashMap<String, Integer>();
        int nextId = 100;

        Pattern pattern = Pattern.compile("=\"oryx_[^\"]*\"");
        StringBuffer epmlBuffer = new StringBuffer(epmlSource);
        StringBuffer resultBuffer = new StringBuffer();
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

}
