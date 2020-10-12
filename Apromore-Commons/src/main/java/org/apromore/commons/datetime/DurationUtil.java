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
package org.apromore.commons.datetime;

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;

import org.apromore.commons.datetime.Constants;

/**
 * Various duration utility functions
 *
 * TO DO:
 * Need to refactor further these duplicates
 *
 * ApromoreCore/Apromore-Extras/APMLogModule/src/main/java/org/apromore/apmlog/util/TimeUtil.java
 * ApromoreEE/Dashboard/src/main/java/dashboard/util/Util.java
 * ApromoreEE/FilterEE/src/main/java/org/apromore/plugin/portal/logfilteree/util/Util.java
 */
public final class DurationUtil {

    public static double convert(double milliseconds, ChronoUnit unit) {
        return milliseconds / DurationUnit.getMilliseconds(unit);
    }

    /**
     * Humanize duration
     *
     * @param milliseconds Source duration
     * @param unit ChronoUnit for fixed unit, otherwise set null for automatic
     * @param forceDecimal Force the last two decimal digit, set false to avoid decimal for whole number.
     * @return Humanized duration
     */
    public static String humanize(double milliseconds, ChronoUnit unit, boolean forceDecimal) {
        DecimalFormat decimalFormat = (forceDecimal) ?
                new DecimalFormat("##############0.00") : new DecimalFormat("##############0.##");

        double duration = 0;
        String label = "";
        double seconds = convert(milliseconds, ChronoUnit.SECONDS);
        double minutes = convert(milliseconds, ChronoUnit.MINUTES);
        double hours = convert(milliseconds, ChronoUnit.HOURS);
        double days = convert(milliseconds, ChronoUnit.DAYS);
        double weeks = convert(milliseconds, ChronoUnit.WEEKS);
        double months = convert(milliseconds, ChronoUnit.MONTHS);
        double years = convert(milliseconds, ChronoUnit.YEARS);

        if (unit == null) {
            if (years >= 1.0D) {
                duration = years;
                unit = ChronoUnit.YEARS;
            } else if (months >= 1.0D) {
                duration = months;
                unit = ChronoUnit.MONTHS;
            } else if (weeks >= 1.0D) {
                duration = weeks;
                unit = ChronoUnit.WEEKS;
            } else if (days >= 1.0D) {
                duration = days;
                unit = ChronoUnit.DAYS;
            } else if (hours >= 1.0D) {
                duration = hours;
                unit = ChronoUnit.HOURS;
            } else if (minutes >= 1.0D) {
                duration = minutes;
                unit = ChronoUnit.MINUTES;
            } else if (seconds >= 1.0D) {
                duration = seconds;
                unit = ChronoUnit.SECONDS;
            } else if (milliseconds >= 1.0D) {
                duration = milliseconds;
                unit = ChronoUnit.MILLIS;
            }
            if (unit != null) {
                if (forceDecimal) {
                    label = DurationUnit.getShortLabelPlural(unit);
                } else {
                    label = DurationUnit.getShortLabel(unit, duration);
                }
            }
        } else {
            duration = convert(milliseconds, unit);
            if (forceDecimal) {
                label = DurationUnit.getShortLabelPlural(unit);
            } else {
                label = DurationUnit.getShortLabel(unit, duration);
            }
        }
        if (duration != 0) {
            return decimalFormat.format(duration) + " " + label;
        }
        return "instant";
    }

    public static String humanize(double milliseconds, boolean forceDecimal) {
        return humanize(milliseconds, null, forceDecimal);
    }

    public static String humanize(long milliseconds, boolean forceDecimal) {
        return humanize((double)milliseconds, forceDecimal);
    }
}
