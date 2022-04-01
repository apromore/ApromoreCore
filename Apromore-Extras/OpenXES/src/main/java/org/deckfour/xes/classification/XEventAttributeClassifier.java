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
package org.deckfour.xes.classification;

import java.io.Serializable;
import java.util.Arrays;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XVisitor;

/**
 * Event classifier which considers two events as equal, if, for a set of given
 * (configurable) attributes, they have the same values.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XEventAttributeClassifier implements XEventClassifier,
		Comparable<XEventAttributeClassifier>, Serializable {

	/**
	 *  Used to connect multiple attributes
	 */
	private static final String CONCATENATION_SYMBOL = "+";

	/**
	 * 
	 */
	private static final long serialVersionUID = -2697438317286269727L;

	/**
	 * Keys of the attributes used for event comparison.
	 */
	protected String[] keys;

	/**
	 * Name of the classifier
	 */
	protected String name;

	/**
	 * Creates a new instance, configured by the given attribute.
	 * 
	 * @param name
	 *            Name of the classifier.
	 * @param attribute
	 *            Attribute to be used for event classification.
	 */
	public XEventAttributeClassifier(String name, String... keys) {
		this.name = name;
		this.keys = keys;
//		Arrays.sort(keys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.classification.XEventClassifier#getClassIdentity(org
	 * .deckfour.xes.model.XEvent)
	 */
	public String getClassIdentity(XEvent event) {
		switch (keys.length) {
			case 1:
				// Fast method in the case of 1 attribute
				XAttribute attr = event.getAttributes().get(keys[0]);
				if (attr != null) {
					return attr.toString();	
				} else {
					// Special case original method would have returned empty String so we keep doing it
					return "";
				}
	
			case 2:
				// A bit faster method avoiding allocation of a StringBuilder in the case of 2 attributes
				XAttribute attr1 = event.getAttributes().get(keys[0]);
				XAttribute attr2 = event.getAttributes().get(keys[1]);
				if (attr1 != null && attr2 != null) {
					String val1 = attr1.toString();
					String val2 = attr2.toString();
					return val1.concat(CONCATENATION_SYMBOL).concat(val2);					
				} else if (attr1 != null) {
					return attr1.toString().concat(CONCATENATION_SYMBOL);
				} else if (attr2 != null) {
					return CONCATENATION_SYMBOL.concat(attr2.toString());
				} else {
					return CONCATENATION_SYMBOL;
				}
	
			default:
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < keys.length; i++) {
					XAttribute attribute = event.getAttributes().get(keys[i]);
					// if (attribute == null) {
					// return null;
					// }
					if (attribute != null) {
						// FM, the "trim" does not make sense here. what if the
						// whitespace is added on purpose?
						// sb.append(attribute.toString().trim());
						sb.append(attribute.toString());
					}
					if (i < (keys.length - 1)) {
						sb.append(CONCATENATION_SYMBOL);
					}
				}
				return sb.toString();
		}
	}

	/**
	 * Assigns a custom name to this classifier
	 * 
	 * @param name
	 *            Name to be assigned to this classifier.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.classification.XEventClassifier#name()
	 */
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.classification.XEventClassifier#sameEventClass(org.deckfour
	 * .xes.model.XEvent, org.deckfour.xes.model.XEvent)
	 */
	public boolean sameEventClass(XEvent eventA, XEvent eventB) {
		return getClassIdentity(eventA).equals(getClassIdentity(eventB));
	}

	public String toString() {
		return name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.classification.XEventClassifier#getDefiningAttributeKeys
	 * ()
	 */
	public String[] getDefiningAttributeKeys() {
		return keys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(XEventAttributeClassifier o) {
		if (!o.name.equals(name)) {
			return name.compareTo(o.name);
		} else {
			if (keys.length != o.keys.length) {
				return keys.length - o.keys.length;
			}
			for (int i = 0; i < keys.length; i++) {
				if (!keys[i].equals(o.keys[i])) {
					return keys[i].compareTo(o.keys[i]);
				}
			}
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keys);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public boolean equals(Object o) {
		if (!(o instanceof XEventAttributeClassifier)) {
			return false;
		}
		return compareTo((XEventAttributeClassifier) o) == 0;
	}

	public void accept(XVisitor visitor, XLog log) {
		/*
		 * First call.
		 */
		visitor.visitClassifierPre(this, log);
		/*
		 * Last call.
		 */
		visitor.visitClassifierPost(this, log);
	}
}
