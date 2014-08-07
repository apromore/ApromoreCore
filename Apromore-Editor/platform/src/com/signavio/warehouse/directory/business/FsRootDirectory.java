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
