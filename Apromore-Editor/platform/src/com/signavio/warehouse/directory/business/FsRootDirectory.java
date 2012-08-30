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
package com.signavio.warehouse.directory.business;

import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.core.impl.FsPlatformPropertiesImpl;



/**
 * Implementation of the root directory in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsRootDirectory extends FsDirectory {
	
	private static FsRootDirectory SINGLETON;
	public static final String ID_OF_SINGLETON = "root-directory";

	public static void createInstance(String path) {

		if (SINGLETON != null) {
			throw new IllegalStateException("File system backend is already initialized");
		}
		
		System.out.println("[INFO] Initializing Root Directory...");
		
		if (path != null && path.length() > 0) {
			SINGLETON = new FsRootDirectory(path);
			System.out.println("[INFO] Initialized Root Directory!");
		} else {
			throw new IllegalStateException("Could initialize file system backend");
		}
		
	}

	public static FsRootDirectory getSingleton() {
		return SINGLETON;
	}

	private FsRootDirectory(String path) {
		super(path);
	}
	
	@Override
	public String getName() {
		return "Root";
	}
	
	@Override
	public void setName(String name){
		throw new UnsupportedOperationException("Cannot rename root directory");
	}
	
	@Override
	public void delete() {
		throw new UnsupportedOperationException("Cannot delete root directory");
	}
	
	@Override
	public FsDirectory getParentDirectory() {
		return null;
	}

	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}

}
