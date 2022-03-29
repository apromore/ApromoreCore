/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.util;

import java.io.File;


/**
 * This class provides runtime utilities for library components.
 * Its main purpose is to identify the host OS, and to locate
 * a standard support folder location on each platform.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XRuntimeUtils {
	
	/**
	 * Version string for the supported XES standard.
	 */
	public static final String XES_VERSION = "2.0";
	/**
	 * Version string for the OpenXES library implementation.
	 */
	public static final String OPENXES_VERSION = "2.27";
	
	
	/**
	 * Enum for defining host platforms.
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 *
	 */
	public enum OS {
		WIN32,
		MACOSX,
		MACOSCLASSIC,
		LINUX,
		BSD,
		RISCOS,
		BEOS,
		UNKNOWN
	};
	
	/**
	 * Current host platform.
	 */
	public static OS currentOs = null;
	
	/**
	 * Determines the current host platform.
	 * 
	 * @return Current host platform.
	 */
	public static OS determineOS() {
		if(currentOs == null) {
			String osString = System.getProperty("os.name").trim().toLowerCase();
			if(osString.startsWith("windows")) {
				currentOs = OS.WIN32;
			} else if(osString.startsWith("mac os x")) {
				currentOs = OS.MACOSX;
			} else if(osString.startsWith("mac os")) {
				currentOs = OS.MACOSCLASSIC;
			} else if(osString.startsWith("risc os")) {
				currentOs = OS.RISCOS;
			} else if( 
					  (osString.indexOf("linux")>-1)
				   || (osString.indexOf("debian")>-1)
				   || (osString.indexOf("redhat")>-1)
				   || (osString.indexOf("lindows")>-1) ) {
				currentOs = OS.LINUX;
			} else if( 
					  (osString.indexOf("freebsd")>-1)
				   || (osString.indexOf("openbsd")>-1)
				   || (osString.indexOf("netbsd")>-1)
				   || (osString.indexOf("irix")>-1)
				   || (osString.indexOf("solaris")>-1)
				   || (osString.indexOf("sunos")>-1)
				   || (osString.indexOf("hp/ux")>-1)
				   || (osString.indexOf("risc ix")>-1)
				   || (osString.indexOf("dg/ux")>-1) ) {
				currentOs = OS.BSD;
			} else if(osString.indexOf("beos")>-1) {
				currentOs = OS.BEOS;
			} else {
				currentOs = OS.UNKNOWN;
			}
		}
		return currentOs;
	}
	
	/**
	 * Checks whether the current platform is Windows.
	 */
	public static boolean isRunningWindows() {
		return XRuntimeUtils.determineOS().equals(OS.WIN32);
	}
	
	/**
	 * Checks whether the current platform is Mac OS X.
	 */
	public static boolean isRunningMacOsX() {
		return XRuntimeUtils.determineOS().equals(OS.MACOSX);
	}
	
	/**
	 * Checks whether the current platform is Linux.
	 */
	public static boolean isRunningLinux() {
		return XRuntimeUtils.determineOS().equals(OS.LINUX);
	}
	
	/**
	 * Checks whether the current platform is some flavor of Unix.
	 */
	public static boolean isRunningUnix() {
		OS os = XRuntimeUtils.determineOS();
		if(     os.equals(OS.BSD) ||
				os.equals(OS.LINUX) ||
				os.equals(OS.MACOSX) ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Retrieves the path of the platform-dependent OpenXES
	 * support folder.
	 */
	public static String getSupportFolder() {
		String homedir = System.getProperty("user.home");
		String dirName = "OpenXES";
		if(isRunningWindows()) {
			// any windows flavor
			(new File(homedir + "\\" + dirName)).mkdirs();	// create directories if necessary
			return homedir + "\\" + dirName + "\\";
		} else if(isRunningMacOsX()) {
			// hey, it's a mac!
			(new File(homedir + "/Library/Application Support/" + dirName)).mkdirs();
			return homedir + "/Library/Application Support/" + dirName + "/";
		} else {
			// most likely Linux or any other *NIX
			(new File(homedir + "/." + dirName)).mkdirs();	// create directories if necessary
			return homedir + "/." + dirName + "/";
		}
	}
	
	/**
	 * Retrieves the directory file of the platform-dependent OpenXES
	 * extension definition file folder.
	 */
	public static File getExtensionCacheFolder() {
		File extFolder = new File(getSupportFolder() + "ExtensionCache");
		extFolder.mkdirs();
		return extFolder;
	}
}
