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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.YAWLResult;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

/**
 * YAWLExportServlet converts a Oryx JSON diagram to a YAWL specification (.yawl
 * file) It only supports POST requests with the JSON submitted as parameter
 * "data".
 * 
 * It should be accessible at: /yawlexport
 * 
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 * 
 */
public class YAWLExportServlet extends HttpServlet {

	private static final long serialVersionUID = 6881808890572459223L;

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String jsonData = req.getParameter("data");

		/* Transform and return as YAWL XML */
		try {
			String oryxBackendUrl = req.getScheme() + "://"
					+ req.getServerName() + ":" + req.getServerPort()
					+ "/backend/poem/model/";
			String rootDir = "/oryx/";
			;
			String yawlString = getYAWLfromJSON(jsonData, rootDir,
					oryxBackendUrl);
			res.setContentType("application/xml");
			res.setStatus(200);
			res.getWriter().print(yawlString);
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

	private String getYAWLfromJSON(String jsonData, String rootDir,
			String oryxBackendUrl) throws JSONException {

		// Convert Oryx Diagram
		YAWLConverter yawlConverter = new YAWLConverter(rootDir, oryxBackendUrl);

		YAWLResult yawlResult = yawlConverter.convertOryxToYAWL(jsonData);

		String warningMessage = "";
		for (ConversionException e : yawlResult.getWarnings()) {
			warningMessage += "- ";
			warningMessage += e.getMessage();
			if (e.getCause() != null) {
				warningMessage += " (" + e.getCause().getMessage();
			}
			warningMessage += ")\n";
		}

		// Write Conversion Result:
		JSONObject jsonExportObject = new JSONObject();
		jsonExportObject.put("yawlXML", yawlResult.getYAWLAsXML());
		jsonExportObject.put("warnings", warningMessage);
		jsonExportObject.put("hasFailed", yawlResult.hasFailed());
		jsonExportObject.put("hasWarnings", yawlResult.hasWarnings());
		jsonExportObject.put("filename", yawlResult.getFilename());

		return jsonExportObject.toString();
	}

}