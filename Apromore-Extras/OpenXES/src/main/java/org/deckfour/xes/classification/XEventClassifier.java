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

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XVisitor;

/**
 * This interface defines a classification of events.
 * It assigns to each event instance a class identity,
 * thereby imposing an equality relation on the set of events.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public interface XEventClassifier {

	/**
	 * Returns the name of this comparator
	 */
	public String name();
	
	/**
	 * Assigns a custom name to this classifier
	 * 
	 * @param name Name to be assigned to this classifier.
	 */
	public void setName(String name);
	
	/**
	 * Checks whether two event instances correspond to
	 * the same event class, i.e. are equal in that sense.
	 */
	public boolean sameEventClass(XEvent eventA, XEvent eventB);
	
	/**
	 * Retrieves the unique class identity string of a given event.
	 */
	public String getClassIdentity(XEvent event);
	
	/**
	 * Retrieves the set of attribute keys which are used
	 * in this event classifier (May be used for the construction
	 * of events that are not part of an existing event class).
	 * 
	 * @return A set of attribute keys, which are
	 * used for defining this classifier.
	 */
	public String[] getDefiningAttributeKeys();
	
	/**
	 * Runs the given visitor for the given log on this classifier.
	 */
	public void accept(XVisitor visior, XLog log);
}
