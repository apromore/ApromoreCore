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
package org.apromore.commons.datetime;

import lombok.Getter;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

/**
 * Duration unit
 *
 * Modified: Chii Chang (2022-02-21)
 */
@Getter
public enum DurationUnit {

    YEARS(ChronoUnit.YEARS, "yrs", "yr", 1000.0D * 60 * 60 * 24 * 365.25),
    MONTHS(ChronoUnit.MONTHS, "mths", "mth", 1000.0D * 60 * 60 * 24 * (365.25 / 12)),
    WEEKS(ChronoUnit.WEEKS, "wks", "wk", 1000.0D * 60 * 60 * 24 * 7),
    DAYS(ChronoUnit.DAYS, "days", "day", 1000.0D * 60 * 60 * 24),
    HOURS(ChronoUnit.HOURS, "hrs", "hr", 1000.0D * 60 * 60),
    MINUTES(ChronoUnit.MINUTES, "mins", "min", 1000.0D * 60),
    SECONDS(ChronoUnit.SECONDS, "secs", "sec", 1000.0D),
    MILLIS(ChronoUnit.MILLIS, "millis", "milli", 1D);

    ChronoUnit unit;
    String pluralString;
    String singularString;
    Double unitValue;

    DurationUnit(ChronoUnit unit, String pluralString, String singularString, Double unitValue) {
        this.unit = unit;
        this.pluralString = pluralString;
        this.singularString = singularString;
        this.unitValue = unitValue;
    }

    public static Optional<DurationUnit> getDurationUnit(double value) {
        return Arrays.stream(DurationUnit.values())
                .filter(unit1 -> unit1.getDurationValue(value) >= 1.0D)
                .findFirst();
    }

    public static Optional<DurationUnit> getDurationUnit(ChronoUnit chronoUnit) {
        return Arrays.stream(DurationUnit.values())
                .filter(unit1 -> unit1.getUnit().equals(chronoUnit))
                .findFirst();
    }

    public double getDurationValue(double value) {
        return value / this.getUnitValue();
    }
}
