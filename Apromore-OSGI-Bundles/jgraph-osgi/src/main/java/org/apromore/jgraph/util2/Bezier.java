/*
 * Copyright (c) 2005 David Benson
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.util2;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Interpolates given points by a bezier curve. The first
 * and the last two points are interpolated by a quadratic
 * bezier curve; the other points by a cubic bezier curve.
 * 
 * Let p a list of given points and b the calculated bezier points,
 * then one get the whole curve by:
 * 
 * sharedPath.moveTo(p[0])
 * sharedPath.quadTo(b[0].x, b[0].getY(), p[1].x, p[1].getY());
 * 
 * for(int i = 2; i < p.length - 1; i++ ) {
 *    Point b0 = b[2*i-3];
 *	  Point b1 = b[2*i-2];
 *	  sharedPath.curveTo(b0.x, b0.getY(), b1.x, b1.getY(), p[i].x, p[i].getY());
 * }
 * 
 * sharedPath.quadTo(b[b.length-1].x, b[b.length-1].getY(), p[n - 1].x, p[n - 1].getY());
 * 
 * @author krueger
 */
public class Bezier {

	private static final float AP = 0.5f;
	private Point2D[] bPoints;

	/**
	 * Creates a new Bezier curve.
	 * @param points
	 */
	public Bezier(Point2D[] points) {
		int n = points.length;
		if (n < 3) {
			// Cannot create bezier with less than 3 points
			return;
		}
		bPoints = new Point[2 * (n - 2)];
		double paX, paY;
		double pbX = points[0].getX();
		double pbY = points[0].getY();
		double pcX = points[1].getX();
		double pcY = points[1].getY();
		for (int i = 0; i < n - 2; i++) {
			paX = pbX;
			paY = pbY;
			pbX = pcX;
			pbY = pcY;
			pcX = points[i + 2].getX();
			pcY = points[i + 2].getY();
			double abX = pbX - paX;
			double abY = pbY - paY;
			double acX = pcX - paX;
			double acY = pcY - paY;
			double lac = Math.sqrt(acX * acX + acY * acY);
			acX = acX /lac;
			acY = acY /lac;

			double proj = abX * acX + abY * acY;
			proj = proj < 0 ? -proj : proj;
			double apX = proj * acX;
			double apY = proj * acY;

			double p1X = pbX - AP * apX;
			double p1Y = pbY - AP * apY;
			bPoints[2 * i] = new Point((int) p1X, (int) p1Y);

			acX = -acX;
			acY = -acY;
			double cbX = pbX - pcX;
			double cbY = pbY - pcY;
			proj = cbX * acX + cbY * acY;
			proj = proj < 0 ? -proj : proj;
			apX = proj * acX;
			apY = proj * acY;

			double p2X = pbX - AP * apX;
			double p2Y = pbY - AP * apY;
			bPoints[2 * i + 1] = new Point((int) p2X, (int) p2Y);
		}
	}

	/**
	 * Returns the calculated bezier points.
	 * @return the calculated bezier points
	 */
	public Point2D[] getPoints() {
		return bPoints;
	}

	/**
	 * Returns the number of bezier points.
	 * @return number of bezier points
	 */
	public int getPointCount() {
		return bPoints.length;
	}

	/**
	 * Returns the bezier points at position i.
	 * @param i
	 * @return the bezier point at position i
	 */
	public Point2D getPoint(int i) {
		return bPoints[i];
	}

}
