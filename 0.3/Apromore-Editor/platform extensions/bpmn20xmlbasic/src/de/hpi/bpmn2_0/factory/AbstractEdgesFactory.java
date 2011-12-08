/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.factory;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.generic.GenericEdge;
import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.bpmn2_0.util.DiagramHelper;

/**
 * Abstract factory that contains basic methods to create edges.
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
public abstract class AbstractEdgesFactory extends AbstractBpmnFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hpi.bpmn2_0.factory.common.AbstractBpmnFactory#createBpmnElement(org
	 * .oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	// @Override
	public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent)
			throws BpmnConverterException {

		BPMNEdge diaElement = this.createDiagramElement(shape);
		BaseElement processElement = this.createProcessElement(shape);
		diaElement.setBpmnElement(processElement);
		
		super.setLabelPositionInfo(shape, processElement);

		BPMNElement bpmnElement = new BPMNElement(diaElement, processElement,
				shape.getResourceId());

		// handle external extension elements like from Activiti
		try {
			super.reinsertExternalExtensionElements(shape, bpmnElement);
		} catch (Exception e) {
			
		} 
		
		return bpmnElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hpi.bpmn2_0.factory.common.AbstractBpmnFactory#createDiagramElement
	 * (org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BPMNEdge createDiagramElement(GenericShape shape) {
		BPMNEdge bpmnEdge = new BPMNEdge();

		super.setVisualAttributes(bpmnEdge, shape);

		if (shape instanceof GenericEdge)
			bpmnEdge.getWaypoint().addAll(this.generateBendpoints((GenericEdge)shape));

		return bpmnEdge;
	}

	/**
	 * Creates the bend points of an edge, starting with the second to second
	 * last docker of the edge's shape.
	 * 
	 * @param shape
	 * @return
	 */
	private List<de.hpi.bpmn2_0.model.bpmndi.dc.Point> generateBendpoints(
			GenericEdge<?,?> shape) {
		List<de.hpi.bpmn2_0.model.bpmndi.dc.Point> wayPoints = new ArrayList<de.hpi.bpmn2_0.model.bpmndi.dc.Point>();

		for (int i = 0; i < shape.getDockersReadOnly().size(); i++) {
			Point wayPoint = shape.getDockersReadOnly().get(i);
			de.hpi.bpmn2_0.model.bpmndi.dc.Point bpmnPoint = new de.hpi.bpmn2_0.model.bpmndi.dc.Point(
					wayPoint);

			/* Convert first docker from relative to source shape to absolute */
			if (i == 0) {
				/* Retrieve source element */
				GenericShape sourceShape = shape.getSource();
				if (sourceShape != null) {
					Bounds diagramBounds = sourceShape.getAbsoluteBounds();
					bpmnPoint.setX(bpmnPoint.getX()
							+ diagramBounds.getUpperLeft().getX());
					bpmnPoint.setY(bpmnPoint.getY()
							+ diagramBounds.getUpperLeft().getY());
				}

			}

			/* Convert last docker from relative to absolute */
			else if (i == shape.getDockersReadOnly().size() - 1) {
				GenericShape targetShape = shape.getTarget();
				if (targetShape != null) {
					Bounds diagramBounds = targetShape.getAbsoluteBounds();
					bpmnPoint.setX(bpmnPoint.getX()
							+ diagramBounds.getUpperLeft().getX());
					bpmnPoint.setY(bpmnPoint.getY()
							+ diagramBounds.getUpperLeft().getY());
				}
			}

			/* All other dockers are already coded in absolute coordinates */

			wayPoints.add(bpmnPoint);
		}

		/* Calculate intersection point each with source and target shape */
		GenericShape sourceShape = shape.getSource();
		if (sourceShape != null) {
			de.hpi.bpmn2_0.model.bpmndi.dc.Point intersectionPoint = getIntersectionPoint(
					sourceShape.getAbsoluteBounds(), wayPoints.get(0), wayPoints.get(1));
			
			wayPoints.remove(0);
			wayPoints.add(0, intersectionPoint);
		}
		
		GenericShape targetShape = shape.getTarget();
		if (targetShape != null) {
			de.hpi.bpmn2_0.model.bpmndi.dc.Point intersectionPoint = getIntersectionPoint(
					targetShape.getAbsoluteBounds(), wayPoints.get(shape.getDockersReadOnly().size() - 1), wayPoints.get(shape.getDockersReadOnly().size() - 2));
			
			wayPoints.remove(shape.getDockersReadOnly().size() - 1);
			wayPoints.add(shape.getDockersReadOnly().size() - 1, intersectionPoint);
		}

		return wayPoints;
	}

	private de.hpi.bpmn2_0.model.bpmndi.dc.Point getIntersectionPoint(
			Bounds bounds, de.hpi.bpmn2_0.model.bpmndi.dc.Point a,
			de.hpi.bpmn2_0.model.bpmndi.dc.Point b) {

		// Calculates the intersection between a and b and the bounds
		Algorithm alg = new Algorithm();

		alg.cohenSutherland(a.getX().intValue(), a.getY().intValue(), b.getX()
				.intValue(), b.getY().intValue(), bounds.getUpperLeft().getX()
				.intValue(), bounds.getUpperLeft().getY().intValue(), bounds
				.getLowerRight().getX().intValue(), bounds.getLowerRight()
				.getY().intValue());

		if (alg.getx2() != 0 && alg.gety2() != 0)
			return new de.hpi.bpmn2_0.model.bpmndi.dc.Point(alg.getx2(), alg
					.gety2());

		else
			return a;
	}

	
	private class Algorithm {

		private int RIGHT = 2;
		private int TOP = 8;
		private int BOTTOM = 4;
		private int LEFT = 1;

		private int x1, x2, y1, y2;

		public int computeOutCode(int x, int y, int xmin, int ymin, int xmax,
				int ymax) {
			int code = 0;
			if (y > ymax)
				code |= TOP;
			else if (y < ymin)
				code |= BOTTOM;
			if (x > xmax)
				code |= RIGHT;
			else if (x < xmin)
				code |= LEFT;
			return code;
		}

		public void cohenSutherland(int x1, int y1, int x2, int y2, int xmin,
				int ymin, int xmax, int ymax) {
			// Outcodes for P0, P1, and whatever point lies outside the clip
			// rectangle
			int outcode0, outcode1, outcodeOut, hhh = 0;
			boolean accept = false, done = false;

			// compute outcodes
			outcode0 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
			outcode1 = computeOutCode(x2, y2, xmin, ymin, xmax, ymax);

			do {
				if ((outcode0 | outcode1) == 0) {
					accept = true;
					done = true;
				} else if ((outcode0 & outcode1) > 0) {
					done = true;
				}

				else {
					// failed both tests, so calculate the line segment to clip
					// from an outside point to an intersection with clip edge
					int x = 0, y = 0;
					// At least one endpoint is outside the clip rectangle; pick
					// it.
					outcodeOut = outcode0 != 0 ? outcode0 : outcode1;
					// Now find the intersection point;
					// use formulas y = y0 + slope * (x - x0), x = x0 +
					// (1/slope)* (y - y0)
					if ((outcodeOut & TOP) > 0) {
						x = x1 + (x2 - x1) * (ymax - y1) / (y2 - y1);
						y = ymax;
					} else if ((outcodeOut & BOTTOM) > 0) {
						x = x1 + (x2 - x1) * (ymin - y1) / (y2 - y1);
						y = ymin;
					} else if ((outcodeOut & RIGHT) > 0) {
						y = y1 + (y2 - y1) * (xmax - x1) / (x2 - x1);
						x = xmax;
					} else if ((outcodeOut & LEFT) > 0) {
						y = y1 + (y2 - y1) * (xmin - x1) / (x2 - x1);
						x = xmin;
					}
					// Now we move outside point to intersection point to clip
					// and get ready for next pass.
					if (outcodeOut == outcode0) {
						x1 = x;
						y1 = y;
						outcode0 = computeOutCode(x1, y1, xmin, ymin, xmax,
								ymax);
					} else {
						x2 = x;
						y2 = y;
						outcode1 = computeOutCode(x2, y2, xmin, ymin, xmax,
								ymax);
					}
				}
				hhh++;
			} while (done != true && hhh < 5000);

			if (accept) {
				set(x1, y1, x2, y2);
			}
		}

		public void set(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public int getx1() {
			return x1;
		}

		public int getx2() {
			return x2;
		}

		public int gety1() {
			return y1;
		}

		public int gety2() {
			return y2;
		}
	}

}
