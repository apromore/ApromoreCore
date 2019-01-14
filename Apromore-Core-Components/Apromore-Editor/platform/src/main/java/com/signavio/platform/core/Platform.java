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
