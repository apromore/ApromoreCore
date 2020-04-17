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

package org.apromore.logman;

import java.util.BitSet;

import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.exception.InvalidAttributeLogStatusUpdateException;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.logman.attribute.log.AttributeTraceVariants;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

public class LogTest extends DataSetup {
    private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    @Test
    public void test_SpecialEmptyLog() {
        ALog log = new ALog(readEmptyLog());
        Assert.assertEquals(0, log.getOriginalTraces().size());
        Assert.assertEquals(0, log.getAttributes().size());
        Assert.assertEquals(0, log.getOriginalTraces().size());
        Assert.assertEquals(0, log.getTraces().size());
        Assert.assertEquals(0, log.getAttributeStore().getAttributeIndexes().length);
        Assert.assertEquals(0, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals(0,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(0,  log.getNumberOfEvents());
        
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        Assert.assertEquals(0, attLog.getTraces().size());
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(0, oriLogSummary.getCaseCount());
        Assert.assertEquals(0, oriLogSummary.getActivityCount());
        Assert.assertEquals(0, oriLogSummary.getEventCount());
        Assert.assertEquals(0, oriLogSummary.getVariantCount());
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(0, logSummary.getCaseCount());
        Assert.assertEquals(0, logSummary.getActivityCount());
        Assert.assertEquals(0, logSummary.getEventCount());
        Assert.assertEquals(0, logSummary.getVariantCount());
        Assert.assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);
    }
    
    public void test_SpecialLogWithOneEmptyTrace() {
        ALog log = new ALog(readLogWithEmptyTrace());
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getTraces().size());
        Assert.assertEquals(1, log.getOriginalTraceStatus().cardinality());

        Assert.assertEquals(null, log.getTraces().get(0).getTraceId()); 
        Assert.assertEquals(0, log.getTraces().get(0).getOriginalActivities().size());
        Assert.assertEquals(0, log.getTraces().get(0).getActivities().size());
        Assert.assertEquals(0, log.getTraces().get(0).getEvents().size());
        Assert.assertEquals(0, log.getTraces().get(0).getOriginalEvents().size());
        Assert.assertEquals(0, log.getTraces().get(0).getAttributes().size());  
        Assert.assertEquals(0, log.getTraces().get(0).getOriginalEventStatus().cardinality()); 
        Assert.assertEquals(0, log.getTraces().get(0).getOriginalActivityStatus().cardinality());
        Assert.assertEquals(0,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(0,  log.getNumberOfEvents());        
        
        // AttributeLog
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        Assert.assertEquals(0, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(0, attLog.getNumberOfEvents());
        Assert.assertEquals(IntLists.mutable.empty(), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntLists.mutable.empty(), attLog.getAttributeValues());
        Assert.assertEquals(1, attLog.getTraces().size());
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(1, attLog.getNumberOfEvents());
        
        // Variants
        Assert.assertEquals(0, attLog.getVariantView().getActiveVariants().size());
        
        // AttributeTrace
        AttributeTrace attTrace = attLog.getTraces().get(0);
        Assert.assertEquals(true, attTrace.isEmpty());
        Assert.assertEquals(true, attTrace.getValueTrace().isEmpty());
        Assert.assertEquals(true, attTrace.getStartTimeTrace().isEmpty());
        Assert.assertEquals(true, attTrace.getEndTimeTrace().isEmpty());
        Assert.assertEquals(0, attTrace.getDuration());
        Assert.assertEquals(0, attTrace.getStartTime());
        Assert.assertEquals(0, attTrace.getEndTime());
        Assert.assertEquals(0, attTrace.getActiveArcs().size());
        Assert.assertEquals(0, attTrace.getActiveNodes().size());
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(0, oriLogSummary.getCaseCount());
        Assert.assertEquals(0, oriLogSummary.getActivityCount());
        Assert.assertEquals(0, oriLogSummary.getEventCount());
        Assert.assertEquals(0, oriLogSummary.getVariantCount());
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(0, logSummary.getCaseCount());
        Assert.assertEquals(0, logSummary.getActivityCount());
        Assert.assertEquals(0, logSummary.getEventCount());
        Assert.assertEquals(0, logSummary.getVariantCount());
        Assert.assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);

    }
    
    @Test
    public void test_SpecialLogWithOneTrace_OneCompleteEventOnly() {
        ALog log = new ALog(readLogWithOneTraceOneEvent());
        ATrace trace0 = log.getTraces().get(0);
        AActivity activity0 = trace0.getActivityFromIndex(0);
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);
        
        // ALog
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getAttributes().size());
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getTraces().size());
        Assert.assertEquals(5, log.getAttributeStore().getAttributeIndexes().length);
        Assert.assertEquals(1, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(1,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(1,  log.getNumberOfEvents());  
        
        // ATrace
        Assert.assertEquals("Case1", log.getTraces().get(0).getTraceId()); 
        Assert.assertEquals(1, trace0.getOriginalActivities().size());
        Assert.assertEquals(1, trace0.getActivities().size());
        Assert.assertEquals(1, trace0.getEvents().size());
        Assert.assertEquals(1, trace0.getOriginalEvents().size());
        Assert.assertEquals(1, trace0.getAttributes().size());  
        Assert.assertEquals(1, trace0.getOriginalEventStatus().cardinality()); 
        Assert.assertEquals(1, trace0.getOriginalActivityStatus().cardinality());
        
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), trace0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), trace0.getEndTime());
        Assert.assertEquals(LongArrayList.newListWith(log.getTraces().get(0).getStartTime()), trace0.getStartTimeTrace());
        Assert.assertEquals(LongArrayList.newListWith(log.getTraces().get(0).getStartTime()), trace0.getEndTimeTrace());
        
        Assert.assertEquals("a", trace0.getEventFromIndex(0).getAttributes().get("concept:name").toString());
        Assert.assertEquals("complete", trace0.getEventFromIndex(0).getAttributes().get("lifecycle:transition").toString());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00"), 
                            dateFormatter.parseDateTime(trace0.getEventFromIndex(0).getAttributes().get("time:timestamp").toString()));
        
        // Activity
        Assert.assertEquals(0, activity0.getDuration());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), activity0.getStartTimestamp());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), activity0.getEndTimestamp());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00"), activity0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00"), activity0.getEndTime());
        Assert.assertEquals(true, activity0.isActive());
        Assert.assertEquals(true, activity0.isUseComplete());
        Assert.assertEquals(true, activity0.isInstant());
        
        // AttributeLog
        Assert.assertEquals(1, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(1, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0), attLog.getAttributeValues());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals(0, attLog.getValueFromString("a"));
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(1, attLog.getTraces().size());
        Assert.assertEquals(1, attLog.getNumberOfEvents());
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(1, oriLogSummary.getActivityCount());
        Assert.assertEquals(1, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(1, logSummary.getActivityCount());
        Assert.assertEquals(1, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);
        
        // Variants
        Assert.assertEquals(1, attLog.getVariantView().getActiveVariants().size());
        IntList variant0 = attLog.getVariantView().getActiveVariants().getVariantAtIndex(0);
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,attLog.getEndEvent()), variant0);
        Assert.assertEquals(1, attLog.getVariantView().getActiveVariants().getFrequency(variant0));
        Assert.assertEquals(Lists.mutable.of(attTrace0), attLog.getVariantView().getActiveVariants().getTraces(variant0));

        // AttributeTrace
        Assert.assertEquals("Case1", attTrace0.getTraceId());
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), attTrace0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), attTrace0.getEndTime());
        Assert.assertEquals(0,attTrace0.getDuration());
        Assert.assertEquals(3, attTrace0.getOriginalEventStatus().cardinality());
        Assert.assertEquals(0,attTrace0.getVariantIndex());
        Assert.assertEquals(0,attTrace0.getVariantRank());
        Assert.assertEquals(IntArrayList.newListWith(attLog.getStartEvent(),0,attLog.getEndEvent()), attTrace0.getOriginalValueTrace());
        Assert.assertEquals(IntArrayList.newListWith(attLog.getStartEvent(),0,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis()), attTrace0.getStartTimeTrace());
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis()), attTrace0.getEndTimeTrace());    
        Assert.assertEquals(false, attTrace0.isEmpty());
        
        Assert.assertEquals(IntSets.mutable.of(3,2), attTrace0.getActiveArcs());
        Assert.assertEquals(IntSets.mutable.of(0,1,2), attTrace0.getActiveNodes());
        
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(0));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(1));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(2));
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(1));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(1));        
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(2));   
        
        Assert.assertEquals(1, attTrace0.getArcTotalCount(2));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(3));
        Assert.assertEquals(0, attTrace0.getArcMinDuration(2));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(2));
        Assert.assertEquals(0, attTrace0.getArcMinDuration(3));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(3));        

    }
    
    @Test
    public void test1_AttributesOf_ATrace_AActivity_AttributeTrace_And_Changing_Attribute() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);
        
        // ALog
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getTraces().size());        
        Assert.assertEquals(1, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(11,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(11,  log.getNumberOfEvents());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getTraces());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());
        
        // ATrace
        Assert.assertEquals("Case1", trace0.getTraceId()); 
        Assert.assertEquals(11, trace0.getOriginalActivities().size());
        Assert.assertEquals(11, trace0.getActivities().size());
        Assert.assertEquals(11, trace0.getEvents().size());
        Assert.assertEquals(11, trace0.getOriginalEvents().size());
        Assert.assertEquals(1, trace0.getAttributes().size());  
        Assert.assertEquals(11, trace0.getOriginalEventStatus().cardinality()); 
        Assert.assertEquals(11, trace0.getOriginalActivityStatus().cardinality());
        
        
        // AttributeLog
        Assert.assertEquals(11, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getAttributeValues());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals("b", attLog.getStringFromValue(1));
        Assert.assertEquals("c", attLog.getStringFromValue(2));
        Assert.assertEquals("d", attLog.getStringFromValue(3));
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(1, attLog.getTraces().size());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(4, oriLogSummary.getActivityCount());
        Assert.assertEquals(11, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(4, logSummary.getActivityCount());
        Assert.assertEquals(11, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);
        
        // AttributeTrace
        Assert.assertEquals("Case1", attTrace0.getTraceId());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(), attTrace0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        Assert.assertEquals(3300000,attTrace0.getDuration());
        Assert.assertEquals(13, attTrace0.getOriginalEventStatus().cardinality());
        Assert.assertEquals(0,attTrace0.getVariantIndex());
        Assert.assertEquals(0,attTrace0.getVariantRank());

        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(IntArrayList.newListWith(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), attTrace0.getOriginalValueTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(), //arc 0: 0->0
                dateFormatter.parseDateTime("2010-10-27T22:01:19.308+10:00").getMillis(), //arc 0: 0->0
                dateFormatter.parseDateTime("2010-10-27T22:03:19.308+10:00").getMillis(), // 1: 0->1
                dateFormatter.parseDateTime("2010-10-27T22:06:19.308+10:00").getMillis(), // 2: 1->2
                dateFormatter.parseDateTime("2010-10-27T22:10:19.308+10:00").getMillis(), // 
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getStartTimeTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:01:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:03:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:06:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:10:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getEndTimeTrace());   
        
        Assert.assertEquals(LongArrayList.newListWith(0,0,0,0,0,0,0,0,0,0,0,0,0), attTrace0.getDurationTrace());         
        
        Assert.assertEquals(false, attTrace0.isEmpty());
        
        Assert.assertEquals(IntSets.mutable.of(0,1,2,8,12,15,17,20,24), attTrace0.getActiveArcs());
        Assert.assertEquals(IntSets.mutable.of(0,1,2,3,4,5), attTrace0.getActiveNodes());
        
        Assert.assertEquals(4, attTrace0.getNodeTotalCount(0));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(1));
        Assert.assertEquals(4, attTrace0.getNodeTotalCount(2));
        Assert.assertEquals(2, attTrace0.getNodeTotalCount(3));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(4)); //-1
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(5)); //-2
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(0));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(1));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(1));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(1));        
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(2));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(3));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(3));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(3));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(4));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(5));   
        
        Assert.assertEquals(2, attTrace0.getArcTotalCount(0)); //0,1,2,8,12,15,17,20,24
        Assert.assertEquals(1, attTrace0.getArcTotalCount(1));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(2));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(8));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(12));
        Assert.assertEquals(2, attTrace0.getArcTotalCount(15));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(17));
        Assert.assertEquals(2, attTrace0.getArcTotalCount(20));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(24));
        
        Assert.assertEquals(60000, attTrace0.getArcMinDuration(0));
        Assert.assertEquals(120000, attTrace0.getArcMaxDuration(0));
        
        Assert.assertEquals(180000, attTrace0.getArcMinDuration(1));
        Assert.assertEquals(180000, attTrace0.getArcMaxDuration(1));
        
        Assert.assertEquals(600000, attTrace0.getArcMinDuration(2));
        Assert.assertEquals(600000, attTrace0.getArcMaxDuration(2));
        
        Assert.assertEquals(240000, attTrace0.getArcMinDuration(8));
        Assert.assertEquals(240000, attTrace0.getArcMaxDuration(8));
        
        Assert.assertEquals(540000, attTrace0.getArcMinDuration(12));
        Assert.assertEquals(540000, attTrace0.getArcMaxDuration(12));
        
        Assert.assertEquals(300000, attTrace0.getArcMinDuration(15));
        Assert.assertEquals(420000, attTrace0.getArcMaxDuration(15));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(17));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(17));
        
        Assert.assertEquals(360000, attTrace0.getArcMinDuration(20));
        Assert.assertEquals(480000, attTrace0.getArcMaxDuration(20));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(24));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(24));
        
        
        // Variants
        Assert.assertEquals(1, attLog.getVariantView().getActiveVariants().size());
        IntList variant0 = variants.getVariantAtIndex(0);
        
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), variant0);
        
        Assert.assertEquals(0, variants.getIndexOf(variant0));
        Assert.assertEquals(1, variants.getFrequency(variant0));
        Assert.assertEquals(0, variants.getRankOf(variant0));
        
        Assert.assertEquals(1.0, variants.getVariantRelativeFrequency(variant0), 0.01);
        
        Assert.assertEquals(Lists.mutable.of(attTrace0), variants.getTraces(variant0));
    }
    
    
    @Test
    public void test_Changing_Attribute() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTrace attTrace0 = attLog.getTraces().get(0);
        
        IndexableAttribute resAtt = log.getAttributeStore().getStandardEventResource();
        attLog.setAttribute(resAtt);
        
        // AttributeLog
        Assert.assertEquals(11, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3,4), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3,4), attLog.getAttributeValues());
        Assert.assertEquals("R1", attLog.getStringFromValue(0));
        Assert.assertEquals("R2", attLog.getStringFromValue(1));
        Assert.assertEquals("R3", attLog.getStringFromValue(2));
        Assert.assertEquals("R4", attLog.getStringFromValue(3));
        Assert.assertEquals("R5", attLog.getStringFromValue(4));        
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        Assert.assertEquals(attTrace0, attLog.getTraceFromTraceId("Case1"));
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(5, oriLogSummary.getActivityCount());
        Assert.assertEquals(11, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(5, logSummary.getActivityCount());
        Assert.assertEquals(11, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);
        
        // AttributeTrace
        Assert.assertEquals("Case1", attTrace0.getTraceId());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(), attTrace0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        Assert.assertEquals(3300000,attTrace0.getDuration());
        Assert.assertEquals(13, attTrace0.getOriginalEventStatus().cardinality());
        Assert.assertEquals(0,attTrace0.getVariantIndex());
        Assert.assertEquals(0,attTrace0.getVariantRank());

        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,1,1,1,2,3,3,2,0,1,4,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(IntArrayList.newListWith(attLog.getStartEvent(),0,1,1,1,2,3,3,2,0,1,4,attLog.getEndEvent()), attTrace0.getOriginalValueTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:01:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:03:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:06:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:10:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getStartTimeTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:01:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:03:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:06:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:10:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getEndTimeTrace());   
        
        Assert.assertEquals(LongArrayList.newListWith(0,0,0,0,0,0,0,0,0,0,0,0,0), attTrace0.getDurationTrace());         
        
        Assert.assertEquals(false, attTrace0.isEmpty());
        
        Assert.assertEquals(IntSets.mutable.of(1,8,9,11,14,17,23,24,34,35), attTrace0.getActiveArcs());
        Assert.assertEquals(IntSets.mutable.of(0,1,2,3,4,5,6), attTrace0.getActiveNodes());
        
        Assert.assertEquals(2, attTrace0.getNodeTotalCount(0));
        Assert.assertEquals(4, attTrace0.getNodeTotalCount(1));
        Assert.assertEquals(2, attTrace0.getNodeTotalCount(2));
        Assert.assertEquals(2, attTrace0.getNodeTotalCount(3));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(4)); 
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(5)); 
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(6)); 
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(0));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(1));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(1));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(1));        
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(2));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(3));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(3));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(3));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(4));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(5));   
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(6));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(6));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(6));          
        
        Assert.assertEquals(2, attTrace0.getArcTotalCount(1)); //1,8,9,11,14,17,23,24,34,35
        Assert.assertEquals(2, attTrace0.getArcTotalCount(8));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(9));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(11));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(14));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(17));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(23));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(24));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(34));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(35));
        
        Assert.assertEquals(60000, attTrace0.getArcMinDuration(1));
        Assert.assertEquals(540000, attTrace0.getArcMaxDuration(1));
        
        Assert.assertEquals(120000, attTrace0.getArcMinDuration(8));
        Assert.assertEquals(180000, attTrace0.getArcMaxDuration(8));
        
        Assert.assertEquals(240000, attTrace0.getArcMinDuration(9));
        Assert.assertEquals(240000, attTrace0.getArcMaxDuration(9));
        
        Assert.assertEquals(600000, attTrace0.getArcMinDuration(11));
        Assert.assertEquals(600000, attTrace0.getArcMaxDuration(11));
        
        Assert.assertEquals(480000, attTrace0.getArcMinDuration(14));
        Assert.assertEquals(480000, attTrace0.getArcMaxDuration(14));
        
        Assert.assertEquals(300000, attTrace0.getArcMinDuration(17));
        Assert.assertEquals(300000, attTrace0.getArcMaxDuration(17));
        
        Assert.assertEquals(420000, attTrace0.getArcMinDuration(23));
        Assert.assertEquals(420000, attTrace0.getArcMaxDuration(23));
        
        Assert.assertEquals(360000, attTrace0.getArcMinDuration(24));
        Assert.assertEquals(360000, attTrace0.getArcMaxDuration(24));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(34));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(34));     
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(35));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(35));           
    }
    
    @Test
    public void test_LogSummary_AfterChanging_Attribute() {
        ALog log = new ALog(readLogWithCompleteEventsOnly());
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(6, oriLogSummary.getCaseCount());
        Assert.assertEquals(5, oriLogSummary.getActivityCount());
        Assert.assertEquals(23, oriLogSummary.getEventCount());
        Assert.assertEquals(3, oriLogSummary.getVariantCount());
        Assert.assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        Assert.assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(6, logSummary.getCaseCount());
        Assert.assertEquals(5, logSummary.getActivityCount());
        Assert.assertEquals(23, logSummary.getEventCount());
        Assert.assertEquals(3, logSummary.getVariantCount());
        Assert.assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        Assert.assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
        
        // Change perspective attribute
        IndexableAttribute resAtt = log.getAttributeStore().getStandardEventResource();
        attLog.setAttribute(resAtt);
        
        oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(6, oriLogSummary.getCaseCount());
        Assert.assertEquals(5, oriLogSummary.getActivityCount());
        Assert.assertEquals(23, oriLogSummary.getEventCount());
        Assert.assertEquals(5, oriLogSummary.getVariantCount());
        Assert.assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        Assert.assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
        
        logSummary = attLog.getLogSummary();
        Assert.assertEquals(6, logSummary.getCaseCount());
        Assert.assertEquals(5, logSummary.getActivityCount());
        Assert.assertEquals(23, logSummary.getEventCount());
        Assert.assertEquals(5, logSummary.getVariantCount());
        Assert.assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        Assert.assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
    }
    
    @Test
    public void test_Trace_Filtering() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTrace attTrace0 = attLog.getTraces().get(0);
        
        // AttributeLogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(4, oriLogSummary.getActivityCount());
        Assert.assertEquals(11, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(4, logSummary.getActivityCount());
        Assert.assertEquals(11, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);
        
        // Filter traces of ALog
        LogBitMap logBitMap1 = null;
        try {
            logBitMap1 = new LogBitMap(log.getOriginalTraces().size());
            logBitMap1.setTraceBitSet(new BitSet(1), 1); // remove trace
            logBitMap1.addEventBitSet(LogBitMap.newBitSet(11), 11); // keep all events
            log.updateLogStatus(logBitMap1);
        } catch (InvalidLogBitMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidALogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributeLogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(0, log.getTraces().size());        
        Assert.assertEquals(0, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(11,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(0,  log.getNumberOfEvents());
        Assert.assertEquals(Lists.mutable.empty(), log.getTraces());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());
        
        // Filter traces of AttributeLog
        try {
            attLog.refresh();
        } catch (InvalidLogBitMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributeLogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Assert.assertEquals(11, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(0, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.empty(), attLog.getAttributeValues());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals("b", attLog.getStringFromValue(1));
        Assert.assertEquals("c", attLog.getStringFromValue(2));
        Assert.assertEquals("d", attLog.getStringFromValue(3));
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(0, attLog.getTraces().size());
        Assert.assertEquals(0, attLog.getNumberOfEvents());
        
        // LogSummary
        oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(4, oriLogSummary.getActivityCount());
        Assert.assertEquals(11, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        logSummary = attLog.getLogSummary();
        Assert.assertEquals(0, logSummary.getCaseCount());
        Assert.assertEquals(0, logSummary.getActivityCount());
        Assert.assertEquals(0, logSummary.getEventCount());
        Assert.assertEquals(0, logSummary.getVariantCount());
        Assert.assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);
        
        
        // Restore 
        try {
            logBitMap1.clear();
            logBitMap1.setTraceBitSet(LogBitMap.newBitSet(1), 1); // re-add trace
            logBitMap1.addEventBitSet(LogBitMap.newBitSet(11), 11); // keep all events
            log.updateLogStatus(logBitMap1);
        } catch (InvalidLogBitMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidALogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributeLogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // ALog
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getTraces().size());        
        Assert.assertEquals(1, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(11, log.getOriginalNumberOfEvents());
        Assert.assertEquals(11, log.getNumberOfEvents());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getTraces());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());
        
        // Refresh AttributeLog
        try {
            attLog.refresh();
        } catch (InvalidLogBitMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributeLogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        Assert.assertEquals(11, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getAttributeValues());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals("b", attLog.getStringFromValue(1));
        Assert.assertEquals("c", attLog.getStringFromValue(2));
        Assert.assertEquals("d", attLog.getStringFromValue(3));
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(1, attLog.getTraces().size());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        
        // LogSummary
        logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(4, logSummary.getActivityCount());
        Assert.assertEquals(11, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);
        
    }
    
    @Test
    public void test_Event_Filtering() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTrace attTrace0 = attLog.getTraces().get(0);
        
        // AttributeLogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(4, oriLogSummary.getActivityCount());
        Assert.assertEquals(11, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(4, logSummary.getActivityCount());
        Assert.assertEquals(11, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);
        
        // Filter traces of ALog
        LogBitMap logBitMap1 = null;
        try {
            logBitMap1 = new LogBitMap(log.getOriginalTraces().size());
            logBitMap1.setTraceBitSet(LogBitMap.newBitSet(1), 1); // keep trace
            logBitMap1.addEventBitSet(LogBitMap.newBitSet(11, 5, 11), 11);
            log.updateLogStatus(logBitMap1);
        } catch (InvalidLogBitMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidALogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributeLogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getTraces().size());        
        Assert.assertEquals(1, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(11,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(6,  log.getNumberOfEvents());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getTraces());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());
        
        // Filter traces of AttributeLog
        try {
            attLog.refresh();
        } catch (InvalidLogBitMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributeLogStatusUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Assert.assertEquals(11, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(6, attLog.getNumberOfEvents());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals("c", attLog.getStringFromValue(2));
        Assert.assertEquals("d", attLog.getStringFromValue(3));        
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,2,3), attLog.getAttributeValues());
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(1, attLog.getTraces().size());
        Assert.assertEquals(6, attLog.getNumberOfEvents());
        
        // AttributeLogSummary
        oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(4, oriLogSummary.getActivityCount());
        Assert.assertEquals(11, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(3, logSummary.getActivityCount());
        Assert.assertEquals(6, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(2400000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(2400000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(2400000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(2400000, logSummary.getTraceDurationMedian(), 0.05);
        
        // AttributeTrace
        Assert.assertEquals("Case1", attTrace0.getTraceId());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(), attTrace0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        Assert.assertEquals(2400000,attTrace0.getDuration());
        Assert.assertEquals(8, attTrace0.getOriginalEventStatus().cardinality());
        Assert.assertEquals(0,attTrace0.getVariantIndex());
        Assert.assertEquals(0,attTrace0.getVariantRank());
        
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),3,2,3,2,0,2,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(IntArrayList.newListWith(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), attTrace0.getOriginalValueTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),                
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getStartTimeTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),                
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getEndTimeTrace());   
        
        Assert.assertEquals(LongArrayList.newListWith(0,0,0,0,0,0,0,0), attTrace0.getDurationTrace());         
        
        Assert.assertEquals(false, attTrace0.isEmpty());
        
        Assert.assertEquals(IntSets.mutable.of(2,12,15,17,20,27), attTrace0.getActiveArcs());
        Assert.assertEquals(IntSets.mutable.of(0,2,3,4,5), attTrace0.getActiveNodes());
        
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(0));
        Assert.assertEquals(3, attTrace0.getNodeTotalCount(2));
        Assert.assertEquals(2, attTrace0.getNodeTotalCount(3));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(4)); //-1
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(5)); //-2
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(0));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(0));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(2));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(2));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(3));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(3));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(3));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(4));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(5));   
        
        Assert.assertEquals(1, attTrace0.getArcTotalCount(2)); //2,12,15,17,20,27
        Assert.assertEquals(1, attTrace0.getArcTotalCount(12));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(15));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(17));
        Assert.assertEquals(2, attTrace0.getArcTotalCount(20));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(27));
        
        Assert.assertEquals(600000, attTrace0.getArcMinDuration(2));
        Assert.assertEquals(600000, attTrace0.getArcMaxDuration(2));
        
        Assert.assertEquals(540000, attTrace0.getArcMinDuration(12));
        Assert.assertEquals(540000, attTrace0.getArcMaxDuration(12));
        
        Assert.assertEquals(420000, attTrace0.getArcMinDuration(15));
        Assert.assertEquals(420000, attTrace0.getArcMaxDuration(15));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(17));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(17));
        
        Assert.assertEquals(360000, attTrace0.getArcMinDuration(20));
        Assert.assertEquals(480000, attTrace0.getArcMaxDuration(20));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(27));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(27));
    }
    
    @Test
    // Similar to test1_AttributesOf_ATrace_AActivity_AttributeTrace_And_Changing_Attribute()
    // but for a log with start and complete events and these pairs are non-overlapping in time.
    public void test2_AttributesOf_ATrace_AActivity_AttributeTrace() {
        ALog log = new ALog(readLogWithOneTrace_StartCompleteEvents_NonOverlapping());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);
        
        // ALog
        Assert.assertEquals(1, log.getOriginalTraces().size());
        Assert.assertEquals(1, log.getTraces().size());        
        Assert.assertEquals(1, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(22,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(22,  log.getNumberOfEvents());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getTraces());
        Assert.assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());
        
        // ATrace
        Assert.assertEquals("Case1", trace0.getTraceId()); 
        Assert.assertEquals(11, trace0.getOriginalActivities().size());
        Assert.assertEquals(11, trace0.getActivities().size());
        Assert.assertEquals(22, trace0.getEvents().size());
        Assert.assertEquals(22, trace0.getOriginalEvents().size());
        Assert.assertEquals(1, trace0.getAttributes().size());  
        Assert.assertEquals(22, trace0.getOriginalEventStatus().cardinality()); 
        Assert.assertEquals(11, trace0.getOriginalActivityStatus().cardinality());
        
        
        // AttributeLog
        Assert.assertEquals(11, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3), attLog.getAttributeValues());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals("b", attLog.getStringFromValue(1));
        Assert.assertEquals("c", attLog.getStringFromValue(2));
        Assert.assertEquals("d", attLog.getStringFromValue(3));
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        
        Assert.assertEquals(1, attLog.getOriginalTraces().size());
        Assert.assertEquals(1, attLog.getTraces().size());
        Assert.assertEquals(11, attLog.getNumberOfEvents());
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(1, oriLogSummary.getCaseCount());
        Assert.assertEquals(4, oriLogSummary.getActivityCount());
        Assert.assertEquals(22, oriLogSummary.getEventCount());
        Assert.assertEquals(1, oriLogSummary.getVariantCount());
        Assert.assertEquals(3360000, oriLogSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3360000, oriLogSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3360000, oriLogSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3360000, oriLogSummary.getTraceDurationMedian(), 0.05);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(1, logSummary.getCaseCount());
        Assert.assertEquals(4, logSummary.getActivityCount());
        Assert.assertEquals(22, logSummary.getEventCount());
        Assert.assertEquals(1, logSummary.getVariantCount());
        Assert.assertEquals(3360000, logSummary.getTraceDurationMin(), 0.05);
        Assert.assertEquals(3360000, logSummary.getTraceDurationMax(), 0.05);
        Assert.assertEquals(3360000, logSummary.getTraceDurationMean(), 0.05);
        Assert.assertEquals(3360000, logSummary.getTraceDurationMedian(), 0.05);
        
        // AttributeTrace
        Assert.assertEquals("Case1", attTrace0.getTraceId());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T21:59:19.308+10:00").getMillis(), attTrace0.getStartTime());
        Assert.assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        Assert.assertEquals(3360000,attTrace0.getDuration());
        Assert.assertEquals(13, attTrace0.getOriginalEventStatus().cardinality());
        Assert.assertEquals(0,attTrace0.getVariantIndex());
        Assert.assertEquals(0,attTrace0.getVariantRank());

        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(IntArrayList.newListWith(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), attTrace0.getOriginalValueTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T21:59:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T21:59:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:02:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:05:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:09:19.308+10:00").getMillis(), 
                dateFormatter.parseDateTime("2010-10-27T22:14:19.308+10:00").getMillis(),  
                dateFormatter.parseDateTime("2010-10-27T22:20:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:27:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:35:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:44:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:54:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getStartTimeTrace());
        
        Assert.assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T21:59:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:01:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:03:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:06:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:10:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
                attTrace0.getEndTimeTrace());   
        
        Assert.assertEquals(LongArrayList.newListWith(0,60000,60000,60000,60000,60000,60000,60000,60000,60000,60000,60000,0), attTrace0.getDurationTrace());         
        
        Assert.assertEquals(IntSets.mutable.of(0,1,2,8,12,15,17,20,24), attTrace0.getActiveArcs());
        Assert.assertEquals(IntSets.mutable.of(0,1,2,3,4,5), attTrace0.getActiveNodes());
        
        Assert.assertEquals(4, attTrace0.getNodeTotalCount(0));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(1));
        Assert.assertEquals(4, attTrace0.getNodeTotalCount(2));
        Assert.assertEquals(2, attTrace0.getNodeTotalCount(3));
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(4)); //-1
        Assert.assertEquals(1, attTrace0.getNodeTotalCount(5)); //-2
        
        Assert.assertEquals(240000, attTrace0.getNodeTotalDuration(0));
        Assert.assertEquals(60000, attTrace0.getNodeMinDuration(0));
        Assert.assertEquals(60000, attTrace0.getNodeMaxDuration(0));
        
        Assert.assertEquals(60000, attTrace0.getNodeTotalDuration(1));
        Assert.assertEquals(60000, attTrace0.getNodeMinDuration(1));
        Assert.assertEquals(60000, attTrace0.getNodeMaxDuration(1));        
        
        Assert.assertEquals(240000, attTrace0.getNodeTotalDuration(2));
        Assert.assertEquals(60000, attTrace0.getNodeMinDuration(2));
        Assert.assertEquals(60000, attTrace0.getNodeMaxDuration(2));   

        Assert.assertEquals(120000, attTrace0.getNodeTotalDuration(3));
        Assert.assertEquals(60000, attTrace0.getNodeMinDuration(3));
        Assert.assertEquals(60000, attTrace0.getNodeMaxDuration(3));
        
        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(4));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(4));   

        Assert.assertEquals(0, attTrace0.getNodeTotalDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMinDuration(5));
        Assert.assertEquals(0, attTrace0.getNodeMaxDuration(5));   
        
        Assert.assertEquals(2, attTrace0.getArcTotalCount(0)); //0,1,2,8,12,15,17,20,24
        Assert.assertEquals(1, attTrace0.getArcTotalCount(1));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(2));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(8));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(12));
        Assert.assertEquals(2, attTrace0.getArcTotalCount(15));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(17));
        Assert.assertEquals(2, attTrace0.getArcTotalCount(20));
        Assert.assertEquals(1, attTrace0.getArcTotalCount(24));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(0));
        Assert.assertEquals(60000, attTrace0.getArcMaxDuration(0));
        
        Assert.assertEquals(120000, attTrace0.getArcMinDuration(1));
        Assert.assertEquals(120000, attTrace0.getArcMaxDuration(1));
        
        Assert.assertEquals(540000, attTrace0.getArcMinDuration(2));
        Assert.assertEquals(540000, attTrace0.getArcMaxDuration(2));
        
        Assert.assertEquals(180000, attTrace0.getArcMinDuration(8));
        Assert.assertEquals(180000, attTrace0.getArcMaxDuration(8));
        
        Assert.assertEquals(480000, attTrace0.getArcMinDuration(12));
        Assert.assertEquals(480000, attTrace0.getArcMaxDuration(12));
        
        Assert.assertEquals(240000, attTrace0.getArcMinDuration(15));
        Assert.assertEquals(360000, attTrace0.getArcMaxDuration(15));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(17));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(17));
        
        Assert.assertEquals(300000, attTrace0.getArcMinDuration(20));
        Assert.assertEquals(420000, attTrace0.getArcMaxDuration(20));
        
        Assert.assertEquals(0, attTrace0.getArcMinDuration(24));
        Assert.assertEquals(0, attTrace0.getArcMaxDuration(24));
        
        
        // Variants
        Assert.assertEquals(1, attLog.getVariantView().getActiveVariants().size());
        IntList variant0 = variants.getVariantAtIndex(0);
        
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,0,0,1,2,3,2,3,2,0,2,attLog.getEndEvent()), variant0);
        
        Assert.assertEquals(0, variants.getIndexOf(variant0));
        Assert.assertEquals(1, variants.getFrequency(variant0));
        Assert.assertEquals(0, variants.getRankOf(variant0));
        
        Assert.assertEquals(1.0, variants.getVariantRelativeFrequency(variant0), 0.01);
        
        Assert.assertEquals(Lists.mutable.of(attTrace0), variants.getTraces(variant0));
        
    }
    

    
    @Test
    public void test_AttributesOf_Alog_AttributeLog_And_AttributeTraceVariants() {
        ALog log = new ALog(readLogWithCompleteEventsOnly());
        
        ATrace trace0 = log.getTraces().get(0);
        ATrace trace1 = log.getTraces().get(1);
        ATrace trace2 = log.getTraces().get(2);
        ATrace trace3 = log.getTraces().get(3);
        ATrace trace4 = log.getTraces().get(4);
        ATrace trace5 = log.getTraces().get(5);
        
        AttributeLog attLog = new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);
        AttributeTrace attTrace1 = attLog.getOriginalTraceFromIndex(1);
        AttributeTrace attTrace2 = attLog.getOriginalTraceFromIndex(2);
        AttributeTrace attTrace3 = attLog.getOriginalTraceFromIndex(3);
        AttributeTrace attTrace4 = attLog.getOriginalTraceFromIndex(4);
        AttributeTrace attTrace5 = attLog.getOriginalTraceFromIndex(5);
        
        // ALog
        Assert.assertEquals(6, log.getOriginalTraces().size());
        Assert.assertEquals(6, log.getTraces().size());        
        Assert.assertEquals(6, log.getOriginalTraceStatus().cardinality());
        Assert.assertEquals("L1_complete_events_only_with_resources", log.getAttributes().get("concept:name").toString());
        Assert.assertEquals(23,  log.getOriginalNumberOfEvents());
        Assert.assertEquals(23,  log.getNumberOfEvents());          
        
        // ATrace
        
        // AttributeLog
        Assert.assertEquals(23, attLog.getOriginalNumberOfEvents());
        Assert.assertEquals(23, attLog.getNumberOfEvents());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3,4), attLog.getOriginalAttributeValues());
        Assert.assertEquals(IntSets.mutable.of(attLog.getEndEvent(),attLog.getStartEvent(),0,1,2,3,4), attLog.getAttributeValues());
        Assert.assertEquals("a", attLog.getStringFromValue(0));
        Assert.assertEquals("e", attLog.getStringFromValue(1));
        Assert.assertEquals("d", attLog.getStringFromValue(2));
        Assert.assertEquals("c", attLog.getStringFromValue(3));
        Assert.assertEquals("b", attLog.getStringFromValue(4));
        Assert.assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        Assert.assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        Assert.assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        Assert.assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());     
        
        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        Assert.assertEquals(6, oriLogSummary.getCaseCount());
        Assert.assertEquals(5, oriLogSummary.getActivityCount());
        Assert.assertEquals(23, oriLogSummary.getEventCount());
        Assert.assertEquals(3, oriLogSummary.getVariantCount());
        Assert.assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        Assert.assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
        
        AttributeLogSummary logSummary = attLog.getLogSummary();
        Assert.assertEquals(6, logSummary.getCaseCount());
        Assert.assertEquals(5, logSummary.getActivityCount());
        Assert.assertEquals(23, logSummary.getEventCount());
        Assert.assertEquals(3, logSummary.getVariantCount());
        Assert.assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        Assert.assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        Assert.assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
        
        // AttributeTrace
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,1,2,attLog.getEndEvent()), attTrace0.getValueTrace());
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,3,4,2,attLog.getEndEvent()), attTrace1.getValueTrace());
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,4,3,2,attLog.getEndEvent()), attTrace2.getValueTrace());
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,4,3,2,attLog.getEndEvent()), attTrace3.getValueTrace());
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,4,3,2,attLog.getEndEvent()), attTrace4.getValueTrace());
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,3,4,2,attLog.getEndEvent()), attTrace5.getValueTrace());
        
        // Variants
        Assert.assertEquals(3, attLog.getVariantView().getActiveVariants().size());
        
        IntList variant0 = variants.getVariantAtIndex(0);
        IntList variant1 = variants.getVariantAtIndex(1);
        IntList variant2 = variants.getVariantAtIndex(2);
        
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,1,2,attLog.getEndEvent()), variant0);
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,3,4,2,attLog.getEndEvent()), variant1);
        Assert.assertEquals(IntLists.mutable.of(attLog.getStartEvent(),0,4,3,2,attLog.getEndEvent()), variant2);
        
        Assert.assertEquals(0, variants.getIndexOf(variant0));
        Assert.assertEquals(1, variants.getIndexOf(variant1));
        Assert.assertEquals(2, variants.getIndexOf(variant2));
        
        Assert.assertEquals(1, variants.getFrequency(variant0));
        Assert.assertEquals(2, variants.getFrequency(variant1));
        Assert.assertEquals(3, variants.getFrequency(variant2));
        
        Assert.assertEquals(2, variants.getRankOf(variant0));
        Assert.assertEquals(1, variants.getRankOf(variant1));
        Assert.assertEquals(0, variants.getRankOf(variant2));
        
        Assert.assertEquals(0.166, variants.getVariantRelativeFrequency(variant0), 0.01);
        Assert.assertEquals(0.333, variants.getVariantRelativeFrequency(variant1), 0.01);
        Assert.assertEquals(0.5, variants.getVariantRelativeFrequency(variant2), 0.01);
        
        Assert.assertEquals(Lists.mutable.of(attTrace0), variants.getTraces(variant0));
        Assert.assertEquals(Lists.mutable.of(attTrace1, attTrace5), variants.getTraces(variant1));
        Assert.assertEquals(Lists.mutable.of(attTrace2, attTrace3, attTrace4), variants.getTraces(variant2));
        

    }

}
