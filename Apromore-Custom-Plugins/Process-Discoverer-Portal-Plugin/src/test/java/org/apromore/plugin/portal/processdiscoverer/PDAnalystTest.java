/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Objects;

import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.logman.attribute.AttributeLevel;
import org.apromore.logman.attribute.AttributeType;
import org.apromore.logman.attribute.log.AttributeInfo;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.processdiscoverer.data.CaseDetails;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.PerspectiveDetails;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.list.ListIterable;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PDAnalystTest extends TestDataSetup {
    
    @Test
    public void test_AnalystConstructor() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        
        assertEquals(false, analyst.hasEmptyData());
        
        assertEquals(2, analyst.getAvailableAttributes().size());
        assertEquals(false, Objects.isNull(analyst.getAttributeLog()));
        assertEquals(false, Objects.isNull(analyst.getAttribute("concept:name")));
        assertEquals(false, Objects.isNull(analyst.getAttribute("lifecycle:transition")));
        assertEquals(true, Objects.isNull(analyst.getAttribute("org:resource")));
        
        assertEquals("concept:name", analyst.getMainAttribute().getKey());
        assertEquals(AttributeLevel.EVENT, analyst.getMainAttribute().getLevel());
        assertEquals(AttributeType.LITERAL, analyst.getMainAttribute().getType());
        
        assertEquals(true, ((List)analyst.getCurrentFilterCriteria()).isEmpty());
        
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
    }
    
    @Test
    public void test_getCaseDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        List<CaseDetails> caseDetails = analyst.getCaseDetails();
        assertEquals(1, caseDetails.size());
        assertEquals("Case1", caseDetails.get(0).getCaseId());
        assertEquals(null, caseDetails.get(0).getCaseIdDigit());
        assertEquals(1d, caseDetails.get(0).getCaseVariantFreq(), 0);
        assertEquals("100", caseDetails.get(0).getCaseVariantFreqStr());
        assertEquals(1, caseDetails.get(0).getCaseVariantId());
        assertEquals(1, caseDetails.get(0).getCaseEvents());
    }
    
    @Test
    public void test_getActivityDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        List<PerspectiveDetails> actDetails = analyst.getActivityDetails();
        assertEquals(1, actDetails.size());
        assertEquals("a", actDetails.get(0).getValue());
        assertEquals(1, actDetails.get(0).getFreq(),0);
        assertEquals("100", actDetails.get(0).getFreqStr());
        assertEquals(1, actDetails.get(0).getOccurrences());
    }
    
    @Test
    public void test_getAttributeInfoList() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        ListIterable<AttributeInfo> attInfoList = analyst.getAttributeInfoList();
        assertEquals(1, attInfoList.size());
        assertEquals("a", attInfoList.get(0).getAttributeValue());
        assertEquals(1, attInfoList.get(0).getAttributeOccurrenceFrequency(), 0);
        assertEquals(1, attInfoList.get(0).getAttributeOccurrenceCount());
    }
    
    @Test
    public void test_getStatistics() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        assertEquals(1, analyst.getFilteredActivityInstanceSize());
        assertEquals(1, analyst.getFilteredCaseVariantSize());
        assertEquals(DateTimeUtils.humanize(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis()), analyst.getFilteredStartTime());
        assertEquals(DateTimeUtils.humanize(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis()), analyst.getFilteredEndTime());
        
        assertEquals("instant", analyst.getFilteredMinDuration());
        assertEquals("instant", analyst.getFilteredMaxDuration());
        assertEquals("instant", analyst.getFilteredMeanDuration());
        assertEquals("instant", analyst.getFilteredMedianDuration());
    }
    
    @Test
    public void test_getXLog() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        XLog xlog = analyst.getXLog();
        assertEquals(1, xlog.size());
        assertEquals(1, xlog.get(0).getAttributes().size());
        assertEquals("Case1", xlog.get(0).getAttributes().get("concept:name").toString());
        assertEquals(1, xlog.get(0).size());
        assertEquals(3, xlog.get(0).get(0).getAttributes().size());
        assertEquals("a", xlog.get(0).get(0).getAttributes().get("concept:name").toString());
        assertEquals("2010-10-27T22:31:19.495+10:00", xlog.get(0).get(0).getAttributes().get("time:timestamp").toString());
        assertEquals("complete", xlog.get(0).get(0).getAttributes().get("lifecycle:transition").toString());
    }
    
    @Test
    public void test_Filters() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        
        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events
        
        analyst.filter_RemoveTracesAnyValueOfEventAttribute("b", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RemoveTracesAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RetainTracesAnyValueOfEventAttribute("a", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        // Need to confirm if this is a correct result
        analyst.filter_RetainTracesAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RemoveEventsAnyValueOfEventAttribute("a", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RemoveEventsAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RetainEventsAnyValueOfEventAttribute("a", "concept:name"); 
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        // Need to confirm
        analyst.filter_RetainEventsAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RemoveTracesAnyValueOfDirectFollowRelation("b => c", "concept:name"); 
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RemoveTracesAnyValueOfDirectFollowRelation("a => c", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RetainTracesAnyValueOfDirectFollowRelation("b => c", "concept:name"); 
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));
        
        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());
        
        analyst.filter_RetainTracesAnyValueOfDirectFollowRelation("a => c", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

    }
    
}
