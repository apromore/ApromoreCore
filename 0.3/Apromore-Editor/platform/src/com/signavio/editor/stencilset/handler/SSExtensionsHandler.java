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
