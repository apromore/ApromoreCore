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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("NodeGraphicsInfo")
public class XPDLNodeGraphicsInfo extends XPDLGraphicsInfo {

    @Attribute("FillColor")
    protected String fillColor = "#255,255,255";
    @Attribute("Height")
    protected double height = 60;
    @Attribute("LaneId")
    protected String laneId;
    @Attribute("Width")
    protected double width = 60;

    public double getHeight() {
        return height;
    }

    public String getLaneId() {
        return laneId;
    }

    public double getWidth() {
        return width;
    }

    public void readJSONbounds(JSONObject modelElement) {
        JSONObject bounds = modelElement.optJSONObject("bounds");
        JSONObject upperLeft = bounds.optJSONObject("upperLeft");
        JSONObject lowerRight = bounds.optJSONObject("lowerRight");

        getCoordinates().add(createCoordinates(upperLeft));

        setHeight(lowerRight.optDouble("y") - upperLeft.optDouble("y"));
        setWidth(lowerRight.optDouble("x") - upperLeft.optDouble("x"));
    }

    public void readJSONgraphicsinfounknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "graphicsinfounknowns");
    }

    public void setHeight(double heightValue) {
        height = heightValue;
    }

    public void setLaneId(String lane) {
        laneId = lane;
    }

    public void setWidth(double widthValue) {
        width = widthValue;
    }

    public void writeJSONbounds(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
        if (coordinatesList != null) {
            if (coordinatesList.size() > 0) {
                XPDLCoordinates firstCoordinate = coordinatesList.get(0);
                JSONObject upperLeft = new JSONObject();
                upperLeft.put("x", firstCoordinate.getXCoordinate());
                upperLeft.put("y", firstCoordinate.getYCoordinate());


                JSONObject lowerRight = new JSONObject();
                lowerRight.put("x", firstCoordinate.getXCoordinate() + getWidth());
                lowerRight.put("y", firstCoordinate.getYCoordinate() + getHeight());

                JSONObject bounds = new JSONObject();
                bounds.put("upperLeft", upperLeft);
                bounds.put("lowerRight", lowerRight);

                modelElement.put("bounds", bounds);
            } else {
                writeEmptyBounds(modelElement);
            }
        } else {
            writeEmptyBounds(modelElement);
        }
    }

    public void writeJSONdockers(JSONObject modelElement) throws JSONException {
        JSONArray dockers = new JSONArray();

        ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
        if (coordinatesList != null) {
            if (coordinatesList.size() > 0) {
                XPDLCoordinates firstCoordinate = coordinatesList.get(0);
                JSONObject docker = new JSONObject();
                docker.put("x", firstCoordinate.getXCoordinate() + getWidth() / 2);
                docker.put("y", firstCoordinate.getYCoordinate() + getHeight() / 2);

                dockers.put(docker);
            } else {
                writeEmptyBounds(modelElement);
            }
        }

        modelElement.put("dockers", dockers);
    }

    public void writeJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "graphicsinfounknowns");
    }
}
