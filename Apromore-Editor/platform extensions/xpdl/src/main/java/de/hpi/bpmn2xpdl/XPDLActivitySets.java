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

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ActivitySets")
public class XPDLActivitySets extends XMLConvertible {

    @Element("ActivitySet")
    protected ArrayList<XPDLActivitySet> actvitySets;

    public void add(XPDLActivitySet set) {
        initializeActivitySets();

        actvitySets.add(set);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getActvitySets() != null) {
            for (XPDLActivitySet thing : getActvitySets()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getId(), thing);
                thing.createAndDistributeMapping(mapping);
            }
        }
    }

    public ArrayList<XPDLActivitySet> getActvitySets() {
        return actvitySets;
    }

    public void readJSONactivitysetsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "activitysetsunknowns");
    }

    public void setActvitySets(ArrayList<XPDLActivitySet> actvitySets) {
        this.actvitySets = actvitySets;
    }

    public void write(JSONObject modelElement, ArrayList<XPDLActivity> activities) throws JSONException {
        ArrayList<XPDLActivitySet> unmapped = getActvitySets();
        if (getActvitySets() != null) {
            for (int i = 0; i < getActvitySets().size(); i++) {
                for (int j = 0; j < activities.size(); j++) {
                    XPDLActivity searchActivity = activities.get(j);
                    if (searchActivity.getBlockActivity() != null) {
                        XPDLBlockActivity block = searchActivity.getBlockActivity();
                        if (block.getActivitySetId().equals(getActvitySets().get(i).getId())) {
                            block.setActivitySet(getActvitySets().get(i));
                            unmapped.remove(getActvitySets().get(i));
                        }
                    }
                }
            }
        }
        writeUnmappedActivitySets(modelElement, unmapped);
    }

    public void writeJSONactivitysetsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "activitysetsunknowns");
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLActivitySet> activitySetsList = getActvitySets();
        if (activitySetsList != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < activitySetsList.size(); i++) {
                JSONObject newActivitySet = new JSONObject();
                activitySetsList.get(i).write(newActivitySet);
                childShapes.put(newActivitySet);
            }
        }
    }

    protected void initializeActivitySets() {
        if (getActvitySets() == null) {
            setActvitySets(new ArrayList<XPDLActivitySet>());
        }
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void writeUnmappedActivitySets(JSONObject modelElement, ArrayList<XPDLActivitySet> sets) throws JSONException {
        if (sets != null) {
            for (int i = 0; i < sets.size(); i++) {
                sets.get(i).writeUnmapped(modelElement);
            }
        }
    }
}
