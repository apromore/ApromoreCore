/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.logfilter.util;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.Comparator;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Chii Chang (28/01/2020)
 */
public class NumberComparator implements Comparator<Object> {

    private boolean ascending;
    private int columnIndex;

    public NumberComparator(boolean ascending, int columnIndex) {
        this.ascending = ascending;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object listitem_1, Object listitem_2) {
        Double cellValue_1 = Double.parseDouble(((Listcell) ((Listitem) listitem_1).getChildren().get(columnIndex)).getLabel());
        Double cellValue_2 = Double.parseDouble(((Listcell) ((Listitem) listitem_2).getChildren().get(columnIndex)).getLabel());
        return cellValue_1.compareTo(cellValue_2) * (ascending ? 1 : -1);
    }

}