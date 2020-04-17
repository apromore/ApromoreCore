/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.logman.attribute.log;

import org.apromore.logman.attribute.graph.AttributeLogGraph;

public class AttributeLogGraphView {
    private AttributeLogGraph logGraph;
    
    public AttributeLogGraphView(AttributeLog attLog) {
        logGraph = new AttributeLogGraph(attLog);
    }
    
    public AttributeLogGraph getLogGraph() {
        return logGraph;
    }
    
    protected void reset() {
        logGraph.reset();
    }
    
    protected void add(AttributeTrace trace) {
        logGraph.addNewNodes(trace.getActiveNodes());
        logGraph.addNewArcs(trace.getActiveArcs());
        
        trace.getActiveNodes().forEach(node -> {
            updateLogGraphNodeWeights(node, trace);
        });
        trace.getActiveArcs().forEach(arc -> {
            updateLogGraphArcWeights(arc, trace);
        });
    }
    
    protected void finalUpdate() {
        logGraph.updateFinalWeights();
    }
    
    private void updateLogGraphNodeWeights(int node, AttributeTrace trace) {
        logGraph.incrementNodeTotalFrequency(node, trace.getNodeTotalCount(node));
        logGraph.incrementNodeCaseFrequency(node, trace.getNodeTotalCount(node));
        logGraph.updateNodeMinFrequency(node, trace.getNodeTotalCount(node));
        logGraph.updateNodeMaxFrequency(node, trace.getNodeTotalCount(node));
        
        logGraph.incrementNodeTotalDuration(node, trace.getNodeTotalDuration(node));
        logGraph.updateNodeMinDuration(node, trace.getNodeMinDuration(node));
        logGraph.updateNodeMaxDuration(node, trace.getNodeMaxDuration(node));
    }
    
    private void updateLogGraphArcWeights(int arc, AttributeTrace trace) {
        logGraph.incrementArcTotalFrequency(arc, trace.getArcTotalCount(arc));
        logGraph.incrementArcCaseFrequency(arc, trace.getArcTotalCount(arc));
        logGraph.updateArcMinFrequency(arc, trace.getArcTotalCount(arc));
        logGraph.updateArcMaxFrequency(arc, trace.getArcTotalCount(arc));
        
        logGraph.incrementArcTotalDuration(arc, trace.getArcTotalDuration(arc));
        logGraph.updateArcMinDuration(arc, trace.getArcMinDuration(arc));
        logGraph.updateArcMaxDuration(arc, trace.getArcMaxDuration(arc));
    }
}
