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
