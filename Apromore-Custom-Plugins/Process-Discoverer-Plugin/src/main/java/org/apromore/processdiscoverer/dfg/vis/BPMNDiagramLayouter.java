package org.apromore.processdiscoverer.dfg.vis;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.elements.ProMGraphPort;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.UIManager;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.DefaultGraphCell;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.views.JGraphPortView;

public class BPMNDiagramLayouter {
	
	public static Layout layout(BPMNDiagram diagram) throws Exception {
		UIContext context = new UIContext();
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        UIPluginContext uiPluginContext = context.getMainPluginContext();
		ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(uiPluginContext, diagram);
		ProMGraphModel graphModel = graphPanel.getGraph().getModel();

		Layout layout = new Layout(diagram);
		for (Object o : graphModel.getRoots()) {
			if (o instanceof ProMGraphCell) {
				ProMGraphCell graphCell = (ProMGraphCell) o;
				getCellLayout(graphCell, layout);
			}
			if (o instanceof ProMGraphPort) {
				ProMGraphPort graphPort = (ProMGraphPort) o;
				if(graphPort.getBoundingNode() != null) {
					getCellLayout(graphPort, layout);
				}
			}
			if (o instanceof ProMGraphEdge) {
				ProMGraphEdge graphEdge = (ProMGraphEdge) o;
				getEdgeLayout(graphEdge, layout);
			}
		}
		
		// Add (distance, weight) points for edges
		// Btw, fix the special case of loop L2 (A-->B, B-->A) because JGraph
		// creates sub-optimal waypoints
		Set<BPMNEdge> checkedLoopEdges = new HashSet<>();
		for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getEdges()) {
			if (checkedLoopEdges.contains(edge)) continue;
			
			List<Point2D> dwPoints = getDistWeight(edge, layout);
			if (!dwPoints.isEmpty()) {
				boolean fixedLoopL2 = false;
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> loopEdge = getLoopL2Edge(edge, diagram);
				if (loopEdge != null) {
					List<Point2D> loopdwPoints = getDistWeight(loopEdge, layout);
					if (!loopdwPoints.isEmpty()) { 
						double d1 = dwPoints.get(0).getX();
						double w1 = dwPoints.get(0).getY();
						w1 = (w1 > 0) ? 0.5 : -0.5; // move to the middle of source to target
						
						double d2 = loopdwPoints.get(0).getX();
						double w2 = loopdwPoints.get(0).getY();
						w2 = (w2 > 0) ? 0.5 : -0.5; // move to the middle of source to target
						
						// The edge closer to the line source to target would keep the same distance 
						BPMNEdge<? extends BPMNNode, ? extends BPMNNode> unchangedDistanceEdge = Math.abs(d1) < Math.abs(d2) ? edge : loopEdge;
						double unchangedDistance = unchangedDistanceEdge == edge ? d1 : d2;
						double unchangedWeight = unchangedDistanceEdge == edge ? w1 : w2;
						
						// The edge further to the line source to target would be placed opposite the other edge over the line
						BPMNEdge<? extends BPMNNode, ? extends BPMNNode> changedDistanceEdge = (unchangedDistanceEdge == edge) ? loopEdge : edge;
						double changedDistance = (unchangedDistanceEdge == edge) ? d1 : d2;
						double changedWeight = (unchangedDistanceEdge == edge) ? w2 : w1;
						
						// Adjust distance so that they are not too close
						if (Math.abs(unchangedDistance) + Math.abs(unchangedDistance) < 10) {
							unchangedDistance = (unchangedDistance < 0) ? unchangedDistance - 2 : unchangedDistance + 2;
							changedDistance = (changedDistance < 0) ? changedDistance - 2 : changedDistance + 2;
						}

						layout.getLayoutElement(unchangedDistanceEdge.getEdgeID().toString()).addDistanceWeightPoint(unchangedDistance, unchangedWeight);
						layout.getLayoutElement(changedDistanceEdge.getEdgeID().toString()).addDistanceWeightPoint(changedDistance, changedWeight);
						
						fixedLoopL2 = true;
					}
					checkedLoopEdges.add(loopEdge);
				}
				
				if (!fixedLoopL2) {
					LayoutElement edgeLayout = layout.getLayoutElement(edge.getEdgeID().toString());
					for (Point2D p : dwPoints) {
						edgeLayout.addDistanceWeightPoint(p.getX(), p.getY());
					}
				}

			}
			
		}
		
		return layout;
	}
	
	private static void getCellLayout(DefaultGraphCell graphCell, Layout layout) {
		DirectedGraphNode graphNode = null;
		if(graphCell instanceof ProMGraphCell) {
			graphNode = ((ProMGraphCell)graphCell).getNode();
		} else if (graphCell instanceof ProMGraphPort) {
			graphNode = ((ProMGraphPort)graphCell).getBoundingNode();
		}
		// Create BPMNShape
//		String bpmnElementId = graphNode.getId().toString().replace(' ', '_');
		String bpmnElementId = graphNode.getId().toString();
		boolean isExpanded = false;
		boolean isHorizontal = false;
		if(graphNode instanceof SubProcess) {
			SubProcess subProcess = (SubProcess)graphNode;
			if(!subProcess.isBCollapsed()) {
				isExpanded = true;
			}
		}
		if(graphNode instanceof Swimlane) {				
			isExpanded = true;
			isHorizontal = true;
		}
		AbstractCellView view = null;
		if(graphCell instanceof ProMGraphCell) {
			view = ((ProMGraphCell)graphCell).getView();
		} else if(graphCell instanceof ProMGraphPort) {
			view = ((ProMGraphPort)graphCell).getView();
		}
		Rectangle2D rectangle = view.getBounds();
		
		double x = rectangle.getX();
		double y = rectangle.getY();
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();
		layout.add(new LayoutElement(bpmnElementId, x, y, width, height, isExpanded, isHorizontal));
		
		getChildLayout(graphCell, layout);
	}
	
	private static void getChildLayout(DefaultGraphCell graphCell, Layout layout){
		for (Object o : graphCell.getChildren()) {
			if (o instanceof ProMGraphCell) {
				ProMGraphCell childGraphCell = (ProMGraphCell) o;
				getCellLayout(childGraphCell, layout);
			}
			if (o instanceof ProMGraphPort) {
				ProMGraphPort childGraphPort = (ProMGraphPort) o;
				if(childGraphPort.getBoundingNode() != null) {
					getCellLayout(childGraphPort, layout);
				}
			}
			if (o instanceof ProMGraphEdge) {
				ProMGraphEdge childGraphEdge = (ProMGraphEdge) o;
				getEdgeLayout(childGraphEdge, layout);
			}
		}
	}
	
	private static void getEdgeLayout(ProMGraphEdge graphEdge, Layout layout) {
		@SuppressWarnings("rawtypes")
		BPMNEdge bpmnEdge = (BPMNEdge)graphEdge.getEdge();
//		String bpmnElementId = bpmnEdge.getEdgeID().toString().replace(' ', '_');
		String bpmnElementId = bpmnEdge.getEdgeID().toString();
		
		LayoutElement layoutEdge = new LayoutElement(bpmnElementId);
		for (Object point : graphEdge.getView().getPoints()) {
			Point2D point2D;
			if(point instanceof JGraphPortView) {
				JGraphPortView portView = (JGraphPortView) point;
				point2D = portView.getLocation();
			} else if(point instanceof Point2D) {
				point2D = (Point2D)point;
			} else {
				continue;
			}
			double x = point2D.getX();
			double y = point2D.getY();
			layoutEdge.addWayPoint(x, y);
		}
		layout.add(layoutEdge);
	}
	
	/**
	 * Convert from a point coordinate (x,y) to (distance,weight) used in cytoscape segment point
	 * @param sX: source node X
	 * @param sY: source node Y
	 * @param tX: target node X
	 * @param tY: target node Y
	 * @param PointX: point X
	 * @param PointY: point Y
	 * @return: Point2D (x=distance, y=weight)
	 */
	private static Point2D getDistWeight(double sX, double sY, double tX, double tY, double pX, double pY) {
	    double W, D;

	    // Distance D is the height from p of the triangle (s,p,t).
	    // So, it is calculated based on Heron's formula for triangle's area
	    // en.wikipedia.org/wiki/Heron%27s_formula
	    // Then, W is calculated based on D and sp
	    double sp =  Math.sqrt(Math.pow(sX-pX,2) + Math.pow(sY-pY,2)); //the side s to p
	    double tp =  Math.sqrt(Math.pow(tX-pX,2) + Math.pow(tY-pY,2)); //the side t to p
	    double st =  Math.sqrt(Math.pow(sX-tX,2) + Math.pow(sY-tY,2)); //the side s to t
	    double semiP = (sp+tp+st)/2; //semi perimeter of the triangle s, p, t
	    
	    if (st == 0) return null;
	    
	    D = 2*Math.sqrt(semiP*(semiP-sp)*(semiP-tp)*(semiP-st))/st;
	    W = Math.sqrt(Math.pow(sp,2) - Math.pow(D,2))/st;
	    

	    //Check whether the point (pX, pY) is on "right" or "left" of the line src to tgt.
	    //This is calculated based on the cross-product of two vectors: st and sp
	    //st = {tX-sX,tY-sY}, sp = {pX-sX,pY-sY}
	    //The cross-product of two vectors is positive if and only if the angle of 
	    //those vectors is in counter-clockwise
	    //www.geeksforgeeks.org/direction-point-line-segment/
	    double delta1 = (tX-sX)*(pY-sY) - (tY-sY)*(pX-sX);
	    if (delta1 < 0) {
	    	delta1 = -1;
	    }
	    else if (delta1 > 0) {
	    	delta1 = 1;
	    }
	    D = D * delta1;
	        
	    //check whether the point (pX, pY) is "behind" the line src to tgt
	    //This is calculated based on the dot-product of two vectors: st and sp
	    //The point is behind the line s to t if the angle between st and sp is obtuse or reflex
	    //www.mathsisfun.com/algebra/vectors-dot-product.html
	    double delta2 = (tX-sX)*(pX-sX) + (tY-sY)*(pY-sY);
	    delta2 = (delta2 >= 0) ? 1 : -1;
	    W = W * delta2;
	    
	    return new Point2D.Double(D, W);
	}
	
//	public Point2D getDistWeight2(double sX, double sY, double tX, double tY, double pX, double pY) {
//	    double W, D;
//
//	    D = ( pY - sY + (sX-pX) * (sY-tY) / (sX-tX) ) /  Math.sqrt( 1 + Math.pow((sY-tY) / (sX-tX), 2) );
//	    W = Math.sqrt(  Math.pow(pX-sY,2) + Math.pow(pX-sX,2) - Math.pow(D,2)  );
//	    double distAB = Math.sqrt(Math.pow(tX-sX, 2) + Math.pow(tY-sY, 2));
//	    W = W / distAB;
//
//	    //Check whether the point (PointX, PointY) is on right or left of the line src to tgt. 
//	    //For instance : a point C(X, Y) and line (AB).  d=(xB-xA)(yC-yA)-(yB-yA)(xC-xA). 
//	    //If d>0, then C is on left of the line. if d<0, it is on right. if d=0, it is on the line.
//	    double delta1 = (tX-sX)*(pY-sY)-(tY-sY)*(pX-sX);
//	    delta1 = (delta1 >= 0) ? 1 : -1;
//	        
//	    //check whether the point (PointX, PointY) is "behind" the line src to tgt
//	    double delta2 = (tX-sX)*(pX-sX)+(tY-sY)*(pY-sY);
//	    delta2 = (delta2 >= 0) ? 1 : -1;
//
//	    D = Math.abs(D) * delta1;   //ensure that sign of D is same as sign of delta1. Hence we need to take absolute value of D and multiply by delta1
//	    W = W * delta2;
//
//	    return new Point2D.Double(D, W);
//	}
	
	/**
	 * Return a list of distance-weight points (X=distance, Y=weight) for an edge
	 * @param edge
	 * @return
	 */
	private static List<Point2D> getDistWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, Layout layout) {
		LayoutElement edgeLayout = layout.getLayoutElement(edge.getEdgeID().toString());
		LayoutElement s = layout.getLayoutElement(edge.getSource().getId().toString());
		LayoutElement t = layout.getLayoutElement(edge.getTarget().getId().toString());
		
		List<Point2D> waypoints = edgeLayout.getWaypoints();
		List<Point2D> dwPoints = new ArrayList<>();
		

		if (waypoints.size() > 2) {
			for (int i=1; i<waypoints.size()-1; i++) {
				Point2D p = waypoints.get(i);
				Point2D dw = getDistWeight(s.getX(), s.getY(), t.getX(), t.getY(), p.getX(), p.getY());
				if (dw != null) dwPoints.add(dw);
			}
		}
		
		//Fix as JGraph does not make good waypoints for LoopL2 edges
//		if (BPMNDiagramLayouter.isLoopL2Edge(edge, layout.getDiagram()) && !dwPoints.isEmpty()) { 
//			double D = dwPoints.get(0).getX();
//			double W = dwPoints.get(0).getY();
//			dwPoints.clear();
//			dwPoints.add(W > 0 ? new Point2D.Double(D, 0.5) : new Point2D.Double(D, -0.5));
//		}
		
		return dwPoints;
	}
	
	private static BPMNEdge<? extends BPMNNode, ? extends BPMNNode> getLoopL2Edge(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, BPMNDiagram diagram) {
		BPMNNode target = edge.getTarget();
		BPMNNode source = edge.getSource();
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(target)) {
			if (e.getTarget() == source) {
				return e;
			}
		}
		return null;
	}
}
