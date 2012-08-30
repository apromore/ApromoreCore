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
