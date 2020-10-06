/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.apmlog;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (07/10/2020) - "schedule" event included; added start time method.
 */
public class AActivity  {
    private String name;
    private List<AEvent> eventList;
    private long startTimeMilli = 0;
    private long endTimeMilli = 0;
    private long duration = 0;

    public AActivity(String name, List<AEvent> eventList, long startTimeMilli, long endTimeMilli,
                     long duration) {
        this.name = name.intern();
        this.eventList = eventList;
        this.startTimeMilli = startTimeMilli;
        this.endTimeMilli = endTimeMilli;
        this.duration = duration;
    }

    public AActivity(List<AEvent> eventList) {
        this.name = eventList.get(0).getName().intern();
        this.eventList = eventList;
        this.startTimeMilli = getStartEvent(eventList).getTimestampMilli();
        this.endTimeMilli = eventList.get(eventList.size()-1).getTimestampMilli();
        if(endTimeMilli > startTimeMilli) this.duration = endTimeMilli - startTimeMilli;
    }

    private AEvent getStartEvent(List<AEvent> events) {

        if (events.size() > 1) {
            for (int i = 0; i < events.size(); i++) {
                AEvent iEvent = events.get(i);
                if (iEvent.getLifecycle().toLowerCase().equals("start")) return iEvent;
            }
        }
        return events.get(0);

    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return eventList.get(0).getResource();
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return eventList.get(0).getAttributeMap();
    }

    public List<AEvent> getEventList() {
        return eventList;
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public long getEndTimeMilli() {
        return endTimeMilli;
    }

    public long getDuration() {
        return duration;
    }

    public AActivity clone() {
        String clnName = this.name.intern();
        List<AEvent> clnEventList = new ArrayList<>();
        for(int i = 0; i<this.eventList.size(); i++) {
            AEvent aEvent = this.eventList.get(i).clone();
            clnEventList.add(aEvent);
        }
        long clnStartTimeMilli = this.startTimeMilli;
        long clnEndTimeMilli = this.endTimeMilli;
        long clnDuration = this.duration;
        AActivity activity = new AActivity(clnName, clnEventList, clnStartTimeMilli,
                clnEndTimeMilli, clnDuration);
        return activity;
    }
}
