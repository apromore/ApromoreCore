package org.apromore.plugin.portal.logfilter.util;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.Comparator;

public class StringComparator implements Comparator<Object> {

    private boolean ascending;
    private int columnIndex;

    public StringComparator(boolean ascending, int columnIndex) {
        this.ascending = ascending;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object o1, Object o2) {
        String contributor1 = ((Listcell) ((Listitem) o1).getChildren().get(columnIndex)).getLabel();
        String contributor2 = ((Listcell) ((Listitem) o2).getChildren().get(columnIndex)).getLabel();
        return contributor1.compareTo(contributor2) * (ascending ? 1 : -1);
    }
}
