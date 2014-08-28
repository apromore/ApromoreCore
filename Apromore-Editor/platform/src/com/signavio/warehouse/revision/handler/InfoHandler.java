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
package com.signavio.warehouse.revision.handler;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.revision.business.FsModelRevision;


/**
 * Concrete implementation to get information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(context=RevisionHandler.class, uri="/info", rel="info")
public class InfoHandler extends AbstractHandler {

	/**
	 * Construct
	 * @param servletContext
	 */
	public InfoHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	/**
	 * Get all information out from a particular model
	 */
	@Override
	@HandlerMethodActivation
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token){

		JSONObject jRev = new JSONObject();
		FsModelRevision rev = (FsModelRevision) sbo;
		
		try {
			
			jRev.put("rev", rev.getRevisionNumber());
			jRev.put("author", "/user/" + rev.getAuthor());
			jRev.put("created", rev.getCreationDate());
			jRev.put("comment", rev.getComment());
			
		} catch (JSONException e) {}
		
		return jRev;
	}

}
