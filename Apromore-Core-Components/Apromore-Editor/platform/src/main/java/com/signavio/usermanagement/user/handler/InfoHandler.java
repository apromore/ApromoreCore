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

import java.util.Set;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.usermanagement.user.business.FsUser;



/**
 * Concrete implementation to get information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(context=UserHandler.class, uri="/info", rel="info")
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
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		
		FsUser user = (FsUser)sbo;
		FsAccount account = user.getAccount();
		
		JSONObject res = new JSONObject();
		try {
			res.put("title",  account.getTitle());
			res.put("firstName",  account.getFirstName());
			res.put("lastName", account.getLastName());
			res.put("name", user.getFullName());
			res.put("principal", account.getPrincipal());
			res.put("mail", account.getMailAddress());
			res.put("state", "active");
			res.put("language", account.getLanguage());
			res.put("isFirstStart", user.isFirstStart());
			res.put("company", account.getCompany());
			res.put("country", account.getCountry());
			
			JSONArray licenses = new JSONArray();
			
			res.put("licenses", licenses);
		} catch (JSONException e) {}
		
		return res;
	}
}
