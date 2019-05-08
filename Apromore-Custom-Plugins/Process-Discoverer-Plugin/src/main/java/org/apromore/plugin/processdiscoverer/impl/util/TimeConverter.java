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

package org.apromore.plugin.processdiscoverer.impl.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class TimeConverter
{
	public static final String DURATION_UNIT_MARKER = " ";
	private static final DecimalFormat decimalFormat = new DecimalFormat(StringValues.b[123]);
  
	/*
	 * Convert milliseconds to text
	 */
	public static String convertMilliseconds(String milliseconds)
	{
	  BigDecimal d = new BigDecimal(milliseconds);
	  BigDecimal seconds = d.divide(BigDecimal.valueOf(1000.0D), 20, RoundingMode.HALF_UP);
	  BigDecimal minutes = seconds.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
	  BigDecimal hours = minutes.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
	  BigDecimal days = hours.divide(BigDecimal.valueOf(24.0D), 20, RoundingMode.HALF_UP);
	  BigDecimal weeks = days.divide(BigDecimal.valueOf(7.0D), 20, RoundingMode.HALF_UP);
	  BigDecimal months = days.divide(BigDecimal.valueOf(30.0D), 20, RoundingMode.HALF_UP);
	  BigDecimal years = days.divide(BigDecimal.valueOf(365.0D), 20, RoundingMode.HALF_UP);
	  
	  BigDecimal value1 = BigDecimal.valueOf(1.0D);
	  
	if (years.compareTo(value1) >= 0) {
	  return decimalFormat.format(years) + " yrs";
	}
	if (months.compareTo(value1) >= 0) {
	  return decimalFormat.format(months) + " mths";
	}
	if (weeks.compareTo(value1) >= 0) {
	  return decimalFormat.format(weeks) + " wks";
	}
	if (days.compareTo(value1) >= 0) {
	  return decimalFormat.format(days) + " d";
	}
	if (hours.compareTo(value1) >= 0) {
	  return decimalFormat.format(hours) + " hrs";
	}
	if (minutes.compareTo(value1) >= 0) {
	  return decimalFormat.format(minutes) + " mins";
	}
	if (seconds.compareTo(value1) >= 0) {
	  return decimalFormat.format(seconds) + " secs";
	}
	if (d.compareTo(value1) >= 0) {
	  return decimalFormat.format(milliseconds) + " millis";
	}
	return "instant";
  }
  
  /*
   * Convert from UI entry value to milliseconds
   */
  public static BigDecimal convertMilliseconds(BigDecimal value, String unit) {
	  BigDecimal d = value;
	  
	  BigDecimal seconds = BigDecimal.valueOf(1000.0D);
	  BigDecimal minutes = seconds.multiply(BigDecimal.valueOf(60.0D));
	  BigDecimal hours = minutes.multiply(BigDecimal.valueOf(60.0D));
	  BigDecimal days = hours.multiply(BigDecimal.valueOf(24.0D));
	  BigDecimal weeks = days.multiply(BigDecimal.valueOf(7.0D));
	  BigDecimal months = days.multiply(BigDecimal.valueOf(30.0D));
	  BigDecimal years = days.multiply(BigDecimal.valueOf(365.0D));

      if(unit.equals("Years")) return d.multiply(years);
      else if(unit.equals("Months")) return d.multiply(months);
      else if(unit.equals("Weeks")) return d.multiply(weeks);
      else if(unit.equals("Days")) return d.multiply(days);
      else if(unit.equals("Hours")) return d.multiply(hours);
      else if(unit.equals("Minutes")) return d.multiply(minutes);
      else if(unit.equals("Seconds")) return d.multiply(seconds);
      
      return d;
  }
  
  /*
   * Value format: X@Y, X is the value, Y is the unit (Years, Months,...)
   */
  public static String[] parseDuration2(String value) {
	  return value.split(TimeConverter.DURATION_UNIT_MARKER);
  }
  
  /*
   * Convert from a stored value to UI display value
   * Return an array: 
   * 1st element is the value, 
   * 2nd element is the selected index in the UI unit list
   */
//	 public static String[] parseDuration(String milliseconds) {
//		  BigDecimal d = new BigDecimal(milliseconds);
//		  
//		  BigDecimal seconds = d.divide(BigDecimal.valueOf(1000.0D), 20, RoundingMode.HALF_UP);
//		  BigDecimal minutes = seconds.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
//		  BigDecimal hours = minutes.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
//		  BigDecimal days = hours.divide(BigDecimal.valueOf(24.0D), 20, RoundingMode.HALF_UP);
//		  BigDecimal weeks = days.divide(BigDecimal.valueOf(7.0D), 20, RoundingMode.HALF_UP);
//		  BigDecimal months = days.divide(BigDecimal.valueOf(30.0D), 20, RoundingMode.HALF_UP);
//		  BigDecimal years = days.divide(BigDecimal.valueOf(365.0D), 20, RoundingMode.HALF_UP);
//		  
//		  BigDecimal value1 = BigDecimal.valueOf(1.0D);
//		  
//		if(years.compareTo(value1) >= 0) {
//			return new String[] { decimalFormat.format(years), "0" }; //" yrs"
//		}
//		if (months.compareTo(value1) >= 0) {
//		  return new String[] { decimalFormat.format(months), "1" }; // " mths"
//		}
//		if (weeks.compareTo(value1) >= 0) {
//		  return new String[] { decimalFormat.format(weeks), "2" }; // " wks"
//		}
//		if (days.compareTo(value1) >= 0) {
//		  return new String[] { decimalFormat.format(days), "3" }; //" d"
//		}
//		if (hours.compareTo(value1) >= 0) {
//		  return new String[] { decimalFormat.format(hours), "4" }; //" hrs"
//		}
//		if (minutes.compareTo(value1) >= 0) {
//		  return new String[] { decimalFormat.format(minutes), "5" }; // " mins"
//		}
//		return new String[] { decimalFormat.format(seconds), "6" }; // " secs"
//	 }
  
//public static String stringify(String number) {
//  BigDecimal milliseconds = new BigDecimal(number);
//  BigDecimal seconds = milliseconds.divide(BigDecimal.valueOf(1000.0D), 20, RoundingMode.HALF_UP);
//  BigDecimal minutes = seconds.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
//  BigDecimal hours = minutes.divide(BigDecimal.valueOf(60.0D), 20, RoundingMode.HALF_UP);
//  BigDecimal days = hours.divide(BigDecimal.valueOf(24.0D), 20, RoundingMode.HALF_UP);
//  BigDecimal weeks = days.divide(BigDecimal.valueOf(7.0D), 20, RoundingMode.HALF_UP);
//  BigDecimal months = days.divide(BigDecimal.valueOf(30.0D), 20, RoundingMode.HALF_UP);
//  BigDecimal years = days.divide(BigDecimal.valueOf(365.0D), 20, RoundingMode.HALF_UP);
//  
//  BigDecimal value1 = BigDecimal.valueOf(1.0D);
//
//  if(years.compareTo(value1) >= 0) return decimalFormat.format(years) + " Years";
//  else if(months.compareTo(value1) >= 0) return decimalFormat.format(months) + " Months";
//  else if(weeks.compareTo(value1) >= 0) return decimalFormat.format(weeks) + " Weeks";
//  else if(days.compareTo(value1) >= 0) return decimalFormat.format(days) + " Days";
//  else if(hours.compareTo(value1) >= 0) return decimalFormat.format(hours) + " Hours";
//  else if(minutes.compareTo(value1) >= 0) return decimalFormat.format(minutes) + " Minutes";
//  else return decimalFormat.format(seconds) + " Seconds";
//}  

}
