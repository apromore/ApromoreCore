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
package de.hbrs.oryx.yawl.servlets;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.OryxResult;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

/**
 * YAWLImportServlet converts a YAWL specification (.yawl file) to the JSON
 * representation of an Oryx diagram. It only supports POST requests with the
 * YAWL specification submitted as parameter "data".
 * 
 * It should be accessible at: /yawlimport
 * 
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 * 
 */
public class YAWLImportServlet extends HttpServlet {

	private static final long serialVersionUID = 3547545139919885168L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {

		String yawlData = req.getParameter("data");

		/* Transform and return as JSON */
		try {
			String oryxBackendUrl = req.getScheme() + req.getServerName() + ":"
					+ req.getServerPort() + "/backend/poem/model/";
			String rootDir = "/oryx/";
			String jsonString = getJsonFromYAWL(yawlData, rootDir,
					oryxBackendUrl);
			res.setContentType("application/json");
			res.setStatus(200);
			res.getWriter().print(jsonString);
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

	/**
	 * Call the conversion from YAWL to the Oryx JSON format.
	 * 
	 * @param yawlXml
	 *            a valid YAWL specification
	 * @param oryxBackendUrl
	 * @param rootDir
	 * @return a valid Oryx diagram in its JSON representation
	 * @throws JSONException
	 *             in case of an invalid JSON conversion
	 * @throws YSyntaxException
	 *             in case of an invalid YAWL specification
	 * @throws IOException
	 *             in case of ?? (error in Loading Layout)
	 * @throws JDOMException
	 *             in case of ?? (error in Loading Layout)
	 */
	private String getJsonFromYAWL(String yawlXml, String rootDir,
			String oryxBackendUrl) throws JSONException, YSyntaxException,
			JDOMException, IOException {

		// Convert YAWL Model
		YAWLConverter yawlConverter = new YAWLConverter(rootDir, oryxBackendUrl);
		OryxResult oryxResult = yawlConverter.convertYAWLToOryx(yawlXml);
		
		String warningMessage = "";
		for (ConversionException e : oryxResult.getWarnings()) {
			warningMessage += "- ";
			warningMessage += e.getMessage();
			if (e.getCause() != null) {
				warningMessage += " (" + e.getCause().getMessage();
			}
			warningMessage += ")\n";
		}	
	
		// Write Converted Model
		JSONArray jsonDiagramList = new JSONArray();

		for (Entry<String, BasicDiagram> diagramEntry : oryxResult.getDiagrams()) {
			JSONObject model = GenericJSONBuilder.parseModel(diagramEntry.getValue());
			jsonDiagramList.put(model);
		}

		JSONObject jsonImportObject = new JSONObject();
		jsonImportObject.put("diagrams", jsonDiagramList);
		jsonImportObject.put("rootNetName", oryxResult.getRootNetId());
		jsonImportObject.put("hasFailed", oryxResult.hasFailed());
		jsonImportObject.put("hasWarnings", oryxResult.hasWarnings());	
		jsonImportObject.put("warnings", warningMessage);

		return jsonImportObject.toString();
	}

}