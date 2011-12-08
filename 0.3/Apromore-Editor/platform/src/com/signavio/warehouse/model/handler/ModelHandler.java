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
import java.util.Set;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.exceptions.EncodingException;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsModelRevision;
import com.signavio.warehouse.revision.business.RepresentationType;

/**
 * Concrete implementation to get all information from a model
 * @author Willi
 *
 */
@HandlerConfiguration(uri = "/model", rel="mod")
public class ModelHandler extends BasisHandler {

	/**
	 * Constructor
	 * @param servletContext
	 */
	public ModelHandler(ServletContext servletContext) {
		super(servletContext);
	}

	
	@Override
	@HandlerMethodActivation
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		FsModel model = (FsModel) sbo;
		
		//get revisions
		Set<FsModelRevision> revs = model.getRevisions();
		if(revs.size() == 0) {
			throw new RequestException("model.noRevisionsFound");
		}
		
		//get json representation of superclass
		JSONArray superRep = (JSONArray) super.getRepresentation(sbo, params, token);
		
		if(superRep == null) {
			throw new RequestException("model.failedToPopulateModelData");
		}
		
		//add json representation of revisions
		for(FsModelRevision rev : revs) {
			superRep.put(this.getRevisionInfo(rev));
		}
		
		return superRep;
	}
	
	
	/**
	 * Get the whole information for a revision.
	 * @param dir
	 * @return
	 * @throws InvalidIdentifierException 
	 */
	public JSONObject getRevisionInfo( FsModelRevision rev ) {
			
		// Get the information handler for the revision
		com.signavio.warehouse.revision.handler.InfoHandler in = new com.signavio.warehouse.revision.handler.InfoHandler(this.getServletContext());
		// Get the annotation from the hander 
		HandlerConfiguration revConf = Platform.getInstance().getHandlerDirectory().get(in.getClass().getName()).getContextClass().getAnnotation(HandlerConfiguration.class);

		return this.generateResource(revConf.rel(), revConf.uri() + "/" + rev.getId(), in.getRepresentation(rev, null, rev.getAccessToken()));
	}


	/**
	 * Moves the model and/or creates a new revision
	 * Expects params parent and/or
	 * name, description, json, svg, comment
	 */
	@Override
	@HandlerMethodActivation
	public <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		JSONObject jParams = (JSONObject) params;	
		FsModel model = (FsModel) sbo;
		
		//set parent
		try {
			String parentId = jParams.getString("parent");
			
			parentId = parentId.replace("/directory/", "");
			FsDirectory dir = FsSecurityManager.getInstance().loadObject(FsDirectory.class, parentId, token);
			
			dir.addChildModel(model);
		} catch (JSONException e) {
			throw new RequestException("invalid_request_missing_parameter", e);
		} catch (UnsupportedEncodingException e) {
			throw new RequestException("unsupported.encoding", e);
		}
		
		String name, description, jsonRep, svgRep, comment;
		try {
			 name = jParams.getString("name");
			 description = jParams.getString("description");
			 jsonRep = jParams.getString("json_xml");
			 svgRep = jParams.getString("svg_xml");
			 comment = jParams.getString("comment");
			 
			 model.setName(name);
			 model.setDescription(description);
			 model.createRevision(jsonRep, svgRep, comment);	
			 
			 if(jParams.has("id")) {
				 String id = jParams.getString("id");
				 this.getServletContext().setAttribute(id, model.getId());
			 }
		} catch (JSONException e) {
			//Do not throw Exception! Optional parameters
			//throw new RequestException("invalid_request_missing_parameter", e);
		} catch (EncodingException e) {
			throw new RequestException("invalid_representation_encoding", e);
		} catch (UnsupportedEncodingException e) {
			throw new RequestException("unsupportedEncoding", e);
		}
		
		return getRepresentation(model, params, token);
	}	
    
	/**
	 * Creates a new model.
	 * Params: parent, name, description, type, comment, json, svg
	 * Optional params: id
	 * @throws InvalidIdentifierException 
	 */
	@Override
	@HandlerMethodActivation
	public Object postRepresentation(Object params, FsAccessToken token) {
		JSONObject jsonParams = (JSONObject) params;
		
		String id=null, copy=null,parentId, name, description, type, comment, jsonRep, svgRep;
		FsDirectory parent;
		try {
			parentId = jsonParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			parent = FsSecurityManager.getInstance().loadObject(FsDirectory.class, parentId, token);
			
			if(jsonParams.has("copy")&&jsonParams.has("id")) {
				//create a copy of the model identified by id
				String mId = jsonParams.getString("id").replace("/model/", "");
				FsModel model = FsSecurityManager.getInstance().loadObject(FsModel.class, mId, token);
				name = model.getName();
				description = model.getDescription();
				type = model.getType();
				comment = "";
				jsonRep = new String(model.getHeadRevision().getRepresentation(RepresentationType.JSON).getContent(), "utf-8");
				svgRep = new String(model.getHeadRevision().getRepresentation(RepresentationType.SVG).getContent(), "utf-8");
			} else {
				name = jsonParams.getString("name");
				description = jsonParams.getString("description");
				type = jsonParams.getString("type");
				comment = jsonParams.getString("comment");
				jsonRep = jsonParams.getString("json_xml");
				svgRep = jsonParams.getString("svg_xml");
				
				if(jsonParams.has("id")) {
					id = jsonParams.getString("id");
				}
			}
			
			
		} catch (JSONException e) {
			throw new RequestException("invalid_request_missing_parameter ", e);
		} catch (UnsupportedEncodingException e) {
			throw new RequestException("unsupported_encoding ", e);
		}
		
		FsModel model;
		
		try { 
			if(id == null) {
				model = parent.createModel(name, description, type, jsonRep, svgRep, comment);
			} else {
				model = parent.createModel(id, name, description, type, jsonRep, svgRep, comment);
				this.getServletContext().setAttribute(id, model.getId());
			}
		} catch (Exception e) {
			throw new RequestException("model_creation_failed", e);
		}
		return getModelInfo(model);
	}
	
	/**
	 * Deletes a model
	 */
	@Override
	@HandlerMethodActivation
	public <T extends FsSecureBusinessObject> void deleteRepresentation(T sbo, Object params, FsAccessToken token) {
		FsModel model = (FsModel) sbo;
		
		FsSecurityManager.getInstance().deleteObject(model, token);
	}	
	
	public JSONObject getModelInfo(FsModel model) {
		
		HandlerConfiguration hc = this.getHandlerConfiguration();
	
		return this.generateResource(hc.rel(), hc.uri() + "/" + model.getId(), getModelRep(model));
	}
	
	private JSONObject getModelRep( FsModel model) {
		
		return (JSONObject) new InfoHandler(this.getServletContext()).getRepresentation( model , null, model.getAccessToken());
	
	}
}
