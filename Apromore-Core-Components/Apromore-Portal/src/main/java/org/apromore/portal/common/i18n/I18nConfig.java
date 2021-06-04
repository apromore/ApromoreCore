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
package org.apromore.portal.common.i18n;

import org.zkoss.util.Locales;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import lombok.Getter;

public class I18nConfig {

    @Getter private Map<Locale, String> supportedLocales;

    /**
     * Constructor
     *
     * @param languageTagList  Vertically-separated list of string languageTags
     * @param dateTimePatternList  Vertically-separated list of string Date Time patterns
     */
    public I18nConfig(String languageTagList, String dateTimePatternList) {
        supportedLocales = new LinkedHashMap<>();
        List<String> languageTags = Arrays.asList(languageTagList.split("\\|"));
        List<String> dateTimePatterns = Arrays.asList(dateTimePatternList.split("\\|"));
        if (languageTags.size() == 0 || languageTags.size() != dateTimePatterns.size()) {
            return;
        }
        System.out.println(languageTags);
        System.out.println(dateTimePatterns);
        for (int i = 0 ; i < languageTags.size() ; i++) {
            Locale locale = Locale.forLanguageTag(languageTags.get(i));
            String dateTimePattern = dateTimePatterns.get(i);
            // TO DO: Fix encoding issue in site.properties,
            if ("ja".equals(languageTags.get(i))) {
                dateTimePattern = "yyyy年MM月dd日, HH:mm";
            }
            supportedLocales.put(locale, dateTimePattern);
        }
    }

    public boolean isSupported(Locale locale) {
        return supportedLocales.containsKey(locale);
    }

    public String getDateTimePattern(Locale locale) {
        return supportedLocales.get(locale);
    }

    public LinkedHashMap<String, String> getSelectionSet() {
        LinkedHashMap<String, String> selectionSet = new LinkedHashMap<>();
        for (Map.Entry<Locale, String> entry: supportedLocales.entrySet()) {
            Locale locale = entry.getKey();
            selectionSet.put(locale.toLanguageTag(), locale.getDisplayName(locale) + " (" + locale.getDisplayName() + ")");
        }
        return selectionSet;
    }

}
