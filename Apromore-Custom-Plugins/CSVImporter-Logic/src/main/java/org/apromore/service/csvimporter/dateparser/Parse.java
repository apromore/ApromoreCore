/**
 * MIT License
 *
 * Copyright (c) 2019 lin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.service.csvimporter.dateparser;

import org.apromore.service.csvimporter.utilities.InvalidCSVException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Parse {

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
