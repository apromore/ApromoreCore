/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.processdiscoverer.util;

import java.text.DecimalFormat;

public class TimeConverter
{
	public static final String DURATION_UNIT_MARKER = " ";
	private static final DecimalFormat decimalFormat = new DecimalFormat(StringValues.b[123]);
  
	  /*
	   * Convert from UI entry value to milliseconds
	   */
	public static Double convertMilliseconds(double value, String unit) {
		double d = value;
  
		double seconds = 1000.0D;
		double minutes = seconds * 60.0D;
		double hours = minutes * 60.0D;
		double days = hours * 24.0D;
		double weeks = days * 7.0D;
		double months = days * 30.0D;
		double years = days * 365.0D;

		if(unit.equals("Years")) return d * years;
		else if(unit.equals("Months")) return d * months;
		else if(unit.equals("Weeks")) return d * weeks;
		else if(unit.equals("Days")) return d * days;
		else if(unit.equals("Hours")) return d * hours;
		else if(unit.equals("Minutes")) return d * minutes;
		else if(unit.equals("Seconds")) return d * seconds;
  
		return d;
	}

	public static String convertMilliseconds(String number) {
		return convertMilliseconds(Double.parseDouble(number));
	}	
	  
	public static String convertMilliseconds(double milliseconds) {
		double seconds = milliseconds / 1000.0D;
		double minutes = seconds / 60.0D;
		double hours = minutes / 60.0D;
		double days = hours / 24.0D;
		double weeks = days / 7.0D;
		double months = days / 30.0D;
		double years = days / 365.0D;
	    
		if (years > 1.0D) {
			return decimalFormat.format(years) + " yrs";
		}
	  
		if (months > 1.0D) {
		  return decimalFormat.format(months) + " mths";
		}
	
		if (weeks > 1.0D) {
		  return decimalFormat.format(weeks) + " wks";
		}
	
		if (days > 1.0D) {
		  return decimalFormat.format(days) + " d";
		}
	
		if (hours > 1.0D) {
		  return decimalFormat.format(hours) + " hrs";
		}
	
		if (minutes > 1.0D) {
		  return decimalFormat.format(minutes) + " mins";
		}
	  
		if (seconds > 1.0D) {
		  return decimalFormat.format(seconds) + " secs";
		}
	
		if (milliseconds > 1.0D) {
		  return decimalFormat.format(milliseconds) + " millis";
		}
	
		return "instant";
	}
  
	/*
	 * Value format: X@Y, X is the value, Y is the unit (Years, Months,...)
	 */
	public static String[] parseDuration2(String value) {
		return value.split(TimeConverter.DURATION_UNIT_MARKER);
	}
	
	/*
	 * Convert milliseconds to text
	 */
//	public static String convertMilliseconds(String milliseconds)
//	{
//	  BigDecimal d = new BigDecimal(milliseconds);
//	  BigDecimal seconds = d.divide(BigDecimal.valueOf(1000.0D), 20, RoundingMode.HALF_UP);
//	  BigDecimal minutes = seconds.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
//	  BigDecimal hours = minutes.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
//	  BigDecimal days = hours.divide(BigDecimal.valueOf(24.0D), 20, RoundingMode.HALF_UP);
//	  BigDecimal weeks = days.divide(BigDecimal.valueOf(7.0D), 20, RoundingMode.HALF_UP);
//	  BigDecimal months = days.divide(BigDecimal.valueOf(30.0D), 20, RoundingMode.HALF_UP);
//	  BigDecimal years = days.divide(BigDecimal.valueOf(365.0D), 20, RoundingMode.HALF_UP);
//	  
//	  BigDecimal value1 = BigDecimal.valueOf(1.0D);
//	  
//	if (years.compareTo(value1) >= 0) {
//	  return decimalFormat.format(years) + " yrs";
//	}
//	if (months.compareTo(value1) >= 0) {
//	  return decimalFormat.format(months) + " mths";
//	}
//	if (weeks.compareTo(value1) >= 0) {
//	  return decimalFormat.format(weeks) + " wks";
//	}
//	if (days.compareTo(value1) >= 0) {
//	  return decimalFormat.format(days) + " d";
//	}
//	if (hours.compareTo(value1) >= 0) {
//	  return decimalFormat.format(hours) + " hrs";
//	}
//	if (minutes.compareTo(value1) >= 0) {
//	  return decimalFormat.format(minutes) + " mins";
//	}
//	if (seconds.compareTo(value1) >= 0) {
//	  return decimalFormat.format(seconds) + " secs";
//	}
//	if (d.compareTo(value1) >= 0) {
//	  return decimalFormat.format(milliseconds) + " millis";
//	}
//	return "instant";
//  }	

}
