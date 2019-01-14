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

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Coordinates")
public class XPDLCoordinates extends XMLConvertible {

    @Attribute("XCoordinate")
    protected double xCoordinate;
    @Attribute("YCoordinate")
    protected double yCoordinate;

    public double getXCoordinate() {
        return xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public void readJSONx(JSONObject modelElement) {
        setXCoordinate(modelElement.optDouble("x", 0.0));
    }

    public void readJSONy(JSONObject modelElement) {
        setYCoordinate(modelElement.optDouble("y", 0.0));
    }

    public void setXCoordinate(double xValue) {
        xCoordinate = xValue;
    }

    public void setYCoordinate(double yValue) {
        yCoordinate = yValue;
    }
}
