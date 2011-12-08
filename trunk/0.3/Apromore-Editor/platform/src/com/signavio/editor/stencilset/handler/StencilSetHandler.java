/**
 * Copyright (c) 2009, Signavio GmbH
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
 */
package com.signavio.editor.stencilset.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.util.FileUtil;
import com.signavio.usermanagement.user.business.FsUser;


@HandlerConfiguration(uri = "/editor_stencilset", rel = "stencilset")
public class StencilSetHandler extends BasisHandler {

	public final String SS_CONFIGURATION_FILE = this.getRootDirectory()
			+ "/WEB-INF/json/stencilsets";

	public final String EDITOR_URL_PREFIX;
	
	public StencilSetHandler(ServletContext servletContext) {
		super(servletContext);
		
		EDITOR_URL_PREFIX = (Platform.getInstance().getPlatformProperties().getEditorUri() + "/").replace(servletContext.getContextPath(), ""); 
	}

	/**
	 * Returns a JSON file that contains information about the available stencil
	 * sets
	 * 
	 * @throws Exception
	 */
	@Override
	public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req,
			HttpServletResponse res, FsAccessToken token, T sbo) {

		FsUser user = token.getUser();
		
		String namespace = getParameter(req, "namespace");
		String jsonp = getParameter(req, "jsonp");
		String embedSvg = getParameter(req, "embedsvg");
		String url = getParameter(req, "url");

		String fileName = SS_CONFIGURATION_FILE + ".json";
		
		// if the namespace is not set, return information
		// about all available stencilsets.

		try {
			if (namespace == null || namespace.equals("")) {
				this.writeFileToResponse(new File(fileName), res);
			} else {
				// get resource url for namespace
				StringBuffer ssConfBuffer = FileUtil.readFile(new File(fileName));
				JSONArray ssConf = new JSONArray(ssConfBuffer.toString());

				String resource = null;

				for (int i = 0; i < ssConf.length(); i++) {
					JSONObject ssInfo = ssConf.getJSONObject(i);
					String ssNamespace = ssInfo.getString("namespace");
					if (namespace.equals(ssNamespace)) {
						 if(ssInfo.has("uri")) {
	                             resource = ssInfo.getString("uri");
	                             break;
	                     } else if(ssInfo.has("stencilset")){
	                             String stencilset = ssInfo.getString("stencilset");
	                             for (int j = 0; j < ssConf.length(); j++) {
	                                     JSONObject ssInfo2 = ssConf.getJSONObject(j);
	                                     String ssNamespace2 = ssInfo2.getString("namespace");
	                                     if (stencilset.equals(ssNamespace2)) {
	                                             resource = ssInfo2.getString("uri");
	                                             break;
	                                     }
	                             }
	                             break;
	                     }
					}
				}

				if (resource == null) {
					// no stencil set for that namespace found
					throw new RequestException(
							"editor.stencilset.namespaceNotFound");
				}

				PrintWriter out = res.getWriter();

				String rootDirectory = this.getRootDirectory();
				if(rootDirectory.endsWith("ROOT")) {
					rootDirectory = rootDirectory.substring(0, rootDirectory.length()-5);
				}
				File jsonFile;
				if (embedSvg != null && embedSvg.equals("true")) { // SVG
																	// embedding
					jsonFile = new File(rootDirectory
							+ EDITOR_URL_PREFIX + "stencilsets/" + resource);
					
				} else { // no SVG embedding (default)
					// try to find stencilset nosvg representation
					int pIdx = resource.lastIndexOf('.');
					jsonFile = new File(rootDirectory
							+ EDITOR_URL_PREFIX + "stencilsets/"
							+ resource.substring(0, pIdx) + "-nosvg"
							+ resource.substring(pIdx));
					if (!jsonFile.exists())
						jsonFile = new File(rootDirectory
								+ EDITOR_URL_PREFIX + "stencilsets/" + resource);
				}

				if (!jsonFile.exists()) {
					System.out.println(jsonFile.getAbsolutePath());
					throw new RequestException(
							"editor.stencilset.ssFileNotfound");
				}

				// if the url parameter is set to true, only return the url of
				// the stencil set's JSON file
				if (url != null && url.equals("true")) {
					res.setContentType("text/plain");
					res.setStatus(200);

					res.getWriter().print(
							new String (this.getServletContext().getContextPath() + EDITOR_URL_PREFIX + "stencilsets/" + resource)
								.replace("\\", "/"));
				} else {
					// return the stencil set file
					res.setContentType("application/json");
					res.setStatus(200);

					// append a jsonp variable name
					if (jsonp != null)
						out.append(jsonp + "(");

					BufferedReader reader = new BufferedReader(new FileReader(
							jsonFile));
					String line = null;
					while ((line = reader.readLine()) != null) {
						out.append(line);
						out.append(System.getProperty("line.separator"));
					}
					if (jsonp != null)
						out.append(");");
				}
			}
		} catch (IOException e) {
			throw new RequestException("editor.stencilset.ioException", e);
		} catch (Exception e) {
			throw new RequestException("editor.stencilset.exception", e);
		}

	}
}
