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
package com.signavio.platform.servlets;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.core.HandlerEntry;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.ExportHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.security.business.exceptions.BusinessObjectCreationFailedException;
import com.signavio.platform.security.business.exceptions.BusinessObjectDoesNotExistException;

public class DispatcherServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6945680310755630698L;
	
	
	/**
	 * Define the RegEXP to get the context, id, and extension from the url
	 */
	private static Pattern urlpattern = null;
	
	private ServletContext servletContext;
	
	/**
	 * Construct 
	 */
	public DispatcherServlet(){
		
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();
		urlpattern = Pattern.compile(config.getServletContext().getContextPath() + "/(p)/([^/]+)(/([^/]+))?(/([^/]+))?(/+(.*))?$");
		servletContext = config.getServletContext();
	}
	
	private void dispatch(HttpServletRequest req, HttpServletResponse res) {

		//First, try to get the access token
		FsAccessToken token = (FsAccessToken) req.getSession().getAttribute("token");
		
		//Get HTTP method
		String met 		= req.getMethod();
		
		//Parse the URL
		String[] path 	= DispatcherServlet.parseURL( req.getRequestURI() );

		String context		= path[0];
		String identifier 	= path[1];
		String extension 	= path[2];
		
		HandlerEntry handler = (HandlerEntry) req.getAttribute("handler");
		
		// Disable cache for everything except Export handlers
		if (handler.getHandlerInstance() instanceof ExportHandler) {
			Date date = new Date();
			date.setYear(date.getYear()+1);
			res.setDateHeader("Expires", date.getTime());
		} else {
			res.setHeader("Cache-Control", "no-cache");
		}
		
		//if the identifier is not null, get the corresponding SBO
		FsSecureBusinessObject sbo = null;
		if(token != null) {
			if(identifier != null) {
				try {
					Object idObjInContext = servletContext.getAttribute(identifier);
					if(idObjInContext != null && idObjInContext instanceof String) {
						sbo = FsSecurityManager.getInstance().loadObject((String)idObjInContext, token);
					} else {
						sbo = FsSecurityManager.getInstance().loadObject(identifier, token);
					}
				} catch (BusinessObjectDoesNotExistException e) {
					throw new RequestException("platform.dispatcher.sboNotFound", e);
				} catch (BusinessObjectCreationFailedException e) {
					throw new RequestException("platform.dispatcher.sboCreationFailed", e);
				}
				
			}
		} else {
			//throw new RequestException("platform.dispatcher.noValidToken");
		}
		
		
		
		// Try to call the Handler
		if( met.equals("GET")){
			handler.getHandlerInstance().doGet(req, res, token, sbo);
		} else if( met.equals("PUT") ){
			handler.getHandlerInstance().doPut(req, res, token, sbo);
		} else if( met.equals("POST") ){
			handler.getHandlerInstance().doPost(req, res, token, sbo);
		} else if( met.equals("DELETE") ){
			handler.getHandlerInstance().doDelete(req, res, token, sbo);
		} else {
			throw new RequestException("platform.dispatcher.methodNotAllowed");
		}
	}
	
	/**
	 * Splits the URL into 4 parts:
	 *  String[0] The current context
	 *  String[1] The requested identifier
	 *  String[2] The extension
	 *  String[3] The rest after the extension
	 * @param url The URL which should e parsed
	 * @return An Array of four elements
	 */
	public static String[] parseURL( String url ) {
		// Extract id from the request URL 
		
		Pattern pattern = DispatcherServlet.urlpattern;
		Matcher matcher = pattern.matcher(new StringBuffer(url));
		
		if (matcher.find()) {

			return new String[]{	
									matcher.groupCount() >= 2 ? matcher.group(2) : null, 
									matcher.groupCount() >= 4 ? matcher.group(4) : null,
									matcher.groupCount() >= 6 ? matcher.group(6) : null,
									matcher.groupCount() >= 8 ? matcher.group(8) : null
								};
		} else {
			return new String[4];
		}
	}
	
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    	dispatch(req, res);
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
    	dispatch(req, res);
	}
    
    public void doPut(HttpServletRequest req, HttpServletResponse res) {
    	dispatch(req, res);
	}
    
    public void doDelete(HttpServletRequest req, HttpServletResponse res) {
    	dispatch(req, res);
	}
    
	

}
