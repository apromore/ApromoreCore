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

package org.apromore.portal.util;

import java.util.Comparator;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.portal.common.VersionSummaryTypes;
import org.apromore.portal.model.VersionSummaryType;

public class VersionSummaryComparator implements Comparator<Object> {
    private final boolean asc;
    private final VersionSummaryTypes versionSummaryTypes;

    public VersionSummaryComparator(boolean asc, VersionSummaryTypes versionSummaryTypes) {
        this.asc = asc;
        this.versionSummaryTypes = versionSummaryTypes;
    }

    @Override
    public int compare(Object object1, Object object2) {
        if (object1 == null || object2 == null) {
            return 0;
        }
        int factor = 1;
        if (!asc) {
            factor = -1;
        }
        switch (versionSummaryTypes.name()) {
            case "BY_UPDATE_DATE":
                return compareDateString(getUpdateDateFromObject(object1), getUpdateDateFromObject(object2)) *
                    factor;
            case "BY_VERSION":
                return (AlphaNumericComparator.compareTo(getLastVersionFromObject(object1),
                    getLastVersionFromObject(object2))) *
                    factor;
            default:
                return 0;
        }
    }

    private String getLastVersionFromObject(Object object) {
        if (object == null) {
            return "";
        }
        if(object instanceof  VersionSummaryType){
            return ((VersionSummaryType)object).getVersionNumber();
        }
        return "";
    }

    private String getUpdateDateFromObject(Object object) {
        if (object == null) {
            return null;
        }
        if(object instanceof  VersionSummaryType){
            return ((VersionSummaryType)object).getLastUpdate();
        }
        return null;
    }

    public int compareDateString(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return 0;
        } else {
            return DateTimeUtils.parse(date1).compareTo(DateTimeUtils.parse(date2));
        }
    }

    public VersionSummaryTypes getVersionOrderTypes() {
        return versionSummaryTypes;
    }

    public boolean isAsc() {
        return this.asc;
    }
}
