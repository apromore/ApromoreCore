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

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;

import org.apromore.commons.datetime.DurationUnit;
import org.apromore.commons.datetime.DurationUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationUtilsUnitTest {

  private static Stream<Arguments> TEST_CASES() {
    return Stream.of(
        Arguments.of(ChronoUnit.SECONDS),
        Arguments.of(ChronoUnit.MINUTES),
        Arguments.of(ChronoUnit.HOURS),
        Arguments.of(ChronoUnit.DAYS),
        Arguments.of(ChronoUnit.WEEKS),
        Arguments.of(ChronoUnit.MONTHS),
        Arguments.of(ChronoUnit.YEARS));
  }

  @ParameterizedTest
  @MethodSource("TEST_CASES")
  void humanize_ShouldGenerateHumanizedDurationAtBorderCases(ChronoUnit unit) {
    DurationUnit durationUnit = DurationUnit.getDurationUnit(unit).get();
    double milliseconds = durationUnit.getUnitValue();
    String label = durationUnit.getPluralString();
    String humanized = DurationUtils.humanize(milliseconds, true);
    assertEquals("1.00 " + label, humanized);
  }

  @ParameterizedTest
  @MethodSource("TEST_CASES")
  void humanize_ShouldGenerateHumanizedDurationSingular(ChronoUnit unit) {
    DurationUnit durationUnit = DurationUnit.getDurationUnit(unit).get();
    double milliseconds = durationUnit.getUnitValue();
    String label = durationUnit.getSingularString();
    String humanized = DurationUtils.humanize(milliseconds, false);
    assertEquals("1 " + label, humanized);
  }

  @ParameterizedTest
  @MethodSource("TEST_CASES")
  void humanize_ShouldGenerateHumanizedDuration(ChronoUnit unit) {
    DurationUnit durationUnit = DurationUnit.getDurationUnit(unit).get();
    double milliseconds = 2.34789D * durationUnit.getUnitValue();
    String label = durationUnit.getPluralString();
    String humanized = DurationUtils.humanize(milliseconds, false);
    assertEquals("2.35 " + label, humanized);
  }

}
