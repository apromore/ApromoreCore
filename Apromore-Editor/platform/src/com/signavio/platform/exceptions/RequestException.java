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
/**
 * 
 */
package com.signavio.platform.exceptions;

/**
 * @author bjnwagner
 *
 */
public class RequestException extends RuntimeException {

	
	private String errorCode;
	private String[] params;
	private int httpStatusCode = 500;
	
	/**
	 * @param errorCode
	 */
	public RequestException(String errorCode) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode
	 * @param cause
	 */
	public RequestException(String errorCode, Throwable cause) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
	}
	
	/**
	 * @param errorCode
	 */
	public RequestException(String errorCode, String ... params) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
		this.params = params;
	}

	/**
	 * @param errorCode
	 * @param cause
	 */
	public RequestException(String errorCode, Throwable cause, String ... params) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
		this.params = params;
	}
	
	public RequestException(String errorCode, int httpStatusCode,
			String[] params) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
		this.params = params;
	}
	
	public RequestException(String errorCode, Throwable cause, int httpStatusCode,
			String[] params) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
		this.params = params;
	}
	
	public RequestException(String errorCode, int httpStatusCode) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
	}
	
	public RequestException(String errorCode, Throwable cause, int httpStatusCode) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
	public String[] getParams() {
		return params;
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
