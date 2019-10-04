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
package org.deckfour.xes.model;

import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;

/**
 * A log is an element of an XES event log structure. Logs are contained in
 * archives. Any log is a list of traces.
 * 
 * Logs represent a collection of traces, which are all representing executions
 * of the same kind of process.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public interface XLog extends XElement, List<XTrace> {

	/**
	 * This method returns the list of classifiers defined for this log. This
	 * list can be used for reading or writing, i.e., it must be supported to
	 * add further classifiers to this list.
	 * 
	 * @return The list of classifiers defined for this log.
	 */
	public List<XEventClassifier> getClassifiers();

	/**
	 * This method returns a list of attributes which are global for all traces,
	 * i.e. every trace in the log is guaranteed to have these attributes.
	 * 
	 * @return List of ubiquitous trace attributes.
	 */
	public List<XAttribute> getGlobalTraceAttributes();

	/**
	 * This method returns a list of attributes which are global for all events,
	 * i.e. every event in the log is guaranteed to have these attributes.
	 * 
	 * @return List of ubiquitous event attributes.
	 */
	public List<XAttribute> getGlobalEventAttributes();

	public boolean accept(XVisitor visitor);
	
	/**
	 * Returns the cached info for the given classifier, null if not available.
	 * 
	 * @param classifier The given classifier.
	 * @return The cached info for the given classifier, null if not available.
	 */
	public XLogInfo getInfo(XEventClassifier classifier);
	
	/**
	 * Adds the given info for the given classifier to the info cache.
	 * 
	 * @param classifier The given classifier.
	 * @param info The given info.
	 */
	public void setInfo(XEventClassifier classifier, XLogInfo info);

}
