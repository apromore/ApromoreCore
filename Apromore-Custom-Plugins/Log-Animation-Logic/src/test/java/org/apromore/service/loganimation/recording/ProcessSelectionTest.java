/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.service.loganimation.recording;

import com.google.common.collect.Lists;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import org.apromore.service.loganimation.AnimationResult;
import org.apromore.service.loganimation.LogAnimationService2;
import org.apromore.service.loganimation.impl.LogAnimationServiceImpl2;
import org.apromore.service.loganimation.impl.ProcessSelection;
import org.apromore.service.loganimation.replay.AnimationLog;
import org.apromore.service.loganimation.replay.ReplayTrace;
import org.apromore.service.loganimation.replay.TraceNode;
import org.deckfour.xes.model.XLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ProcessSelectionTest extends TestDataSetup {

    @Test
    void test_remove_data_object() throws Exception {
        String bpmn = readBPMNDiagramAsString("src/test/logs/diagram_with_data_object.bpmn");
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        Process firstProcess = processSelection.getFirstProcess();
        boolean containsDataObject = false;
        for (FlowElement element : firstProcess.getFlowElement()) {
            if (element instanceof DataObject) {
                containsDataObject = true;
                break;
            }
        }
        assertFalse(containsDataObject);
    }

    @Test
    void test_remove_text_annotation() throws Exception {
        String bpmn = readBPMNDiagramAsString("src/test/logs/diagram_with_text_annotation.bpmn");
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        Process firstProcess = processSelection.getFirstProcess();
        boolean containsTextAnnotation = false;
        for (FlowElement element : firstProcess.getFlowElement()) {
            if (element instanceof TextAnnotation) {
                containsTextAnnotation = true;
                break;
            }
        }
        assertFalse(containsTextAnnotation);
    }

    @Test
    void test_selection_first_pool() throws Exception {
        String bpmn = readBPMNDiagramAsString("src/test/logs/invalid_diagram_two_lanes.bpmn");
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        Process firstProcess = processSelection.getFirstProcess();
        assertEquals("Process_1l2r87v", firstProcess.getId());
    }

    @Test
    void test_selection_pool_start_end_event() throws Exception {
        String bpmn = readBPMNDiagramAsString("src/test/logs/invalid_diagram_second_pool.bpmn");
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        Process firstProcess = processSelection.getFirstProcess();
        assertEquals("Process_1dw2j0j", firstProcess.getId());
    }

    @Test
    void test_add_implicit_and_split() throws Exception {
        String bpmn = readBPMNDiagramAsString("src/test/logs/diagram_task_multi_outgoing.bpmn");
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        Process firstProcess = processSelection.getFirstProcess();
        boolean containsImplicitSplit = false;
        boolean correctGatewayInserted = false;
        for (FlowElement element : firstProcess.getFlowElement()) {
            if (!(element instanceof Gateway)) {
                if (element.getOutgoing().size() > 1) {
                    containsImplicitSplit = true;
                }
            } else {
                if (element.getId().contains(ProcessSelection.DELETE)) {
                    if (element instanceof ParallelGateway && element.getOutgoing().size() > 1) {
                        correctGatewayInserted = true;
                    }
                }
            }
        }
        assertFalse(containsImplicitSplit);
        assertTrue(correctGatewayInserted);
    }

    @Test
    void test_add_implicit_xor_join() throws Exception {
        String bpmn = readBPMNDiagramAsString("src/test/logs/invalid_diagram_task_multi_incoming.bpmn");
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        Process firstProcess = processSelection.getFirstProcess();
        boolean containsImplicitJoin = false;
        boolean correctGatewayInserted = false;
        for (FlowElement element : firstProcess.getFlowElement()) {
            if (!(element instanceof Gateway)) {
                if (element.getIncoming().size() > 1) {
                    containsImplicitJoin = true;
                }
            } else {
                if (element.getId().contains(ProcessSelection.DELETE)) {
                    if (element instanceof ExclusiveGateway && element.getIncoming().size() > 1) {
                        correctGatewayInserted = true;
                    }
                }
            }
        }
        assertFalse(containsImplicitJoin);
        assertTrue(correctGatewayInserted);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/logs/invalid_diagram_task_multi_incoming.bpmn",
            "src/test/logs/diagram_task_multi_outgoing.bpmn"
    })
    void test_post_processing(String modelFilename) throws Exception {
        String bpmn = readBPMNDiagramAsString(modelFilename);
        XLog ab = readXESFile("src/test/logs/ab.xes");
        List<LogAnimationService2.Log> serviceLogs = Lists.newArrayList();
        LogAnimationService2.Log serviceLog = new LogAnimationService2.Log();
        serviceLog.xlog = ab;
        serviceLogs.add(serviceLog);
        LogAnimationService2 logAnimationService2 = new LogAnimationServiceImpl2();
        AnimationResult result = logAnimationService2.createAnimation(bpmn, serviceLogs);
        AnimationLog animationLog = result.getAnimationLogs().get(0);
        ProcessSelection processSelection = new ProcessSelection(bpmn);
        animationLog = processSelection.removeImplicitElementsFrom(animationLog);

        //Test removeImplicitFlowElements
        Process firstProcess = processSelection.getFirstProcess();
        boolean containsImplicitFlowElements = false;
        for(FlowElement flowElement : firstProcess.getFlowElement())
        {
            if(flowElement.getId().contains(ProcessSelection.DELETE)) {
                containsImplicitFlowElements = true;
                break;
            }
        }
        assertFalse(containsImplicitFlowElements);

        //Test cleaning of an Animation log
        boolean containsAnyImplicitReplayNode = false;
        for(ReplayTrace replayTrace : animationLog.getTraces())
        {
            for(TraceNode traceNode : replayTrace.getTimeOrderedReplayedNodes())
            {
                if(traceNode.getId().contains(ProcessSelection.DELETE)) {
                    containsAnyImplicitReplayNode = true;
                    break;
                }
            }
        }
        assertFalse(containsAnyImplicitReplayNode);
    }


}
