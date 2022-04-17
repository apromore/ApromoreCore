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
import java.util.List;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.portal.common.ArtifactOrderTypes;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;

public class ArtifactsComparator implements Comparator<Object> {
    private final boolean asc;
    private final ArtifactOrderTypes artifactOrderTypes;

    public ArtifactsComparator(boolean asc, ArtifactOrderTypes artifactOrderTypes) {
        this.asc = asc;
        this.artifactOrderTypes = artifactOrderTypes;
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
        switch (artifactOrderTypes.name()) {
            case "BY_NAME":
                return (AlphaNumericComparator.compareTo(getNameFromObject(object1), getNameFromObject(object2))) *
                    factor;
            case "BY_ID":
                return compareToInt(getIdFromObject(object1), getIdFromObject(object2)) *
                    factor;
            case "BY_UPDATE_DATE":
                return compareDateString(getUpdateDateFromObject(object1), getUpdateDateFromObject(object2)) *
                    factor;
            case "BY_TYPE": // same as name sorting but always ascending order. Its related logic is kept into another place
                return AlphaNumericComparator.compareTo(getNameFromObject(object1), getNameFromObject(object2));
            case "BY_OWNER":
                return (AlphaNumericComparator.compareTo(getOwnerFromObject(object1), getOwnerFromObject(object2))) *
                    factor;
            case "BY_LAST_VERSION":
                return (AlphaNumericComparator.compareTo(getLastVersionFromObject(object1),
                    getLastVersionFromObject(object2))) *
                    factor;
            case "BY_CREATED_DATE":
                return compareDateString(getCreatedDateFromObject(object1), getCreatedDateFromObject(object2)) *
                    factor;
            default:
                return 0;
        }
    }

    private String getNameFromObject(Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof FolderType) {
            return ((FolderType) object).getFolderName();
        }
        if (object instanceof SummaryType) {
            return ((SummaryType) object).getName();
        }
        return "";
    }

    private String getLastVersionFromObject(Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof FolderType) {
            return "";
        }
        if (object instanceof ProcessSummaryType) {
            ProcessSummaryType process = (ProcessSummaryType) object;
            return process.getLastVersion();
        }
        return "";
    }

    private String getOwnerFromObject(Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof FolderType) {
            return ((FolderType) object).getOwnerName();
        }
        if (object instanceof SummaryType) {
            return ((SummaryType) object).getOwnerName();
        }
        return "";
    }

    private Integer getIdFromObject(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof FolderType) {
            return ((FolderType) object).getId();
        }
        if (object instanceof SummaryType) {
            return ((SummaryType) object).getId();
        }
        return 0;
    }

    private String getUpdateDateFromObject(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof FolderType) {
            return ((FolderType) object).getLastUpdate();
        }
        else if (object instanceof LogSummaryType) {
            return ((SummaryType) object).getCreateDate();
        }
        else if (object instanceof ProcessSummaryType) { // taken from renderer logic
            List<VersionSummaryType> summaries = ((ProcessSummaryType)object).getVersionSummaries();
            int lastIndex = summaries.size() - 1;
            return (lastIndex < 0) ? null : summaries.get(lastIndex).getLastUpdate();
       }
        return null;
    }

    private String getCreatedDateFromObject(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof FolderType) {
            return ((FolderType) object).getCreatedDate();
        }
        else if (object instanceof LogSummaryType) {
            return ((SummaryType) object).getCreateDate();
        }
        else if (object instanceof ProcessSummaryType) { // taken from renderer logic
            return ((SummaryType) object).getCreateDate();
        }
        return null;
    }

    public int compareToInt(Integer id1, Integer id2) {
        if (id1 == null || id2 == null) {
            return 0;
        } else {
            return id1.compareTo(id2);
        }
    }

    public int compareDateString(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return 0;
        } else {
            return DateTimeUtils.parse(date1).compareTo(DateTimeUtils.parse(date2));
        }
    }

    public ArtifactOrderTypes getArtifactOrder() {
        return this.artifactOrderTypes;
    }

    public boolean isAsc() {
        return this.asc;
    }
}
