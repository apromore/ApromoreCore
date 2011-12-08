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
package com.signavio.warehouse.revision.handler;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.revision.business.FsModelRevision;


/**
 * Concrete implementation to get information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(context=RevisionHandler.class, uri="/info", rel="info")
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
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token){

		JSONObject jRev = new JSONObject();
		FsModelRevision rev = (FsModelRevision) sbo;
		
		try {
			
			jRev.put("rev", rev.getRevisionNumber());
			jRev.put("author", "/user/" + rev.getAuthor());
			jRev.put("created", rev.getCreationDate());
			jRev.put("comment", rev.getComment());
			
		} catch (JSONException e) {}
		
		return jRev;
	}

}
