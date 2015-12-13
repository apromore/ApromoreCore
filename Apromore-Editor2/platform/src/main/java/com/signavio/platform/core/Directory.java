/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
/**
 * 
 */
package com.signavio.platform.core;

/**
 * Interface for all Platform directories 
 * @author Bjoern Wagner
 *
 */
public interface Directory {

	/**
	 * This method is called during the bootstrapping of the platform and can be used to
	 * initialize the directory.
	 */
	public void start();
	
	/**
	 * This method is called during the shutdown of the platform and can be used to 
	 * free resources.
	 */
	public void stop();
}
