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
package org.apromore.commons.item;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apromore.commons.item.ItemNameUtils;

/**
 * Test filename validation
 * <p>
 * Implicitly test the regex used by zk validation
 * in ApromoreCore/Apromore-Core-Components/Apromore-Portal/src/main/webapp/WEB-INF/ui.properties
 */
public class ItemNameUtilsUnitTest {

    private static Stream<Arguments> VALID_FILENAMES() {
        return Stream.of(
                Arguments.of("Letters&0123456789"),
                Arguments.of("(Brackets) [Square Brackets]"),
                Arguments.of("hyphen-ated_underscored"),
                Arguments.of("filename+plus+plus"),
                Arguments.of("filename with spaces"),
                Arguments.of("汉字"),
                Arguments.of("\u9fff")
        );
    }

    private static Stream<Arguments> INVALID_FILENAMES() {
        return Stream.of(
                Arguments.of("MoreThan60Chars012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"),
                Arguments.of("\r\n\t"),
                Arguments.of(""),
                Arguments.of("\u0040") // @ sign
        );
    }

    private static Stream<Arguments> MERGE_CASES() {
        return Stream.of(
                Arguments.of("somethingFast", "somethingSlow", "something_merged"),
                Arguments.of("something.fast", "something.slow", "something_merged"),
                Arguments.of("something fast", "something slow", "something_merged"),
                Arguments.of("something+fast", "something+slow", "something_merged"),
                Arguments.of("something-fast", "something-slow", "something_merged"),
                Arguments.of("something_fast", "something_slow", "something_merged"),
                Arguments.of("something", "another", "something_another_merged"),
                Arguments.of("01234567890123456789012345678901234567890123456789", "01234567890123456789012345678901234567890123456789",
                    "01234567890123456789012345678901234567890123456789_012345678901234567890123456789012345678901_merged")
        );
    }

    private static Stream<Arguments> MERGE_LIST_CASES() {
        return Stream.of(
                Arguments.of(Arrays.asList("a"), "a_merged"),
                Arguments.of(Arrays.asList("aFast", "aSlow"), "a_merged"),
                Arguments.of(Arrays.asList("ab.fast", "ab.med", "ab.slow"), "ab_merged"),
                Arguments.of(Arrays.asList("x fast", "x slow"), "x_merged"),
                Arguments.of(Arrays.asList("xyz+fast", "xyz+med", "xyz+slow"), "xyz_merged"),
                Arguments.of(Arrays.asList("a-fast", "a-slow"), "a_merged"),
                Arguments.of(Arrays.asList("a_fast", "a_slow"), "a_merged"),
                Arguments.of(Arrays.asList("abc", "xyz", "123"), "abc_xyz_123_merged"),
                Arguments.of(Arrays.asList("01234567890123456789012345678901234567890123456789", "01234567890123456789012345678901234567890123456789"),
                    "01234567890123456789012345678901234567890123456789_012345678901234567890123456789012345678901_merged")
        );
    }

    @ParameterizedTest
    @MethodSource("VALID_FILENAMES")
    public void hasValidName_ShouldPassValidFilename(String filename) {
        if (!ItemNameUtils.hasValidName(filename)) {
            fail("Invalid name " + filename);
        }
    }

    @ParameterizedTest
    @MethodSource("INVALID_FILENAMES")
    public void hasValidName_ShouldNotPassInvalidFilename(String filename) {
        if (ItemNameUtils.hasValidName(filename)) {
            fail("Invalid name " + filename);
        }
    }

    @ParameterizedTest
    @MethodSource("MERGE_CASES")
    public void mergeNames_ShouldProvideValidMergedNames(String filename1, String filename2, String expected) {
        assertEquals(ItemNameUtils.mergeNames(filename1, filename2), expected);
    }

    @ParameterizedTest
    @MethodSource("MERGE_LIST_CASES")
    public void mergeNames_ShouldProvideValidMergedNames(List<String> filenames, String expected) {
        assertEquals(ItemNameUtils.mergeNames(filenames), expected);
    }

}
