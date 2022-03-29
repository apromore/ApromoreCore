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
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
package org.deckfour.xes.extension;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XVisitor;

/**
 * This class defines and implements extensions to the basic log meta-model.
 * 
 * Extensions have a name, a defined prefix, and a unique URI. They can define
 * additional, typed attributes on the level of the log, trace, and event. Also,
 * extensions may define meta attributes, i.e. attributes which describe other,
 * higher-level attributes.
 * 
 * Implementations of XExtension as supposed to implement the Singleton pattern.
 * The method instance should return the instance, which is stored in a static
 * field.
 * 
 * Note that implementing classes have to properly implement a private Object
 * readResolve(); in order to get a pointer to the right instance uppon
 * deserialization.
 * 
 * A default implementation of this method would be:
 * <code> protected Object readResolve() { return singleton; } </code>
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XExtension implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -677323212952951508L;

	/**
	 * The name of the extension.
	 */
	protected String name;
	/**
	 * Prefix string of the extension, used for addressing attributes.
	 */
	protected String prefix;
	/**
	 * Unique URI of the extension. This URI should point to the file defining
	 * the extension, and must be able to be resolved. Extension files should be
	 * accessible over the internet, e.g. stored on web servers.
	 */
	protected URI uri;
	/**
	 * Set containing all attributes defined by this extension, on all possible
	 * levels of abstraction.
	 */
	protected UnifiedSet<XAttribute> allAttributes;
	/**
	 * Set containing all attributes defined by this extension on the level of
	 * logs.
	 */
	protected UnifiedSet<XAttribute> logAttributes;
	/**
	 * Set containing all attributes defined by this extension on the level of
	 * traces.
	 */
	protected UnifiedSet<XAttribute> traceAttributes;
	/**
	 * Set containing all attributes defined by this extension on the level of
	 * events.
	 */
	protected UnifiedSet<XAttribute> eventAttributes;
	/**
	 * Set containing all meta-attributes defined by this extension, i.e. on the
	 * level of attributes.
	 */
	protected UnifiedSet<XAttribute> metaAttributes;

	/**
	 * Creates a new extension instance.
	 * 
	 * @param name
	 *            Name of the extension.
	 * @param prefix
	 *            Prefix string to be used for this extension.
	 * @param uri
	 *            Unique URI used for identifying this extension. This URI
	 *            should point to the file defining the extension, and must be
	 *            able to be resolved. Extension files should be accessible over
	 *            the internet, e.g. stored on web servers.
	 */
	protected XExtension(String name, String prefix, URI uri) {
		this.name = name;
		this.prefix = prefix;
		this.uri = uri;
		this.allAttributes = null; // created on demand
		this.logAttributes = new UnifiedSet<XAttribute>();
		this.traceAttributes = new UnifiedSet<XAttribute>();
		this.eventAttributes = new UnifiedSet<XAttribute>();
		this.metaAttributes = new UnifiedSet<XAttribute>();
	}

	/**
	 * Returns the human-readable name of this extension.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a unique URI associated with this extension. This URI should
	 * point to the file defining the extension, and must be able to be
	 * resolved. Extension files should be accessible over the internet, e.g.
	 * stored on web servers.
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Returns a unique prefix associated with this extension. This prefix
	 * should be no longer than 5 characters, so as not to unnecessarily blow up
	 * storage files.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the collection of attributes defined by this extension for any
	 * log elements (archive-, log-, trace-, event-, and meta-attributes).
	 */
	public Collection<XAttribute> getDefinedAttributes() {
		if (allAttributes == null) {
			// create collection on demand
			allAttributes = new UnifiedSet<XAttribute>();
			allAttributes.addAll(getLogAttributes());
			allAttributes.addAll(getTraceAttributes());
			allAttributes.addAll(getEventAttributes());
			allAttributes.addAll(getEventAttributes());
		}
		return allAttributes;
	}

	/**
	 * Returns the collection of attributes defined by this extension for log
	 * elements.
	 */
	public Collection<XAttribute> getLogAttributes() {
		return logAttributes;
	}

	/**
	 * Returns the collection of attributes defined by this extension for trace
	 * elements.
	 */
	public Collection<XAttribute> getTraceAttributes() {
		return traceAttributes;
	}

	/**
	 * Returns the collection of attributes defined by this extension for event
	 * elements.
	 */
	public Collection<XAttribute> getEventAttributes() {
		return eventAttributes;
	}

	/**
	 * Returns the collection of meta-attributes defined by this extension for
	 * attributes.
	 */
	public Collection<XAttribute> getMetaAttributes() {
		return metaAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XExtension) {
			return uri.equals(((XExtension) obj).uri);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/*
	 * Runs the given visitor for the given log on this extension.
	 */
	public void accept(XVisitor visitor, XLog log) {
		/*
		 * First call.
		 */
		visitor.visitExtensionPre(this, log);
		/*
		 * Last call.
		 */
		visitor.visitExtensionPost(this, log);
	}
}
