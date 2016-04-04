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

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Activities")
public class XPDLActivities extends XMLConvertible {

    @Element("Activity")
    protected ArrayList<XPDLActivity> activities;

    public void add(XPDLActivity newActivity) {
        initializeActivities();

        getActivities().add(newActivity);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getActivities() != null) {
            for (XPDLThing thing : getActivities()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getId(), thing);
            }
        }
    }

    public ArrayList<XPDLActivity> getActivities() {
        return activities;
    }

    public void readJSONactivitiesunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "activitiesunknowns");
    }

    public void setActivities(ArrayList<XPDLActivity> activities) {
        this.activities = activities;
    }

    public void writeJSONactivitiesunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "activitiesunknowns");
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLActivity> activitiesList = getActivities();
        if (activitiesList != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < activitiesList.size(); i++) {
                JSONObject newActivity = new JSONObject();
                activitiesList.get(i).write(newActivity);
                childShapes.put(newActivity);
            }
        }
    }

    protected void initializeActivities() {
        if (getActivities() == null) {
            setActivities(new ArrayList<XPDLActivity>());
        }
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }
}
