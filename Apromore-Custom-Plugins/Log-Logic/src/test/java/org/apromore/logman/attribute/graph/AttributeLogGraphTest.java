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

package org.apromore.logman.attribute.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;
import org.apromore.logman.ALog;
import org.apromore.logman.Constants;
import org.apromore.logman.DataSetup;
import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AttributeLogGraphTest extends DataSetup {

    @Test
    void test_OneTraceAndCompleteEvents_StructureWithCaseFrequency() throws Exception {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        
        AttributeLogGraph graph = attLog.getGraphView();
        graph.sortNodesAndArcs(MeasureType.FREQUENCY, MeasureAggregation.TOTAL);
        graph.buildSubGraphs(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        assertEquals(IntSets.mutable.of(0,1,2,3,4,5), graph.getNodes());
        assertEquals(IntSets.mutable.of(0,1,2,8,12,15,17,20,24), graph.getArcs());
        assertEquals(6, graph.getNodeBitMask().cardinality());
        assertEquals(9, graph.getArcBitMask().cardinality());
        assertEquals(4, graph.getSourceNode());
        assertEquals(5, graph.getSinkNode());
        
        assertEquals("a", graph.getNodeName(0));
        assertEquals("b", graph.getNodeName(1));
        assertEquals("c", graph.getNodeName(2));
        assertEquals("d", graph.getNodeName(3));
        assertEquals(Constants.START_NAME, graph.getNodeName(4));
        assertEquals(Constants.END_NAME, graph.getNodeName(5));
        
        assertEquals(IntLists.mutable.of(1,4,5,3,0,2), graph.getSortedNodes());
        
        assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(100)); // non-existent node
        assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(100));
        
        assertEquals(IntSets.mutable.of(0,1,2), graph.getOutgoingArcs(0)); //a
        assertEquals(IntSets.mutable.of(0,12,24), graph.getIncomingArcs(0));
        
        assertEquals(IntSets.mutable.of(8), graph.getOutgoingArcs(1)); //b
        assertEquals(IntSets.mutable.of(1), graph.getIncomingArcs(1));
        
        assertEquals(IntSets.mutable.of(12,15,17), graph.getOutgoingArcs(2)); //c
        assertEquals(IntSets.mutable.of(2,8,20), graph.getIncomingArcs(2));
        
        assertEquals(IntSets.mutable.of(20), graph.getOutgoingArcs(3)); //d
        assertEquals(IntSets.mutable.of(15), graph.getIncomingArcs(3));
        
        assertEquals(IntSets.mutable.of(24), graph.getOutgoingArcs(4)); //source -1
        assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(4));
        
        assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(5)); //sink -2
        assertEquals(IntSets.mutable.of(17), graph.getIncomingArcs(5));
        
        assertEquals(0, graph.getArc(0, 0)); //a->a
        assertEquals(1, graph.getArc(0, 1)); //a->b
        assertEquals(2, graph.getArc(0, 2)); //a->c
        assertEquals(8, graph.getArc(1, 2)); //b->c
        assertEquals(12, graph.getArc(2, 0)); //c->a
        assertEquals(15, graph.getArc(2, 3)); //c->d
        assertEquals(20, graph.getArc(3, 2)); //d->c
        assertEquals(24, graph.getArc(4, 0)); //-1 -> a
        assertEquals(17, graph.getArc(2, 5)); //c->-2
        
        assertEquals(IntLists.mutable.of(1,2,8,12,17,24,0,15,20), graph.getSortedArcs());
        
        assertEquals(4, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(4, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        
        // 0,1,2,8,12,15,17,20,24
        assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.0);
        
        assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(2, graph.getArcWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(8, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(12, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(15, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(17, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(24, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(180000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(720000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(840000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(60000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(300000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(360000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(120000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(420000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(480000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(90000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(360000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(420000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(90000, graph.getArcWeight(0, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(600000, graph.getArcWeight(2, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(240000, graph.getArcWeight(8, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(540000, graph.getArcWeight(12, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(360000, graph.getArcWeight(15, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(17, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(420000, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(24, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
    }
    
    @Test
    void testSubGraphs_OneTraceAndCompleteEvents_StructureWithCaseFrequency() throws Exception {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeLogGraph graph = attLog.getGraphView();
        graph.sortNodesAndArcs(MeasureType.FREQUENCY, MeasureAggregation.TOTAL);
        graph.buildSubGraphs(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        FilteredGraph nodeBasedGraph0 = graph.getSubGraphs().get(0);
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(0).getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(0).getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(1).getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(1).getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(2).getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 8, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(2).getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(3).getNodes());
        assertEquals(IntSets.mutable.of(1, 8, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(3).getArcs());
        
        FilteredGraph nodeBasedGraph1 = graph.getSubGraphs().get(1);
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 15, 17, 20, 24), nodeBasedGraph1.getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(0).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(0).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(1).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(1).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(2).getNodes());
        assertEquals(IntSets.mutable.of(2, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(2).getArcs());
        
        FilteredGraph nodeBasedGraph2 = graph.getSubGraphs().get(2);
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 17, 24), nodeBasedGraph2.getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(0).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 17, 24), nodeBasedGraph2.getSubGraphs().get(0).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(1).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 17, 24), nodeBasedGraph2.getSubGraphs().get(1).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(2).getNodes());
        assertEquals(IntSets.mutable.of(2, 17, 24), nodeBasedGraph2.getSubGraphs().get(2).getArcs());
    }
    
    void testSubGraphs_OneTraceAndCompleteEvents_StructureWithMeanDuration() throws Exception {
        ALog log = new ALog(readLogWithStartCompleteEventsOverlapping());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeLogGraph graph = attLog.getGraphView();
        graph.sortNodesAndArcs(MeasureType.DURATION, MeasureAggregation.MEAN);
        graph.buildSubGraphs(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        FilteredGraph nodeBasedGraph0 = graph.getSubGraphs().get(0);
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(0).getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(0).getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(1).getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 8, 12, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(1).getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(2).getNodes());
        assertEquals(IntSets.mutable.of(0, 1, 8, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(2).getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), nodeBasedGraph0.getSubGraphs().get(3).getNodes());
        assertEquals(IntSets.mutable.of(1, 8, 15, 17, 20, 24), nodeBasedGraph0.getSubGraphs().get(3).getArcs());
        
        FilteredGraph nodeBasedGraph1 = graph.getSubGraphs().get(1);
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 15, 17, 20, 24), nodeBasedGraph1.getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(0).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(0).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(1).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(1).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), nodeBasedGraph1.getSubGraphs().get(2).getNodes());
        assertEquals(IntSets.mutable.of(2, 15, 17, 20, 24), nodeBasedGraph1.getSubGraphs().get(2).getArcs());
        
        FilteredGraph nodeBasedGraph2 = graph.getSubGraphs().get(2);
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 17, 24), nodeBasedGraph2.getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(0).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 12, 17, 24), nodeBasedGraph2.getSubGraphs().get(0).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(1).getNodes());
        assertEquals(IntSets.mutable.of(0, 2, 17, 24), nodeBasedGraph2.getSubGraphs().get(1).getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 4, 5), nodeBasedGraph2.getSubGraphs().get(2).getNodes());
        assertEquals(IntSets.mutable.of(2, 17, 24), nodeBasedGraph2.getSubGraphs().get(2).getArcs());
    }
    
    
    @Test
    void test_Exception() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeLogGraph graph = attLog.getGraphView();
        graph.sortNodesAndArcs(MeasureType.FREQUENCY, MeasureAggregation.TOTAL);
        graph.buildSubGraphs(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        // Add invalid node: node not exist in the matrix graph
        boolean addResult = graph.addNode(100);
        assertEquals(false, addResult);
        
        // Add invalid node: node already exists in the current graph
        addResult = graph.addNode(1);
        assertEquals(false, addResult);
        
        int arc1 = graph.getArc(0, 2);
        assertNotEquals(-1, arc1);
        
        int arc2 = graph.getArc(100, 0);
        assertEquals(-1, arc2);
    }
    
    @Test
    void test_LogWithCompleteEventsOnly() {
        ALog log = new ALog(readLogWithCompleteEventsOnly());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        
        AttributeLogGraph graph = attLog.getGraphView();
        graph.sortNodesAndArcs(MeasureType.FREQUENCY, MeasureAggregation.TOTAL);
        graph.buildSubGraphs(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        assertEquals(IntSets.mutable.of(0,1,2,3,4,5,6), graph.getNodes());
        assertEquals(IntSets.mutable.of(1,3,4,9,20,23,25,30,31,35), graph.getArcs());
        assertEquals(7, graph.getNodeBitMask().cardinality());
        assertEquals(10, graph.getArcBitMask().cardinality());
        assertEquals(5, graph.getSourceNode());
        assertEquals(6, graph.getSinkNode());
        
        assertEquals("a", graph.getNodeName(0));
        assertEquals("e", graph.getNodeName(1));
        assertEquals("d", graph.getNodeName(2));
        assertEquals("c", graph.getNodeName(3));
        assertEquals("b", graph.getNodeName(4));
        assertEquals("|>", graph.getNodeName(5));
        assertEquals("[]", graph.getNodeName(6));
        assertEquals(Constants.START_NAME, graph.getNodeName(5));
        assertEquals(Constants.END_NAME, graph.getNodeName(6));
        
        assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(100)); // non-existent node
        assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(100));
        
        assertEquals(IntSets.mutable.of(1,3,4), graph.getOutgoingArcs(0)); //a
        assertEquals(IntSets.mutable.of(35), graph.getIncomingArcs(0));
        
        assertEquals(IntSets.mutable.of(9), graph.getOutgoingArcs(1)); //b
        assertEquals(IntSets.mutable.of(1), graph.getIncomingArcs(1));
        
        assertEquals(IntSets.mutable.of(20), graph.getOutgoingArcs(2)); //c
        assertEquals(IntSets.mutable.of(9,23,30), graph.getIncomingArcs(2));
        
        assertEquals(IntSets.mutable.of(23,25), graph.getOutgoingArcs(3)); //d
        assertEquals(IntSets.mutable.of(3,31), graph.getIncomingArcs(3));
        
        assertEquals(IntSets.mutable.of(30,31), graph.getOutgoingArcs(4));
        assertEquals(IntSets.mutable.of(4,25), graph.getIncomingArcs(4));
        
        assertEquals(IntSets.mutable.of(35), graph.getOutgoingArcs(5));
        assertEquals(IntSets.mutable.empty(), graph.getIncomingArcs(5));
        
        assertEquals(IntSets.mutable.empty(), graph.getOutgoingArcs(6));
        assertEquals(IntSets.mutable.of(20), graph.getIncomingArcs(6));
        
        assertEquals(1, graph.getArc(0, 1)); //a->a
        assertEquals(3, graph.getArc(0, 3)); //a->b
        assertEquals(4, graph.getArc(0, 4)); //a->c
        assertEquals(9, graph.getArc(1, 2)); //b->c
        assertEquals(23, graph.getArc(3, 2)); //c->a
        assertEquals(25, graph.getArc(3, 4)); //c->d
        assertEquals(30, graph.getArc(4, 2)); //d->c
        assertEquals(31, graph.getArc(4, 3)); //-1 -> a
        assertEquals(35, graph.getArc(5,0)); //c->-2
        
        assertEquals(6, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(5, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(5, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(IntLists.mutable.of(1,3,4,0,2,5,6), graph.getSortedNodes());
     
        assertEquals(6, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(5, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(5, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.16667, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.83333, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.83333, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        
        assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getNodeWeight(0, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.16666, graph.getNodeWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(2, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.83333, graph.getNodeWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.83333, graph.getNodeWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(5, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(6, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        
        assertEquals(1, graph.getNodeWeight(graph.getNodeFromName("a"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(graph.getNodeFromName("e"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(graph.getNodeFromName("d"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(graph.getNodeFromName("c"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(graph.getNodeFromName("b"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(graph.getNodeFromName("|>"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getNodeWeight(graph.getNodeFromName("[]"), MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(1, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(2, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(3, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(4, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(5, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(6, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(1, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(2, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(3, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(4, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(5, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getNodeWeight(6, MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0, graph.getNodeWeight(0, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(1, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(2, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(3, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(4, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(5, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getNodeWeight(6, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        
        // 1,3,4,9,20,23,25,30,31,35
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(IntLists.mutable.of(1,9,3,25,30,4,23,31,20,35), graph.getSortedArcs());

        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(2, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(6, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0.16667, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.33333, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.5, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.16667, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.5, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.33333, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.33333, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(0.5, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.RELATIVE),0.001);
        
        assertEquals(0, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(1, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0.16666, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.33333, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.5, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.16666, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.5, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.33333, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.33333, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.5, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        
        assertEquals(0, graph.getArcWeight(1, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getArcWeight(3, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.5, graph.getArcWeight(4, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getArcWeight(9, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getArcWeight(20, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.5, graph.getArcWeight(23, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getArcWeight(25, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0, graph.getArcWeight(30, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(0.5, graph.getArcWeight(31, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph.getArcWeight(35, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        
        assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(120000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(120000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(180000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(120000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(90000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(60000, graph.getArcWeight(1, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(3, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(4, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(9, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(20, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(23, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(90000, graph.getArcWeight(25, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(30, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(60000, graph.getArcWeight(31, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph.getArcWeight(35, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.0);
    }
    
    @Test
    void test_LogWithStartAndCompleteEvents() {
        ALog log = new ALog(readLogWithStartCompleteEventsNonOverlappingRepeats());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeLogGraph graph = attLog.getGraphView();
        
        assertEquals(IntSets.mutable.of(0,1,2,3), graph.getNodes());
        assertEquals(IntSets.mutable.of(1,3,5,7,8), graph.getArcs());
        assertEquals("a", graph.getNodeName(0));
        assertEquals("b", graph.getNodeName(1));
        assertEquals("|>", graph.getNodeName(2));
        assertEquals("[]", graph.getNodeName(3));
        assertEquals(2, graph.getSourceNode());
        assertEquals(3, graph.getSinkNode());
        assertEquals(Constants.START_NAME, graph.getNodeName(2));
        assertEquals(Constants.END_NAME, graph.getNodeName(3));
        
        assertEquals(1, graph.getArc(0, 1));
        assertEquals(-1, graph.getArc(0, 2));
        assertEquals(3, graph.getArc(0, 3));
        assertEquals(-1, graph.getArc(1, 0));
        assertEquals(5, graph.getArc(1, 1));
        assertEquals(-1, graph.getArc(1, 2));
        assertEquals(7, graph.getArc(1, 3));
        assertEquals(8, graph.getArc(2, 0));
        assertEquals(-1, graph.getArc(2, 1));
        assertEquals(-1, graph.getArc(2, 2));
        assertEquals(-1, graph.getArc(2, 3));
        assertEquals(-1, graph.getArc(3, 0));
        assertEquals(-1, graph.getArc(3, 1));
        assertEquals(-1, graph.getArc(3, 2));
        assertEquals(-1, graph.getArc(3, 3));
        
        
        assertEquals(3, graph.getNodeTotalFrequency(0));
        assertEquals(3, graph.getNodeTotalFrequency(1));
        assertEquals(3, graph.getNodeTotalFrequency(2));
        assertEquals(3, graph.getNodeTotalFrequency(3));
        
        assertEquals(1, graph.getNodeMedianFrequency(0), 0.0);
        assertEquals(1, graph.getNodeMedianFrequency(1), 0.0);
        assertEquals(1, graph.getNodeMedianFrequency(2), 0.0);
        assertEquals(1, graph.getNodeMedianFrequency(3), 0.0);
        
        assertEquals(60000, graph.getNodeMedianDuration(0), 0.0);
        assertEquals(0, graph.getNodeMedianDuration(1), 0.0);
        assertEquals(0, graph.getNodeMedianDuration(2), 0.0);
        assertEquals(0, graph.getNodeMedianDuration(3), 0.0);
        
        assertEquals(1, graph.getArcMedianFrequency(1), 0.0);
        assertEquals(0, graph.getArcMedianFrequency(3), 0.0);
        assertEquals(0, graph.getArcMedianFrequency(5), 0.0);
        assertEquals(1, graph.getArcMedianFrequency(7), 0.0);
        assertEquals(1, graph.getArcMedianFrequency(8), 0.0);
        
        assertEquals(90000, graph.getArcMedianDuration(1), 0.0);
        assertEquals(0, graph.getArcMedianDuration(3), 0.0);
        assertEquals(0, graph.getArcMedianDuration(5), 0.0);
        assertEquals(0, graph.getArcMedianDuration(7), 0.0);
        assertEquals(0, graph.getArcMedianDuration(8), 0.0);
    }
    
    @Test
    // Special graph: this graph has start node connecting to distinct activity connecting to the end node.
    // No arcs between activity nodes.
    void test_readLogWithTraceWithOneSingleUniqueEvent() {
        ALog log = new ALog(readLogWithTraceWithOneSingleUniqueEvent());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeLogGraph graph = attLog.getGraphView();
        
        assertEquals(IntSets.mutable.of(0,1,2,3,4), graph.getNodes());
        assertEquals(IntSets.mutable.of(4,9,14,15,16,17), graph.getArcs());
        assertEquals("a", graph.getNodeName(0));
        assertEquals("b", graph.getNodeName(1));
        assertEquals("c", graph.getNodeName(2));
        assertEquals(Constants.START_NAME, graph.getNodeName(3));
        assertEquals(Constants.END_NAME, graph.getNodeName(4));
        
        graph.buildSubGraphs(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        //Each node-based subgraph has itself as the only subgraph
        assertEquals(1, graph.getSubGraphs().get(0).getSubGraphs().size());
        assertEquals(1, graph.getSubGraphs().get(1).getSubGraphs().size());
        assertEquals(1, graph.getSubGraphs().get(2).getSubGraphs().size());
        
        assertEquals(graph.getSubGraphs().get(0).getNodes(),
                                graph.getSubGraphs().get(0).getSubGraphs().get(0).getNodes());
        assertEquals(graph.getSubGraphs().get(0).getArcs(),
                                graph.getSubGraphs().get(0).getSubGraphs().get(0).getArcs());
        
        assertEquals(graph.getSubGraphs().get(1).getNodes(),
                graph.getSubGraphs().get(1).getSubGraphs().get(0).getNodes());
        assertEquals(graph.getSubGraphs().get(1).getArcs(),
                graph.getSubGraphs().get(1).getSubGraphs().get(0).getArcs());
        
        assertEquals(graph.getSubGraphs().get(2).getNodes(),
                graph.getSubGraphs().get(2).getSubGraphs().get(0).getNodes());
        assertEquals(graph.getSubGraphs().get(2).getArcs(),
                graph.getSubGraphs().get(2).getSubGraphs().get(0).getArcs());
    }
    
    @Disabled
    @Test
    void testDurationWithSpecialCalendars() {
        ALog log = new ALog(readLogWithOneTrace_TwoActivities_StartCompleteEvents_Friday());
        
        AttributeLog attLog0 = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeLogGraph graph0 = attLog0.getGraphView();
        
        AttributeLog attLog1 = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getFriday_9To10AM_Calendar());
        AttributeLogGraph graph1 = attLog1.getGraphView();
    
        AttributeLog attLog2 = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getFriday_10To11AM_Calendar());
        AttributeLogGraph graph2 = attLog2.getGraphView();
        
        AttributeLog attLog3 = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getFriday_11To12AM_Calendar());
        AttributeLogGraph graph3 = attLog3.getGraphView();
        
        assertEquals(3600000, graph0.getNodeWeight("a", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);
        assertEquals(3600000, graph0.getNodeWeight("b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);
        assertEquals(3600000, graph0.getArcWeight("a", "b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);
        
        assertEquals(3600000, graph1.getNodeWeight("a", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph1.getNodeWeight("b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph1.getArcWeight("a", "b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0, graph2.getNodeWeight("a", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3600000, graph2.getNodeWeight("b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph2.getArcWeight("a", "b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        
        assertEquals(0, graph3.getNodeWeight("a", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(0, graph3.getNodeWeight("b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
        assertEquals(3600000, graph3.getArcWeight("a", "b", MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.0);
    }

    @Test
    void testCostWeight() {
        ALog log = new ALog(readLogWithOneTrace_TwoActivities_StartCompleteEvents());
        AttributeLog attLog0 = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(),
                getAllDayAllTimeCalendar(),
                Map.ofEntries(Map.entry("O1", 3D), Map.entry("O2", 1D)));
        AttributeLogGraph graph0 = attLog0.getGraphView();

        assertEquals(0, graph0.getNodeWeight(Constants.START_NAME, MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);
        assertEquals(0, graph0.getNodeWeight(Constants.END_NAME, MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);

        assertEquals(1.5, graph0.getNodeWeight("a", MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);

        assertEquals(1, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE),0);
        assertEquals(4, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE),0);
        assertEquals(2.333333, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(2, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(7, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(7, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE),0.001);
        assertEquals(1, graph0.getNodeWeight("b", MeasureType.COST, MeasureAggregation.MIN, MeasureRelation.RELATIVE),0);

        assertEquals(0, graph0.getArcWeight("a", "b", MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);
        assertEquals(0, graph0.getArcWeight("a", "a", MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE),0);

    }

    @Test
    void testCostWeight_Missing_Role() {
        ALog log = new ALog(readLogWithOneTrace_TwoActivities_StartCompleteEvents());
        AttributeLog attLog0 = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(),
            getAllDayAllTimeCalendar(),
            Map.ofEntries(Map.entry("O2", 1D))); // Missing role O1's cost
        AttributeLogGraph graph0 = attLog0.getGraphView();
        assertEquals(0,
            graph0.getNodeWeight("a", MeasureType.COST, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE), 0);
    }
}
