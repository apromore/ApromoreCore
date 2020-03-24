/*
 * $Id: ParallelEdgeRouter.java,v 1.4 2008/11/13 00:12:39 david Exp $
 * 
 * Copyright (c) 2001-2007 Gaudenz Alder
 * Copyright (c) 2004-2007 David Benson
 *  
 */
package org.apromore.jgraph.util2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.AbstractCellView;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.Edge;
import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.apromore.jgraph.graph.GraphModel;
import org.apromore.jgraph.graph.DefaultEdge.LoopRouting;

/**
 * A routing algorithm that 
 */
public class ParallelEdgeRouter extends LoopRouting {
	/**
	 * Singleton to reach parallel edge router
	 */
	protected static final ParallelEdgeRouter sharedInstance = new ParallelEdgeRouter();

	/**
	 * Distance between each parallel edge
	 */
	private static double edgeSeparation = 10.;

	/**
	 * Distance between intermediate and source/target points
	 */
	private static double edgeDeparture = 10.;

	/**
	 * Getter for singleton managing parallel edges
	 * 
	 * @return ParallelEdgeRouter for parallel edges
	 */
	public static ParallelEdgeRouter getSharedInstance() {
		return ParallelEdgeRouter.sharedInstance;
	}

	/**
	 * Calc of intermediates points
	 * 
	 * @param edge
	 *            Edge for which routing is demanding
	 */
	public List routeEdge(GraphLayoutCache cache, EdgeView edge) {
		List newPoints = new ArrayList();

		CellView nodeFrom = edge.getSource();
		CellView nodeTo = edge.getTarget();
		// Check presence of source/target nodes
		if (null == nodeFrom) {
			nodeFrom = edge.getSourceParentView();
		}
		if (null == nodeTo) {
			nodeTo = edge.getTargetParentView();
		}
		if ((null == nodeFrom) || (null == nodeTo)) {
//			System.out.println("EdgeView has no source or target view : "
//					+ edge.toString());
			return null;
		}
		if (nodeFrom == nodeTo) {
//			System.out.println("nodeFrom and NodeTo are the same cell view");
			return null;
		}

		List points = edge.getPoints();
		Object startPort = points.get(0);
		Object endPort = points.get(points.size() - 1);
		newPoints.add(startPort);

		// Promote edges up to first visible connected parents

//		if (graph == null) {
//			System.out
//					.println("graph variable not correctly set, must be set to obtain parallel routing");
//		}
		// Check presence of parallel edges
		Object[] edges = getParallelEdges(cache, edge, nodeFrom, nodeTo);
		if (edges == null) {
			return null;
		}

		// For one edge, no intermediate point
		if (edges.length >= 2) {
//			System.out.println("EdgeView indicates " + edges.length
//					+ " parallel edges");
			// Looking for position of edge
			int position = 0;
//			System.out.println();
//			System.out.println("edges.length = " + edges.length);
			for (int i = 0; i < edges.length; i++) {
//				System.out
//				.println("edge value = "
//						+ String.valueOf(((DefaultGraphCell) edges[i])
//								.getUserObject()));
//				System.out
//				.println("compared edge value = "
//						+ String.valueOf(((DefaultGraphCell) edge.getCell())
//								.getUserObject()));
				Object e = edges[i];
				if (e == edge.getCell()) {
					position = i + 1;
				}
			}
//			System.out.println("position = " + position);
			// Looking for position of source/target nodes (edge=>port=>vertex)
			Point2D from;
			Point2D perimeterPoint = edge.getTarget() != null ? edge
					.getPoint(edge.getPointCount() - 1) : AbstractCellView
					.getCenterPoint(nodeTo);
			if (perimeterPoint == null) {
				perimeterPoint = AbstractCellView.getCenterPoint(nodeTo);
			}
			if (edge.getSource() == null || edge.getSource().getParentView() == null) {
//				System.out.println(edge+"-source promoted");
				from = nodeFrom.getPerimeterPoint(edge,
						AbstractCellView.getCenterPoint(nodeFrom),
						perimeterPoint);
			} else {
				from = edge.getSource().getParentView().getPerimeterPoint(edge,
						AbstractCellView.getCenterPoint(edge.getSource().getParentView()),
						(edge.getTarget() != null && edge.getTarget().getParentView() != null) ?
								AbstractCellView.getCenterPoint(edge.getTarget().getParentView()) :
								AbstractCellView.getCenterPoint(nodeTo));
			}
			Point2D to;
			if (edge.getTarget() == null || edge.getTarget().getParentView() == null) { // INV: nodeTo != null
//				System.out.println(edge+"-target promoted");
				to = nodeTo.getPerimeterPoint(edge, AbstractCellView.getCenterPoint(nodeTo), from);
			} else {
				to = edge.getTarget().getParentView().getPerimeterPoint
					(edge, AbstractCellView.getCenterPoint(edge.getTarget().getParentView()), from);
			}

//			System.out.println("from Point = " + String.valueOf(from));
//			System.out.println("to Point = " + String.valueOf(to));

			if (from != null && to != null) {
				double dy = from.getY() - to.getY();
				double dx = from.getX() - to.getX();

				if (dy == 0 && dx == 0) {
					return null;
				}
				double theta = 0;
				if (dy == 0) {
					theta = Math.PI / 2.0;
				} else if (dx == 0) {
					theta = 0;
				} else {
					double m = dy / dx;
					theta = Math.atan(-1 / m);
				}
				// Calc of radius

				double length = Math.sqrt(dx * dx + dy * dy);
//				System.out.println("length = " + length);
				double rx = dx / length;
				double ry = dy / length;

				// Memorize size of source/target nodes
				double sizeFrom = Math.max(nodeFrom.getBounds().getWidth(),
						nodeFrom.getBounds().getHeight()) / 2.;
				double sizeTo = Math.max(nodeTo.getBounds().getWidth(), nodeTo
						.getBounds().getHeight()) / 2.;

				// Calc position of central point
				double edgeMiddleDeparture = (Math.sqrt(dx * dx + dy * dy)
						- sizeFrom - sizeTo)
						/ 2 + sizeFrom;

				// Calc position of intermediates points
				double edgeFromDeparture = edgeDeparture + sizeFrom;
				double edgeToDeparture = edgeDeparture + sizeTo;

				// Calc distance between edge and mediane source/target
				double r = edgeSeparation * Math.floor(position / 2);
				if (0 == (position % 2)) {
					r = -r;
				}

				// Convert coordinate
				double ex = r * Math.cos(theta);
				double ey = r * Math.sin(theta);

				// Check if is not better to have only one intermediate point
				if (edgeMiddleDeparture <= edgeFromDeparture) {
					double midX = from.getX() - rx * edgeMiddleDeparture;
					double midY = from.getY() - ry * edgeMiddleDeparture;
					Point2D controlPoint = new Point2D.Double(ex + midX, ey
							+ midY);

					// Add intermediate point
					newPoints.add(controlPoint);
				} else {
					double midXFrom = from.getX() - rx * edgeFromDeparture;
					double midYFrom = from.getY() - ry * edgeFromDeparture;
					double midXTo = to.getX() + rx * edgeToDeparture;
					double midYTo = to.getY() + ry * edgeToDeparture;

					Point2D controlPointFrom = new Point2D.Double(
							ex + midXFrom, ey + midYFrom);
					Point2D controlPointTo = new Point2D.Double(ex + midXTo, ey
							+ midYTo);

					// Add intermediates points
					newPoints.add(controlPointFrom);
					newPoints.add(controlPointTo);
				}
				// Reposition the label, only if it's not been moved from its
				// default location
				Point2D labelPos = edge.getLabelPosition();
				if (labelPos != null) {
					double x = labelPos.getX();
					if (x == GraphConstants.PERMILLE / 2) {
						Map allAttributes = edge.getAllAttributes();
						if (allAttributes != null) {
							// Reverse the direction of r for up to down
							// connections
							if (dy < 0) {
								r = -r;
							}
							int lineStyle = getPreferredLineStyle(edge);
							if (lineStyle == Edge.Routing.NO_PREFERENCE) {
								lineStyle = GraphConstants
										.getLineStyle(allAttributes);
							}
							// The middle of the edge (where the label is) can
							// vary in height
							if (lineStyle == GraphConstants.STYLE_BEZIER
									|| lineStyle == GraphConstants.STYLE_SPLINE) {
								// TODO, sort this magic number out
								GraphConstants.setLabelPosition(allAttributes,
										new Point2D.Double(x, r
												* edgeMiddleDeparture / 79));
							} else {
								GraphConstants.setExactSegmentLabel(allAttributes,true);
							}
						}
					}
				}
			}
		}
		newPoints.add(endPort);
		return newPoints;
	}

	/**
	 * Getter to obtain the distance between each parallel edge
	 * 
	 * @return Distance
	 */
	public static double getEdgeSeparation() {
		return ParallelEdgeRouter.edgeSeparation;
	}

	/**
	 * Setter to define distance between each parallel edge
	 * 
	 * @param edgeSeparation
	 *            New distance
	 */
	public static void setEdgeSeparation(double edgeSeparation) {
		ParallelEdgeRouter.edgeSeparation = edgeSeparation;
	}

	/**
	 * Getter to obtain the distance between intermediate and source/target
	 * points
	 * 
	 * @return Distance
	 */
	public static double getEdgeDeparture() {
		return ParallelEdgeRouter.edgeDeparture;
	}

	/**
	 * Setter to define distance between intermediate and source/target points
	 * 
	 * @param edgeDeparture
	 *            New distance
	 */
	public static void setEdgeDeparture(double edgeDeparture) {
		ParallelEdgeRouter.edgeDeparture = edgeDeparture;
	}

	/**
	 * Getter to obtain the list of parallel edges
	 * 
	 * @param edge
	 *            Edge on which one wants to know parallel edges
	 * @return Object[] Array of parallel edges (include edge passed on
	 *         argument)
	 */
	protected Object[] getParallelEdges(GraphLayoutCache cache, EdgeView edge,
			CellView cellView1, CellView cellView2) {
		GraphModel model = cache.getModel();
		Object cell1 = cellView1.getCell();
		Object cell2 = cellView2.getCell();
		// Need to exit if a load has just been performed and the model
		// isn't in place properly yet
		Object[] roots = DefaultGraphModel.getRoots(model);
		if (roots.length == 0) {
			return null;
		}
		// Need to order cells so direction of the edges doesn't
		// affect the ordering of the output edges
		Object[] cells = new Object[] { cell1, cell2 };
		cells = DefaultGraphModel.order(model, cells);
		if (cells == null || cells.length < 2) {
			return null;
		}
		cell1 = cells[0];
		cell2 = cells[1];
		// System.out
		// .println("cell1 of parallel edges = "
		// + String.valueOf(((DefaultGraphCell) cell1)
		// .getUserObject()));
		while (model.getParent(cell1) != null && !cache.isVisible(cell1)) {
			cell1 = model.getParent(cell1);
			// if (cache.isVisible(cell1)) {
			// System.out
			// .println("cell1 promoted to = "
			// + String.valueOf(((DefaultGraphCell) cell1)
			// .getUserObject()));

			// }
		}
		// System.out
		// .println("cell2 of parallel edges = "
		// + String.valueOf(((DefaultGraphCell) cell2)
		// .getUserObject()));
		while (model.getParent(cell2) != null && !cache.isVisible(cell2)) {
			cell2 = model.getParent(cell2);
			// if (cache.isVisible(cell2)) {
			// System.out
			// .println("cell2 promoted to = "
			// + String.valueOf(((DefaultGraphCell) cell2)
			// .getUserObject()));

			// }
		}

		List cell1Children = DefaultGraphModel.getDescendants(model,
				new Object[] { cell1 });
		List cells1 = new ArrayList();
		cells1.add(cell1);
		Iterator iter = cell1Children.iterator();
		while (iter.hasNext()) {
			Object childCell = iter.next();
			if (DefaultGraphModel.isVertex(model, childCell)
					&& (!cache.isVisible(childCell))) {
				cells1.add(childCell);
				// System.out
				// .println("cell1 has child "
				// + String.valueOf(((DefaultGraphCell) childCell)
				// .getUserObject()));
			}
		}

		List cell2Children = DefaultGraphModel.getDescendants(model,
				new Object[] { cell2 });
		List cells2 = new ArrayList();
		cells2.add(cell2);
		iter = cell2Children.iterator();
		while (iter.hasNext()) {
			Object childCell = iter.next();
			if (DefaultGraphModel.isVertex(model, childCell)
					&& (!cache.isVisible(childCell))) {
				cells2.add(childCell);
				// System.out
				// .println("cell2 has child "
				// + String.valueOf(((DefaultGraphCell) childCell)
				// .getUserObject()));
			}
		}

		// Optimise for the standard case of no child cells
		if (cells1.size() == 1 && cells2.size() == 1) {
			// System.out.println("cells have no valid children");
			return DefaultGraphModel
					.getEdgesBetween(model, cell1, cell2, false);
		}
		// The object array to be returned
		Object[] edgesBetween = null;
		Iterator iter1 = cells1.iterator();
		while (iter1.hasNext()) {
			Object tempCell1 = iter1.next();
			Iterator iter2 = cells2.iterator();
			while (iter2.hasNext()) {
				Object tempCell2 = iter2.next();
				Object[] edges = DefaultGraphModel.getEdgesBetween(model,
						tempCell1, tempCell2, false);
				if (edges.length > 0) {
					// for (int i = 0; i < edges.length; i++) {
					// System.out
					// .println("edge between "
					// + i
					// + " = "
					// + String
					// .valueOf(((DefaultGraphCell) edges[i])
					// .getUserObject()));
					// System.out
					// .println("between cell "
					// + String.valueOf(((DefaultGraphCell) tempCell1)
					// .getUserObject()));
					// System.out
					// .println("and cell "
					// + String.valueOf(((DefaultGraphCell) tempCell2)
					// .getUserObject()));
					// }
					if (edgesBetween == null) {
						edgesBetween = edges;
					} else {
						// need to copy everything into a new array
						Object[] newArray = new Object[edges.length
								+ edgesBetween.length];
						System.arraycopy(edgesBetween, 0, newArray, 0,
								edgesBetween.length);
						System.arraycopy(edges, 0, newArray,
								edgesBetween.length, edges.length);
						edgesBetween = newArray;
					}
				}
			}
		}
		return edgesBetween;
	}

	/**
	 * @deprecated graph instance retained internally
	 * @param graph
	 *            The graph to set.
	 */
	public static void setGraph(JGraph graph) {
		// No longer used from 5.10 API
	}

}
