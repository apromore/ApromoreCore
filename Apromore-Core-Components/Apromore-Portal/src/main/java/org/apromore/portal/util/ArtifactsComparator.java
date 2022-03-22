package org.apromore.portal.util;

import java.util.Comparator;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.portal.common.ArtifactOrderTypes;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;

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
                    -factor;
            case "BY_TYPE": // same as name sorting but always ascending order. Its related logic is kept into another place
                return AlphaNumericComparator.compareTo(getNameFromObject(object1), getNameFromObject(object2));
            case "BY_OWNER":
                return (AlphaNumericComparator.compareTo(getOwnerFromObject(object1), getOwnerFromObject(object2))) *
                    factor;
            case "BY_LAST_VERSION":
                return (AlphaNumericComparator.compareTo(getLastVersionFromObject(object1),
                    getLastVersionFromObject(object2))) *
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
        if (object instanceof SummaryType) {
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
