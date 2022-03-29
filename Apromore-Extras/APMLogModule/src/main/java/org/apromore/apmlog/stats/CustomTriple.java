/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
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
package org.apromore.apmlog.stats;

import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.util.CalendarDuration;
import org.apromore.calendar.model.CalendarModel;

public class CustomTriple {
    ActivityInstance activity1, activity2;
    String value;
    public CustomTriple(ActivityInstance activity1, ActivityInstance activity2, String value) {
        this.activity1 = activity1;
        this.activity2 = activity2;
        this.value = value;
    }

    public ActivityInstance getActivity1() {
        return activity1;
    }

    public ActivityInstance getActivity2() {
        return activity2;
    }

    public String getValue() {
        return value;
    }

    public double getDuration() {
        CalendarModel calendarModel = activity1.getCalendarModel();
        return CalendarDuration.getDuration(calendarModel, activity1.getEndTime(), activity2.getStartTime());
    }

    public int getCaseIndex() {
        return activity1.getImmutableTraceIndex();
    }

    public static CustomTriple of(ActivityInstance activity1, ActivityInstance activity2, String value) {
        return new CustomTriple(activity1, activity2, value);
    }
}
