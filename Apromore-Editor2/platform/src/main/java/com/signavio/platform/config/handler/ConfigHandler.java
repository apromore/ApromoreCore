/*
 * Copyright © 2009-2015 The Apromore Initiative.
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
package com.signavio.platform.config.handler;

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
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.usermanagement.user.business.FsUser;

@HandlerConfiguration(uri="/config")
public class ConfigHandler extends BasisHandler {

	public ConfigHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	@Override
	@HandlerMethodActivation
	public Object getRepresentation(Object params, FsAccessToken token){
		
		JSONObject j = new JSONObject();
		JSONArray a = new JSONArray();
		
		try {
			a.put("de");
			a.put("en_us");
			j.put("languages", a);
			
			j.put("supportedBrowserEditor", Platform.getInstance().getPlatformProperties().getSupportedBrowserEditorRegExp());
			
			// Write the current user
			FsUser u = FsSecurityManager.getInstance().loadObject(FsUser.class, token.getUserId(), token);
			FsTenant tenant = u.getTenant();
			j.put("user", "/user/" + u.getId());
			j.put("tenant", "/tenant/" + tenant.getId());
			
			// Add owner
			FsUser owner = (FsUser) tenant.getOwner().getUserObject(tenant);
			j.put("owner", "/user/" + owner.getId());
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		} catch (Exception e) {
			throw new RequestException("config.exception", e);
		}
		
		return j;
		
	}

}
