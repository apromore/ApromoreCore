/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.portal.common;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

import java.util.Comparator;

public class ProcessIdColComparator implements Comparator {
    private boolean _asc;

    public ProcessIdColComparator(boolean asc) {
        _asc = asc;
    }

    public int compare(Object o1, Object o2) {

        //o1 and o2 are rows to be compared according to their 4th children (toolbarbutton)
        Row r1 = (Row) o1,
                r2 = (Row) o2;

        Label l1 = (Label) r1.getChildren().get(3),
                l2 = (Label) r2.getChildren().get(3);

        Integer i1 = Integer.parseInt(l1.getValue()),
                i2 = Integer.parseInt(l2.getValue());

        int v = i1.compareTo(i2);
        return _asc ? v : -v;
    }
}
