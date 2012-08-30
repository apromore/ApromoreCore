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
