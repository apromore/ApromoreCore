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

@RootElement("Transitions")
public class XPDLTransitions extends XMLConvertible {

    @Element("Transition")
    protected ArrayList<XPDLTransition> transitions;

    public void add(XPDLTransition newTransition) {
        initializeTransitions();

        getTransitions().add(newTransition);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getTransitions() != null) {
            for (XPDLThing thing : getTransitions()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getId(), thing);
            }
        }
    }

    public ArrayList<XPDLTransition> getTransitions() {
        return transitions;
    }

    public void readJSONtransitionsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "transitionsunknowns");
    }

    public void setTransitions(ArrayList<XPDLTransition> transitions) {
        this.transitions = transitions;
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLTransition> transitionsList = getTransitions();
        if (transitionsList != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < transitionsList.size(); i++) {
                JSONObject newTransition = new JSONObject();
                transitionsList.get(i).write(newTransition);
                childShapes.put(newTransition);
            }
        }
    }

    public void writeJSONtransitionsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "transitionsunknowns");
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void initializeTransitions() {
        if (getTransitions() == null) {
            setTransitions(new ArrayList<XPDLTransition>());
        }
    }
}
