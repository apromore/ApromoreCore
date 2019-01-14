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

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("GraphicsInfo")
public abstract class XPDLGraphicsInfo extends XMLConvertible {

    @Attribute("BorderColor")
    protected String borderColor = "#0,0,0";
    @Element("Coordinates")
    protected ArrayList<XPDLCoordinates> coordinates;
    @Attribute("FillColor")
    protected String fillColor;
    @Attribute("ToolId")
    protected String toolId = "Oryx";

    public XPDLGraphicsInfo() {
        setCoordinates(new ArrayList<XPDLCoordinates>());
    }

    public String getBorderColor() {
        return borderColor;
    }

    public ArrayList<XPDLCoordinates> getCoordinates() {
        return coordinates;
    }

    public String getFillColor() {
        return fillColor;
    }

    public String getToolId() {
        return toolId;
    }

    public void readJSONbgcolor(JSONObject modelElement) {
        setFillColor(modelElement.optString("bgcolor"));
    }

    public void readJSONbounds(JSONObject modelElement) {
    }

    public void setBorderColor(String color) {
        borderColor = color;
    }

    public void setCoordinates(ArrayList<XPDLCoordinates> coordinatesList) {
        coordinates = coordinatesList;
    }

    public void setFillColor(String color) {
        fillColor = color;
    }

    public void setToolId(String tool) {
        toolId = tool;
    }

    public void writeJSONbgcolor(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "bgcolor", getFillColor());
    }

    protected XPDLCoordinates createCoordinates(JSONObject modelElement) {
        XPDLCoordinates createdCoordinates = new XPDLCoordinates();
        createdCoordinates.parse(modelElement);
        return createdCoordinates;
    }

    protected JSONObject getProperties(JSONObject modelElement) {
        return modelElement.optJSONObject("properties");
    }

    protected void initializeCoordinates() {
        if (getCoordinates() == null) {
            setCoordinates(new ArrayList<XPDLCoordinates>());
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

    protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
        initializeProperties(modelElement);

        getProperties(modelElement).put(key, value);
    }

    protected void writeEmptyBounds(JSONObject modelElement) throws JSONException {
        JSONObject upperLeft = new JSONObject();
        upperLeft.put("x", 0);
        upperLeft.put("y", 0);

        JSONObject lowerRight = new JSONObject();
        lowerRight.put("x", 0);
        lowerRight.put("y", 0);

        JSONObject bounds = new JSONObject();
        bounds.put("upperLeft", upperLeft);
        bounds.put("lowerRight", lowerRight);

        modelElement.put("bounds", bounds);
    }
}
