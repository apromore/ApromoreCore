/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

@RootElement("Lanes")
public class XPDLLanes extends XMLConvertible {

    @Element("Lane")
    protected ArrayList<XPDLLane> lanes;

    public void add(XPDLLane newLane) {
        initializeLanes();

        getLanes().add(newLane);
    }

    public ArrayList<XPDLLane> getLanes() {
        return lanes;
    }

    public void readJSONlanesunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "lanesunknowns");
    }

    public void setLanes(ArrayList<XPDLLane> lanes) {
        this.lanes = lanes;
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLLane> lanesList = getLanes();
        if (lanesList != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < lanesList.size(); i++) {
                JSONObject newLane = new JSONObject();
                lanesList.get(i).write(newLane);
                childShapes.put(newLane);
            }
        }
    }

    public void writeJSONlanesunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "lanesunknowns");
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void initializeLanes() {
        if (getLanes() == null) {
            setLanes(new ArrayList<XPDLLane>());
        }
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getLanes() != null) {
            for (XPDLThing thing : getLanes()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getResourceId(), thing);
            }
        }
    }
}
