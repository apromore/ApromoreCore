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
package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLogImpl implements Serializable {

    // =====================================
    // Expect to be the filename of the log
    // =====================================
    protected String logName;

    protected HashBiMap<String, Integer> activityNameIndicatorMap;

    protected final List<ActivityInstance> activityInstances = new ArrayList<>();

    protected String timeZone;

    protected long startTime;
    protected long endTime;

    // ===============================================================================================================
    // Protected methods
    // ===============================================================================================================

    protected void setLogName(String logName) {
        this.logName = logName;
    }

    protected void setActivityNameIndicatorMap(HashBiMap<String, Integer> activityNameIndicatorMap) {
        this.activityNameIndicatorMap = activityNameIndicatorMap;
    }

    // ===============================================================================================================
    // GET methods
    // ===============================================================================================================

    public String getLogName() {
        return logName;
    }

    public List<ActivityInstance> getActivityInstances() {
        return activityInstances;
    }

    public HashBiMap<String, Integer> getActivityNameIndicatorMap() {
        return activityNameIndicatorMap;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    // ===============================================================================================================
    // SET methods
    // ===============================================================================================================

    protected void setActivityInstances(List<ActivityInstance> activityInstances) {
        this.activityInstances.clear();

        if (activityInstances != null) {
            this.activityInstances.addAll(activityInstances);
            startTime = TimeStatsProcessor.getStartTime(activityInstances);
            endTime = TimeStatsProcessor.getEndTime(activityInstances);
        }
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
