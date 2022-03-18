package org.apromore.portal.util;

import java.util.Comparator;
import org.apromore.portal.model.SummaryType;
/*
 * @author Mohammad Ali
 */
public class SummaryTypeComparator implements Comparator<SummaryType> {
    @Override
    public int compare(SummaryType summaryType1, SummaryType summaryType2) {
        if (summaryType1 == null || summaryType2 == null) {
            return 0;
        }
        return AlphaNumericComparator.compareTo(summaryType1.getName(), summaryType2.getName());
    }
}
