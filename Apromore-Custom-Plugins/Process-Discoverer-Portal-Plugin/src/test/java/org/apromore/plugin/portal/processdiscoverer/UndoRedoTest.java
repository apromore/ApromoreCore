package org.apromore.plugin.portal.processdiscoverer;

import static org.junit.Assert.assertEquals;

import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.processdiscoverer.actions.ActionManager;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnClearFilter;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRemoveEvent;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class UndoRedoTest extends TestDataSetup {
    @Mock
    private PDController pdController;
    
    @Mock
    private EventLogService eventLogService;
    
    @InjectMocks
    private ActionManager actionManager;
    
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(pdController).updateUI(Mockito.anyBoolean());
    }
    
    private PDAnalyst createPDAnalyst(XLog xlog) throws Exception {
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0, "logName", 0, "folderName");
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(xlog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
                XLogToImmutableLog.convertXLog("ProcessLog", xlog));
        Mockito.when(eventLogService.getCalendarFromLog(contextData.getLogId())).thenReturn(getAllDayAllTimeCalendar());
        ConfigData configData = ConfigData.DEFAULT;
        PDAnalyst analyst = new PDAnalyst(contextData, configData, eventLogService);
        return analyst;
    }
    
    @Test
    public void test_Do_Then_ClearFilter() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnNodeRemoveEvent removeNodeFilterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
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
    public void test_Do_Undo_Then_Redo() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        FilterActionOnNodeRemoveEvent removeNodeFilterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
        removeNodeFilterAction.setElement("a", "concept:name");
        actionManager.executeAction(removeNodeFilterAction);    
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
    public void test_Do_Undo_NewAction_Then_Redo() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        // Do
        FilterActionOnNodeRemoveEvent removeNodeFilterAction = new FilterActionOnNodeRemoveEvent(pdController, analyst);
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
        FilterActionOnNodeRemoveEvent removeNodeFilterAction2 = new FilterActionOnNodeRemoveEvent(pdController, analyst);
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

}
