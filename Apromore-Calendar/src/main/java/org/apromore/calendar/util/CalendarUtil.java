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

package org.apromore.calendar.util;

import java.time.DayOfWeek;
import java.util.function.Predicate;

// This is temporary as spring 3 does not support lambda functions in spring beans
public class CalendarUtil {

    public static Predicate<DayOfWeek> getWeekendOffPRedicate(boolean weekendsOff) {
        Predicate<DayOfWeek> isWeekendOff = (DayOfWeek dayOfWeek) -> {
            return (weekendsOff
                && (dayOfWeek.equals(DayOfWeek.SATURDAY)
                || dayOfWeek.equals(DayOfWeek.SUNDAY)));
        };
        return isWeekendOff;
    }

}
