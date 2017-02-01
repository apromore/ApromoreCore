/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.warehouse.directory.handler;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.handler.AbstractInfoHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;


/**
 * Concrete implementation to get information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(context=DirectoryHandler.class, uri="/info", rel="info")
public class InfoHandler extends AbstractInfoHandler {

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
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		
		JSONObject res = (JSONObject) super.getRepresentation(sbo, params, token);
		
		try {
			
			FsDirectory  directory= (FsDirectory) sbo;
			
			res.put("name", directory.getName());
			res.put("description", directory.getDescription());
			res.put("created", directory.getCreationDate().toString());
			
			FsDirectory originalDirectory = null;
			
			
			originalDirectory = directory.getParentDirectory();
			
			// Set folder
			if (originalDirectory != null){
				res.put("parent", "/directory/" + originalDirectory.getId());
			}
			
			if(directory instanceof FsRootDirectory) {
				res.put("type", "public");
			}
			
		} catch (JSONException e) {}
		
		return res;
	}

	/**
	 * Expects a params "name" and "description"
	 */
	@HandlerMethodActivation
	@Override
	public <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		super.putRepresentation(sbo, params, token);
		
		JSONObject data = (JSONObject) params;
		
		FsDirectory dir = (FsDirectory) sbo;
		
		try {
			dir.setName(data.getString("name"));
			dir.setDescription(data.getString("description"));
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		}
		
		return getRepresentation(sbo, params, token);
	}
}
