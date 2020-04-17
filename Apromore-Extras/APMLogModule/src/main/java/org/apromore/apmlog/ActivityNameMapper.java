/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

/**
 * @author Chii Chang (created: 04/02/2020)
 */
public class ActivityNameMapper {
    private int index = 0;
    private HashBiMap<Integer, String> activityNameIndexBiMap;

    public ActivityNameMapper() {
        activityNameIndexBiMap = new HashBiMap<>();
    }

    public int set(String activityName) {
        if (activityNameIndexBiMap.containsValue(activityName)) {
            return activityNameIndexBiMap.inverse().get(activityName);
        } else {
            index += 1;
            activityNameIndexBiMap.put(index, activityName);
            return index;
        }
    }

    public int get(String activityName) {
        return activityNameIndexBiMap.inverse().get(activityName);
    }

    public String get(int activityNameIndex) {
        return activityNameIndexBiMap.get(activityNameIndex);
    }
}
