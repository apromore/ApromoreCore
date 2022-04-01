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

import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.ALog;
import org.apromore.logman.Constants;
import org.apromore.logman.LogBitMap;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processdiscoverer.bpmn.TraceBPMNDiagram;
import org.apromore.processdiscoverer.bpmn.TraceVariantBPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.deckfour.xes.model.XLog;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ProcessDiscovererTest extends LogicDataSetup {
    
    private AbstractionParams createAbstrationParams(IndexableAttribute mainAttribute,
            double nodeSlider, double arcSlider, double paraSlider,
            MeasureType structureType, MeasureAggregation structureAggregate, MeasureRelation structureRelation,
            MeasureType primaryType, MeasureAggregation primaryAggregate, MeasureRelation primaryRelation,
            MeasureType secondaryType, MeasureAggregation secondaryAggregate, MeasureRelation secondaryRelation,
            boolean bpmn) throws Exception {
        
        return new AbstractionParams(
                mainAttribute,
                nodeSlider,
                arcSlider,
                paraSlider,
                true, true,
                false,
                false,
                false,
                structureType,
                structureAggregate,
                structureRelation,
                primaryType,
                primaryAggregate,
                primaryRelation,
                secondaryType,
                secondaryAggregate,
                secondaryRelation,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                null);
    }
    
    private Abstraction discoverProcess(AttributeLog attLog, double nodeSlider, double arcSlider, double paraSlider,
            MeasureType structureType, MeasureAggregation structureAggregate, MeasureRelation structureRelation,
            MeasureType primaryType, MeasureAggregation primaryAggregate, MeasureRelation primaryRelation,
            MeasureType secondaryType, MeasureAggregation secondaryAggregate, MeasureRelation secondaryRelation,
            boolean bpmn) throws Exception {
        
        ProcessDiscoverer pd = new ProcessDiscoverer();
        AbstractionParams params = createAbstrationParams(attLog.getAttribute(),
                                        nodeSlider, arcSlider, paraSlider,
                                        structureType, structureAggregate, structureRelation,
                                        primaryType, primaryAggregate, primaryRelation,
                                        secondaryType, secondaryAggregate, secondaryRelation, bpmn);
        Abstraction dfgAbs = pd.generateDFGAbstraction(attLog, params);
        return (!bpmn ? dfgAbs : pd.generateBPMNAbstraction(attLog, params, dfgAbs));
    }

    private Abstraction discoverProcess(XLog xlog, double nodeSlider, double arcSlider, double paraSlider,
                                        MeasureType structureType, MeasureAggregation structureAggregate, MeasureRelation structureRelation,
                                        MeasureType primaryType, MeasureAggregation primaryAggregate, MeasureRelation primaryRelation,
                                        MeasureType secondaryType, MeasureAggregation secondaryAggregate, MeasureRelation secondaryRelation,
                                        boolean bpmn) throws Exception {
        return discoverProcess(createAttributeLog(xlog),
                nodeSlider,
                arcSlider,
                paraSlider,
                structureType,
                structureAggregate,
                structureRelation,
                primaryType,
                primaryAggregate,
                primaryRelation,
                secondaryType,
                secondaryAggregate,
                secondaryRelation,
                bpmn);
    }
    
    private Abstraction discoverProcess(XLog xlog, double nodeSlider, double arcSlider, double paraSlider,
            MeasureType structureType, MeasureAggregation structureAggregate, MeasureRelation structureRelation,
                        MeasureType primaryType, MeasureAggregation primaryAggregate, MeasureRelation primaryRelation,
                        MeasureType secondaryType, MeasureAggregation secondaryAggregate, MeasureRelation secondaryRelation,
                        boolean bpmn,
                        CalendarModel cal, Map<String, Double> costTable) throws Exception {

        return discoverProcess(createAttributeLog(xlog, cal, costTable),
                        nodeSlider,
                        arcSlider,
                        paraSlider,
                        structureType,
                        structureAggregate,
                        structureRelation,
                        primaryType,
                        primaryAggregate,
                        primaryRelation,
                        secondaryType,
                        secondaryAggregate,
                        secondaryRelation,
                        bpmn);
    }
    
    private AttributeLog createAttributeLog(XLog xlog) {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute, getAllDayAllTimeCalendar(), Map.of());
        return attLog;
    }

    private AttributeLog createAttributeLog(XLog xlog, CalendarModel cal, Map<String, Double> costTable) {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute, cal, costTable);
        return attLog;
    }
    
    private Abstraction discoverTraceAbstraction(XLog xlog, String traceID) throws Exception {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute, getAllDayAllTimeCalendar());
        ProcessDiscoverer pd = new ProcessDiscoverer();
        AbstractionParams params = new AbstractionParams(
                                            mainAttribute,
                                            1.0,
                                            1.0,
                                            0.4,
                                            true, true,
                                            false,
                                            false,
                                            false,
                                            MeasureType.FREQUENCY,
                                            MeasureAggregation.CASES,
                                            MeasureRelation.ABSOLUTE,
                                            MeasureType.FREQUENCY,
                                            MeasureAggregation.CASES,
                                            MeasureRelation.ABSOLUTE,
                                            MeasureType.DURATION,
                                            MeasureAggregation.MEAN,
                                            MeasureRelation.ABSOLUTE,
                                            Integer.MAX_VALUE,
                                            Integer.MAX_VALUE,
                                            null);
        Abstraction traceAbs = pd.generateTraceAbstraction(attLog, traceID, params);
        return traceAbs;
    }

    private Abstraction discoverTraceVariantAbstraction(XLog xlog, List<String> traceIDs) throws Exception {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute, getAllDayAllTimeCalendar());
        ProcessDiscoverer pd = new ProcessDiscoverer();
        AbstractionParams params = new AbstractionParams(
                mainAttribute,
                1.0,
                1.0,
                0.4,
                true, true,
                false,
                false,
                false,
                MeasureType.FREQUENCY,
                MeasureAggregation.CASES,
                MeasureRelation.ABSOLUTE,
                MeasureType.FREQUENCY,
                MeasureAggregation.CASES,
                MeasureRelation.ABSOLUTE,
                MeasureType.DURATION,
                MeasureAggregation.MEAN,
                MeasureRelation.ABSOLUTE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                null);
        Abstraction traceAbs = pd.generateTraceVariantAbstraction(attLog, traceIDs, params);
        return traceAbs;
    }

    
    
    @Test
    void testDFG_LogWithOneTraceOneEvent_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithOneTraceOneEvent(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false);
            BPMNDiagram d = this.readDFG_LogWithOneTraceOneEvent();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
            else {
                int nodeCount = 0;
                for (BPMNNode node: abs.getDiagram().getNodes()) {
                    if (node.getLabel().equalsIgnoreCase("a")) {
                        assertEquals(1, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.START_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.END_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                }
                if (nodeCount != abs.getDiagram().getNodes().size()) {
                    fail("Missing tests for nodes");
                }
                
                int edgeCount = 0;
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: abs.getDiagram().getEdges()) {
                    if (((BPMNNode)e.getSource()).getLabel().equals(Constants.START_NAME) &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("a")) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals(Constants.END_NAME)) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                }
                if (edgeCount != abs.getDiagram().getEdges().size()) {
                    fail("Missing tests for edges");
                }
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testDFG_LogWithOneTrace_StartCompleteEvents_Cost() {
        try {
            Abstraction abs = discoverProcess(readLogWithOneTrace_StartCompleteEvents(),
                    1.0, 1.0, 0.4,
                    MeasureType.FREQUENCY,
                    MeasureAggregation.CASES,
                    MeasureRelation.ABSOLUTE,
                    MeasureType.COST,
                    MeasureAggregation.MEAN,
                    MeasureRelation.ABSOLUTE,
                    MeasureType.DURATION,
                    MeasureAggregation.MEAN,
                    MeasureRelation.ABSOLUTE,
                    false,
                    getAllDayAllTimeCalendar(),
                    Map.ofEntries(Map.entry("O1", 3D), Map.entry("O2", 1D)));
            BPMNDiagram d = this.readDFG_LogWithOneTrace_StartCompleteEvents();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
            else {
                int nodeCount = 0;
                for (BPMNNode node: abs.getDiagram().getNodes()) {
                    if (node.getLabel().equalsIgnoreCase("a")) {
                        assertEquals(1.5, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    if (node.getLabel().equalsIgnoreCase("b")) {
                        assertEquals(2.33333, abs.getNodePrimaryWeight(node), 0.001);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.START_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.END_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                }

                if (nodeCount != abs.getDiagram().getNodes().size()) {
                    fail("Missing tests for nodes");
                }

                int edgeCount = 0;
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: abs.getDiagram().getEdges()) {
                    if (((BPMNNode)e.getSource()).getLabel().equals(Constants.START_NAME) &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("a")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals(Constants.END_NAME)) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                }
                if (edgeCount != abs.getDiagram().getEdges().size()) {
                    fail("Missing tests for edges");
                }
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testDFG_LogWithCompleteEventsOnly_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false);
            BPMNDiagram d = this.readDFG_LogWithCompleteEventsOnly();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
            else {
                int nodeCount = 0;
                for (BPMNNode node: abs.getDiagram().getNodes()) {
                    if (node.getLabel().equalsIgnoreCase(Constants.START_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("a")) {
                        assertEquals(6, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("b")) {
                        assertEquals(5, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("c")) {
                        assertEquals(5, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("d")) {
                        assertEquals(6, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("e")) {
                        assertEquals(1, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.END_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                }
                
                if (nodeCount != abs.getDiagram().getNodes().size()) {
                    fail("Missing tests for nodes");
                }
                
                int edgeCount = 0;
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: abs.getDiagram().getEdges()) {
                    if (((BPMNNode)e.getSource()).getLabel().equals(Constants.START_NAME) &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("a")) {
                        assertEquals(6, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("c")) {
                        assertEquals(2, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("e")) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("c")) {
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("c") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(2, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(2, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("c") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("e") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("d") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals(Constants.END_NAME)) {
                        assertEquals(6, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                }
                
                if (edgeCount != abs.getDiagram().getEdges().size()) {
                    fail("Missing tests for edges");
                }
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testBPMN_LogWithCompleteEventsOnly_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
            BPMNDiagram d = this.readBPMN_LogWithCompleteEventsOnly();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
            else {
                int nodeCount = 0;
                for (BPMNNode node: abs.getDiagram().getNodes()) {
                    if (node.getLabel().equalsIgnoreCase(Constants.START_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("a")) {
                        assertEquals(6, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("b")) {
                        assertEquals(5, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("c")) {
                        assertEquals(5, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("d")) {
                        assertEquals(6, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("e")) {
                        assertEquals(1, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.END_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                }
                
                if (nodeCount != (abs.getDiagram().getActivities().size() + abs.getDiagram().getEvents().size())) {
                    fail("Missing tests for nodes");
                }
                
                int edgeCount = 0;
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: abs.getDiagram().getEdges()) {
                    if (((BPMNNode)e.getSource()).getLabel().equals(Constants.START_NAME) &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("a")) {
                        assertEquals(6, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            e.getTarget() instanceof Gateway) {
                        assertEquals(6, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource() instanceof Gateway &&
                            ((Gateway)e.getSource()).getGatewayType() == GatewayType.DATABASED &&
                            e.getTarget() instanceof Gateway &&
                            ((Gateway)e.getTarget()).getGatewayType() == GatewayType.PARALLEL) {
                        assertEquals(5, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("e")) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(5, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("c")) {
                        assertEquals(5, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("")) {
                        assertEquals(5, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("c") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("")) {
                        assertEquals(5, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("e") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("")) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource() instanceof Gateway &&
                            ((Gateway)e.getSource()).getGatewayType() == GatewayType.PARALLEL &&
                            e.getTarget() instanceof Gateway &&
                            ((Gateway)e.getTarget()).getGatewayType() == GatewayType.DATABASED) {
                        assertEquals(5, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(6, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("d") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals(Constants.END_NAME)) {
                        assertEquals(6, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                }
                
                if (edgeCount != abs.getDiagram().getEdges().size()) {
                    fail("Missing tests for edges");
                }
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    // Test consistency of BPMN Diagram for different types of measures
    @Test
    void testBPMNMining_SameAbstractionLeveDifferentWeightMeasures() {
    	 try {
    		 BPMNDiagram sourceDiagram = this.readBPMN_LogWithCompleteEventsOnly();
    		 
    		 Abstraction absCaseFrequency = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE, //absolute case frequency
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absCaseRelativeFrequency = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.RELATIVE, //relative case frequency
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
            
    		 Abstraction absMinFrequency = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.MIN, //min frequency
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMaxFrequency = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.MAX, //max frequency
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMeanFrequency = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.MEAN, //max frequency
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMedianFrequency = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.MEDIAN, //median frequency
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMeanDuration = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN, //mean duration
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMedianDuration = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEDIAN, //median duration
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMinDuration = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MIN, //min duration
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
    		 
    		 Abstraction absMaxDuration = discoverProcess(readLogWithCompleteEventsOnly(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MAX, //max duration
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
            
            if (!absCaseFrequency.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Case Frequency meausure is different from the source!");
            }
            
            if (!absCaseRelativeFrequency.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Case Relative Frequency meausure is different from the source!");
            }
            
            if (!absMinFrequency.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Min Frequency meausure is different from the source!");
            }
            
            if (!absMaxFrequency.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Max Frequency meausure is different from the source!");
            }
            
            if (!absMeanFrequency.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Mean Frequency meausure is different from the source!");
            }
            
            if (!absMedianFrequency.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Median Frequency meausure is different from the source!");
            }
            
            if (!absMeanDuration.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Mean Duration meausure is different from the source!");
            }
            
            if (!absMinDuration.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Min Duration meausure is different from the source!");
            }
            
            if (!absMaxDuration.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Max Duration meausure is different from the source!");
            }
            
            if (!absMedianDuration.getDiagram().checkSimpleEquality(sourceDiagram)) {
                fail("BPMN Diagram for Median Duration meausure is different from the source!");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    
    @Test
    void testDFG_LogWithStartCompleteEventsOverlapping_Duration() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsOverlapping(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                false);
            BPMNDiagram d = this.readDFG_LogWithStartCompleteEventsOverlapping();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
            else {
                int nodeCount = 0;
                for (BPMNNode node: abs.getDiagram().getNodes()) {
                    if (node.getLabel().equalsIgnoreCase(Constants.START_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("a")) {
                        assertEquals(100000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("b")) {
                        assertEquals(96000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("c")) {
                        assertEquals(96000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("d")) {
                        assertEquals(90000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("e")) {
                        assertEquals(120000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.END_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                }
                
                if (nodeCount != abs.getDiagram().getNodes().size()) {
                    fail("Missing tests for nodes");
                }
                
                int edgeCount = 0;
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: abs.getDiagram().getEdges()) {
                    if (((BPMNNode)e.getSource()).getLabel().equals(Constants.START_NAME) &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("a")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(20000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("c")) {
                        assertEquals(30000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("a") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("e")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("c")) {
                        assertEquals(60000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("c") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(60000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(30000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("c") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(20000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("e") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(60000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("d") &&
                            ((BPMNNode)e.getTarget()).getLabel().equals(Constants.END_NAME)) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                }
                
                if (edgeCount != abs.getDiagram().getEdges().size()) {
                    fail("Missing tests for edges");
                }
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testBPMN_LogWithStartCompleteEventsOverlaping_Duration() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsOverlapping(),
                                                1.0, 1.0, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                true);
            BPMNDiagram d = this.readBPMN_LogWithStartCompleteEventsOverlapping();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
            else {
                int nodeCount = 0;
                for (BPMNNode node: abs.getDiagram().getNodes()) {
                    if (node.getLabel().equalsIgnoreCase(Constants.START_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("a")) {
                        assertEquals(100000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("b")) {
                        assertEquals(96000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("c")) {
                        assertEquals(96000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("d")) {
                        assertEquals(90000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase("e")) {
                        assertEquals(120000, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                    else if (node.getLabel().equalsIgnoreCase(Constants.END_NAME)) {
                        assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                        nodeCount++;
                    }
                }
                
                if (nodeCount != (abs.getDiagram().getActivities().size() + abs.getDiagram().getEvents().size())) {
                    fail("Missing tests for nodes");
                }
                
                int edgeCount = 0;
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: abs.getDiagram().getEdges()) {
                    if (e.getSource().getLabel().equals(Constants.START_NAME) &&
                            e.getTarget().getLabel().equals("a")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("a") &&
                            e.getTarget() instanceof Gateway) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource() instanceof Gateway &&
                            ((Gateway)e.getSource()).getGatewayType() == GatewayType.DATABASED &&
                            e.getTarget() instanceof Gateway &&
                            ((Gateway)e.getTarget()).getGatewayType() == GatewayType.PARALLEL) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("") &&
                            e.getTarget().getLabel().equals("e")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("") &&
                            e.getTarget().getLabel().equals("b")) {
                        assertEquals(20000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("") &&
                            e.getTarget().getLabel().equals("c")) {
                        assertEquals(30000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("b") &&
                            e.getTarget().getLabel().equals("")) {
                        assertEquals(30000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("c") &&
                            e.getTarget().getLabel().equals("")) {
                        assertEquals(20000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("e") &&
                            e.getTarget().getLabel().equals("")) {
                        assertEquals(60000, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource() instanceof Gateway &&
                            ((Gateway)e.getSource()).getGatewayType() == GatewayType.PARALLEL &&
                            e.getTarget() instanceof Gateway &&
                            ((Gateway)e.getTarget()).getGatewayType() == GatewayType.DATABASED) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("") &&
                            e.getTarget().getLabel().equals("d")) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource().getLabel().equals("d") &&
                            e.getTarget().getLabel().equals(Constants.END_NAME)) {
                        assertEquals(0, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                }
                
                if (edgeCount != abs.getDiagram().getEdges().size()) {
                    fail("Missing tests for edges");
                }
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testDFG_Sepsis_100_10_GraphStructure() {
        try {
            Abstraction abs = discoverProcess(read_Sepsis(),
                                                1.0, 0.1, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false);
            BPMNDiagram d = this.readDFG_Sepsis_100_10();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testBPMN_Sepsis_100_30_DiagramStructure() {
        try {
            Abstraction abs = discoverProcess(read_Sepsis(),
                                                1.0, 0.3, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true);
            BPMNDiagram d = this.readBPMN_Sepsis_100_30();
            if (!abs.getDiagram().checkSimpleEquality(d)) {
                fail("BPMNDiagram is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    
    @Test
    void testTraceAbstraction_Sepsis() {
        try {
            Abstraction abs = discoverTraceAbstraction(read_Sepsis(), "B");
            BPMNDiagram d = abs.getDiagram();
            BPMNNode startNode = ((TraceBPMNDiagram)abs.getDiagram()).getStartNode();
            BPMNNode node = startNode;
            int i = 0;
            double[] expected = new double[] {0, 775000, 1121000, 0, 0, 2385000, 1083000, 7000, 2593000, 240172000, 194400000, 14400000, 0};
            while (!d.getOutEdges(node).isEmpty()) {
                assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                assertEquals(expected[i], abs.getArcPrimaryWeight(d.getOutEdges(node).iterator().next()), 0);
                node = d.getOutEdges(node).iterator().next().getTarget();
                i++;
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
        
    }

    @Test
    void testTraceAbstraction_NodeDuration() {
        try {
            Abstraction abs = discoverTraceAbstraction(readLogWithStartCompleteEventsOverlapping(), "Case3.0");
            BPMNDiagram d = abs.getDiagram();
            BPMNNode startNode = ((TraceBPMNDiagram)abs.getDiagram()).getStartNode();
            BPMNNode node = startNode;
            int i = 0;
            double[] node_expected = new double[] {0, 120000, 120000, 60000, 0};
            double[] edge_expected = new double[] {0, 0, 60000, 0};
            while (!d.getOutEdges(node).isEmpty()) {
                assertEquals(node_expected[i], abs.getNodePrimaryWeight(node), 0);
                assertEquals(edge_expected[i], abs.getArcPrimaryWeight(d.getOutEdges(node).iterator().next()), 0);
                node = d.getOutEdges(node).iterator().next().getTarget();
                i++;
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }

    }

    @Test
    void testTraceVariantAbstraction_Sepsis() {
        try {
            String traceIDs[] = {"B"};
            Abstraction abs = discoverTraceVariantAbstraction(read_Sepsis(), new ArrayList<>(Arrays.asList(traceIDs)));
            BPMNDiagram d = abs.getDiagram();
            BPMNNode startNode = ((TraceVariantBPMNDiagram)abs.getDiagram()).getStartNode();
            BPMNNode node = startNode;
            int i = 0;
            double[] expected = new double[] {0, 775000, 1121000, 0, 0, 2385000, 1083000, 7000, 2593000, 240172000, 194400000, 14400000, 0};
            while (!d.getOutEdges(node).isEmpty()) {
                assertEquals(0, abs.getNodePrimaryWeight(node), 0);
                assertEquals(expected[i], abs.getArcPrimaryWeight(d.getOutEdges(node).iterator().next()), 0);
                node = d.getOutEdges(node).iterator().next().getTarget();
                i++;
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration() {
        try {
            String traceIDs[] = {"Case3.0"};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            BPMNDiagram d = abs.getDiagram();
            BPMNNode startNode = ((TraceVariantBPMNDiagram)abs.getDiagram()).getStartNode();
            BPMNNode node = startNode;
            int i = 0;
            double[] node_expected = new double[] {0, 120000, 120000, 60000, 0};
            double[] edge_expected = new double[] {0, 0, 60000, 0};
            while (!d.getOutEdges(node).isEmpty()) {
                assertEquals(node_expected[i], abs.getNodePrimaryWeight(node), 0);
                assertEquals(edge_expected[i], abs.getArcPrimaryWeight(d.getOutEdges(node).iterator().next()), 0);
                node = d.getOutEdges(node).iterator().next().getTarget();
                i++;
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration_no_IDs() {
        try {
            String traceIDs[] = {};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            fail("No exception occurred");
        } catch (Exception e) {
            String noIDErrorMessage = "A trace variant must contain at least one trace";
            assertEquals(noIDErrorMessage, e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration_empty_ID() {
        try {
            String traceIDs[] = {"Case3.0", ""};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            fail("No exception occurred");
        } catch (Exception e) {
            String noIDErrorMessage = "At least one trace id is empty or null";
            assertEquals(noIDErrorMessage, e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration_null_ID() {
        try {
            String traceIDs[] = {"Case3.0", null};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            fail("No exception occurred");
        } catch (Exception e) {
            String noIDErrorMessage = "At least one trace id is empty or null";
            assertEquals(noIDErrorMessage, e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration_ID_not_in_log() {
        try {
            String traceIDs[] = {"not a trace in log"};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            fail("No exception occurred");
        } catch (Exception e) {
            String noIDErrorMessage = "The trace with ID = not a trace in log is not in the current log (may have been filtered out)!";
            assertEquals(noIDErrorMessage, e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration_IDs_diff_variant() {
        try {
            String traceIDs[] = {"Case2.0", "Case3.0"};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            fail("No exception occurred");
        } catch (Exception e) {
            String noIDErrorMessage = "All traces must be of the same variant";
            assertEquals(noIDErrorMessage, e.getMessage());
        }
    }

    @Test
    void testTraceVariantAbstraction_NodeDuration_Multiple_Cases() {
        try {
            String traceIDs[] = {"Case2.0", "Case2.1"};
            Abstraction abs = discoverTraceVariantAbstraction(readLogWithStartCompleteEventsOverlapping(),
                    new ArrayList<>(Arrays.asList(traceIDs)));
            BPMNDiagram d = abs.getDiagram();
            BPMNNode startNode = ((TraceVariantBPMNDiagram)abs.getDiagram()).getStartNode();
            BPMNNode node = startNode;
            int i = 0;
            double[] node_expected = new double[] {0, 90000, 90000, 90000, 90000, 0};
            double[] edge_expected = new double[] {0, 30000, 60000, 30000, 0};
            while (!d.getOutEdges(node).isEmpty()) {
                assertEquals(node_expected[i], abs.getNodePrimaryWeight(node), 0);
                assertEquals(edge_expected[i], abs.getArcPrimaryWeight(d.getOutEdges(node).iterator().next()), 0);
                node = d.getOutEdges(node).iterator().next().getTarget();
                i++;
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testDFG_ApplyThenClearFilterCriteria() {
        try {
            AttributeLog attLog = createAttributeLog(readLogWithCompleteEventsOnly());
            ProcessDiscoverer pd = new ProcessDiscoverer();
            AbstractionParams params = createAbstrationParams(attLog.getAttribute(),
                                                1.0, 0.1, 0.4,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false);
            
            // Before filter diagram
            BPMNDiagram beforeFilterDiagram = pd.generateDFGAbstraction(attLog, params).getDiagram();
            if (!beforeFilterDiagram.checkSimpleEquality(readDFG_LogWithCompleteEventsOnly_100_10())) {
                fail("BPMNDiagram is different");
            }
            
            // Apply filter criteria
            LogBitMap logBitMap = new LogBitMap(attLog.getOriginalTraces().size());
            logBitMap.setTraceBitSet(LogBitMap.newBitSet(6, 0, 1), 6); // keep the first trace
            logBitMap.addEventBitSet(LogBitMap.newBitSet(5), 5); // keep all events including start and end events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            attLog.updateLogStatus(logBitMap);
            pd.invalidateAbstraction();
            
            // Generate abstraction and check diagram
            BPMNDiagram afterFilterDiagram = pd.generateDFGAbstraction(attLog, params).getDiagram();
            if (!afterFilterDiagram.checkSimpleEquality(readDFG_LogWithCompleteEventsOnly_100_10_Filtered())) {
                fail("BPMNDiagram is different");
            }

            // Clear filter criteria
            LogBitMap logBitMapAll = new LogBitMap(attLog.getOriginalTraces().size());
            logBitMapAll.setTraceBitSet(LogBitMap.newBitSet(6, 0, 6), 6); // keep all traces
            logBitMapAll.addEventBitSet(LogBitMap.newBitSet(5), 5); // keep all events including start and end events
            logBitMapAll.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMapAll.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMapAll.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMapAll.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMapAll.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            attLog.updateLogStatus(logBitMapAll);
            pd.invalidateAbstraction();
            
            // Generate abstraction and check diagram
            BPMNDiagram afterClearingFilterDiagram = pd.generateDFGAbstraction(attLog, params).getDiagram();
            if (!afterClearingFilterDiagram.checkSimpleEquality(readDFG_LogWithCompleteEventsOnly_100_10())) {
                fail("BPMNDiagram is different");
            }
            
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testBPMN_Before_And_After_FilterCriteria() {
        try {
            AttributeLog attLog = createAttributeLog(readLogWithCompleteEventsOnly());
            ProcessDiscoverer pd = new ProcessDiscoverer();
            AbstractionParams params = createAbstrationParams(attLog.getAttribute(),
                                                            1.0, 1.0, 0.4,
                                                            MeasureType.FREQUENCY,
                                                            MeasureAggregation.CASES,
                                                            MeasureRelation.ABSOLUTE,
                                                            MeasureType.FREQUENCY,
                                                            MeasureAggregation.CASES,
                                                            MeasureRelation.ABSOLUTE,
                                                            MeasureType.DURATION,
                                                            MeasureAggregation.MEAN,
                                                            MeasureRelation.ABSOLUTE,
                                                            true);
            Abstraction dfgAbs = pd.generateDFGAbstraction(attLog, params);
            
            // Before filter diagram
            BPMNDiagram beforeFilterDiagram = pd.generateBPMNAbstraction(attLog, params, dfgAbs).getDiagram();
            if (!beforeFilterDiagram.checkSimpleEquality(readBPMN_LogWithCompleteEventsOnly())) {
                fail("BPMNDiagram is different");
            }
            
            // Apply filter criteria
            LogBitMap logBitMap = new LogBitMap(attLog.getOriginalTraces().size());
            logBitMap.setTraceBitSet(LogBitMap.newBitSet(6, 0, 1), 6); // keep the first trace
            logBitMap.addEventBitSet(LogBitMap.newBitSet(5), 5); // keep all events including start and end events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            logBitMap.addEventBitSet(LogBitMap.newBitSet(6), 6); // keep all events
            attLog.updateLogStatus(logBitMap);
            pd.invalidateAbstraction();
            
            // Generate abstraction after applying filter criteria
            BPMNDiagram afterFilterDiagram = pd.generateDFGAbstraction(attLog, params).getDiagram();
            
            // Check after filter diagram and before filter diagram
            if (afterFilterDiagram.checkSimpleEquality(beforeFilterDiagram)) {
                fail("BPMNDiagram after filtering is the same as the diagram before filtering");
            }
            
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}
