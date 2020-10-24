/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.common.calendar;

import java.util.Date;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

/**
 * UI model for calendar entry
 *
 * Note: We need to use Date for ZK
 */
@AllArgsConstructor
public class CalendarItem {
    @Getter @Setter private String label; // for weekday or date
    @Getter @Setter private Boolean holiday;
    @Getter @Setter private Date date;
    @Getter @Setter private String format;
    @Getter @Setter private Date startTime;
    @Getter @Setter private Date endTime;
}
