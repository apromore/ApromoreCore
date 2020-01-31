package org.apromore.plugin.portal.logfilter.util;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.Comparator;

/**
 * @author Bruce Hoang Nguyen (29/08/2019)
 * Modified by Chii Chang (28/01/2020)
 */
public class StringComparator implements Comparator<Object> {

    private boolean ascending;
    private int columnIndex;

    public StringComparator(boolean ascending, int columnIndex) {
        this.ascending = ascending;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object listitem_1, Object listitem_2) {
        String cellLabel_1 = ((Listcell) ((Listitem) listitem_1).getChildren().get(columnIndex)).getLabel();
        String cellLabel_2 = ((Listcell) ((Listitem) listitem_2).getChildren().get(columnIndex)).getLabel();
        return cellLabel_1.compareTo(cellLabel_2) * (ascending ? 1 : -1);
    }
}
