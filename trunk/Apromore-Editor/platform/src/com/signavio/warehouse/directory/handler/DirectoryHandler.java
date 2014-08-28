/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.warehouse.directory.handler;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecureBusinessSubject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;



/**
 * Concrete implementation to get all information from a directory
 * @author Willi
 *
 */
@HandlerConfiguration(uri = "/directory", rel="dir")
public class DirectoryHandler extends BasisHandler {

	/**
	 * Constructor
	 * @param servletContext
	 */
	public DirectoryHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Get all root directories
	 */
	@Override
	@HandlerMethodActivation
	public Object getRepresentation(Object params, FsAccessToken token){
		return getRootDirectories(token);
	}
	
	/**
	 * Get the specific representation for directory, includes child entries.
	 */
	@Override
	@HandlerMethodActivation
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		
		JSONArray rep = (JSONArray) super.getRepresentation(sbo, params, token);
			
		FsDirectory directory = (FsDirectory) sbo;
		
		for (FsDirectory childDir : directory.getChildDirectories()) {
			rep.put( this.getDirectoryInfo(childDir));
		}
		
		for (FsModel childModel : directory.getChildModels()) {
			JSONObject jModel = this.getModelInfo(childModel);
			if(jModel != null) {
				rep.put( this.getModelInfo(childModel));
			}
		}
		
		return rep;
	}
	
	/**
	 * Get all resources for the root directories
	 * @return
	 * @throws InvalidIdentifierException 
	 */
	private JSONArray getRootDirectories(FsAccessToken token) {
		
		JSONArray j = new JSONArray();
		
		FsEntityManager entitiyManager = FsEntityManager.getTenantManagerInstance(FsEntityManager.class, token.getTenantId(), token);
		// user public folder
		j.put( getDirectoryInfo(entitiyManager.getTenantRootDirectory(), "public"));
		// user private folder
		FsSecureBusinessSubject u = (FsSecureBusinessSubject) FsSecurityManager.getInstance().loadObject(token.getUserId(), token);
//		j.put( getDirectoryInfo(u.getChildren(PrivateDirectory.class).iterator().next(), "private"));
		// user trash folder
//		j.put( getDirectoryInfo(u.getChildren(Trash.class).iterator().next(), "trash"));
		
		
		return j;
	}

	/**
	 * Get the whole information for a specific directory.
	 * @param dir
	 * @return
	 * @throws InvalidIdentifierException 
	 */
	public JSONObject getModelInfo( FsModel model ) {
			
		// Get the information handler from model
		com.signavio.warehouse.model.handler.InfoHandler in = new com.signavio.warehouse.model.handler.InfoHandler(this.getServletContext());
		// Get the annotation from the model 
		HandlerConfiguration modelCA = Platform.getInstance().getHandlerDirectory().get(in.getClass().getName()).getContextClass().getAnnotation(HandlerConfiguration.class);

		return this.generateResource(modelCA.rel(), modelCA.uri() + "/" + model.getId(), in.getRepresentation(model, null, model.getAccessToken()));
	}
	
	
	public JSONObject getDirectoryInfo( FsDirectory dir) {
		return this.getDirectoryInfo(dir, null);
	}
	/**
	 * Get the whole information for a specific directory.
	 * @param dir
	 * @return
	 * @throws InvalidIdentifierException 
	 */
	public JSONObject getDirectoryInfo( FsDirectory dir, String directorytype) {
		
		HandlerConfiguration hc = this.getHandlerConfiguration();
		JSONObject rep = getDirectoryRep(dir);
		
		if (directorytype != null){
			try {
				rep.put("type", directorytype);
			} catch (JSONException e) {}
		}
		return this.generateResource(hc.rel(), hc.uri() + "/" + dir.getId(), rep);
	}
	
	/**
	 * Get a specific representation for a particular directory.
	 * Right now, the InfoHandler is used for that.
	 * @param dir
	 * @return
	 * @throws InvalidIdentifierException 
	 */
	private JSONObject getDirectoryRep( FsDirectory dir) {
	
		return (JSONObject) new InfoHandler(this.getServletContext()).getRepresentation( dir , null, dir.getAccessToken());
	
	}
	
	@HandlerMethodActivation
	@Override
	public  <T extends FsSecureBusinessObject> void deleteRepresentation(T sbo, Object params, FsAccessToken token) {
		FsDirectory dir = (FsDirectory) sbo;
		
//		dir.getParentDirectory().removeChildDirectory(dir);
//		dir.delete();
		FsSecurityManager.getInstance().deleteObject(dir, token);
	}
	
	/**
	 * Expects a param "parent" that contains the id of the new parent.
	 */
	@HandlerMethodActivation
	@Override
	public  <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		FsDirectory dir = (FsDirectory) sbo;
		JSONObject obj = (JSONObject) params;
		try {
			String pId = obj.get("parent").toString();
			pId = pId.replace("/directory/", "");
			FsDirectory parent = (FsDirectory) FsSecurityManager.getInstance().loadObject(pId, token);
			
			parent.addChildDirectory(dir);
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		}
		return getDirectoryInfo(dir);
	}
	
	/**
	 * Creates a new directory.
	 * Params: parent, title 
	 * @throws InvalidIdentifierException 
	 */
	@Override
	@HandlerMethodActivation
	public Object postRepresentation(Object params, FsAccessToken token) {
		JSONObject jsonParams = (JSONObject) params;
		
		String parentId;
		String title;
		try {
			parentId = jsonParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			
			title = jsonParams.getString("name");
		} catch (JSONException e) {
			throw new JSONRequestException(e);
		}
		
		FsDirectory parent = (FsDirectory) FsSecurityManager.getInstance().loadObject(parentId, token);
		FsDirectory newDir = FsSecurityManager.getInstance().createObject(FsDirectory.class, parent, token, title);
		newDir.setName(title);
		//parent.addChildDirectory(newDir);
		
		return getDirectoryInfo(newDir);
		
	}
}
