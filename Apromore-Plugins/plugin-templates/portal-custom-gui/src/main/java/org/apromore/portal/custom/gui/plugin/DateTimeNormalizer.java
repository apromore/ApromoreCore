/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.portal.custom.gui.plugin;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Created by Ivo Widjaja.
 */
public class DateTimeNormalizer {

    private DateTimeNormalizer() {}

    public static LocalDateTime parse(String lastUpdate, DateTimeFormatter formatter) {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(lastUpdate, formatter);
        } catch (Exception e) {
            dateTime = null;
        }
        return dateTime;
    }

    public static String parse(String lastUpdate) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // 05-05-2020 22:37:05
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // 05/05/2020 00:00:00
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yy, HH:mm");
        LocalDateTime dateTime;

        dateTime = parse(lastUpdate, outputFormatter); // check for correct format
        if (dateTime == null) {
            dateTime = parse(lastUpdate, formatter1);
            if (dateTime == null) {
                if (lastUpdate.length() < 19) {
                    lastUpdate += " 00:00:00";
                }
                dateTime = parse(lastUpdate, formatter2);
            }
            if (dateTime != null) {
                lastUpdate = dateTime.format(outputFormatter);
            }
        }
        return lastUpdate;
    }

}
