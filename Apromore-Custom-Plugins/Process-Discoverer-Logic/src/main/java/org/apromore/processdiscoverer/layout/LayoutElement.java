/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.processdiscoverer.layout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * If node: elementId, x,y,width,height
 * If edge: elementId, wayPoints
 * @author Bruce Nguyen
 *
 */
public class LayoutElement {
	private String elementId;
	private double x;
	private double y;
	private double width;
	private double height;	
	private List<Point2D> wayPoints = new ArrayList<>(); //waypoints used by some graph libraries like JGraph, bpmn.io.
	private List<Point2D> dwPoints = new ArrayList<>(); //(distance,weight) points used by some graph libraries like cytoscape 
	Boolean isExpanded;
	Boolean isHorizontal;
	
	public LayoutElement(String elementId) {
		this.elementId = elementId;	
	}
	
	public LayoutElement(String elementId, double x, double y, double width, double height, 
						boolean isExpanded, boolean isHorizontal) {
		this.elementId = elementId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.isExpanded = isExpanded;
		this.isHorizontal = isHorizontal;
	}
	
	public String getElementId() {
		return this.elementId;
	}
	
	public double getX() {
		return this.x;
	}
	
	public void setX(double newX) {
		this.x = newX;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setY(double newY) {
		this.y = newY;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public Boolean isExpanded() {
		return this.isExpanded;
	}
	
	public Boolean isHorizontal() {
		return this.isHorizontal;
	}
	
	public void addWayPoint(double x, double y) {
		this.wayPoints.add(new Point2D.Double(x, y));
	}
	
	public void addDistanceWeightPoint(double d, double w) {
		this.dwPoints.add(new Point2D.Double(d, w));
	}
	
	public List<Point2D> getWaypoints() {
		return Collections.unmodifiableList(this.wayPoints);
	}
	
	public List<Point2D> getDWPoints() {
		return Collections.unmodifiableList(this.dwPoints);
	}
	
	public boolean isEdge() {
		return !wayPoints.isEmpty();
	}
	
}
