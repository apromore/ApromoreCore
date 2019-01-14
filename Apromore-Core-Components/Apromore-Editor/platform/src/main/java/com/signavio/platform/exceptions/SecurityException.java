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
package com.signavio.platform.exceptions;

/**
 * @author Bjoern Wagner
 * This exception is thrown whenever an access violation occurs. 
 *  
 */
public class SecurityException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 63324283263845758L;


	public SecurityException() {
		super("platform.securityexception");
	}
	
	public SecurityException(Throwable e) {
		super("platform.securityexception", e);
	}
}
