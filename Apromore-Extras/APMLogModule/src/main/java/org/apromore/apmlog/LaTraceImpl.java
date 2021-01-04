/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.apmlog;

import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.List;

/**
 * @author Chii Chang (07/10/2020)
 * Moved the common methods of ATrace and PTrace to this class
 */
public class LaTraceImpl {

    public IntArrayList markedIndex;

    public IntArrayList getFollowUpIndexList(List<AEvent> eventList, int fromIndex, AEvent baseEvent) {
        IntArrayList followUpIndex = new IntArrayList();

        boolean startObtained = false;

        if ( (fromIndex + 1) < eventList.size()) {
            for (int i = (fromIndex + 1); i < eventList.size(); i++) {
                if (!markedIndex.contains(i)) {
                    AEvent aEvent = eventList.get(i);
                    String lifecycle = aEvent.getLifecycle().toLowerCase();

                    if (haveCommonMainAttributes(aEvent, baseEvent)) {
                        boolean valid = true;
                        if (lifecycle.equals("start") && startObtained) valid = false;

                        if (valid) {
                            followUpIndex.add(i);
                            if (lifecycle.equals("start")) startObtained = true;
                        }

                        if (lifecycle.equals("complete") ||
                                lifecycle.equals("manualskip") ||
                                lifecycle.equals("autoskip")) {
                            break;
                        }
                    }
                }
            }
            return followUpIndex;
        } else return null;
    }

    public boolean haveCommonMainAttributes(AEvent event1, AEvent event2) {
        return event1.getName().equals(event2.getName());
//        if (!event1.getName().equals(event2.getName())) return false;
//        if (!event1.getResource().equals(event2.getResource())) return false;
//        UnifiedMap<String, String> attrMap1 = event1.getAttributeMap();
//        UnifiedMap<String, String> attrMap2 = event2.getAttributeMap();
//        for (String key : attrMap1.keySet()) {
//            String val1 = attrMap1.get(key);
//            String val2 = attrMap2.get(key);
//            if (!val1.equals(val2)) return false;
//        }
//        return true;
    }
}
