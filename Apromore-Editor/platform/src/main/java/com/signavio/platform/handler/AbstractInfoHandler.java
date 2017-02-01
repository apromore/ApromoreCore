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
package com.signavio.platform.handler;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;


/**
 * Abstract Info Handler that adds and updates general information about an object to the result
 * @author Willi
 *
 */
public abstract class AbstractInfoHandler extends AbstractHandler {

	/**
	 * Construct
	 * @param servletContext
	 */
	public AbstractInfoHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	/**
	 * Get general information about an SBO
	 */
	@Override
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		
		JSONObject res = new JSONObject();
		try {
			
			// Set generic info attributes
			res.put("privilegeInheritanceBlocked", sbo.isPrivilegeInheritanceBlocked());
			res.put("deleted", sbo.isDeleted());
			
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		}
		
		return res;
	}

	/**
	 * Optionally expects boolean params 'deleted' and 'privilegeInheritanceBlocked'
	 */
	@Override
	public <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		JSONObject data = (JSONObject) params;

		try {
			boolean deleted = data.getBoolean("deleted");
			
			if(sbo.isDeleted() != deleted) {
				sbo.setDeleted(deleted);
			}
		} catch (JSONException e) {
			//do nothing, because we do not force a client to always send this information back to the server
		}
		
		try {
			boolean privInheritanceBlocked = data.getBoolean("privilegeInheritanceBlocked");
			
			if(sbo.isPrivilegeInheritanceBlocked() != privInheritanceBlocked) {
				sbo.setPrivilegeInheritanceBlocked(privInheritanceBlocked);
			}
		} catch (JSONException e) {
			//do nothing, because we do not force a client to always send this information back to the server
		}
		
		return getRepresentation(sbo, params, token);
	}
}
