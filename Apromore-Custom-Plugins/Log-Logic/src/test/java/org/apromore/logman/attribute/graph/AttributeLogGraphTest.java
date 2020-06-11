/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.logman.attribute.graph;

import org.apromore.logman.ALog;
import org.apromore.logman.Constants;
import org.apromore.logman.DataSetup;
import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.junit.Assert;
import org.junit.Test;

public class AttributeLogGraphTest extends DataSetup {

    @Test
    public void test_OneTraceAndCompleteEvents() throws Exception {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        
        AttributeLogGraph graph = attLog.getGraphView();
        graph.buildSubGraphs(attLog.getAttribute(), MeasureType.FREQUENCY, MeasureAggregation.TOTAL, false, false);
        
        Assert.assertEquals(IntSets.mutable.of(0,1,2,3,4,5), graph.getNodes());
        Assert.assertEquals(IntSets.mutable.of(0,1,2,8,12,15,17,20,24), graph.getArcs());
        Assert.assertEquals(6, graph.getNodeBitMask().cardinality());
        Assert.assertEquals(9, graph.getArcBitMask().cardinality());
        Assert.assertEquals(4, graph.getSourceNode());
        Assert.assertEquals(5, graph.getSinkNode());
        
        Assert.assertEquals("a", graph.getNodeName(0));
        Assert.assertEquals("b", graph.getNodeName(1));
        Assert.assertEquals("c", graph.getNodeName(2));
        Assert.assertEquals("d", graph.getNodeName(3));
        Assert.assertEquals(Constants.START_NAME, graph.getNodeName(4));
        Assert.assertEquals(Constants.END_NAME, graph.getNodeName(5));
        
        Assert.assertEquals(IntLists.mutable.of(1,4,5,3,0,2), graph.getSortedNodes());
        
        Assert.assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(100)); // non-existent node
        Assert.assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(100));
        
        Assert.assertEquals(IntSets.mutable.of(0,1,2), graph.getOutgoingArcs(0)); //a
        Assert.assertEquals(IntSets.mutable.of(0,12,24), graph.getIncomingArcs(0)); 
        
        Assert.assertEquals(IntSets.mutable.of(8), graph.getOutgoingArcs(1)); //b
        Assert.assertEquals(IntSets.mutable.of(1), graph.getIncomingArcs(1)); 
        
        Assert.assertEquals(IntSets.mutable.of(12,15,17), graph.getOutgoingArcs(2)); //c
        Assert.assertEquals(IntSets.mutable.of(2,8,20), graph.getIncomingArcs(2)); 
        
        Assert.assertEquals(IntSets.mutable.of(20), graph.getOutgoingArcs(3)); //d
        Assert.assertEquals(IntSets.mutable.of(15), graph.getIncomingArcs(3)); 
        
        Assert.assertEquals(IntSets.mutable.of(24), graph.getOutgoingArcs(4)); //source -1
        Assert.assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(4)); 
        
        Assert.assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(5)); //sink -2
        Assert.assertEquals(IntSets.mutable.of(17), graph.getIncomingArcs(5)); 
        
        Assert.assertEquals(0, graph.getArc(0, 0)); //a->a
        Assert.assertEquals(1, graph.getArc(0, 1)); //a->b
        Assert.assertEquals(2, graph.getArc(0, 2)); //a->c
        Assert.assertEquals(8, graph.getArc(1, 2)); //b->c
        Assert.assertEquals(12, graph.getArc(2, 0)); //c->a
        Assert.assertEquals(15, graph.getArc(2, 3)); //c->d
        Assert.assertEquals(20, graph.getArc(3, 2)); //d->c
        Assert.assertEquals(24, graph.getArc(4, 0)); //-1 -> a
        Assert.assertEquals(17, graph.getArc(2, 5)); //c->-2
        
        Assert.assertEquals(IntLists.mutable.of(1,2,8,12,17,24,0,15,20), graph.getSortedArcs());
        
        Assert.assertEquals(4, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(4, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(2, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        
        // 0,1,2,8,12,15,17,20,24
        Assert.assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(1, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        
        Assert.assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        
        Assert.assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        
        Assert.assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.0);
        
        Assert.assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.0);
        
        Assert.assertEquals(180000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(720000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(840000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(60000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(300000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(360000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        
        Assert.assertEquals(120000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(420000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(480000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        
        Assert.assertEquals(90000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(360000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(420000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        
        Assert.assertEquals(90000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(360000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(420000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        
        
        //Subgraphs
        
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), graph.getSubGraphs().get(0).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), graph.getSubGraphs().get(0).getArcs());
        
        Assert.assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), graph.getSubGraphs().get(1).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 12, 15, 17, 20, 24), graph.getSubGraphs().get(1).getArcs());
        
        Assert.assertEquals(IntSets.mutable.of(0, 2, 4, 5), graph.getSubGraphs().get(2).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 12, 17, 24), graph.getSubGraphs().get(2).getArcs());
        
        FilteredGraph nodeBasedGraph2 = graph.getSubGraphs().get(2);
        Assert.assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(0).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 12, 17, 24), nodeBasedGraph2.getSubGraphs().get(0).getArcs());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(1).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 17, 24), nodeBasedGraph2.getSubGraphs().get(1).getArcs());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(2).getNodes());
        Assert.assertEquals(IntSets.mutable.of(2, 17, 24), nodeBasedGraph2.getSubGraphs().get(2).getArcs());  
        
        FilteredGraph nodeBasedGraph1 = graph.getSubGraphs().get(1);
        Assert.assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(0).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 12, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(0).getArcs());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(1).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(1).getArcs());
        Assert.assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(2).getNodes());
        Assert.assertEquals(IntSets.mutable.of(2, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(2).getArcs());  
        
        FilteredGraph nodeBasedGraph0 = graph.getSubGraphs().get(0);
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(0).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(0).getArcs());  
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(1).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 1, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(1).getArcs());     
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(2).getNodes());
        Assert.assertEquals(IntSets.mutable.of(0, 1, 8, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(2).getArcs());           
        Assert.assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(3).getNodes());
        Assert.assertEquals(IntSets.mutable.of(1, 8, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(3).getArcs());  

    }
    
    @Test 
    public void test_Exception() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeLogGraph graph = attLog.getGraphView();
        graph.buildSubGraphs(attLog.getAttribute(), MeasureType.FREQUENCY, MeasureAggregation.TOTAL, false, false);
        
        // Add invalid node: node not exist in the matrix graph
        boolean addResult = graph.addNode(100);
        Assert.assertEquals(false, addResult);
        
        // Add invalid node: node already exists in the current graph
        addResult = graph.addNode(1);
        Assert.assertEquals(false, addResult);
        
        int arc1 = graph.getArc(0, 2);
        Assert.assertNotEquals(-1, arc1);
        
        int arc2 = graph.getArc(100, 0);
        Assert.assertEquals(-1, arc2);
    }
    
    @Test
    public void test_LogWithCompleteEventsOnly() {
        ALog log = new ALog(readLogWithCompleteEventsOnly());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        
        AttributeLogGraph graph = attLog.getGraphView();
        graph.buildSubGraphs(attLog.getAttribute(), MeasureType.FREQUENCY, MeasureAggregation.TOTAL, false, false);
        
        Assert.assertEquals(IntSets.mutable.of(0,1,2,3,4,5,6), graph.getNodes());
        Assert.assertEquals(IntSets.mutable.of(1,3,4,9,20,23,25,30,31,35), graph.getArcs());
        Assert.assertEquals(7, graph.getNodeBitMask().cardinality());
        Assert.assertEquals(10, graph.getArcBitMask().cardinality());
        Assert.assertEquals(5, graph.getSourceNode());
        Assert.assertEquals(6, graph.getSinkNode());
        
        Assert.assertEquals("a", graph.getNodeName(0));
        Assert.assertEquals("e", graph.getNodeName(1));
        Assert.assertEquals("d", graph.getNodeName(2));
        Assert.assertEquals("c", graph.getNodeName(3));
        Assert.assertEquals("b", graph.getNodeName(4));
        Assert.assertEquals("|>", graph.getNodeName(5));
        Assert.assertEquals("[]", graph.getNodeName(6));
        Assert.assertEquals(Constants.START_NAME, graph.getNodeName(5));
        Assert.assertEquals(Constants.END_NAME, graph.getNodeName(6));
        
        Assert.assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(100)); // non-existent node
        Assert.assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(100));
        
        Assert.assertEquals(IntSets.mutable.of(1,3,4), graph.getOutgoingArcs(0)); //a
        Assert.assertEquals(IntSets.mutable.of(35), graph.getIncomingArcs(0)); 
        
        Assert.assertEquals(IntSets.mutable.of(9), graph.getOutgoingArcs(1)); //b
        Assert.assertEquals(IntSets.mutable.of(1), graph.getIncomingArcs(1)); 
        
        Assert.assertEquals(IntSets.mutable.of(20), graph.getOutgoingArcs(2)); //c
        Assert.assertEquals(IntSets.mutable.of(9,23,30), graph.getIncomingArcs(2)); 
        
        Assert.assertEquals(IntSets.mutable.of(23,25), graph.getOutgoingArcs(3)); //d
        Assert.assertEquals(IntSets.mutable.of(3,31), graph.getIncomingArcs(3)); 
        
        Assert.assertEquals(IntSets.mutable.of(30,31), graph.getOutgoingArcs(4)); 
        Assert.assertEquals(IntSets.mutable.of(4,25), graph.getIncomingArcs(4)); 
        
        Assert.assertEquals(IntSets.mutable.of(35), graph.getOutgoingArcs(5)); 
        Assert.assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(5)); 
        
        Assert.assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(6)); 
        Assert.assertEquals(IntSets.mutable.of(20), graph.getIncomingArcs(6));
        
        Assert.assertEquals(1, graph.getArc(0, 1)); //a->a
        Assert.assertEquals(3, graph.getArc(0, 3)); //a->b
        Assert.assertEquals(4, graph.getArc(0, 4)); //a->c
        Assert.assertEquals(9, graph.getArc(1, 2)); //b->c
        Assert.assertEquals(23, graph.getArc(3, 2)); //c->a
        Assert.assertEquals(25, graph.getArc(3, 4)); //c->d
        Assert.assertEquals(30, graph.getArc(4, 2)); //d->c
        Assert.assertEquals(31, graph.getArc(4, 3)); //-1 -> a
        Assert.assertEquals(35, graph.getArc(5,0)); //c->-2
        
        Assert.assertEquals(6, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(6, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(5, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(5, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(6, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(6, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(IntLists.mutable.of(1,3,4,0,2,5,6), graph.getSortedNodes());
     
        Assert.assertEquals(6, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(6, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(5, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(5, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(6, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(6, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        
        Assert.assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.16666, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.83333, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.83333, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        
        Assert.assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(1, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(2, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(3, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(4, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(5, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(6, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(1, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(2, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(3, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(4, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(5, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(0, graph.getNodeWeight(6, MeasureType.DURATION, MeasureAggregation.CASES),0.0);
        
        Assert.assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(0, graph.getNodeWeight(1, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(0, graph.getNodeWeight(2, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(0, graph.getNodeWeight(3, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(0, graph.getNodeWeight(4, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(0, graph.getNodeWeight(5, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(0, graph.getNodeWeight(6, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.001);
        
        // 1,3,4,9,20,23,25,30,31,35
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(2, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(3, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(6, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(3, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(2, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(2, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(3, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(6, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.TOTAL),0.0);     
        
        Assert.assertEquals(IntLists.mutable.of(1,9,3,25,30,4,23,31,20,35), graph.getSortedArcs());

        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(2, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(3, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(6, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(3, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(2, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(2, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(3, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0);
        Assert.assertEquals(6, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.CASES),0.0); 
        
        Assert.assertEquals(0, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MIN),0.0);  
        
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MAX),0.0);
        
        Assert.assertEquals(0.16666, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.33333, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.5, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.16666, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.5, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.33333, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.33333, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(0.5, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MEAN),0.001);
        
        Assert.assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        Assert.assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN),0.001);
        
        Assert.assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(120000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(120000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(180000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        Assert.assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.TOTAL),0.0);
        
        Assert.assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MIN),0.0);
        
        Assert.assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(120000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        Assert.assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MAX),0.0);
        
        Assert.assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(90000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MEAN),0.0);
        
        Assert.assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(90000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
        Assert.assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MEDIAN),0.0);
    }
    
    @Test
    public void test_LogWithStartAndCompleteEvents() {
        ALog log = new ALog(readLogWithStartCompleteEventsNonOverlappingRepeats());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeLogGraph graph = attLog.getGraphView();
        
        Assert.assertEquals(IntSets.mutable.of(0,1,2,3), graph.getNodes());
        Assert.assertEquals(IntSets.mutable.of(1,3,5,7,8), graph.getArcs());
        Assert.assertEquals("a", graph.getNodeName(0));
        Assert.assertEquals("b", graph.getNodeName(1));
        Assert.assertEquals("|>", graph.getNodeName(2));
        Assert.assertEquals("[]", graph.getNodeName(3));
        Assert.assertEquals(2, graph.getSourceNode());
        Assert.assertEquals(3, graph.getSinkNode());        
        Assert.assertEquals(Constants.START_NAME, graph.getNodeName(2));
        Assert.assertEquals(Constants.END_NAME, graph.getNodeName(3));
        
        Assert.assertEquals(1, graph.getArc(0, 1));
        Assert.assertEquals(-1, graph.getArc(0, 2));
        Assert.assertEquals(3, graph.getArc(0, 3));
        Assert.assertEquals(-1, graph.getArc(1, 0));
        Assert.assertEquals(5, graph.getArc(1, 1));
        Assert.assertEquals(-1, graph.getArc(1, 2));
        Assert.assertEquals(7, graph.getArc(1, 3));
        Assert.assertEquals(8, graph.getArc(2, 0));
        Assert.assertEquals(-1, graph.getArc(2, 1));
        Assert.assertEquals(-1, graph.getArc(2, 2));
        Assert.assertEquals(-1, graph.getArc(2, 3));
        Assert.assertEquals(-1, graph.getArc(3, 0));
        Assert.assertEquals(-1, graph.getArc(3, 1));
        Assert.assertEquals(-1, graph.getArc(3, 2));
        Assert.assertEquals(-1, graph.getArc(3, 3));
        
        
        Assert.assertEquals(3, graph.getNodeTotalFrequency(0));
        Assert.assertEquals(3, graph.getNodeTotalFrequency(1));
        Assert.assertEquals(3, graph.getNodeTotalFrequency(2));
        Assert.assertEquals(3, graph.getNodeTotalFrequency(3));
        
        Assert.assertEquals(1, graph.getNodeMedianFrequency(0), 0.0);
        Assert.assertEquals(1.5, graph.getNodeMedianFrequency(1), 0.0);
        Assert.assertEquals(1, graph.getNodeMedianFrequency(2), 0.0);
        Assert.assertEquals(1, graph.getNodeMedianFrequency(3), 0.0);
        
        Assert.assertEquals(60000, graph.getNodeMedianDuration(0), 0.0);
        Assert.assertEquals(0, graph.getNodeMedianDuration(1), 0.0);
        Assert.assertEquals(0, graph.getNodeMedianDuration(2), 0.0);
        Assert.assertEquals(0, graph.getNodeMedianDuration(3), 0.0);
        
        Assert.assertEquals(1, graph.getArcMedianFrequency(1), 0.0);
        Assert.assertEquals(1, graph.getArcMedianFrequency(3), 0.0);
        Assert.assertEquals(1, graph.getArcMedianFrequency(5), 0.0);
        Assert.assertEquals(1, graph.getArcMedianFrequency(7), 0.0);
        Assert.assertEquals(1, graph.getArcMedianFrequency(8), 0.0);
        
        Assert.assertEquals(90000, graph.getArcMedianDuration(1), 0.0);
        Assert.assertEquals(0, graph.getArcMedianDuration(3), 0.0);
        Assert.assertEquals(0, graph.getArcMedianDuration(5), 0.0);
        Assert.assertEquals(0, graph.getArcMedianDuration(7), 0.0);
        Assert.assertEquals(0, graph.getArcMedianDuration(8), 0.0);      
    }
}
