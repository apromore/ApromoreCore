/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */
package servlet;



import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EPMLImportServlet converts a EPML specification (.epml file) to the JSON
 * representation of an Signavio diagram. It only supports POST requests with the
 * EPML submitted as parameter "data".
 * It should be accessible at: /epmlimport
 *
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 */
public class BPMNImportServlet extends HttpServlet {

    private static final long serialVersionUID = 4651535054294830523L;
    private static final Logger LOGGER = Logger.getLogger(BPMNImportServlet.class.getCanonicalName());

    /* (non-Javadoc)
      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String bpmnData = req.getParameter("data");
        OutputStream out = null;;

        /* Transform and return as JSON */
        try {
            out = res.getOutputStream();
            res.setContentType("application/json");
            res.setStatus(200);
            BPMN2DiagramConverter bpmnConverter = new BPMN2DiagramConverter("/signaviocore/editor/");
            bpmnConverter.getBPMN(bpmnData, "UTF-8", out);
        } catch (Exception e) {
            try {
                LOGGER.severe(e.toString());
                res.setStatus(500);
                res.setContentType("text/plain");
                (new OutputStreamWriter(out)).write(e.getCause().getMessage());
            } catch (Exception e1) {
                System.err.println("Original exception was:");
                e.printStackTrace();
                System.err.println("Exception in exception handler was:");
                e1.printStackTrace();
            }
        }

    }

}
