/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package com.signavio.editor.handler;

import java.io.*;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import org.apache.commons.io.IOUtils;
import org.apromore.plugin.editor.EditorPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;


@HandlerConfiguration(uri = "/editor_plugins", rel="plugins")
public class PluginsHandler extends BasisHandler {

	@Autowired
	private ApplicationContext appContext;

	public PluginsHandler(ServletContext servletContext) {
		super(servletContext);
		//Workaround to get the Spring applicationContext injected properly
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, servletContext);
	}

	/**
	 * Returns a plugins configuration xml file that fits to the current user's license.
	 * @throws Exception
	 */
	@Override
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  	
  		File pluginConf = new File(this.getRootDirectory() + "/WEB-INF/xml/editor/plugins.xml");

		List<EditorPlugin> editorPlugins = (List<EditorPlugin>) appContext.getBean("editorPlugins");
		StringBuilder additionalPlugins = new StringBuilder();
		for (EditorPlugin plugin: editorPlugins) {
			additionalPlugins.append("<plugin source=\""+plugin.getJavaScriptURI()+"\" name=\""+plugin.getJavaScriptPackage()+"\"/>");
		}

		if(pluginConf.exists()) {
  			res.setStatus(200);
  	  		res.setContentType("text/xml");

			try {
				String pluginDef = IOUtils.toString(new FileReader(pluginConf));

				// Insert the Editor plug-ins replacing the placeholde
				//TODO switch to a real XML reader/writer based implementation
				pluginDef = pluginDef.replace("<?EDITOR-PLUGINS?>", additionalPlugins.toString());

				res.getWriter().append(pluginDef);
			} catch (IOException e) {
				throw new RequestException("platform.ioexception", e);
			}

  		} else {
  			throw new RequestException("editor.pluginXmlForProfileNotFound");
  		}
  		
  		
  		
	}
}
