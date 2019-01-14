/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package com.signavio.usermanagement.user.handler;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.usermanagement.business.UserManagementException;
import com.signavio.usermanagement.user.business.FsUser;


//import com.sun.xml.internal.fastinfoset.util.StringArray;

/**
 * Concrete implementation to get all information from a directory
 * @author Willi
 *
 */
@HandlerConfiguration(uri = "/user", rel="user")
public class UserHandler extends BasisHandler {

	/**
	 * Constructor
	 * @param servletContext
	 */
	public UserHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Get all root directories
	 */
	@Override
	@HandlerMethodActivation
	public Object getRepresentation(Object params, FsAccessToken token){
		// Return all users from the tenants
		return getAllUsers(token);
	}
	
	/**
	 * Returns the representation of all available users
	 * @return
	 */
	private Object getAllUsers(FsAccessToken token) {
		JSONArray a = new JSONArray();
			
		InfoHandler ih = new InfoHandler(this.getServletContext());
		HandlerConfiguration hc = this.getHandlerConfiguration();
		
		FsRoleManager manager = FsRoleManager.getTenantManagerInstance(FsRoleManager.class, token.getTenantId(), token);
		Set<FsUser> users = manager.getChildren(FsUser.class);
		
		for( FsUser user : users){	
			// Generate the resource for every principal, includes the information from the info handler
			a.put( this.generateResource(hc.rel(), hc.uri() + "/" + user.getId(), ih.getRepresentation(user, null, token) ) );
		}
		
		return a;
	}
	
}
