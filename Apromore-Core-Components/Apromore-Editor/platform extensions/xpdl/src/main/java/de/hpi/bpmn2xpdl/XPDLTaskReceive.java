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

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("TaskReceive")
public class XPDLTaskReceive extends XMLConvertible {

    @Attribute("Implementation")
    protected String implementation;
    @Attribute("Instantiate")
    protected String instantiate;

    public String getImplementation() {
        return implementation;
    }

    public String getInstantiate() {
        return instantiate;
    }

    public void readJSONimplementation(JSONObject modelElement) {
        setImplementation(modelElement.optString("implementation"));
    }

    public void readJSONinstantiate(JSONObject modelElement) {
        setInstantiate(modelElement.optString("instantiate"));
    }

    public void readJSONtaskref(JSONObject modelElement) {
    }

    public void readJSONtasktypeunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "tasktypeunknowns");
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public void setInstantiate(String instantiate) {
        this.instantiate = instantiate;
    }

    public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
        modelElement.put("implementation", getImplementation());
    }

    public void writeJSONinstantiate(JSONObject modelElement) throws JSONException {
        modelElement.put("instantiate", getInstantiate());
    }

    public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
        modelElement.put("tasktype", "Receive");
    }

    public void writeJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "tasktypeunknowns");
    }
}
