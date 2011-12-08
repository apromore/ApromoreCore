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

import java.lang.reflect.Constructor;

import com.signavio.platform.exceptions.InitializationException;

/**
 * The Platform class stores the active {@link PlatformInstance} and provides an interface for 
 * booting and stopping the platform
 * @author Bjoern Wagner
 *
 */
public class Platform  {
	
	private static PlatformInstance INSTANCE;

	/**
	 * Returns the active {@link PlatformInstance}
	 * @throws InitializationException if no platform is running
	 */
	public static PlatformInstance getInstance() {
		
		if (INSTANCE != null) {
			return INSTANCE;
		} else {
			throw new InitializationException("Platform not running!");
		}
	}
	
	/**
	 * Returns true if an {@link PlatformInstance} is running.
	 * @return
	 */
	public static boolean isRunning() {
		return INSTANCE != null;
	}
	
	/**
	 * Bootstraps a new Platform instance
	 * @param instanceImpl The actual implementation class of the new instance
	 * @param parameters Implementation specific parameters that will be used to 
	 * initialize the {@link PlatformInstance}
	 * @throws InitializationException if another instance is already running
	 */
	public static <T extends PlatformInstance> T  bootInstance(Class<T> instanceImpl, 
			Object ... parameters) throws InitializationException {
		
		try {
			if (isRunning()) {
				throw new InitializationException("Platform boot failed! Another PlatformInstance is already running. ");
			}
			// create a new instance of the platform and boot with the parameters
			Constructor<T> constructor = instanceImpl.getConstructor(new Class<?>[0]);
			T platformInstance = constructor.newInstance(new Object[0]);
			INSTANCE = platformInstance;
			platformInstance.bootInstance(parameters);
			return platformInstance;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InitializationException("Platform boot failed!", e);
		} 
	}
	
	/**
	 * Shutdown the running {@link PlatformInstance}. This call is ignored if no instance
	 * is running.
	 */
	public static void shutdownInstance() {
		if (isRunning()) {
			INSTANCE.shutdownInstance();
			INSTANCE = null;
		}
	}
}
