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

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("TriggerResultMessage")
public class XPDLTriggerResultMessage extends XMLConvertible {

    @Attribute("CatchThrow")
    protected String catchThrow;
    @Element("Message")
    protected XPDLMessage message;

    public String getCatchThrow() {
        return catchThrow;
    }

    public XPDLMessage getMessage() {
        return message;
    }

    public void readJSONmessage(JSONObject modelElement) throws JSONException {
        passInformationToMessage(modelElement, "message");
    }

    public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
        passInformationToMessage(modelElement, "messageunknowns");
    }

    public void readJSONtriggerresultunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "triggerresultunknowns");
    }

    public void setCatchThrow(String catchThrow) {
        this.catchThrow = catchThrow;
    }

    public void setMessage(XPDLMessage message) {
        this.message = message;
    }

    public void writeJSONmessage(JSONObject modelElement) throws JSONException {
        XPDLMessage messageObject = getMessage();
        if (messageObject != null) {
            initializeProperties(modelElement);
            messageObject.write(getProperties(modelElement));
        }
    }

    public void writeJSONtriggerresultunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "triggerresultunknowns");
    }

    protected JSONObject getProperties(JSONObject modelElement) {
        return modelElement.optJSONObject("properties");
    }

    protected void initializeMessage(JSONObject modelElement) {
        if (getMessage() == null) {
            setMessage(new XPDLMessage());
        }
    }

    protected void initializeProperties(JSONObject modelElement) throws JSONException {
        JSONObject properties = modelElement.optJSONObject("properties");
        if (properties == null) {
            JSONObject newProperties = new JSONObject();
            modelElement.put("properties", newProperties);
            properties = newProperties;
        }
    }

    protected void passInformationToMessage(JSONObject modelElement, String key) throws JSONException {
        initializeMessage(modelElement);

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));

        getMessage().parse(passObject);
    }

    protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
        initializeProperties(modelElement);

        getProperties(modelElement).put(key, value);
    }
}
