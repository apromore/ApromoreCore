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
package org.apromore.common.converters.epml.servlet;

import java.io.StringReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import de.epml.CorrectedEPML;
import org.apromore.common.converters.epml.EPMLToJSONConverter;

/**
 * EPMLImportServlet converts a EPML specification (.epml file) to the JSON
 * representation of an Signavio diagram. It only supports POST requests with the
 * EPML submitted as parameter "data".
 * It should be accessible at: /epmlimport
 *
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 */
public class EPMLImportServlet extends HttpServlet {

    private static final long serialVersionUID = 4651535054294830523L;


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {

        /* Transform and return as JSON */
        try {
            res.setContentType("application/json");
            res.setStatus(200);

            CorrectedEPML correctedEPML = new CorrectedEPML(new StreamSource(new StringReader(req.getParameter("data"))));
            EPMLToJSONConverter epmlConverter = new EPMLToJSONConverter();
            epmlConverter.convert(correctedEPML.toString(), "UTF-8", res.getOutputStream());

        } catch (Exception e) {
            try {
                e.printStackTrace();
                res.setStatus(500);
                res.setContentType("text/plain");
                res.getWriter().write(e.getCause().getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

}
