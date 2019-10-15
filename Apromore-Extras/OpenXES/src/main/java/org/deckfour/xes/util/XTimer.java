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
package org.deckfour.xes.util;

/**
 * This class implements a simple timer that can
 * be used to quickly profile the speed of operations
 * within library components.
 * 
 * The timer simply uses the system time for timing,
 * and thus does not incur significant overhead on
 * runtime.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XTimer {

	/**
	 * Milliseconds in one day.
	 */
	public static final long DAY_MILLIS = 86400000;
	/**
	 * Milliseconds in one hour.
	 */
	public static final long HOUR_MILLIS = 3600000;
	/**
	 * Milliseconds in one minute.
	 */
	public static final long MINUTE_MILLIS = 60000;
	/**
	 * Milliseconds in one second.
	 */
	public static final long SECOND_MILLIS = 1000;
	
	/**
	 * Start time of timer.
	 */
	protected long start;
	/**
	 * Stop time of timer.
	 */
	protected long stop;
	
	/**
	 * Creates a new timer.
	 * (starts running implicitly)
	 */
	public XTimer() {
		start = System.currentTimeMillis();
		stop = start;
	}
	
	/**
	 * Starts the timer.
	 */
	public void start() {
		start = System.currentTimeMillis();
		stop = start;
	}
	
	/**
	 * Stops the timer (takes time).
	 */
	public void stop() {
		stop = System.currentTimeMillis();
	}
	
	/**
	 * Retrieve the runtime of the timer.
	 * 
	 * @return Runtime between start (or creation of 
	 * timer) and stop, in milliseconds.
	 */
	public long getDuration() {
		if(start == stop) {
			return System.currentTimeMillis() - start;
		} else {
			return stop - start;
		}
	}
	
	/**
	 * Retrieve the runtime of the timer as
	 * a pretty-print string.
	 * 
	 * @return Runtime between start (or creation of 
	 * timer) and stop, as a pretty-print string.
	 */
	public String getDurationString() {
		return formatDuration(getDuration());
	}
	
	/**
	 * Formats a duration in milliseconds as
	 * a pretty-print string.
	 * 
	 * @param millis Duration in milliseconds.
	 * @return Given duration as a pretty-print string.
	 */
	public static String formatDuration(long millis) {
		StringBuilder sb = new StringBuilder();
		if(millis > DAY_MILLIS) {
			sb.append(millis / DAY_MILLIS);
			sb.append(" days, ");
			millis %= DAY_MILLIS;
		}
		if(millis > HOUR_MILLIS) {
			sb.append(millis / HOUR_MILLIS);
			sb.append(" hours, ");
			millis %= HOUR_MILLIS;
		}
		if(millis > MINUTE_MILLIS) {
			sb.append(millis / MINUTE_MILLIS);
			sb.append(" minutes, ");
			millis %= MINUTE_MILLIS;
		}
		if(millis > SECOND_MILLIS) {
			sb.append(millis / SECOND_MILLIS);
			sb.append(" seconds, ");
			millis %= SECOND_MILLIS;
		}
		sb.append(millis);
		sb.append(" milliseconds");
		return sb.toString();
	}

}
