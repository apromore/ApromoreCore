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
import org.xmappr.RootElement;

@RootElement("TaskManual")
public class XPDLTaskManual extends XMLConvertible {

    public void readJSONimplementation(JSONObject modelElement) {
    }

    public void readJSONinstantiate(JSONObject modelElement) {
    }

    public void readJSONtaskref(JSONObject modelElement) {
    }

    public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
        modelElement.put("tasktype", "Manual");
    }

    public void readJSONtasktypeunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "tasktypeunknowns");
    }
}
