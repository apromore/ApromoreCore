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

package org.apromore.plugin.portal.calendar;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apromore.commons.datetime.TimeUtils;

/**
 * UI model for time range.
 *
 * <p>Note: We need to use Date for ZK
 */
public class TimeRange {

    // These two are for ZK view model
    @Getter
    @Setter
    private Date start; // needed for timebox
    @Getter
    @Setter
    private Date end; // needed for timebox

    @Getter
    @Setter
    private OffsetTime startTime;
    @Getter
    @Setter
    private OffsetTime endTime;

    public TimeRange(OffsetTime startTime, OffsetTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.start = TimeUtils.localDateAndOffsetTimeToDate(Constants.LOCAL_DATE_REF, startTime);
        this.end = TimeUtils.localDateAndOffsetTimeToDate(Constants.LOCAL_DATE_REF, endTime);
    }

    public void updateStartTime(int startHour, int startMin) {
        this.start = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, startHour, startMin);
        this.startTime = OffsetTime.of(LocalTime.of(startHour, startMin), ZoneOffset.UTC);
    }

    public void updateEndTime(int endHour, int endMin) {
        this.start = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, endHour, endMin);
        this.startTime = OffsetTime.of(LocalTime.of(endHour, endMin), ZoneOffset.UTC);
    }

}
