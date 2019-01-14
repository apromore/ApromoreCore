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

@RootElement("ConformanceClass")
public class XPDLConformanceClass extends XMLConvertible {

    @Attribute("GraphConformance")
    protected String graphConformance = "NON-BLOCKED";
    @Attribute("BPMNModelPortabilityConformance")
    protected String bpmnConformance = "STANDARD";

    public String getBpmnConformance() {
        return bpmnConformance;
    }

    public String getGraphConformance() {
        return graphConformance;
    }

    public void readJSONconformanceclassunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "conformanceclassunknowns");
    }

    public void setBpmnConformance(String conformance) {
        bpmnConformance = conformance;
    }

    public void setGraphConformance(String conformance) {
        graphConformance = conformance;
    }

    public void writeJSONconformanceclassunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "conformanceclassunknowns");
    }
}
