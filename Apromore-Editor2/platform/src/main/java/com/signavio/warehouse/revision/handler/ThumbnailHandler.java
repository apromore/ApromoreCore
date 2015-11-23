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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerExportConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.revision.business.RepresentationType;

@HandlerConfiguration(uri="/thumbnail", context=RevisionHandler.class, rel="exp")
@HandlerExportConfiguration(name="PNG Thumbnail Export", icon="/explorer/src/img/famfamfam/picture.png", mime="image/png")
public class ThumbnailHandler extends AbstractImageHandler {

	private float HEIGHT = 55f;
	private float WIDTH = 50f;
	
	public ThumbnailHandler(ServletContext servletContext) {
		super(servletContext);
		
	}

	@Override
	@HandlerMethodActivation
	public  <T extends FsSecureBusinessObject> byte[] doExport(T sbo, Object params){
		Map<TranscodingHints.Key, Object> hints = new HashMap<TranscodingHints.Key, Object>();
		
		hints.put(PNGTranscoder.KEY_MAX_WIDTH, WIDTH);
		hints.put(PNGTranscoder.KEY_MAX_HEIGHT, HEIGHT);
		
		return getImage(RepresentationType.PNG_SMALL, new PNGTranscoder(), sbo, hints);
	}
}
