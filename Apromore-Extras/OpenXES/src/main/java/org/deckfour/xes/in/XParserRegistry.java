/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
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
package org.deckfour.xes.in;

import org.deckfour.xes.util.XRegistry;

/**
 * System-wide registry for XES parser implementations.
 * Applications can use this registry as a convenience
 * to provide an overview about parseable formats, e.g.,
 * in the user interface.
 * Any custom parser implementation can be registered
 * with this registry, so that it transparently becomes
 * available also to any other using application.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XParserRegistry extends XRegistry<XParser> {
	
	/**
	 * Singleton registry instance.
	 */
	private static XParserRegistry singleton = new XParserRegistry();
	
	/**
	 * Retrieves the singleton registry instance.
	 */
	public static XParserRegistry instance() {
		return singleton;
	}
	
	/**
	 * Creates the singleton.
	 */
	private XParserRegistry() {
		super();
		register(new XMxmlParser());
		register(new XMxmlGZIPParser());
		register(new XesXmlParser());
		setCurrentDefault(new XesXmlGZIPParser());
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.util.XRegistry#areEqual(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean areEqual(XParser a, XParser b) {
		return a.getClass().equals(b.getClass());
	}

}
