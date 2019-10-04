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
package org.deckfour.xes.logging;

/**
 * This class provides low-level logging for library
 * components. Used for debugging.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XLogging {
	
	/**
	 * Defines the importance of logging messages.
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 *
	 */
	public enum Importance {
		DEBUG, INFO, WARNING, ERROR;
	}
	
	/**
	 * Logging listener for receiving log messages.
	 */
	private static XLoggingListener listener = new XStdoutLoggingListener();
	
	/**
	 * Sets a new logging listener.
	 * 
	 * @param listener New logging listener.
	 */
	public static void setListener(XLoggingListener listener) {
		XLogging.listener = listener;
	}
	
	/**
	 * Logs the given message with debug importance.
	 * 
	 * @param message Message to be logged.
	 */
	public static void log(String message) {
		log(message, Importance.DEBUG);
	}
	
	/**
	 * Logs a message.
	 * 
	 * @param message Log message.
	 * @param importance Message importance.
	 */
	public static void log(String message, Importance importance) {
		if(listener != null) {
			listener.log(message, importance);
		}
	}

}
