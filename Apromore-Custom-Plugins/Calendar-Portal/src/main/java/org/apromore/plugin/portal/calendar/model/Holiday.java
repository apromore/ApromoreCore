/*-
 * #%L
 * This file is part of "Apromore Core".
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

/*
 * This is a Pojo which is a DTO to Holiday model in JPA.
 * This contains all holiday information which is associated with a calendar.
 * This is used in calculation of duration, where the number of hours is 0 for a holiday period.
 */

package org.apromore.plugin.portal.calendar.model;

import java.time.LocalDate;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apromore.calendar.model.HolidayType;
import org.apromore.commons.datetime.TimeUtils;

@Getter
@Setter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class Holiday {
    private @NonNull Long id = 0L; // only used for mapping from database object
    private @NonNull HolidayType holidayType = HolidayType.PUBLIC;
    private @NonNull String name;
    private @NonNull String description;
    private @NonNull LocalDate holidayDate;

    protected Holiday(HolidayType type, String name, String desc, LocalDate date) {
        this.holidayType = type;
        this.name = name;
        this.description = desc;
        this.holidayDate = date;
    }

    public boolean isPublic() {
        return HolidayType.PUBLIC.equals(holidayType);
    }

    public Date getDate() {
        return TimeUtils.localDateToDate(holidayDate);
    }
}
