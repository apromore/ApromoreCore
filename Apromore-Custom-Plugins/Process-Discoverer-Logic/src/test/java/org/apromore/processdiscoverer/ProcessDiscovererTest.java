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

package org.apromore.processdiscoverer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apromore.logman.ALog;
import org.apromore.logman.Constants;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processdiscoverer.bpmn.TraceBPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.deckfour.xes.model.XLog;
import org.junit.Test;

public class ProcessDiscovererTest extends LogicDataSetup {
    
    private Abstraction discoverProcess(XLog xlog, double nodeSlider, double arcSlider, double paraSlider,
                                        MeasureType structureType, MeasureAggregation structureAggregate,
                                        MeasureType primaryType, MeasureAggregation primaryAggregate,
                                        MeasureType secondaryType, MeasureAggregation secondaryAggregate,
                                        boolean bpmn) throws Exception {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute);
        ProcessDiscoverer pd = new ProcessDiscoverer(attLog);
        AbstractionParams params = new AbstractionParams(
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
                primaryType,
                primaryAggregate,
                secondaryType,
                secondaryAggregate,
                null,
                null);
        
        Abstraction dfgAbs = pd.generateDFGAbstraction(params);
        return (!bpmn ? dfgAbs : pd.generateBPMNAbstraction(params, dfgAbs));
    }
    
    private Abstraction discoverTraceAbstraction(XLog xlog, String traceID) throws Exception {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute);
        ProcessDiscoverer pd = new ProcessDiscoverer(attLog);
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
                                            MeasureType.FREQUENCY,
                                            MeasureAggregation.CASES,
                                            MeasureType.DURATION,
                                            MeasureAggregation.MEAN,
                                            null,
                                            null);
        Abstraction traceAbs = pd.generateTraceAbstraction(traceID, params);
        return traceAbs;
}


    
    
    @Test
    public void testDFG_LogWithOneTraceOneEvent_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithOneTraceOneEvent(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
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
    public void testDFG_LogWithCompleteEventsOnly_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithCompleteEventsOnly(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
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
    public void testBPMN_LogWithCompleteEventsOnly_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithCompleteEventsOnly(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
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
                        assertEquals(4, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (e.getSource() instanceof Gateway && 
                            ((Gateway)e.getSource()).getGatewayType() == GatewayType.DATABASED &&
                            e.getTarget() instanceof Gateway && 
                            ((Gateway)e.getTarget()).getGatewayType() == GatewayType.PARALLEL) {
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") && 
                            ((BPMNNode)e.getTarget()).getLabel().equals("e")) {
                        assertEquals(1, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") && 
                            ((BPMNNode)e.getTarget()).getLabel().equals("b")) {
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") && 
                            ((BPMNNode)e.getTarget()).getLabel().equals("c")) {
                        assertEquals(2, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("b") && 
                            ((BPMNNode)e.getTarget()).getLabel().equals("")) {
                        assertEquals(2, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("c") && 
                            ((BPMNNode)e.getTarget()).getLabel().equals("")) {
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
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
                        assertEquals(3, abs.getArcPrimaryWeight(e), 0);
                        edgeCount++;
                    }
                    else if (((BPMNNode)e.getSource()).getLabel().equals("") && 
                            ((BPMNNode)e.getTarget()).getLabel().equals("d")) {
                        assertEquals(4, abs.getArcPrimaryWeight(e), 0);
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
    public void testDFG_LogWithStartCompleteEventsOverlapping_Duration() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
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
    public void testBPMN_LogWithStartCompleteEventsOverlaping_Duration() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
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
                        assertEquals(25000, abs.getArcPrimaryWeight(e), 0);
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
                        assertEquals(25000, abs.getArcPrimaryWeight(e), 0);
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
    public void testDFG_Sepsis_100_10_GraphStructure() {
        try {
            Abstraction abs = discoverProcess(read_Sepsis(), 
                                                1.0, 0.1, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
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
    public void testBPMN_Sepsis_100_30_DiagramStructure() {
        try {
            Abstraction abs = discoverProcess(read_Sepsis(), 
                                                1.0, 0.3, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
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
    public void testTraceAbstraction_Sepsis() {
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
    public void testTraceAbstraction_NodeDuration() {
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

}
