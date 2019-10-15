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
package org.deckfour.xes.info.impl;

import java.util.Date;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XTimeBounds;
import org.deckfour.xes.model.XEvent;

/**
 * This class implements timestamp boundaries, which can be
 * used to describe the temporal extent of a log, or of a
 * contained trace.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XTimeBoundsImpl implements XTimeBounds {
	
	/**
	 * The earliest timestamp of these boundaries (left bound).
	 */
	protected Date first;
	/**
	 * The latest timestamp of these boundaries (right bound).
	 */
	protected Date last;
	
	/**
	 * Creates new timestamp boundaries.
	 */
	public XTimeBoundsImpl() {
		first = null;
		last = null;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XTimeBoundaries#getStartDate()
	 */
	public Date getStartDate() {
		return first;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XTimeBoundaries#getEndDate()
	 */
	public Date getEndDate() {
		return last;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XTimeBoundaries#isWithin(java.util.Date)
	 */
	public boolean isWithin(Date date) {
		if(first == null) {
			return false;
		} else if(date.equals(first)) {
			return true;
		} else if(date.equals(last)) {
			return true;
		} else if(date.after(first) && date.before(last)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Registers the given event, i.e. if it has a timestamp, the
	 * timestamp boundaries will be potentially adjusted to accomodate
	 * for inclusion of this event.
	 * 
	 * @param event Event to be registered.
	 */
	public void register(XEvent event) {
		Date date = XTimeExtension.instance().extractTimestamp(event);
		if(date != null) {
			register(date);
		}
	}
	
	/**
	 * Registers the given date. The timestamp boundaries will be 
	 * potentially adjusted to accomodate for inclusion of this date.
	 * 
	 * @param date Date to be registered.
	 */
	public void register(Date date) {
		if(date != null) {
			if(first == null) {
				// initialization
				first = date;
				last = date;
			} else if(date.before(first)) {
				first = date;
			} else if(date.after(last)) {
				last = date;
			}
		}
	}
	
	/**
	 * Registers the given timestamp boundaries. 
	 * These timestamp boundaries will be potentially adjusted to 
	 * accomodate for inclusion of the given boundaries.
	 * 
	 * @param date Timestamp boundaries to be registered.
	 */
	public void register(XTimeBounds boundary) {
		register(boundary.getStartDate());
		register(boundary.getEndDate());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XTimeBoundaries#toString()
	 */
	public String toString() {
		return first.toString() + " -- " + last.toString();
	}

}
