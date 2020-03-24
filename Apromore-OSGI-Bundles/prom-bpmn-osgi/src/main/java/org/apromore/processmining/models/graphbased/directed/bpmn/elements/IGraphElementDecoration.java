package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

/**
 * @author aadrians
 *
 */
public interface IGraphElementDecoration {
	public void decorate(Graphics2D g2d, double x, double y, double width, double height);
}
