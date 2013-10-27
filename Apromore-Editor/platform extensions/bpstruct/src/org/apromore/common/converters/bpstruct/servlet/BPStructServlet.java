package org.apromore.common.converters.bpstruct.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import org.apromore.common.converters.bpstruct.BPStructConverter;

/**
 * Servlet to access the BPStruct service.
 */
public class BPStructServlet extends HttpServlet {

    private static final long serialVersionUID = 4651531154294830523L;
    private static final Logger LOGGER = Logger.getLogger(BPStructServlet.class.getName());

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        doPost(req, res);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        // Read the test JSON
        String jsonData = req.getParameter("data");

        /* Transform and return as YAWL XML */
        try {
            if (jsonData == null || jsonData.isEmpty()) {
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                res.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                LOGGER.info("JSON FROM editor: " + jsonData);
                BPStructConverter converter = new BPStructConverter();
                String model = converter.convert(jsonData);
                res.setContentType("application/xml; charset=UTF-8");
                res.setStatus(200);
                res.getWriter().write(model);
            }
        } catch (Exception e) {
            try {
                LOGGER.severe(e.toString());
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                if (e.getCause() != null) {
                    res.getWriter().write(e.getCause().getMessage());
                } else {
                    res.getWriter().write(e.getMessage());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}