/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
import java.util.Optional;
import java.util.Arrays;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;
import lombok.Getter;

/**
 * Duration unit
 */
@Getter
public enum DurationUnit {

  YEARS(ChronoUnit.YEARS, "yrs", "yr", new Double(1000.0D * 60 * 60 * 24 * 365)),
  MONTHS(ChronoUnit.MONTHS, "mths", "mth", new Double(1000.0D * 60 * 60 * 24 * 30.42)),
  WEEKS(ChronoUnit.WEEKS, "wks", "wk", new Double(1000.0D * 60 * 60 * 24 * 7)),
  DAYS(ChronoUnit.DAYS, "days", "day", new Double(1000.0D * 60 * 60 * 24)),
  HOURS(ChronoUnit.HOURS, "hrs", "hr", new Double(1000.0D * 60 * 60)),
  MINUTES(ChronoUnit.MINUTES, "mins", "min", new Double(1000.0D * 60)),
  SECONDS(ChronoUnit.SECONDS, "secs", "sec", new Double(1000.0D)),
  MILLIS(ChronoUnit.MILLIS, "millis", "milli", new Double(1D));

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
    Optional<DurationUnit> findFirst = Arrays.asList(DurationUnit.values()).stream()
        .filter(unit -> unit.getDurationValue(value) >= 1.0D)
        .findFirst();
    return findFirst;
  }

  public static Optional<DurationUnit> getDurationUnit(ChronoUnit chronoUnit) {
    Optional<DurationUnit> findFirst = Arrays.asList(DurationUnit.values()).stream()
        .filter(unit -> unit.getUnit().equals(chronoUnit))
        .findFirst();
    return findFirst;
  }

  public double getDurationValue(double value) {
    return value / this.getUnitValue();
  }
}


