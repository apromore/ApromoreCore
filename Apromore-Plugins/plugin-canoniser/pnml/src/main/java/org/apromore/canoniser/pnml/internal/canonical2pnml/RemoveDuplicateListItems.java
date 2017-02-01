/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RemoveDuplicateListItems {
    List<String> list = new LinkedList<String>();

    public List<String> transform(List<String> list) {
        this.list = list;
        Collections.sort(list);
        String lastValue = null;
        for (Iterator<String> i = list.iterator(); i.hasNext(); ) {
            String currentValue = i.next();
            if (lastValue != null && currentValue.equals(lastValue)) {

            } else {
                i.remove();
            }
            lastValue = currentValue;
        }
        return list;
    }

}
