/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.processdiscoverer.abstraction;

import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.bpmn.ProcessBPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * This class represents the directly-follows graph abstraction of logs
 * @author Bruce Nguyen
 *
 */
public class DFGAbstraction extends AbstractAbstraction {
	public DFGAbstraction(AttributeLog log, FilteredGraph graph, AbstractionParams params) throws Exception {
		super(log, params);
		this.diagram = new ProcessBPMNDiagram(graph, log);
		
		long timer1 = System.currentTimeMillis();
		this.updateWeights(params);
		System.out.println("Update weights on BPMNDiagram: " + (System.currentTimeMillis() - timer1) + " ms.");
	}
	
	// Create a DFGAbstraction based on an existing DFGAbstraction
	// The diagram would be the same as the input DFGAbstraction, but the
	// weights and other parameters are different
	protected DFGAbstraction(DFGAbstraction dfgAbs, AbstractionParams params) throws Exception {
		super(dfgAbs.getLog(), params);
		this.diagram = dfgAbs.getDiagram();
		this.updateWeights(params);
	}
	

	@Override
	protected void updateArcWeights(AbstractionParams params) throws Exception {
		arcPrimaryWeights.clear();
		arcSecondaryWeights.clear();
		minArcPrimaryWeight = Double.MAX_VALUE;
		maxArcPrimaryWeight = 0;
		
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
			int arc = log.getGraphView().getArc(edge.getSource().getLabel(), edge.getTarget().getLabel());
			if (arc == -1) {
			    throw new Exception("Updating arc weights: unable to find an arc with source=" + edge.getSource().getLabel() + 
			                            ", target=" + edge.getTarget().getLabel());
			}
			
			double arcWeight = log.getGraphView().getArcWeight(arc, params.getPrimaryType(), params.getPrimaryAggregation());
			arcPrimaryWeights.put(edge, arcWeight);
			maxArcPrimaryWeight = Math.max(maxArcPrimaryWeight, arcWeight);
			minArcPrimaryWeight = Math.min(minArcPrimaryWeight, arcWeight);
			if (params.getSecondary()) {
				arcSecondaryWeights.put(edge, log.getGraphView().getArcWeight(arc, params.getSecondaryType(), params.getSecondaryAggregation()));
			}
			
		}
	}
	
	//Return null if not found
	public Double getEdgeWeight(String sourceLabel, String targetLabel, boolean secondary) {
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
			if (edge.getSource().getLabel().equals(sourceLabel) && edge.getTarget().getLabel().equals(targetLabel)) {
				if (!secondary) {
					return arcPrimaryWeights.get(edge);
				}
				else {
					return arcSecondaryWeights.get(edge);
				}
			}
		}
		return null;
	}

    @Override
    public BPMNDiagram getValidBPMNDiagram() {
        return ((ProcessBPMNDiagram)diagram).createBPMNDiagramWithGateways();
    }
}
