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
package com.signavio.platform.exceptions;

import org.apache.log4j.Logger;

/**
 * This class is the base for all runtime platform exceptions. It logs each thrown exception as fatal using log4j.
 * @author Bjoern Wagner
 *
 */

public abstract class LoggedRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4180454029425889094L;

	private static Logger logger = Logger.getLogger(InconsistentDataException.class);

	protected void logException() {
		logger.fatal(this.getMessage(), this);
	}
	
	public LoggedRuntimeException() {
		logException();
	}

	public LoggedRuntimeException(String message) {
		super(message);
		logException();
	}

	public LoggedRuntimeException(Throwable cause) {
		super(cause);
		logException();
	}

	public LoggedRuntimeException(String message, Throwable cause) {
		super(message, cause);
		logException();
	}

}
