package org.processmining.models.shapes;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Shape {

	public GeneralPath getPath(double x, double y, double width, double height);

	public Point2D getPerimeterPoint(Rectangle2D bounds, Point2D source, Point2D p);

}
