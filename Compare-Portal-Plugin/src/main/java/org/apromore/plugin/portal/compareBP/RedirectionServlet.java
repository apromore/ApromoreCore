package org.apromore.plugin.portal.compareBP;

// Java 2 Standard Edition
import java.io.IOException;

// Java 2 Enterprise Edition
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Kludge to redirect ZK asynchronous updates back to the portal.
 *
 * @author <a href=mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class RedirectionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", "/portal/zkau" + request.getPathInfo());
    }
}
