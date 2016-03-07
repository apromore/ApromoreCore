/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.warehouse.model.handler;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.AbstractInfoHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsModelRevision;


/**
 * Concrete implementation to get information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(context=ModelHandler.class, uri="/info", rel="info")
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
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token){
		
		JSONObject res = (JSONObject) super.getRepresentation(sbo, params, token);
		
		try {
			
			FsModel model = (FsModel) sbo;
			
			res.put("name", model.getName());
			res.put("description", model.getDescription());
			res.put("created", model.getCreationDate());
			res.put("type", model.getType());
			
			FsModelRevision head = model.getHeadRevision();
			if (head != null) {
				res.put("updated", head.getCreationDate());
				res.put("author", "/user/" + head.getAuthor());				// TODO: Get /user/ and /revision/ from the annotations
				res.put("rev", head.getRevisionNumber());
				res.put("comment", head.getComment());
				res.put("revision", "/revision/" + head.getId());
			}
			
			FsDirectory originalDirectory = null;

			originalDirectory = model.getParentDirectory();
			
			// Set folder
			if (originalDirectory != null){
				res.put("parent", "/directory/" + originalDirectory.getId());
			}
			
		} catch (JSONException e) {
			throw new RequestException("warehouse.noValidModelInfo");
		}
		
		return res;
	}

	@Override
	@HandlerMethodActivation
	public <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		super.putRepresentation(sbo, params, token);
		
		JSONObject jParams = (JSONObject) params;
		
		FsModel model = (FsModel) sbo;
		
		try {
			model.setName(jParams.getString("name"));
			model.setDescription(jParams.getString("description"));
			model.setType(jParams.getString("type"));
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RequestException("unsupportedEncoding", e);
		}
		
		
		return getRepresentation(sbo, params, token);
	}
	

}
