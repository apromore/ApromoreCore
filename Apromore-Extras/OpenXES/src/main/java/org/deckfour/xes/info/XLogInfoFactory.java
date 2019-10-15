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

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;

/**
 * Factory for deriving log info summaries from logs.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XLogInfoFactory {
	
	/**
	 * Creates a new log info with the standard event classifier.
	 * 
	 * @param log The event log to create an info summary for.
	 * @return The log info for this log.
	 */
	public static XLogInfo createLogInfo(XLog log) {
		return createLogInfo(log, XLogInfoImpl.STANDARD_CLASSIFIER);
	}
	
	/**
	 * Creates a new log info summary with a custom event classifier.
	 * 
	 * @param log The event log to create an info summary for.
	 * @param classifier The event classifier to be used.
	 * @return The log info summary for this log.
	 */
	public static XLogInfo createLogInfo(XLog log, XEventClassifier classifier) {
		/*
		 * Get the possible info cached by the log.
		 */
		XLogInfo info = log.getInfo(classifier);
		if (info == null) {
			/*
			 * Info not cached. Create it.
			 */
			info = XLogInfoImpl.create(log, classifier);
			/*
			 * Cache it.
			 */
			log.setInfo(classifier, info);
		}
		return info;
	}

}
