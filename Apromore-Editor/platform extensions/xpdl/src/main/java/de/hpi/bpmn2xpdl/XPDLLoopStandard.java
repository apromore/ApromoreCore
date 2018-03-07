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

@RootElement("LoopStandard")
public class XPDLLoopStandard extends XMLConvertible {

    @Attribute("LoopCondition")
    protected String loopCondition;
    @Attribute("LoopCounter")
    protected String loopCounter;
    @Attribute("LoopMaximum")
    protected String loopMaximum;
    @Attribute("TestTime")
    protected String testTime;

    public String getLoopCondition() {
        return loopCondition;
    }

    public String getLoopCounter() {
        return loopCounter;
    }

    public String getLoopMaximum() {
        return loopMaximum;
    }

    public String getTestTime() {
        return testTime;
    }

    public void readJSONloopcondition(JSONObject modelElement) {
        setLoopCondition(modelElement.optString("loopcondition"));
    }

    public void readJSONloopcounter(JSONObject modelElement) {
        setLoopCounter(modelElement.optString("loopcounter"));
    }

    public void readJSONloopmaximum(JSONObject modelElement) {
        setLoopMaximum(modelElement.optString("loopmaximum"));
    }

    public void readJSONstandardloopunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "standardloopunknowns");
    }

    public void readJSONtesttime(JSONObject modelElement) {
        setTestTime(modelElement.optString("testtime"));
    }

    public void setLoopCondition(String loopCondition) {
        this.loopCondition = loopCondition;
    }

    public void setLoopCounter(String loopCounter) {
        this.loopCounter = loopCounter;
    }

    public void setLoopMaximum(String loopMaximum) {
        this.loopMaximum = loopMaximum;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public void writeJSONloopcondition(JSONObject modelElement) throws JSONException {
        modelElement.put("loopcondition", getLoopCondition());
    }

    public void writeJSONloopcounter(JSONObject modelElement) throws JSONException {
        modelElement.put("loopcounter", getLoopCounter());
    }

    public void writeJSONloopmaximum(JSONObject modelElement) throws JSONException {
        modelElement.put("loopmaximum", getLoopMaximum());
    }

    public void writeJSONstandardloopunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "standardloopunknowns");
    }

    public void writeJSONtesttimes(JSONObject modelElement) throws JSONException {
        modelElement.put("testtime", getTestTime());
    }
}
