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

package de.unihannover.se.infocup2008.bpmn.model;

import de.hpi.layouting.model.LayoutingDockers;
import de.hpi.layouting.model.LayoutingDockers.Point;
import de.hpi.layouting.model.LayoutingElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BPMNElementJSON extends BPMNAbstractElement implements BPMNElement {

    private JSONObject elementJSON;
    private JSONObject boundsJSON;
    private JSONArray dockersJSON;

    public void updateDataModel() {
        try {
            //bounds
            boundsJSON.getJSONObject("upperLeft").put("x", this.getGeometry().getX());
            boundsJSON.getJSONObject("upperLeft").put("y", this.getGeometry().getY());
            boundsJSON.getJSONObject("lowerRight").put("x", this.getGeometry().getX2());
            boundsJSON.getJSONObject("lowerRight").put("y", this.getGeometry().getY2());
            //dockers
            JSONArray dockers = new JSONArray();
            for (Point p : this.getDockers().getPoints()) {
                JSONObject point = new JSONObject();
                point.put("x", p.x);
                point.put("y", p.y);
                dockers.put(point);
            }
            elementJSON.put("dockers", dockers);
        } catch (JSONException e) {

        }
    }

    public void setBoundsJSON(JSONObject boundsJSON) {
        this.boundsJSON = boundsJSON;
    }

    public JSONObject getBoundsJSON() {
        return boundsJSON;
    }

    public void setDockersJSON(JSONArray dockers) {
        this.dockersJSON = dockers;
    }

    public JSONArray getDockersJSON() {
        return dockersJSON;
    }

    public void setElementJSON(JSONObject elementJSON) {
        this.elementJSON = elementJSON;
    }

    public JSONObject getElementJSON() {
        return elementJSON;
    }

}
