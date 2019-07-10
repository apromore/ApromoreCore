package org.apromore.processdiscoverer.dfg.vis;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

import com.google.gwt.dev.util.collect.HashMap;


/**
 * Represent the layout for BPMNDiagram 
 * @author Bruce Nguyen
 *
 */
public class Layout {
	private BPMNDiagram diagram;
	private Map<String,LayoutElement> elementIDMap = new HashMap<>();
	private double nodeWidth = 10;
	
	public Layout(BPMNDiagram diagram) {
		this.diagram = diagram;
	}
	
	public BPMNDiagram getDiagram() {
		return this.diagram;
	}
	
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
	
	public LayoutElement getLayoutElement(String elementId) {
		return elementIDMap.get(elementId);
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
}
