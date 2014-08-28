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
package com.signavio.platform.handler;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.core.HandlerDirectory;
import com.signavio.platform.core.HandlerEntry;
import com.signavio.platform.core.Platform;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;

/**
 * Implementation of Handler which abstracts all Handler for 
 * a specific context, e.g. Model or Directory.
 * @author Willi
 *
 */
public abstract class BasisHandler extends AbstractHandler {

	public BasisHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	/**
	 * Get all available handlers for the specific context
	 * and add there representation to it.
	 * @param id 
	 * @param params
	 */
	@Override
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		// Get all Handlers
		Collection<HandlerEntry> hes = HandlerDirectory.getInstance().getAllHandlerByContext( this.getClass() );
		
		JSONArray res = new JSONArray();
		// For every Handler
		for( HandlerEntry he : hes )
		{
			// Checks if the method is activated
			if( !isRepresenationActivated("get", true) ){
				continue;
			}
			
			// Adds there representation to it.
			Object js = he.getHandlerInstance().getRepresentation(sbo, params, token);
			if( js != null )
			{
				HandlerEntry context = Platform.getInstance().getHandlerDirectory().get( he.getContextClass().getName() );
				
				try {
					JSONObject entry = new JSONObject();
					entry.put("rel", 	he.getRel());
					entry.put("href",  	context.getUri() + "/" + sbo.getId() + he.getUri());
					entry.put("rep", 	js);
					res.put(entry);
				} catch (JSONException e) {}	
			}
		}
		return res;
	}
	
}
