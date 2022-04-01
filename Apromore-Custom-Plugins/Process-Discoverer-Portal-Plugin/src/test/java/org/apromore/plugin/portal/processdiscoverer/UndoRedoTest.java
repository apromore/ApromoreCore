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
package org.apromore.plugin.portal.processdiscoverer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.processdiscoverer.actions.ActionManager;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterAction;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnClearFilter;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnCompositeFilterCriteria;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnEdgeRemoveTrace;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnEdgeRetainTrace;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnElementFilter;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRemoveEvent;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRemoveTrace;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRetainEvent;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRetainTrace;
import org.apromore.service.EventLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UndoRedoTest extends TestDataSetup {
    @Mock
    private PDController pdController;
    
    @Mock
    private EventLogService eventLogService;
    
    @InjectMocks
    private ActionManager actionManager;
    
    @Test
    void test_Do_Then_ClearFilter() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter removeNodeFilterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        removeNodeFilterAction.setElement("a", "concept:name");
        actionManager.executeAction(removeNodeFilterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        FilterActionOnClearFilter clearFilterAction = new FilterActionOnClearFilter(pdController, analyst);
        actionManager.executeAction(clearFilterAction);   
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
    }
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnNodeRemoveEvent() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter filterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        filterAction.setElement("a", "concept:name");
        actionManager.executeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnNodeRemoveTrace() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter filterAction = new FilterActionOnNodeRemoveTrace(pdController, analyst);
        filterAction.setElement("b", "concept:name");
        actionManager.executeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnNodeRetainEvent() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter filterAction = new FilterActionOnNodeRetainEvent(pdController, analyst);
        filterAction.setElement("b", "concept:name");
        actionManager.executeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnNodeRetainTrace() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter filterAction = new FilterActionOnNodeRetainTrace(pdController, analyst);
        filterAction.setElement("b", "concept:name");
        actionManager.executeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnEdgeRemoveTrace() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter filterAction = new FilterActionOnEdgeRemoveTrace(pdController, analyst);
        filterAction.setElement("b => c", "concept:name");
        actionManager.executeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnEdgeRetainTrace() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnElementFilter filterAction = new FilterActionOnEdgeRetainTrace(pdController, analyst);
        filterAction.setElement("b => c", "concept:name");
        actionManager.executeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Undo_Then_Redo_FilterActionOnCompositeFilterCriteria() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterAction filterAction = new FilterActionOnCompositeFilterCriteria(pdController, analyst);
        filterAction.setPreActionFilterCriteria(new ArrayList<>());
        List<LogFilterRule> filterRules = Arrays.asList(
                new LogFilterRuleImpl(Choice.REMOVE, 
                Inclusion.ANY_VALUE, 
                Section.CASE,
                FilterType.CASE_EVENT_ATTRIBUTE,
                "concept:name",
                new HashSet<>(Arrays.asList(
                        new RuleValue(FilterType.CASE_EVENT_ATTRIBUTE, OperationType.EQUAL, "concept:name", "a"))),
                null));
        filterAction.setPostActionFilterCriteria(filterRules);
        
        analyst.filter(filterRules);
        actionManager.storeAction(filterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        actionManager.redoAction();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Undo_NewAction_Then_Redo() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        // Do
        FilterActionOnElementFilter removeNodeFilterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        removeNodeFilterAction.setElement("a", "concept:name");
        actionManager.executeAction(removeNodeFilterAction);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        // Undo
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        // New action: causing the redo history empty.
        FilterActionOnElementFilter removeNodeFilterAction2 = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        removeNodeFilterAction2.setElement("b", "concept:name");
        actionManager.executeAction(removeNodeFilterAction2);    
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(3, attLog.getTraces().get(1).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        assertEquals("c", attLog.getStringFromValue(attLog.getTraces().get(1).getValueTrace().get(1)));
        assertEquals(false, actionManager.canRedo());
        assertEquals(true, actionManager.canUndo());
        
        // Redo: nothing can be redo because of the previous new action 
        actionManager.redoAction(); //  
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(3, attLog.getTraces().get(1).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        assertEquals("c", attLog.getStringFromValue(attLog.getTraces().get(1).getValueTrace().get(1)));
        
        actionManager.undoAction(); // now the redo history has the new action
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        assertEquals(true, actionManager.canRedo());
        
        actionManager.redoAction(); 
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(3, attLog.getTraces().get(1).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        assertEquals("c", attLog.getStringFromValue(attLog.getTraces().get(1).getValueTrace().get(1)));
    }
    
    @Test
    void test_Do_Do_Undo_Undo_Then_Redo() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        // Do #1
        FilterActionOnElementFilter removeNodeFilterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        removeNodeFilterAction.setElement("b", "concept:name");
        actionManager.executeAction(removeNodeFilterAction);    
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(3, attLog.getTraces().get(1).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        assertEquals("c", attLog.getStringFromValue(attLog.getTraces().get(1).getValueTrace().get(1)));
        
        // Do #2
        FilterActionOnElementFilter removeNodeFilterAction2 = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        removeNodeFilterAction2.setElement("c", "concept:name");
        actionManager.executeAction(removeNodeFilterAction2);    
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        // Undo #1: undo removeNodeFilterAction2
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(3, attLog.getTraces().get(1).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        assertEquals("c", attLog.getStringFromValue(attLog.getTraces().get(1).getValueTrace().get(1)));
        
        // Undo #2: undo removeNodeFilterAction1
        actionManager.undoAction();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); 
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); 
        
        // Now, Redo: redo removeNodeFilterAction1
        actionManager.redoAction(); //  
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(3, attLog.getTraces().get(1).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        assertEquals("c", attLog.getStringFromValue(attLog.getTraces().get(1).getValueTrace().get(1)));
    }


}
