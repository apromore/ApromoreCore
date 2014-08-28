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
