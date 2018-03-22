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
package com.signavio.editor.stencilset.handler;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.usermanagement.user.business.FsUser;


@HandlerConfiguration(uri = "/editor_ssextensions", rel = "stencilset")
public class SSExtensionsHandler extends BasisHandler {

	public final String SS_EXT_CONFIGURATION_FILE = this.getRootDirectory()
			+ "/WEB-INF/json/extensions";

	public final String EDITOR_URL_PREFIX;

	public SSExtensionsHandler(ServletContext servletContext) {
		super(servletContext);
		
		EDITOR_URL_PREFIX = Platform.getInstance().getPlatformProperties().getEditorUri() + "/"; 
	}

	/**
	 * Returns a JSON file that contains information about the available stencil
	 * set extensions for the user's profile
	 * 
	 * @throws Exception
	 */
	@Override
	public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req,
			HttpServletResponse res, FsAccessToken token, T sbo) {

		FsUser user = token.getUser();

		String fileName = SS_EXT_CONFIGURATION_FILE + ".json";

		try {
			this.writeFileToResponse(new File(fileName), res);
		} catch (IOException e) {
			throw new RequestException("editor.ssextension.ioException", e);
		} catch (Exception e) {
			throw new RequestException("editor.ssextension.exception", e);
		}

	}
}
