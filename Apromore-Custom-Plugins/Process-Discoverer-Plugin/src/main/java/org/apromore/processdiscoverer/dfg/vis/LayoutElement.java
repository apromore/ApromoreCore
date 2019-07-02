package org.apromore.processdiscoverer.dfg.vis;

import java.awt.Point;
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
	private List<Point2D> wayPoints = new ArrayList<>();
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
	
	public List<Point2D> getWaypoints() {
		return Collections.unmodifiableList(this.wayPoints);
	}
	
	public boolean isEdge() {
		return !wayPoints.isEmpty();
	}
	
}
