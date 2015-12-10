/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
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
import org.xmappr.Text;

@RootElement("Condition")
public class XPDLCondition extends XMLConvertible {

    @Attribute("Type")
    protected String conditionType;
    @Text
    protected String conditionExpression;

    public String getConditionType() {
        return conditionType;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void readJSONconditionexpression(JSONObject modelElement) {
        setConditionExpression(modelElement.optString("conditionexpression"));
    }

    public void readJSONconditiontype(JSONObject modelElement) {
        String conditionTypeValue = modelElement.optString("conditiontype");
        if (conditionTypeValue.equals("None")) {
            setConditionType(null);
        } else {
            setConditionType(conditionTypeValue);
        }
    }

    public void readJSONconditionunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "conditionsunknowns");
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public void writeJSONconditionexpression(JSONObject modelElement) throws JSONException {
        modelElement.put("conditionexpression", getConditionExpression());
    }

    public void writeJSONconditiontype(JSONObject modelElement) throws JSONException {
        modelElement.put("conditiontype", getConditionType());
    }

    public void writeJSONconditionunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "conditionunknowns");
    }
}
