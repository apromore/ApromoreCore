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
