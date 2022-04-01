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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeUtilsUnitTest {

    private static Stream<Arguments> TEST_CASES() {
        return Stream.of(
                Arguments.of("25/05/2020", "25 May 20, 00:00"),
                Arguments.of("2021/05/20 23:56:10", "20 May 21, 23:56"),
                Arguments.of("25-05-2020 22:37:55", "25 May 20, 22:37"),
                Arguments.of("25/05/2020 22:37:05", "25 May 20, 22:37"),
                Arguments.of("2020-05-25T10:36:54.336Z", "25 May 20, 10:36"),
                Arguments.of("May 25, 2020", "25 May 20, 00:00"),
                Arguments.of("21-05-2020 05:33:32", "21 May 20, 05:33"),
                Arguments.of("2020-05-21T05-33-32", "21 May 20, 05:33"),
                Arguments.of("30 Aug 21, 10:39", "30 Aug 21, 10:39")
        );
    }

    @ParameterizedTest
    @MethodSource("TEST_CASES")
    void normalize_ShouldGenerateNormalizedDate(String input, String expected) {
        String normalizedDate = DateTimeUtils.normalize(input);
        assertEquals(expected, normalizedDate);
    }

    @ParameterizedTest
    @MethodSource("TEST_CASES")
    void format_ShouldGenerateSimpleFormattedDate(String input, String expected) {
        LocalDateTime localDateTime = DateTimeUtils.parse(input);
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        long milliseconds = zdt.toInstant().toEpochMilli();
        String formattedDate = DateTimeUtils.format(milliseconds, Constants.DATE_TIME_FORMAT_HUMANIZED);
        assertEquals(expected, formattedDate);
    }
}
