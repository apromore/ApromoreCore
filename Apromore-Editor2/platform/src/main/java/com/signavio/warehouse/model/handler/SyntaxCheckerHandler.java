/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.signavio.warehouse.model.handler;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.exceptions.IORequestException;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.model.syntaxchecker.SyntaxCheckerPerformer;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;

@HandlerConfiguration(uri="/syntaxchecker", rel="syntaxchecker")
public class SyntaxCheckerHandler extends BasisHandler {

	public SyntaxCheckerHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Implementation of a POST request
     * @param req
     * @param res
	 * @param identifier
     * @throws Exception
     */
    public <T extends FsSecureBusinessObject> void doPost(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
    	try {
    		// Get the parameter list
        	JSONObject params = (JSONObject)req.getAttribute("params");
        	
    		String isJson = params.getString("isJson");
			
			JSONObject result = null;
			if(isJson.equals("true")) {
				String json = params.getString("data_json");
				
				List<Class<? extends AbstractBpmnFactory>> factoryClasses = AbstractBpmnFactory.getFactoryClasses();
				
				SyntaxCheckerPerformer checker = new SyntaxCheckerPerformer();
				
				result = checker.processDocument(BasicDiagramBuilder.parseJson(json), factoryClasses);
			} //else {
//				String rdf = req.getParameter("data");
//				
//				context = req.getParameter("context");
//				
//				DocumentBuilder builder;
//				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//				builder = factory.newDocumentBuilder();
//				Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
//				
//				processDocument(document, res.getWriter());			
//			}
			
			if(result == null) {
				result = new JSONObject();
			}
			
			res.setContentType("application/json");
			res.setStatus(200);
			res.getWriter().write(result.toString());
		} catch(BpmnConverterException e) {
			throw new RequestException("syntaxchecker.failed", e);
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		} catch (IOException e) {
			throw new IORequestException(e);
		}
    }
}
