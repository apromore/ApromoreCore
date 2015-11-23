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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.epml.EPMLSchema;
import org.apromore.common.converters.epml.JSONToEPMLConverter;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.xml.sax.SAXException;

/**
 * Currently, we use client side processing to do the transformation from json to epml.
 *
 * @author <a href="mailto"cam.james@gmail.com">Cameron James</a>
 */
public class EPMLExportServlet extends HttpServlet {

    private static final long serialVersionUID = 4651535054221330523L;

    /* (non-Javadoc)
      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        /* Transform and return as JSON */
        try {
            String jsonString = req.getParameter("data");

            String epmlString = jsonToEpml(jsonString);
            
            res.setContentType("application/xml; charset=UTF-8");
            res.setStatus(200);
            res.getWriter().write(epmlString);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            if (e.getCause() != null) {
                res.setContentType("text/plain");
                assert e.getCause() != null;
                assert e.getCause().getMessage() != null;
                try {
                    res.getWriter().write(e.getCause().getMessage());
                } catch (IOException e1) {
                    throw new ServletException(e1);
                }
            }
        }

    }

    /**
     * @param jsonString  an EPC represented in Signavio's serialized JSON, never <code>null</code>
     * @return an EPML serialization of the EPC
     */
    private String jsonToEpml(String jsonString) throws JAXBException, JSONException, SAXException {
        ServletContext context = getServletConfig().getServletContext();

        /* The following commented-out code duplicates the original Javascript implementation, in which
           JSON is converted in turn to eRDF, RDF, and finally EPML.

        // JSON to ERDF
        JsonErdfTransformation jsonErdf = new JsonErdfTransformation(jsonString);
        String erdfString = jsonErdf.toString();
        String resource = "Oryx-EPC";
        erdfString =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:b3mn=\"http://b3mn.org/2007/b3mn\" " +
                "               xmlns:ext=\"http://b3mn.org/2007/ext\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
                "               xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">" +
                "  <head profile=\"http://purl.org/NET/erdf/profile\">" +
                "    <link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />" +
                "    <link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/\" />" +
                "    <link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />" +
                "    <link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />" +
                "    <link rel=\"schema.raziel\" href=\"http://raziel.org/\" />" +
                "    <base href=\"" + "http://example.com/" *//* location.href.split("?")[0] *//* + "\" />" +
                "  </head>" +
                "  <body>" +
                erdfString +
                "    <div id=\"generatedProcessInfos\">" +
                "      <span class=\"oryx-id\">" + resource + "</span>" +
                "      <span class=\"oryx-name\">" + resource + "</span>" +
                "    </div>" +
                "  </body>" +
                "</html>";
        context.log("TALISMAN-ERDF:" + erdfString);

        // ERDF TO RDF
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(
            new StreamSource(context.getResourceAsStream("WEB-INF/xslt/extract-rdf.xsl"))
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(new StringReader(erdfString)), new StreamResult(baos));
        String rdfString = baos.toString();
        context.log("TALISMAN-RDF:" + rdfString);

        // RDF TO EPML
        Transformer transformer2 = transformerFactory.newTransformer(
            new StreamSource(context.getResourceAsStream("WEB-INF/xslt/RDF2EPML.xslt"))
        );
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        transformer2.transform(new StreamSource(new StringReader(rdfString)), new StreamResult(baos2));
        String epmlString = baos2.toString();
        */

        // Workaround because the JSON we get doesn't have its stencil set namespace set
        JSONObject object = new JSONObject(jsonString);
        JSONObject stencilset = object.getJSONObject("stencilset");
        stencilset.put("namespace", "http://b3mn.org/stencilset/epc#");
        object.put("stencilset", stencilset);
        jsonString = object.toString();

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        JSONToEPMLConverter jsonToEpmlConverter = new JSONToEPMLConverter();
        context.log("TALISMAN-JSON: " + jsonString);
        EPMLSchema.marshalEPMLFormat(baos2, jsonToEpmlConverter.toEPML(BasicDiagramBuilder.parseJson(jsonString)), true /* is validating */);

        return baos2.toString();
    }
}
