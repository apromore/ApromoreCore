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
package org.deckfour.xes.factory;

import org.deckfour.xes.util.XRegistry;

/**
 * XModelFactoryRegistry is the most important integration point for external
 * contributors, aside from the extension infrastructure.
 * 
 * This singleton class serves as a system-wide registry for XES factory
 * implementations. It provides a current, i.e. standard, factory implementation,
 * which can be switched by applications. This factory will be used in any
 * internal places, e.g., for creating models from reading XES serializations.
 * 
 * Other, e.g. proprietary or domain-specific, implementations of the XES
 * standard (and the OpenXES model hierarchy interface) are suggested to implement
 * the XModelFactory interface, and to register their factory with this registry.
 * This enables to transparently switch the storage implementation of the complete
 * OpenXES system (wherever applicable), and every application making use of this
 * registry to create new models.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XFactoryRegistry extends XRegistry<XFactory> {
	
	/**
	 * Singleton registry instance.
	 */
	private static XFactoryRegistry singleton = new XFactoryRegistry();
	
	/**
	 * Retrieves the singleton registry instance.
	 */
	public static XFactoryRegistry instance() {
		return singleton;
	}
	
	/**
	 * Creates the singleton.
	 */
	private XFactoryRegistry() {
		super();
//		register(new XFactoryNaiveImpl());
//		setCurrentDefault(new XFactoryBufferedImpl());
		setCurrentDefault(new XFactoryNaiveImpl());
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.util.XRegistry#areEqual(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean areEqual(XFactory a, XFactory b) {
		return a.getClass().equals(b.getClass());
	}
	
	
}
