/*
 * Copyright © 2009-2016 The Apromore Initiative.
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
package com.signavio.warehouse.revision.handler;

import javax.servlet.ServletContext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerExportConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.revision.business.FsModelRepresentationInfo;
import com.signavio.warehouse.revision.business.FsModelRevision;
import com.signavio.warehouse.revision.business.RepresentationType;

@HandlerConfiguration(uri="/svg", context=RevisionHandler.class, rel="exp")
@HandlerExportConfiguration(name="SVG", icon="/explorer/src/img/famfamfam/page_white_vector.png", mime="image/svg+xml")
public class SvgHandler extends AbstractRevisionExportHandler {

	public SvgHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	@Override
	@HandlerMethodActivation
	public  <T extends FsSecureBusinessObject> byte[] doExport(T sbo, Object params){
		FsModelRevision rev = (FsModelRevision) sbo;
		
		FsModelRepresentationInfo rep = rev.getRepresentation(RepresentationType.SVG);
		
		if(rep == null) {
			throw new RequestException("warehouse.noSVGRepresentationAvailable");
		} else {
			return rep.getContent();
		}
	}
}
