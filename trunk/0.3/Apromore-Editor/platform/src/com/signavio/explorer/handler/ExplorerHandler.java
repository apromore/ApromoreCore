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
package com.signavio.explorer.handler;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.IORequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;


@HandlerConfiguration(uri = "/explorer", rel="explorer")
public class ExplorerHandler extends BasisHandler {
	
	public ExplorerHandler(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	@HandlerMethodActivation
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
		try {

			addJSPAttributes(req);
			req.setAttribute("language", token.getUser().getAccount().getLanguageCode());
			req.setAttribute("country", token.getUser().getAccount().getCountryCode());
			res.setContentType("text/html");
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/explorer.jsp").include(req, res);
			//res.sendRedirect(INDEX_PAGE_URI);
		} catch (IOException e) {
			throw new IORequestException(e);
		} catch (ServletException e) {
			//TODO exception handling
			throw new RequestException("platform.servletexeption", e);
		}
	}
}
