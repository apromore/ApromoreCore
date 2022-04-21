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

package org.apromore.calendar.model;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represent a holiday.
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayModel {
    @EqualsAndHashCode.Exclude
    protected @NonNull Long id = 0L; // only used for mapping from database object

    protected @NonNull HolidayType holidayType = HolidayType.PUBLIC;
    protected @NonNull String name = "";
    protected @NonNull String description = "";
    protected @NonNull LocalDate holidayDate = LocalDate.now();

    @EqualsAndHashCode.Exclude
    protected String createdBy = "";

    @EqualsAndHashCode.Exclude
    protected String updatedBy = "";

    public HolidayModel(String holidayTypeLabel, String name, String description, LocalDate holidayDate) {
        this.holidayType = HolidayType.valueOf(holidayTypeLabel);
        this.name = name;
        this.description = description;
        this.holidayDate = holidayDate;
    }

    public HolidayModel(HolidayType holidayType, String name, String description, LocalDate holidayDate) {
        this.holidayType = holidayType;
        this.name = name;
        this.description = description;
        this.holidayDate = holidayDate;
    }

    public boolean isPublic() {
        return HolidayType.PUBLIC.equals(holidayType);
    }

    public HolidayModel immutable() {
        return new ImmutableHolidayModel(this);
    }

}
