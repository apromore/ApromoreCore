/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.apromore.model.*;
import java.io.Serializable;
import java.util.Comparator;

public class SummaryComparator implements Comparator<Object>, Serializable {

    private boolean asc = true;
    private int type = 0;

    public SummaryComparator(boolean asc, int type) {
        this.asc = asc;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName(Object obj) {
        if (obj instanceof SummaryType) {
            SummaryType summaryType = (SummaryType)obj;
            return summaryType.getName();
        } else if (obj instanceof FolderType) {
            FolderType folder = (FolderType)obj;
            return folder.getFolderName();
        }
        return obj.toString();
    }

    @Override
    public int compare(Object o1, Object o2) {
        String name1, name2;

        switch (type) {
            case 1:
                name1 = getName(o1);
                name2 = getName(o2);
                return name1.compareTo(name2) * (asc ? 1 : -1);
            default: // name
                name1 = getName(o1);
                name2 = getName(o2);
                return name1.compareTo(name2) * (asc ? 1 : -1);
        }
    }
}
