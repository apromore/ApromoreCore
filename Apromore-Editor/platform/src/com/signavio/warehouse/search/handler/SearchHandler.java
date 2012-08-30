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
package com.signavio.warehouse.search.handler;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONArray;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.IORequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;

@HandlerConfiguration(uri="/search")
public class SearchHandler extends BasisHandler {

	public SearchHandler(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	public <T extends FsSecureBusinessObject> 
		void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res, 
				FsAccessToken token, T sbo) {
		
		String searchTerm = getParameter(req, "q");
		if (searchTerm == null) {
			throw new RequestException("search_term_is_missing");
		}
		JSONArray jsonResult = new JSONArray();
		List<FsSecureBusinessObject> businessObjects= FsEntityManager.getTenantManagerInstance(  
				FsEntityManager.class, token.getTenantId(), token).searchWarehouse(searchTerm);
		
		for (FsSecureBusinessObject object : businessObjects) {
			
			AbstractHandler info = null;
			
			if (object instanceof FsModel) {
				// Get the information handler from model
				info = new com.signavio.warehouse.model.handler.InfoHandler(this.getServletContext());
			} 
			else if (object instanceof FsDirectory) {				
				// Get the information handler from directory
				info = new com.signavio.warehouse.directory.handler.InfoHandler(this.getServletContext());
			}
			
			// Get the annotation from the model 
			HandlerConfiguration ann = Platform.getInstance().getHandlerDirectory().get(info.getClass().getName()).getContextClass().getAnnotation(HandlerConfiguration.class);
			// Add the representation of a model
			jsonResult.put(this.generateResource(ann.rel(), ann.uri() + "/" + object.getId(), info.getRepresentation(object, null, token)));

		}
		
		try {
			res.getWriter().write(jsonResult.toString());
		} catch (IOException e) {
			throw new IORequestException(e);
		}
	}

}
