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
