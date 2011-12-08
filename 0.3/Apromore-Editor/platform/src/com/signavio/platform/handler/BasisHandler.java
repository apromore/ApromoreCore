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

import java.util.Collection;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.core.HandlerDirectory;
import com.signavio.platform.core.HandlerEntry;
import com.signavio.platform.core.Platform;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;

/**
 * Implementation of Handler which abstracts all Handler for 
 * a specific context, e.g. Model or Directory.
 * @author Willi
 *
 */
public abstract class BasisHandler extends AbstractHandler {

	public BasisHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	/**
	 * Get all available handlers for the specific context
	 * and add there representation to it.
	 * @param id 
	 * @param params
	 */
	@Override
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		// Get all Handlers
		Collection<HandlerEntry> hes = HandlerDirectory.getInstance().getAllHandlerByContext( this.getClass() );
		
		JSONArray res = new JSONArray();
		// For every Handler
		for( HandlerEntry he : hes )
		{
			// Checks if the method is activated
			if( !isRepresenationActivated("get", true) ){
				continue;
			}
			
			// Adds there representation to it.
			Object js = he.getHandlerInstance().getRepresentation(sbo, params, token);
			if( js != null )
			{
				HandlerEntry context = Platform.getInstance().getHandlerDirectory().get( he.getContextClass().getName() );
				
				try {
					JSONObject entry = new JSONObject();
					entry.put("rel", 	he.getRel());
					entry.put("href",  	context.getUri() + "/" + sbo.getId() + he.getUri());
					entry.put("rep", 	js);
					res.put(entry);
				} catch (JSONException e) {}	
			}
		}
		return res;
	}
	
}
