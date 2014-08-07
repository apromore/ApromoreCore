/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
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
