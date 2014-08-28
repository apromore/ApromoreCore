/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.security.business.exceptions;

public class BusinessObjectDoesNotExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3503854846490543911L;

	public BusinessObjectDoesNotExistException(String id) {
		super("Business Object with id " + id + " does not exist.");
	}

	public BusinessObjectDoesNotExistException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BusinessObjectDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BusinessObjectDoesNotExistException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
