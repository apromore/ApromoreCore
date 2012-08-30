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
package com.signavio.warehouse.model.handler;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.AbstractInfoHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsModelRevision;


/**
 * Concrete implementation to get information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(context=ModelHandler.class, uri="/info", rel="info")
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
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token){
		
		JSONObject res = (JSONObject) super.getRepresentation(sbo, params, token);
		
		try {
			
			FsModel model = (FsModel) sbo;
			
			res.put("name", model.getName());
			res.put("description", model.getDescription());
			res.put("created", model.getCreationDate());
			res.put("type", model.getType());
			
			FsModelRevision head = model.getHeadRevision();
			if (head != null) {
				res.put("updated", head.getCreationDate());
				res.put("author", "/user/" + head.getAuthor());				// TODO: Get /user/ and /revision/ from the annotations
				res.put("rev", head.getRevisionNumber());
				res.put("comment", head.getComment());
				res.put("revision", "/revision/" + head.getId());
			}
			
			FsDirectory originalDirectory = null;

			originalDirectory = model.getParentDirectory();
			
			// Set folder
			if (originalDirectory != null){
				res.put("parent", "/directory/" + originalDirectory.getId());
			}
			
		} catch (JSONException e) {
			throw new RequestException("warehouse.noValidModelInfo");
		}
		
		return res;
	}

	@Override
	@HandlerMethodActivation
	public <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		super.putRepresentation(sbo, params, token);
		
		JSONObject jParams = (JSONObject) params;
		
		FsModel model = (FsModel) sbo;
		
		try {
			model.setName(jParams.getString("name"));
			model.setDescription(jParams.getString("description"));
			model.setType(jParams.getString("type"));
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RequestException("unsupportedEncoding", e);
		}
		
		
		return getRepresentation(sbo, params, token);
	}
	

}
