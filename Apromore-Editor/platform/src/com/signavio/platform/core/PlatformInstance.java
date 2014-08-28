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
package com.signavio.platform.core;

import java.io.File;

/**
 * A platform instance represents the one and only running platform of a server. PlatformInstance are
 * created and booted by the Plaform factory.
 * @author Bjoern Wagner
 *
 */
public interface PlatformInstance {
	/**
	 * Returns the directory of all available handlers of the platform if available
	 * @return Returns the instance of the HandlerDirectory or null if the handler layer isn't loaded
	 */
	public abstract HandlerDirectory getHandlerDirectory();

	/**
	 * Returns a all platform configuration properties defined in the web.xml of the servlet container
	 * @return
	 */
	public abstract PlatformProperties getPlatformProperties();
	
	/**
	 * Initializes the PlatformInstance  
	 * @param parameters Implementation specific parameters needed to initialize the platform
	 */
	public abstract void bootInstance(Object ... parameters);
	
	/**
	 * Returns a file object for the given path
	 * @param path The path of the file
	 * @return
	 */
	public abstract File getFile(String path);
	
	/**
	 * Stop the platform and all its components
	 */
	public abstract void shutdownInstance();

}
