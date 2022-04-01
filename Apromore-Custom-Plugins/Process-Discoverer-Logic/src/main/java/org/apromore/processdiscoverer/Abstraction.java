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

package org.apromore.processdiscoverer;

import org.apromore.processdiscoverer.layout.Layout;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * Abstraction represents an abstraction of log or trace.
 * It can be a directly-follows graph or a BPMN diagram model
 * In addition, it is affected by different abstraction parameters
 * It uses BPMNDiagram as a underlying representation for layout and visualization
 * The nodes and edges on the diagram can be two types of weights: primary and secondary
 * and they can be displayed at the same time 
 * 
 * @author Bruce Nguyen
 *
 */
public interface Abstraction {
	AbstractionParams getAbstractionParams();
	BPMNDiagram getDiagram(); // could be a graph in a BPMNDiagram representation
	BPMNDiagram getValidBPMNDiagram(); // always a valid BPMNDiagram
	int getNodeId(BPMNNode node);
	double getNodePrimaryWeight(BPMNNode node);
	double getNodeSecondaryWeight(BPMNNode node);
	double getArcPrimaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
	double getArcSecondaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
	double getMinNodePrimaryWeight();
	double getMaxNodePrimaryWeight();
	double getMinEdgePrimaryWeight();
	double getMaxEdgePrimaryWeight();
	double getNodeRelativePrimaryWeight(BPMNNode node);
	double getEdgeRelativePrimaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
	
	Layout getLayout();
	void setLayout(Layout layout);
	boolean equal(Abstraction other);
	void cleanUp(); // An abstraction is usually memory-intensive, needs cleanup.
}
