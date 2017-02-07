/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.explorer.handler;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;



@HandlerConfiguration(uri = "/explorer_plugins", rel="plugins")
public class PluginsHandler extends BasisHandler {

	public PluginsHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Returns a plugins configuration xml file that fits to the current user's license.
	 * @throws Exception
	 */
	@Override
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  		
  		File pluginConf = new File(this.getRootDirectory() + "/WEB-INF/xml/explorer/plugins.xml");
  		
  		if(pluginConf.exists()) {
  			res.setStatus(200);
  	  		res.setContentType("text/xml");
  	  		
  	  		try {
				this.writeFileToResponse(pluginConf, res);
			} catch (IOException e) {
				throw new RequestException("platform.ioexception", e);
			}
  		} else {
  			throw new RequestException("editor.pluginXmlForProfileNotFound");
  		}
  		
  		
	}
}
