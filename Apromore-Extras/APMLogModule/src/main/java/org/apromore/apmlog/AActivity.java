/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
package org.apromore.apmlog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chii Chang (11/2019)
 */
public class AActivity  {
    private String name;
    private List<AEvent> eventList;
    private long startTimeMilli = 0;
    private long endTimeMilli = 0;
    private long duration = 0;

    public AActivity(String name, List<AEvent> eventList, long startTimeMilli, long endTimeMilli,
                     long duration) {
        this.name = name;
        this.eventList = eventList;
        this.startTimeMilli = startTimeMilli;
        this.endTimeMilli = endTimeMilli;
        this.duration = duration;
    }

    public AActivity(List<AEvent> eventList) {
        this.name = eventList.get(0).getName();
        this.eventList = eventList;
        this.startTimeMilli = eventList.get(0).getTimestampMilli();
        this.endTimeMilli = eventList.get(eventList.size()-1).getTimestampMilli();
        if(endTimeMilli > startTimeMilli) this.duration = endTimeMilli - startTimeMilli;
    }

    public String getName() {
        return name;
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
        String clnName = this.name;
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
