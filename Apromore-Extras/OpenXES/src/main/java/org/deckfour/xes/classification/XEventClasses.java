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
package org.deckfour.xes.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * A set of event classes. For any log, this class can be used to impose a
 * classification of events. Two events which belong to the same event class can
 * be considered equal, i.e. to refer to the same higher-level concept they
 * represent (e.g., an activity).
 * 
 * Event classes are imposed on a log by a specific classifier. This class can
 * be configured with such a classifier, which is then used to derive the actual
 * event classes from a log, by determining the identity of the contained
 * events.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XEventClasses {

	/**
	 * Creates a new set of event classes, factory method.
	 * 
	 * @param classifier
	 *            The classifier to be used for event comparison.
	 * @param log
	 *            The log, on which event classes should be imposed.
	 * @return A set of event classes, as an instance of this class.
	 */
	public static synchronized XEventClasses deriveEventClasses(
			XEventClassifier classifier, XLog log) {
		XEventClasses nClasses = new XEventClasses(classifier);
		nClasses.register(log);
		nClasses.harmonizeIndices();
		return nClasses;
	}

	/**
	 * The classifier used for creating the set of event classes.
	 */
	protected XEventClassifier classifier;
	/**
	 * Map holding the event classes, indexed by their unique identifier string.
	 */
	protected UnifiedMap<String, XEventClass> classMap;

	/**
	 * Creates a new instance, i.e. an empty set of event classes.
	 * 
	 * @param classifier
	 *            The classifier used for event comparison.
	 */
	public XEventClasses(XEventClassifier classifier) {
		this.classifier = classifier;
		this.classMap = new UnifiedMap<String, XEventClass>();
	}

	/**
	 * Returns the classifier used for determining event classes.
	 * 
	 * @return A classifier used in this set of classes.
	 */
	public XEventClassifier getClassifier() {
		return classifier;
	}

	/**
	 * Returns the collection of event classes contained in this instance.
	 * 
	 * @return A collection of event classes.
	 */
	public Collection<XEventClass> getClasses() {
		return classMap.values();
	}

	/**
	 * Returns the size of this set of event classes.
	 * 
	 * @return The number of event classes contained in this set.
	 */
	public int size() {
		return classMap.size();
	}

	/**
	 * For any given event, returns the corresponding event class as determined
	 * by this set.
	 * 
	 * @param event
	 *            The event of which the event class should be determined.
	 * @return The event class of this event, as found in this set of event
	 *         classes. If no matching event class is found, this method may
	 *         return <code>null</code>.
	 */
	public XEventClass getClassOf(XEvent event) {
		return classMap.get(classifier.getClassIdentity(event));
	}

	/**
	 * Returns a given event class by its identity, i.e. its unique identifier
	 * string.
	 * 
	 * @param classIdentity
	 *            Identifier string of the requested event class.
	 * @return The requested event class. If no matching event class is found,
	 *         this method may return <code>null</code>.
	 */
	public XEventClass getByIdentity(String classIdentity) {
		return classMap.get(classIdentity);
	}

	/**
	 * Returns a given event class by its unique index.
	 * 
	 * @param index
	 *            Unique index of the requested event class.
	 * @return The requested event class. If no matching event class is found,
	 *         this method may return <code>null</code>.
	 */
	public XEventClass getByIndex(int index) {
		for (XEventClass eventClass : classMap.values()) {
			if (eventClass.getIndex() == index) {
				return eventClass;
			}
		}
		return null;
	}

	/**
	 * Registers a log with this set of event classes. This will result in all
	 * events of this log being analyzed, and potentially new event classes
	 * being added to this set of event classes. Event classes will be
	 * incremented in size, as new members of these classes are found among the
	 * events in the log.
	 * 
	 * @param log
	 *            The log to be analyzed.
	 */
	public void register(XLog log) {
		for (XTrace trace : log) {
			register(trace);
		}
	}

	/**
	 * Registers a trace with this set of event classes. This will result in all
	 * events of this trace being analyzed, and potentially new event classes
	 * being added to this set of event classes. Event classes will be
	 * incremented in size, as new members of these classes are found among the
	 * events in the trace.
	 * 
	 * @param trace
	 *            The trace to be analyzed.
	 */
	public void register(XTrace trace) {
		for (XEvent event : trace) {
			register(event);
		}
	}

	/**
	 * Registers an event with this set of event classes. This will potentially
	 * add a new event class to this set of event classes. An event class will
	 * be incremented in size, if the given event is found to be a member of it.
	 * 
	 * @param event
	 *            The event to be analyzed.
	 */
	public void register(XEvent event) {
		register(classifier.getClassIdentity(event));
	}
		
	/**
	 * Registers an event class with this set of event classes. This will potentially
	 * add a new event class to this set of event classes. An event class will
	 * be incremented in size, if the given event is found to be a member of it.
	 * 
	 * @param classId
	 *            The event class to be analyzed.
	 */
	public synchronized void register(String classId) {
		XEventClass eventClass = classMap.get(classId);
		if (eventClass == null && classId != null) {
			eventClass = new XEventClass(classId, classMap.size());
			classMap.put(classId, eventClass);
		}
		if (eventClass != null) {
			eventClass.incrementSize();
		}
	}

	/**
	 * This method harmonizeds the indices of all contained event classes.
	 * Indices are re-assigned according to the natural order of class
	 * identities, i.e., the alphabetical order of class identity strings. This
	 * method should be called after the composition or derivation of event
	 * classes is complete, e.g., after scanning a log for generating the log
	 * info. Using parties should not have to worry about event class
	 * harmonization, and can thus safely ignore this method.
	 */
	public synchronized void harmonizeIndices() {
		ArrayList<XEventClass> classList = new ArrayList<XEventClass>(classMap
				.values());
		Collections.sort(classList);
		classMap.clear();
		for (int i = 0; i < classList.size(); i++) {
			XEventClass original = classList.get(i);
			XEventClass harmonized = new XEventClass(original.getId(), i);
			harmonized.setSize(original.size());
			classMap.put(harmonized.getId(), harmonized);
		}
	}

	/**
	 * Equality of event classes is based on their classifier, i.e., if two
	 * event classes have the same classifier, they are considered as equal in
	 * terms of this method.
	 */
	public boolean equals(Object o) {
		if (o instanceof XEventClasses) {
			return ((XEventClasses) o).getClassifier().equals(this.classifier);
		} else {
			return false;
		}
	}

	public String toString() {
		return "Event classes defined by " + classifier.name();
	}

}
