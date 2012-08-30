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
