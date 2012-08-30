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
