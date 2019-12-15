/*
 * Copyright © 2009-2019 The Apromore Initiative.
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

package org.apromore.service.csvimporter.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.zkoss.zul.Messagebox;


public class Parse {

    static final private Logger LOGGER = Logger.getAnonymousLogger();
	
    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
    	{
//        put("^(\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))$", "yyyyMMdd");
//        put("^(\\d{4}(0[1-9]|[12]\\d|3[01])(0[1-9]|1[0-2]))$", "yyyyddMM");
//        put("^((0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{4})$", "MMddyyyy");
//        put("^((0[1-9]|[12]\\d|3[01])(0[1-9]|1[0-2])\\d{4})$", "ddMMyyyy");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}-\\d{1,2}-\\d{2,4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        
        put("^\\d{1,2}/\\d{1,2}/\\d{2,4}\\s\\d{1,2}:\\d{1,2}$", "dd/MM/yyyy HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", "dd/MM/yyyy HH:mm:ss");   //21/11/2011 11:11:11
        put("^\\d{1,2}/\\d{1,2}/\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}:\\d{3}$", "dd/MM/yyyy HH:mm:ss:SSS");   //21/11/2011 11:11:11:111
        put("^\\d{1,2}-\\d{1,2}-\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}:\\d{3}$", "dd-MM-yyyy HH:mm:ss:SSS");   //21/11/2011 11:11:11:111
        put("^\\d{1,2}.\\d{1,2}.\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}:\\d{3}$", "dd.MM.yyyy HH:mm:ss:SSS");   //21.11.2011 11:11:11:111
        put("^\\d{2}/\\d{1,2}/\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}:\\d{3}$", "MM/dd/yyyy HH:mm:ss:SSS");   //11/21/2011 11:11:11:111
        put("^\\d{2}-\\d{1,2}-\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}:\\d{3}$", "MM-dd-yyyy HH:mm:ss:SSS");   //11/21/2011 11:11:11:111
        put("^\\d{2}.\\d{1,2}.\\d{2,4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}:\\d{3}$", "MM-dd-yyyy HH:mm:ss:SSS");   //11/21/2011 11:11:11:111

        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{4}\\d{1,2}\\d{2}\\d{6}$", "yyyyMMddHHmmss");   // 20110625031548
        put("^\\d{4}\\d{1,2}\\d{1,2}\\d{6}$", "yyyyddMMHHmmss");   // 20112506031548
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");  //20110520 031548
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss"); //21-11-2011 11:11:11
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm"); //21-11-2011 11:11:11
        put("^\\d{1,2}-\\d{1,2}-\\d{2}\\s\\d{1,2}:\\d{2}$", "MM-dd-yy HH:mm"); //21-11-19 11:11:11
        put("^\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}$", "MM/dd/yy HH:mm"); //21/11/19 11:11:11

//        put("^\\d{1,2}/\\d{1,2}/\\d{4} \\s\\d{1,2}:\\d{2}:\\d{2}$.\\d{3}", "dd/MM/yyyy HH:mm:ss.SSS"); // 21/11/2011 11:11:11.111
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");    // 2011-11-21 11:11:11

//        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", "MM/dd/yyyy HH:mm:ss");   //21/11/2011 11:11:11
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss"); // 2011/11/21 11:11:11
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/dd HH:mm:ss.SSS");  //2011/11/11 03:05:12.522
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");   // 11 Nov 2011 03:05:12
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss"); // 11 November 2011 03:05:12
        put("^\\d{1,2}.\\d{1,2}.\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", "dd.MM.yy HH:mm"); //19.3.10 8:05
        put("^\\d{1,2}.\\d{1,2}.\\d{2}\\s\\d{1,2}:\\d{1,2}$", "MM.dd.yy HH:mm"); //9.13.10 8:05
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd HH:mm:ss.SSS");//2011-11-11 03:05:12.522
        put("^\\d{4}-\\d{1,2}-\\d{1,2}[a-zA-Z]\\d{1,2}:\\d{2}:\\d{2}.\\d{3}[a-zA-Z]$", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");//2011-11-11T03:05:12.522Z
        put("^\\d{4}-\\d{1,2}-\\d{1,2}[a-zA-Z]\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd'T'HH:mm:ss.SSS");//2011-11-11T03:05:12.522
        put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm:ss");//2011-11-11T03:05:12.522
        put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/dd'T'HH:mm:ss.SSS");//2011/11/11T03:05:12.522
        put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd'T'HH:mm:ss");//2011/11/11T03:05:12.522

    }};
	
	public static Timestamp parseTimestamp(String theDate, String theFormate) {
//	    Messagebox.show("Date: " + theDate + " Format: " + theFormate);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(theFormate);
            formatter.setLenient(false);
		    Calendar cal = Calendar.getInstance();
		    Date d = formatter.parse(theDate);
			cal.setTime(d);
			return new Timestamp(cal.getTimeInMillis());
		} catch (Exception e) {
            System.out.print(e.getStackTrace());
            return null;
		}
	}
	
	
    public String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
//            LOGGER.info("Checking: " + dateString.toLowerCase() + " -- to -- : " + DATE_FORMAT_REGEXPS.get(regexp));
//            LOGGER.severe("Trying: " + DATE_FORMAT_REGEXPS.get(regexp) + "want to match: " + dateString);
            if (dateString.toLowerCase().matches(regexp)) {
//                LOGGER.severe("dateString is: " +  dateString.toLowerCase() + " and regexp is: " + regexp);
                try{
                    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_REGEXPS.get(regexp));
                    formatter.setLenient(false);
                    Calendar cal = Calendar.getInstance();
                    Date d = formatter.parse(dateString);
//                    LOGGER.info("FOUND! :" + d);
                        return DATE_FORMAT_REGEXPS.get(regexp);

                } catch (ParseException e) {
                    LOGGER.severe(e.getMessage() + "Tried: " + DATE_FORMAT_REGEXPS.get(regexp));
                }

            }
        }
        return null; // Unknown format.
    }

}
