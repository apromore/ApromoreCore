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
 * Copyright (c) 2013 F. Mannhardt (f.mannhardt@tue.nl)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package org.deckfour.xes.util;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.deckfour.xes.util.XsDateTimeConversion;

/**
 * Provides a faster conversion of DateTime for XES serialization using the new
 * parse patterns of the SimpleDateFormat class in Java 7
 * 
 * @author F. Mannhardt
 */
public class XsDateTimeConversionJava7 extends XsDateTimeConversion {

	public static final boolean SUPPORTS_JAVA7_DATE_FORMAT;

	static {
		boolean biggerEqualJava7 = false;
		String[] splittedVersion = System.getProperty("java.version").split("\\.");
		if (splittedVersion.length > 1) {			
			try {
				biggerEqualJava7 = Integer.parseInt(splittedVersion[1]) > 6;
			} catch (NumberFormatException e) {
				biggerEqualJava7 = false;
			}
		}
		SUPPORTS_JAVA7_DATE_FORMAT = biggerEqualJava7;
	}	
	
	private static final ThreadLocal<SoftReference<DateFormat>> THREAD_LOCAL_DF_WITH_MILLIS = new ThreadLocal<SoftReference<DateFormat>>();
	private static final ThreadLocal<SoftReference<DateFormat>> THREAD_LOCAL_DF_WITHOUT_MILLIS = new ThreadLocal<SoftReference<DateFormat>>();

	/**
	 * Returns a DateFormat for each calling thread, using {@link ThreadLocal}.
	 * 
	 * @return a DateFormat that is safe to use in multi-threaded environments
	 */
	private static DateFormat getDateFormatWithMillis() {
		return getThreadLocaleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
				THREAD_LOCAL_DF_WITH_MILLIS);
	}

	/**
	 * Returns a DateFormat for each calling thread, using {@link ThreadLocal}.
	 * 
	 * @return a DateFormat that is safe to use in multi-threaded environments
	 */
	private static DateFormat getDateFormatWithoutMillis() {
		return getThreadLocaleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX",
				THREAD_LOCAL_DF_WITHOUT_MILLIS);
	}

	private static DateFormat getThreadLocaleDateFormat(String formatString,
			ThreadLocal<SoftReference<DateFormat>> threadLocal) {
		if (SUPPORTS_JAVA7_DATE_FORMAT) {
			SoftReference<DateFormat> softReference = threadLocal.get();
			if (softReference != null) {
				DateFormat dateFormat = softReference.get();
				if (dateFormat != null) {
					return dateFormat;
				}
			}
			DateFormat result = new SimpleDateFormat(formatString, Locale.US);
			softReference = new SoftReference<DateFormat>(result);
			threadLocal.set(softReference);
			return result;
		} else {
			throw new RuntimeException(
					"Error parsing XES log. This method should not be called unless running on Java 7!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.util.XsDateTimeConversion#parseXsDateTime(java.lang.
	 * String)
	 */
	public Date parseXsDateTime(String xsDateTime) {
		// Try Java 7 parsing method
		if (SUPPORTS_JAVA7_DATE_FORMAT) {
			// Use with ParsePosition to avoid throwing and catching a lot of
			// exceptions, if our parsing method does not work
			ParsePosition position = new ParsePosition(0);
			Date parsedDate = getDateFormatWithMillis().parse(xsDateTime,
					position);
			if (parsedDate == null) {
				// Try format without milliseconds
				position.setIndex(0);
				position.setErrorIndex(0);
				parsedDate = getDateFormatWithoutMillis().parse(xsDateTime,
						position);
				if (parsedDate == null) {
					// Fallback to old Java 6 method
					return super.parseXsDateTime(xsDateTime);
				} else {
					return parsedDate;
				}
			} else {
				return parsedDate;
			}
		} else {
			// Fallback to old Java 6 method
			return super.parseXsDateTime(xsDateTime);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.util.XsDateTimeConversion#format(java.util.Date)
	 */
	@Override
	public String format(Date date) {
		if (SUPPORTS_JAVA7_DATE_FORMAT) {
			return getDateFormatWithMillis().format(date);
		} else {
			// Fallback to old Java 6 method
			return super.format(date);
		}
	}

}
