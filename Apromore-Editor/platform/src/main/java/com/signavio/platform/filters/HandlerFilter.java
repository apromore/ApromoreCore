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
/**
 * 
 */
package com.signavio.platform.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.core.HandlerEntry;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.servlets.DispatcherServlet;
import com.signavio.platform.tenant.LicenseException;

/**
 * This filter checks, if the requested handler exists.
 * If not, an exception is thrown. If the handler exists,
 * the HandlerEntry is added as an attribute to the request
 * (attribute "handler").
 * 
 * @author Nicolas Peters
 *
 */
public class HandlerFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		
		if(request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			
			//check if this request is a post to the login handler
			String[] path 	= DispatcherServlet.parseURL( req.getRequestURI() );

			String context		= path[0];
			//String identifier 	= path[1];
			String extension 	= path[2];
			
			HandlerEntry handler = null;
			
			// If there is no extension
			if( extension == null ){
				// Find the BasisHandler
				handler = Platform.getInstance().getHandlerDirectory().getBasisHandler( context );
			} else if( context != null ) {
				// If not, findExtension Handler
				handler =Platform.getInstance().getHandlerDirectory().getHandlerByContextAndUri(context, extension);
			}
			
			if(handler == null) {
				throw new RequestException("handlerfilter.handlerNotFound", context);
			} else {
				req.setAttribute("handler", handler);
			}

			chain.doFilter(req, res);
		}
	}

	//@Override
	public void destroy() {

	}

	//@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
