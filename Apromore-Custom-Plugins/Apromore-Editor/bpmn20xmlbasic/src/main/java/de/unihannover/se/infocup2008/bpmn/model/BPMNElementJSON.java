
package de.unihannover.se.infocup2008.bpmn.model;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 The University of Melbourne.
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * #L%
 */

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
