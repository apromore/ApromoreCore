package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BPMNOutputServlet converts the diagram (JSON) into a BPMN file.
 * It should be accessible at: /bpmnoutput
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BPMNOutputServlet extends HttpServlet {

    private static final long serialVersionUID = 4651531154294830523L;


    /* (non-Javadoc)
      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {

    }

}
