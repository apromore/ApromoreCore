/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 Marie Christine.
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String xpdlDate2standardDate(String xpdlDate) {
        // example of xpdl date: 2010-02-16T20:18:32.1102462+10:0
        // example of standard date: 2010-02-16 20:18:32

        String[] comp = xpdlDate.split("T");
        String day = xpdlDate.split("T")[0];
        String time = (xpdlDate.split("T")[1]).split("\\+")[0];
        time = time.split("\\.")[0];

        return day + " " + time;
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
