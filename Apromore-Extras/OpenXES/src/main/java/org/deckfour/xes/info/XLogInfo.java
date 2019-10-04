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
package org.deckfour.xes.info;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * This interface defines a bare-bones log summary.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public interface XLogInfo {

	/**
	 * Retrieves the log used for this summary.
	 * 
	 * @return The event log which this summary describes.
	 */
	public abstract XLog getLog();

	/**
	 * Retrieves the total number of events in this log.
	 * 
	 * @return Total number of events.
	 */
	public abstract int getNumberOfEvents();

	/**
	 * Retrieves the number of traces in this log.
	 * 
	 * @return Number of traces available in this log.
	 */
	public abstract int getNumberOfTraces();
	
	/**
	 * Retrieves the set of event classifiers covered by
	 * this log info, i.e., for which event classes are
	 * registered in this log info instance.
	 * 
	 * @return The collection of event classifiers covered
	 * by this log info instance.
	 */
	public abstract Collection<XEventClassifier> getEventClassifiers();
	
	/**
	 * Retrieves the event classes for a given classifier.
	 * <p><b>Note:</b>The given event classifier must be
	 * covered by this log info, i.e., the log info must
	 * have been created with this classifier. Otherwise,
	 * this method will return <code>null</code>. You can
	 * retrieve the collection of event classifiers covered
	 * by this log info instance by calling the method
	 * <code>getEventClassifiers()</code>.
	 * 
	 * @param classifier The classifier for which to retrieve
	 * the event classes.
	 * @return The requested event classes, or <code>null</code>
	 * if the given event classifier is not covered by this
	 * log info instance.
	 */
	public abstract XEventClasses getEventClasses(XEventClassifier classifier);

	/**
	 * Retrieves the event classes of the summarized log,
	 * as defined by the event classifier used for this
	 * summary.
	 * 
	 * @return The event classes of the summarized log.
	 */
	public abstract XEventClasses getEventClasses();

	/**
	 * Retrieves the resource classes of the summarized log.
	 * 
	 * @return The resource classes of the summarized log.
	 */
	public abstract XEventClasses getResourceClasses();

	/**
	 * Retrieves the event name classes of the summarized log.
	 * 
	 * @return The event name classes of the summarized log.
	 */
	public abstract XEventClasses getNameClasses();

	/**
	 * Retrieves the lifecycle transition classes of the summarized log.
	 * 
	 * @return The lifecycle transition classes of the summarized log.
	 */
	public abstract XEventClasses getTransitionClasses();

	/**
	 * Retrieves the global timestamp boundaries of this log.
	 * 
	 * @return Timestamp boundaries for the complete log.
	 */
	public abstract XTimeBounds getLogTimeBoundaries();

	/**
	 * Retrieves the timestamp boundaries for a specified trace.
	 * 
	 * @param trace Trace to be queried for.
	 * @return Timestamp boundaries for the indicated trace.
	 */
	public abstract XTimeBounds getTraceTimeBoundaries(XTrace trace);
	
	/**
	 * Retrieves attribute information about all attributes
	 * this log contains on the log level.
	 * 
	 * @return Attribute information on the log level.
	 */
	public abstract XAttributeInfo getLogAttributeInfo();
	
	/**
	 * Retrieves attribute information about all attributes
	 * this log contains on the trace level.
	 * 
	 * @return Attribute information on the trace level.
	 */
	public abstract XAttributeInfo getTraceAttributeInfo();
	
	/**
	 * Retrieves attribute information about all attributes
	 * this log contains on the event level.
	 * 
	 * @return Attribute information on the event level.
	 */
	public abstract XAttributeInfo getEventAttributeInfo();
	
	/**
	 * Retrieves attribute information about all attributes
	 * this log contains on the meta (i.e., attribute) level.
	 * 
	 * @return Attribute information on the meta level.
	 */
	public abstract XAttributeInfo getMetaAttributeInfo();

	/**
	 * Returns a string representation of this summary.
	 */
	public abstract String toString();

}