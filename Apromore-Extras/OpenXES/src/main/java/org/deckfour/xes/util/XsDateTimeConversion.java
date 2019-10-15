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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.bind.DatatypeConverter;

/**
 * This class serves as a provider for static xs:dateTime-related manipulation
 * and parsing methods.
 * 
 * @author Christian W. Guenther (christian at deckfour dot org)
 */
public class XsDateTimeConversion {

	/**
	 * Date/Time parsing format including milliseconds and time zone
	 * information.
	 */
	protected static final String XSDATETIME_FORMAT_STRING_MILLIS_TZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	/**
	 * Date/Time parsing instance with milliseconds and time zone information.
	 */
	protected final SimpleDateFormat dfMillisTZone = new SimpleDateFormat(
			XsDateTimeConversion.XSDATETIME_FORMAT_STRING_MILLIS_TZONE);

	/**
	 * Pattern used for matching the XsDateTime formatted timestamp strings.
	 */
	protected final Pattern xsDtPattern = Pattern
			.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{3}))?(.+)?");

	/**
	 * Calendar instance used for calculating dates for timestamps
	 */
	protected GregorianCalendar cal = new GregorianCalendar();

	/**
	 * Expects an XML xs:dateTime lexical format string, as in
	 * <code>2005-10-24T11:57:31.000+01:00</code>. Some bad MXML files miss
	 * timezone or milliseconds information, thus a certain amount of tolerance
	 * is applied towards malformed timestamp string representations. If
	 * unparseable, this method will return <code>null</code>.
	 * 
	 * @param xsDateTime
	 *            Timestamp string in the XML xs:dateTime format.
	 * @return Parsed Date object.
	 */
	public Date parseXsDateTime(String xsDateTime) {
		try {
			Calendar.getInstance().setLenient(true);
			Calendar cal = DatatypeConverter.parseDateTime(xsDateTime);
			return cal.getTime();
		} catch (IllegalArgumentException e) {
			/*
			 * Standard techniques do not get us a timestamp. For sake of
			 * leniency, try the former pattern-based approach.
			 */
			return parseXsDateTimeUsingPattern(xsDateTime);
		}
	}

	private Date parseXsDateTimeUsingPattern(String xsDateTime) {
		// try to parse with date format hack: Replace time zones like +01:00 to
		// +0100.
		if (xsDateTime.length() >= 6
				&& xsDateTime.charAt(xsDateTime.length() - 6) == '+'
				&& xsDateTime.charAt(xsDateTime.length() - 3) == ':') {
			String modified = xsDateTime.substring(0, xsDateTime.length() - 3)
					+ xsDateTime.substring(xsDateTime.length() - 2);
			try {
				synchronized (dfMillisTZone) {
					return dfMillisTZone.parse(modified);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// match pattern against timestamp string
		Matcher matcher = xsDtPattern.matcher(xsDateTime);
		if (matcher.matches() == true) {
			// extract data particles from matched groups / subsequences
			int year = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2)) - 1;
			int day = Integer.parseInt(matcher.group(3));
			int hour = Integer.parseInt(matcher.group(4));
			int minute = Integer.parseInt(matcher.group(5));
			int second = Integer.parseInt(matcher.group(6));
			int millis = 0;
			// probe for successful parsing of milliseconds
			if (matcher.group(7) != null) {
				millis = Integer.parseInt(matcher.group(8));
			}
			cal.set(year, month, day, hour, minute, second);
			cal.set(GregorianCalendar.MILLISECOND, millis);
			String tzString = matcher.group(9);
			if (tzString != null) {
				// timezone matched
				tzString = "GMT" + tzString.replace(":", "");
				cal.setTimeZone(TimeZone.getTimeZone(tzString));
			} else {
				cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			}
			return cal.getTime();
		} else {
			System.err.println("\"" + xsDateTime + "\" is not a valid representation of a XES timestamp.");
			return null;
		}
	}

	/**
	 * Formats a given date to the xs:dateTime format of XML.
	 * 
	 * @param date
	 *            Date to be formatted.
	 * @return String formatting the given date.
	 */
	public String format(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return DatatypeConverter.printDateTime(cal);
	}
}
