/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.handler;


import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerExportConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;

public abstract class ExportHandler extends AbstractHandler {

	public ExportHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	@Override
	@HandlerMethodActivation
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token){

		JSONObject j = new JSONObject();
		
		HandlerExportConfiguration an = this.getClass().getAnnotation(HandlerExportConfiguration.class);
		
		if( an != null  ){
			try {
				j.put("name", an.name()); 
				j.put("icon", an.icon());
				j.put("mime", an.mime());
			} catch (JSONException e) {}
		}

		return j;
	}
	
	public  <T extends FsSecureBusinessObject> byte[] doExport(T sbo, Object params){
		return null;
	}
	
	@Override
    public  <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo ) {
		
		HandlerMethodActivation ac;
		try {
			ac = this.getClass().getMethod("doExport", new Class[]{FsSecureBusinessObject.class, Object.class}).getAnnotation(HandlerMethodActivation.class);
		
			HandlerExportConfiguration an = this.getClass().getAnnotation(HandlerExportConfiguration.class);
			if( an != null && ac != null ){
				res.setStatus(200);
				res.setContentType(an.mime());
				res.setHeader("Expires", "0");
				
				setFileName(sbo, res);
				
				res.getOutputStream().write( doExport(sbo, req.getAttribute("params")) );
			} else {
				res.setStatus(404);
			}
		}catch (NoSuchMethodException e) {
			throw new RequestException("platform.nosuchmethod", e);
		} catch (IOException e) {
			throw new RequestException("platform.ioexception", e);
		}
		
		
	}

	protected void setFileName(FsSecureBusinessObject sbo, HttpServletResponse res) {
		HandlerExportConfiguration an = this.getClass().getAnnotation(HandlerExportConfiguration.class);
		res.setHeader("Content-Disposition", " "+(an.download()?"attachment":"inline")+"; filename=\"" + sbo.getId() + "." + an.name().toLowerCase()+"\"");
	}
}
