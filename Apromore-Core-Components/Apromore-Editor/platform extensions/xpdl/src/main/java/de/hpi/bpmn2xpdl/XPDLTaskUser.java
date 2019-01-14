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

@RootElement("TaskUser")
public class XPDLTaskUser extends XMLConvertible {

    @Attribute("Implementation")
    protected String implementation;

    public String getImplementation() {
        return implementation;
    }

    public void readJSONimplementation(JSONObject modelElement) {
        setImplementation(modelElement.optString("implementation"));
    }

    public void readJSONinstantiate(JSONObject modelElement) {
    }

    public void readJSONtaskref(JSONObject modelElement) {
    }

    public void readJSONtasktypeunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "tasktypeunknowns");
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
        modelElement.put("implementation", getImplementation());
    }

    public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
        modelElement.put("tasktype", "User");
    }

    public void writeJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "tasktypeunknowns");
    }
}
