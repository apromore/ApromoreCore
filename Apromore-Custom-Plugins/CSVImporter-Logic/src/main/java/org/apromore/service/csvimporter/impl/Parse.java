/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.service.csvimporter.impl;

import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.impl.dateparser.DateParserUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


class Parse {

    private String parseFailMess;

    public Timestamp tryParsingWithFormat(String theDate, String theFormat) {
        try {
            if (theDate == null || theDate.isEmpty()) {
                throw new InvalidCSVException("Field is empty or has a null value!");
            }

            SimpleDateFormat formatter = new SimpleDateFormat(theFormat);
            formatter.setLenient(false);
            Calendar cal = Calendar.getInstance();
            Date d = formatter.parse(theDate);
            cal.setTime(d);
            return new Timestamp(cal.getTimeInMillis());

        } catch (Exception e) {
            parseFailMess = e.getMessage();
            return null;
        }
    }

    public Timestamp tryParsing(String theDate) {
        try {
            if (theDate == null || theDate.isEmpty()) {
                throw new InvalidCSVException("Field is empty or has a null value!");
            }

            return new Timestamp(DateParserUtils.parseCalendar(theDate).getTimeInMillis());
        } catch (Exception e) {
            parseFailMess = e.getMessage();
            return null;
        }
    }


    public boolean getPreferMonthFirst() {
        return DateParserUtils.getPreferMonthFirst();
    }

    public String getParseFailMess() {
        return parseFailMess;
    }
}
