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
package de.hbrs.oryx.yawl.converter.layout;

import java.util.ArrayList;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

/**
 * Layout information of a Flow
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class FlowLayout {

    private Bounds bounds;
    private ArrayList<Point> dockers;
    private String label;
    private int lineStyle;

    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setDockers(final ArrayList<Point> dockers) {
        this.dockers = dockers;
    }

    public ArrayList<Point> getDockers() {
        return dockers;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLineStyle(final int linestyle) {
        this.lineStyle = linestyle;
    }

    public int getLineStyle() {
        return lineStyle;
    }

}
