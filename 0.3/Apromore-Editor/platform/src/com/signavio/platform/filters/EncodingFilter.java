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
package com.signavio.platform.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;



/**
 * 
 * Encoding filter sets the character encoding of the request and the response
 * to the encoding specified as init-param "encoding" in the web.xml file.
 * 
 * @author Nicolas Peters
 * 
 */
public class EncodingFilter implements Filter {

	private static final Logger logger = Logger.getLogger(EncodingFilter.class);

	private String encoding = null;

	/** PUBLIC METHODS */

	public void destroy() {
		logger.info("EncodingFilter destroyed...");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("Filtering: "
					+ ((HttpServletRequest) request).getRequestURL());
		}
		String encoding = getEncoding();
		
		if (encoding != null) {
			//request.setCharacterEncoding(encoding);
			response.setCharacterEncoding(encoding);
		}

		chain.doFilter(request, response);

		if (logger.isDebugEnabled()) {
			logger.debug("Leaving");
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("EncodingFilter initializing...");
		this.setEncoding(filterConfig.getInitParameter("encoding"));
		logger.info("Using encoding: " + this.encoding);
	}

	/** PROTECTED METHODS */
	protected String getEncoding() {
		return (this.encoding);
	}

	protected void setEncoding(String enc) {
		if (enc != null) {
			this.encoding = enc;
		}
	}
}
