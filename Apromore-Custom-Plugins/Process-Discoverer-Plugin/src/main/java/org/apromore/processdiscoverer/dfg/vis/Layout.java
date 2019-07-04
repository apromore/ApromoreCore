package org.apromore.processdiscoverer.dfg.vis;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

import com.google.gwt.dev.util.collect.HashMap;

/**
 * Represent the layout for BPMNDiagram 
 * @author Bruce Nguyen
 *
 */
public class Layout {
	private Map<String,LayoutElement> elementIDMap = new HashMap<>();
	private double nodeWidth = 10;
	
	public double getNodeWidth() {
		return nodeWidth;
	}

	public void add(LayoutElement element) {
		elementIDMap.put(element.getElementId(), element);
	}
	
	public void remove(LayoutElement element) {
		elementIDMap.remove(element.getElementId());
	}
	
	public Collection<LayoutElement> getLayoutElements() {
		return Collections.unmodifiableCollection(elementIDMap.values());
	}
	
	public LayoutElement getLayoutElement(BPMNNode node) {
		return elementIDMap.get(node.getId().toString());
	}
	
	public double getHorizontalLength(BPMNNode node1, BPMNNode node2) {
		LayoutElement ele1 = this.getLayoutElement(node1);
		LayoutElement ele2 = this.getLayoutElement(node2);
		if (ele1 != null && ele2 != null) {
			return Math.abs(ele1.getX() - ele2.getX());
		}
		else {
			return Double.MAX_VALUE;
		}
	}
	
	public double getVerticalLength(BPMNNode node1, BPMNNode node2) {
		LayoutElement ele1 = this.getLayoutElement(node1);
		LayoutElement ele2 = this.getLayoutElement(node2);
		if (ele1 != null && ele2 != null) {
			return Math.abs(ele1.getY() - ele2.getY());
		}
		else {
			return Double.MAX_VALUE;
		}
	}
	
	public double getHorizontalLength(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, double nodeWidth) {
		return this.getHorizontalLength(edge.getSource(), edge.getTarget()) - nodeWidth;
	}
	
	public double getVerticalLength(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		return this.getVerticalLength(edge.getSource(), edge.getTarget());
	}
	
	/**
	 * Convert from a point coordinate (x,y) to (distance,weight) used in cytoscape segment point
	 * From: stackoverflow.com/questions/53622515/cytoscape-js-for-edge-segment-transforming-coordinates-onto-segment-distances/54551842
	 * @param sX: source node X
	 * @param sY: source node Y
	 * @param tX: target node X
	 * @param tY: target node Y
	 * @param PointX: point X
	 * @param PointY: point Y
	 * @return: 2-element array {distance, weight}
	 */
	public Double[] getDistWeight(double sX, double sY, double tX, double tY, double PointX, double PointY) {
	    double W, D;

	    D = ( PointY - sY + (sX-PointX) * (sY-tY) / (sX-tX) ) /  Math.sqrt( 1 + Math.pow((sY-tY) / (sX-tX), 2) );
	    W = Math.sqrt(  Math.pow(PointY-sY,2) + Math.pow(PointX-sX,2) - Math.pow(D,2)  );

	    double distAB = Math.sqrt(Math.pow(tX-sX, 2) + Math.pow(tY-sY, 2));
	    W = W / distAB;

	    //Check whether the point (PointX, PointY) is on right or left of the line src to tgt. 
	    //For instance : a point C(X, Y) and line (AB).  d=(xB-xA)(yC-yA)-(yB-yA)(xC-xA). 
	    //If d>0, then C is on left of the line. if d<0, it is on right. if d=0, it is on the line.
	    double delta1 = (tX-sX)*(PointY-sY)-(tY-sY)*(PointX-sX);
	    delta1 = (delta1 >= 0) ? 1 : -1;
	        
	    //check whether the point (PointX, PointY) is "behind" the line src to tgt
	    double delta2 = (tX-sX)*(PointX-sX)+(tY-sY)*(PointY-sY);
	    delta2 = (delta2 >= 0) ? 1 : -1;

	    D = Math.abs(D) * delta1;   //ensure that sign of D is same as sign of delta1. Hence we need to take absolute value of D and multiply by delta1
	    W = W * delta2;

	    return new Double[] {D,W};
	}
}
