/**
 * 
 */
package org.processmining.models.shapes;

import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

/**
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version Apr 5, 2010
 * 
 */
public class RoundedRect extends AbstractShape {

	public GeneralPath getPath(double x, double y, double width, double height) {
		double m = Math.max(width, height) * .125;

		// Width and height have correct ratio;
		GeneralPath path = new GeneralPath();

		// main border
		java.awt.Shape rect = new RoundRectangle2D.Double(x, y, width, height, m, m);
		path.append(rect, false);

		return path;
	}

}
