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
package com.signavio.platform.handler;

import javax.servlet.ServletContext;

import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.InconsistentDataException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsRootUser;

/**
 * Implementation of Handler which abstracts all Handler for 
 * a specific context, e.g. Model or Directory.
 * @author Willi
 *
 */
public abstract class UnauthorizedBasisHandler extends BasisHandler {

	public UnauthorizedBasisHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	/**
	 * 
	 * @return The root token! This token has to be used in handlers that do not need authentication, e. g. login
	 */
	protected FsAccessToken getRootToken() {
		// ISSUE : This section has been changed as it used database-dependent code
		return FsAccessToken.getDummy();
	}
}
