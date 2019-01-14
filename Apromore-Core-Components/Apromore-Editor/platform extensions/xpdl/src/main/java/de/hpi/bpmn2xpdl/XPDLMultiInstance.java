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

@RootElement("MultiInstance")
public class XPDLMultiInstance extends XMLConvertible {

    @Attribute("MI_Condition")
    protected String mi_condition;
    @Attribute("LoopCounter")
    protected String loopCounter;
    @Attribute("MI_Ordering")
    protected String mi_ordering;
    @Attribute("MI_FlowCondition")
    protected String mi_flowCondition;
    @Attribute("ComplexMI_FlowCondition")
    protected String complexMi_flowCondition;

    public String getMi_condition() {
        return mi_condition;
    }

    public String getLoopCounter() {
        return loopCounter;
    }

    public String getMi_ordering() {
        return mi_ordering;
    }

    public String getMi_flowCondition() {
        return mi_flowCondition;
    }

    public String getComplexMi_flowCondition() {
        return complexMi_flowCondition;
    }

    public void readJSONcomplexmi_condition(JSONObject modelElement) {
        setComplexMi_flowCondition(modelElement.optString("complexmi_condition"));
    }

    public void readJSONloopcounter(JSONObject modelElement) {
        setLoopCounter(modelElement.optString("loopcounter"));
    }

    public void readJSONmiloopunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "miloopunknowns");
    }

    public void readJSONmi_condition(JSONObject modelElement) {
        setMi_condition(modelElement.optString("mi_condition"));
    }

    public void readJSONmi_flowcondition(JSONObject modelElement) {
        setMi_flowCondition(modelElement.optString("mi_flowcondition"));
    }

    public void readJSONmi_ordering(JSONObject modelElement) {
        setMi_ordering(modelElement.optString("mi_ordering"));
    }

    public void setMi_condition(String miCondition) {
        mi_condition = miCondition;
    }

    public void setLoopCounter(String loopCounter) {
        this.loopCounter = loopCounter;
    }

    public void setMi_ordering(String miOrdering) {
        mi_ordering = miOrdering;
    }

    public void setMi_flowCondition(String miFlowCondition) {
        mi_flowCondition = miFlowCondition;
    }

    public void setComplexMi_flowCondition(String complexMiFlowCondition) {
        complexMi_flowCondition = complexMiFlowCondition;
    }

    public void writeJSONcomplexmi_condition(JSONObject modelElement) throws JSONException {
        modelElement.put("complexmi_condition", getComplexMi_flowCondition());
    }

    public void writeJSONloopcounter(JSONObject modelElement) throws JSONException {
        modelElement.put("loopcounter", getLoopCounter());
    }

    public void writeJSONmiloopunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "miloopunknowns");
    }

    public void writeJSONmi_condition(JSONObject modelElement) throws JSONException {
        modelElement.put("mi_condition", getMi_condition());
    }

    public void writeJSONmi_flowcondition(JSONObject modelElement) throws JSONException {
        modelElement.put("mi_flowcondition", getMi_flowCondition());
    }

    public void writeJSONmi_ordering(JSONObject modelElement) throws JSONException {
        modelElement.put("mi_ordering", getMi_ordering());
    }
}
