package org.apromore.portal.util;


import java.util.Comparator;
import org.apromore.portal.model.FolderType;

/*
 * @author Mohammad Ali
 */

public class FolderTypeComparator implements Comparator<FolderType> {

    public int compare(FolderType folderType1, FolderType folderType2) {
        if (folderType1 == null || folderType2 == null) {
            return 0;
        }
        return AlphaNumericComparator.compareTo(folderType1.getFolderName(), folderType2.getFolderName());
    }
}