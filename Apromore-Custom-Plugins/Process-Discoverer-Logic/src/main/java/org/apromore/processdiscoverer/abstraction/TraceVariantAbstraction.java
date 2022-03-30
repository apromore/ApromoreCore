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
package org.apromore.processdiscoverer.abstraction;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.bpmn.ProcessBPMNDiagram;
import org.apromore.processdiscoverer.bpmn.TraceVariantBPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

import java.util.List;

/**
 * Abstraction of trace variant
 */
public class TraceVariantAbstraction  extends AbstractAbstraction {
    /**
     * A constructor to create a trace variant abstraction.
     * @param attTraces a list of traces of the same variant.
     * @param log a view of ALog based on an attribute.
     * @param params parameters used to generate different abstractions for a log.
     */
    public TraceVariantAbstraction(List<AttributeTrace> attTraces, AttributeLog log, AbstractionParams params)
            throws IllegalArgumentException
    {
        super(log, params);
        this.diagram = new TraceVariantBPMNDiagram(attTraces, log);
        this.updateWeights(params);
    }

    @Override
    protected void updateNodeWeights(AbstractionParams params) {
        //Do nothing
    }

    @Override
    protected void updateArcWeights(AbstractionParams params) {
        //Do nothing
    }

    @Override
    public void updateWeights(AbstractionParams params) {
        minNodePrimaryWeight = Double.MAX_VALUE;
        maxNodePrimaryWeight = 0;
        minArcPrimaryWeight = Double.MAX_VALUE;
        maxArcPrimaryWeight = 0;

        int nodeId = 0;
        TraceVariantBPMNDiagram traceVariantDiagram = (TraceVariantBPMNDiagram) diagram;
        for(BPMNNode node : diagram.getNodes()) {
            nodePrimaryWeights.put(node, (double)traceVariantDiagram.getNodeWeight(node));
            maxNodePrimaryWeight = Math.max(maxNodePrimaryWeight, nodePrimaryWeights.get(node));
            minNodePrimaryWeight = Math.min(minNodePrimaryWeight, nodePrimaryWeights.get(node));
            nodeSecondaryWeights.put(node, 1.0);

            nodeIdMapping.put(node, nodeId);
            nodeId++;
        }

        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
            arcPrimaryWeights.put(edge, (double)traceVariantDiagram.getArcWeight(edge));
            maxArcPrimaryWeight = Math.max(maxArcPrimaryWeight, arcPrimaryWeights.get(edge));
            minArcPrimaryWeight = Math.min(minArcPrimaryWeight, arcPrimaryWeights.get(edge));
        }

    }

    @Override
    public BPMNDiagram getValidBPMNDiagram() {
        return ((ProcessBPMNDiagram)diagram).createBPMNDiagramWithGateways();
    }

}
