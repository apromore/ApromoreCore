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
