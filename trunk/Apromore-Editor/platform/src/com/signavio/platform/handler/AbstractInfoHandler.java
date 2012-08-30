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
