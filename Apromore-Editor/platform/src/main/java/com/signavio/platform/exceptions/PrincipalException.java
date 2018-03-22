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
 * @author bjnwagner
 *
 */
public class PrincipalException extends Exception {

	/**
	 * 
	 */
	public PrincipalException() {
	}

	/**
	 * @param arg0
	 */
	public PrincipalException(String message) {
		super(message);
	}

	/**
	 * @param arg0
	 */
	public PrincipalException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PrincipalException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
