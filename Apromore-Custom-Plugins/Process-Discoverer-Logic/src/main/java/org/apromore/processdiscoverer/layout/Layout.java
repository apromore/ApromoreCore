/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.processdiscoverer.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.jgraph.ProMJGraph;

/**
 * Represent the layout for BPMNDiagram 
 * 
 * @todo Layout is coupled with JGraph (ProMJGraph), should be independent of layouter implementations.
 * This requires refactoring of the prom-bpmn module as it is coupled with ProMJGraph.
 * 
 * @author Bruce Nguyen
 *
 */
public class Layout {
	private BPMNDiagram diagram;
	private Map<String,LayoutElement> elementIDMap = new HashMap<>();
	private double nodeWidth = 10;
	private ProMJGraph graphLayout;
	
	public Layout(BPMNDiagram diagram, ProMJGraph graphLayout) {
		this.diagram = diagram;
		this.graphLayout = graphLayout;
	}
	
	public BPMNDiagram getDiagram() {
		return this.diagram;
	}
	
    public ProMJGraph getGraphLayout() {
        return graphLayout;
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
	
	// Cleaning up is required because the layout engine likely consumes substantial memory resources 
	// This is a good thing to do regardless of what layout engine is being used.
	public void cleanUp() {
	    diagram = null;
	    if (elementIDMap != null) elementIDMap.clear();
	    graphLayout.cleanUp();
	}
}
