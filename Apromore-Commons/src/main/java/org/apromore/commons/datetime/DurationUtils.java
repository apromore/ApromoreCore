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

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apromore.commons.datetime.Constants;

/**
 * Various duration utility functions
 * <p>
 * TO DO: Need to refactor further these duplicates
 * <p>
 * ApromoreCore/Apromore-Extras/APMLogModule/src/main/java/org/apromore/apmlog/util/TimeUtil.java
 * ApromoreEE/Dashboard/src/main/java/dashboard/util/Util.java
 * ApromoreEE/FilterEE/src/main/java/org/apromore/plugin/portal/logfilteree/util/Util.java
 */
public final class DurationUtils {

  /**
   * Humanize duration
   *
   * @param milliseconds Source duration
   * @param unit         ChronoUnit for fixed unit, otherwise set null for automatic
   * @param forceDecimal Force the last two decimal digit, set false to avoid decimal for whole
   *                     number.
   * @return Humanized duration
   */
  public static String humanize(double milliseconds, ChronoUnit unit, boolean forceDecimal) {
    double duration = 0;
    String label = "";

    DecimalFormat decimalFormat = (forceDecimal)
        ? new DecimalFormat("##############0.00")
        : new DecimalFormat("##############0.##");

    Optional<DurationUnit> optionalDurationUnit = (unit == null)
        ? DurationUnit.getDurationUnit(milliseconds)
        : DurationUnit.getDurationUnit(unit);

    if (optionalDurationUnit.isPresent()) {
      DurationUnit durationUnit = optionalDurationUnit.get();
      duration = durationUnit.getDurationValue(milliseconds);

      label = (forceDecimal || duration > 1.0D)
          ? durationUnit.getPluralString()
          : durationUnit.getSingularString();
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
    return humanize((double) milliseconds, forceDecimal);
  }
}
