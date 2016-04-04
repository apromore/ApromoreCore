/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("TriggerTimer")
public class XPDLTriggerTimer extends XMLConvertible {

    @Attribute("TimerCycle")
    protected String timerCycle;
    @Attribute("TimerDate")
    protected String timerDate;

    public String getTimerCycle() {
        return timerCycle;
    }

    public String getTimerDate() {
        return timerDate;
    }

    public void readJSONtimecycle(JSONObject modelElement) {
        setTimerCycle(modelElement.optString("timecycle"));
    }

    public void readJSONtimedate(JSONObject modelElement) {
        setTimerDate(modelElement.optString("timedate"));
    }

    public void readJSONtriggerresultunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "triggerresultunknowns");
    }

    public void setTimerCycle(String timerCycle) {
        this.timerCycle = timerCycle;
    }

    public void setTimerDate(String timerDate) {
        this.timerDate = timerDate;
    }

    public void writeJSONtimecycle(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "timecycle", getTimerCycle());
    }

    public void writeJSONtimedate(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "timedate", getTimerDate());
    }

    public void writeJSONtriggerresultunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "triggerresultunknowns");
    }

    protected JSONObject getProperties(JSONObject modelElement) {
        return modelElement.optJSONObject("properties");
    }

    protected void initializeProperties(JSONObject modelElement) throws JSONException {
        JSONObject properties = modelElement.optJSONObject("properties");
        if (properties == null) {
            JSONObject newProperties = new JSONObject();
            modelElement.put("properties", newProperties);
            properties = newProperties;
        }
    }

    protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
        initializeProperties(modelElement);

        getProperties(modelElement).put(key, value);
    }
}
