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

import com.signavio.platform.security.business.FsSecureBusinessObject;

public class BusinessObjectCreationFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1129675556771340274L;

	public BusinessObjectCreationFailedException() {
		super("Creating business object failed.");
	}
	
	public BusinessObjectCreationFailedException(String msg) {
		super("Creating business object failed: " + msg);
	}

	public BusinessObjectCreationFailedException(String msg, String className) {
		super("Creating business object failed: " + msg + " Class: " + className);
	}
	
	public BusinessObjectCreationFailedException(String msg, String className, Throwable e) {
		super("Creating business object failed: " + msg + " Class: " + className, e);
	}
	
	public BusinessObjectCreationFailedException(String msg, Class<? extends FsSecureBusinessObject> classObj) {
		super("Creating business object failed: " + msg + " Class: " + classObj.getName());
	}
	
	public BusinessObjectCreationFailedException(String msg, Class<? extends FsSecureBusinessObject> classObj, Throwable e) {
		super("Creating business object failed: " + msg + " Class: " + classObj.getName(), e);
	}
}
