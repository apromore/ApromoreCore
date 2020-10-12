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

import java.util.Map;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;

/**
 * Duration unit
 */
public final class DurationUnit {

    private static Map<ChronoUnit, String> shortLabelPluralMap = new HashMap<ChronoUnit, String>() {
        {
            put(ChronoUnit.MILLIS, "millis");
            put(ChronoUnit.SECONDS, "secs");
            put(ChronoUnit.MINUTES, "mins");
            put(ChronoUnit.HOURS, "hrs");
            put(ChronoUnit.DAYS, "days");
            put(ChronoUnit.WEEKS, "wks");
            put(ChronoUnit.MONTHS, "mths");
            put(ChronoUnit.YEARS, "yrs");
        }
    };

    private static Map<ChronoUnit, String> shortLabelSingularMap = new HashMap<ChronoUnit, String>() {
        {
            put(ChronoUnit.MILLIS, "milli");
            put(ChronoUnit.SECONDS, "sec");
            put(ChronoUnit.MINUTES, "min");
            put(ChronoUnit.HOURS, "hr");
            put(ChronoUnit.DAYS, "day");
            put(ChronoUnit.WEEKS, "wk");
            put(ChronoUnit.MONTHS, "mth");
            put(ChronoUnit.YEARS, "yr");
        }
    };

    /**
     * Unit to milliseconds ratio
     */
    private static Map<ChronoUnit, Double> toMillisecondMap = new HashMap<ChronoUnit, Double>() {
        {
            put(ChronoUnit.MILLIS, new Double(1.0D));
            put(ChronoUnit.SECONDS, new Double(1000.0D));
            put(ChronoUnit.MINUTES, new Double(1000.0D * 60));
            put(ChronoUnit.HOURS, new Double(1000.0D * 60 * 60));
            put(ChronoUnit.DAYS, new Double(1000.0D * 60 * 60 * 24));
            put(ChronoUnit.WEEKS, new Double(1000.0D * 60 * 60 * 24 * 7));
            put(ChronoUnit.MONTHS, new Double(1000.0D * 60 * 60 * 24 * 30.42));
            put(ChronoUnit.YEARS, new Double(1000.0D * 60 * 60 * 24 * 365));
        }
    };

    public static String getShortLabel(ChronoUnit unit, double value) {
        if (Double.compare(new Double(value), 1.0D) == 0) {
            return getShortLabelSingular(unit);
        }
        return getShortLabelPlural(unit);
    }

    public static String getShortLabelPlural(ChronoUnit unit) {
        return shortLabelPluralMap.get(unit);
    }

    public static String getShortLabelSingular(ChronoUnit unit) {
        return shortLabelSingularMap.get(unit);
    }

    public static double getMilliseconds(ChronoUnit unit) {
        return (double) toMillisecondMap.get(unit);
    }

    public static String getLabel(ChronoUnit unit) {
        return unit.toString().toLowerCase();
    }
}


