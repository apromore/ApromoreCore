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
