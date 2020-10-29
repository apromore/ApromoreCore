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
package org.apromore.plugin.portal.calendar;

import java.lang.Long;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import lombok.Getter;
import lombok.Setter;

import org.apromore.commons.datetime.TimeUtils;
import org.apromore.plugin.portal.calendar.TimeRange;
import org.apromore.plugin.portal.calendar.Constants;

/**
 * UI model for calendar item
 */
public class CalendarItem {

    @Getter @Setter private Long id;
    @Getter @Setter private String name;
    @Getter @Setter private OffsetDateTime created;

    public CalendarItem(Long id, String name, OffsetDateTime created) {
        this.id = id;
        this.name = name;
        this.created = created;
    }
}
