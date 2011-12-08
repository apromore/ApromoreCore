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
