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
package org.apromore.commons.i18n;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public final class I18Utils {

    private static final String DEFAULT_COUNTRY_CODE = "GB";

    // Map to UPPER-CASE of country code
    // Note that the Locale country code is different to the Unicode flag country code.
    private static Map<String, String> flagMap = Map.of(
        "en", "GB",
        "ja", "JP"
    );

    private I18Utils() {
        throw new IllegalStateException("I18n utility class");
    }

    /**
     * Return the flag of a langTag
     *
     * @param langTag
     * @return Unicode flag
     */
    public static final String langTagToFlag(String langTag) {
        String countryCode = flagMap.getOrDefault(langTag, DEFAULT_COUNTRY_CODE);
        return Stream.of(0, 1)
            .map(i -> new String(Character.toChars(Character.codePointAt(countryCode, i) + 0x1F1A5)))
            .collect(Collectors.joining(""));
    }

    /**
     * Return the country code of a langTag
     *
     * @param langTag
     * @return country code
     */
    public static final String langTagToCountryCode(String langTag) {
        return flagMap.getOrDefault(langTag, DEFAULT_COUNTRY_CODE).toLowerCase();
    }
}
