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
/**
 * 
 */
package com.signavio.platform.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.util.StringUtil;

/**
 * @author Bjoern Wagner
 *
 */
public class RequestExceptionFilter implements Filter {

	private ServletContext servletContext;
	
	private void handleThrowable(Throwable t, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException  {
		t.printStackTrace();
		RequestException re = null;
		if(t instanceof RequestException) {
			re = (RequestException) t;
		} else if(t.getCause() instanceof RequestException) {
			re = (RequestException) t.getCause();
		}
		
		if(re != null) {
			res.setStatus(re.getHttpStatusCode());
		} else if (t instanceof SecurityException || t.getCause() instanceof SecurityException) {
			res.setStatus(403);
		} else {
			res.setStatus(500);
		}
		
//		Properties translation = TranslationFactory.getTranslation(req); 
		
		// this is very dirty since it requires 
		String message = null;
		if(re != null) {
//			message = translation.getProperty(re.getErrorCode());
			if (message != null) {
				message = StringUtil.formatString(message, re.getParams());
			} else {
//				message = translation.getProperty("unknownError") + " (" + re.getErrorCode() + ")";
			}
		}
		
		if(message == null) {
//			message = translation.getProperty("unknownError") + " (" + t.getLocalizedMessage() + ")";
		}
		
		if (req.getHeader("Accept").contains("application/json")) 
		{
			JSONObject errorObject = new JSONObject();
			try {
				errorObject.put("message", message);
			} catch (JSONException e1) {
				e1.printStackTrace();
				throw new ServletException("Error Handling Failed", t);
			}
			res.getWriter().write(errorObject.toString());
		} else {
			req.setAttribute("message", message);
			servletContext.getRequestDispatcher("/WEB-INF/jsp/error.jsp").include(req, res);
		}
	}
	
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
	
		HttpServletRequest httpReq = (HttpServletRequest) req; 
		HttpServletResponse httpRes = (HttpServletResponse) res;
		
		try {
			chain.doFilter(req, res);
		} catch (Throwable t) {			
			handleThrowable(t, httpReq, httpRes);
		}
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		this.servletContext = arg0.getServletContext();
		
	}
}
