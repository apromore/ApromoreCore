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

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerExportConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.revision.business.FsModelRepresentationInfo;
import com.signavio.warehouse.revision.business.FsModelRevision;
import com.signavio.warehouse.revision.business.RepresentationType;

@HandlerConfiguration(uri="/json", context=RevisionHandler.class, rel="exp")
@HandlerExportConfiguration(name="JSON", icon="/explorer/src/img/famfamfam/page_white_code.png", mime="application/json")
public class JsonHandler extends AbstractRevisionExportHandler {

	public JsonHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	@Override
	@HandlerMethodActivation
	public  <T extends FsSecureBusinessObject> byte[] doExport(T sbo, Object params){
		FsModelRevision rev = (FsModelRevision) sbo;
		
		FsModelRepresentationInfo rep = rev.getRepresentation(RepresentationType.JSON);
		
		if(rep == null) {
			throw new RequestException("warehouse.noJSONRepresentationAvailable");
		} else {
			
  			
 			try {
 				JSONObject p = (JSONObject) params;
 				if (p.has("jsonp")){
 					String prepend = (String) p.get("jsonp");
 					return  (prepend + "(" + new String(rep.getContent(), "utf-8") + ")").getBytes("utf-8");
 				} else {
 					return rep.getContent();
 				}
				
				
			} catch (JSONException e) {
				return rep.getContent();
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("Could not read data", e);
			}
  			

  			
		}
	}
}
