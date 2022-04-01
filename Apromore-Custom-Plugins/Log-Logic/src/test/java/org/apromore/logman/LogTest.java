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

package org.apromore.logman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.AttributeTraceGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.logman.attribute.log.variants.AttributeTraceVariants;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LogTest extends DataSetup {
    private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Test
    void test_SpecialEmptyLog() {
        ALog log = new ALog(readEmptyLog());
        assertEquals(0, log.getOriginalTraces().size());
        assertEquals(0, log.getAttributes().size());
        assertEquals(0, log.getOriginalTraces().size());
        assertEquals(0, log.getTraces().size());
        assertEquals(0, log.getAttributeStore().getAttributeIndexes().length);
        assertEquals(0, log.getOriginalTraceStatus().cardinality());
        assertEquals(0, log.getOriginalNumberOfEvents());
        assertEquals(0, log.getNumberOfEvents());

        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        assertEquals(0, attLog.getTraces().size());

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(0, oriLogSummary.getCaseCount());
        assertEquals(0, oriLogSummary.getActivityCount());
        assertEquals(0, oriLogSummary.getEventCount());
        assertEquals(0, oriLogSummary.getVariantCount());
        assertEquals(0, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(0, logSummary.getCaseCount());
        assertEquals(0, logSummary.getActivityCount());
        assertEquals(0, logSummary.getEventCount());
        assertEquals(0, logSummary.getVariantCount());
        assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);
    }

    void test_SpecialLogWithOneEmptyTrace() {
        ALog log = new ALog(readLogWithEmptyTrace());
        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getTraces().size());
        assertEquals(1, log.getOriginalTraceStatus().cardinality());

        assertEquals(null, log.getTraces().get(0).getTraceId());
        assertEquals(0, log.getTraces().get(0).getOriginalActivities().size());
        assertEquals(0, log.getTraces().get(0).getActivities().size());
        assertEquals(0, log.getTraces().get(0).getEvents().size());
        assertEquals(0, log.getTraces().get(0).getOriginalEvents().size());
        assertEquals(0, log.getTraces().get(0).getAttributes().size());
        assertEquals(0, log.getTraces().get(0).getOriginalEventStatus().cardinality());
        assertEquals(0, log.getTraces().get(0).getOriginalActivityStatus().cardinality());
        assertEquals(0, log.getOriginalNumberOfEvents());
        assertEquals(0, log.getNumberOfEvents());

        // AttributeLog
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        assertEquals(0, attLog.getOriginalNumberOfEvents());
        assertEquals(0, attLog.getNumberOfEvents());
        assertEquals(IntLists.mutable.empty(), attLog.getOriginalAttributeValues());
        assertEquals(IntLists.mutable.empty(), attLog.getAttributeValues());
        assertEquals(1, attLog.getTraces().size());
        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(1, attLog.getNumberOfEvents());

        // Variants
        assertEquals(0, attLog.getVariantView().getActiveVariants().size());

        // AttributeTrace
        AttributeTrace attTrace = attLog.getTraces().get(0);
        assertEquals(true, attTrace.isEmpty());
        assertEquals(true, attTrace.getValueTrace().isEmpty());
        assertEquals(true, attTrace.getStartTimeTrace().isEmpty());
        assertEquals(true, attTrace.getEndTimeTrace().isEmpty());
        assertEquals(0, attTrace.getDuration());
        assertEquals(0, attTrace.getStartTime());
        assertEquals(0, attTrace.getEndTime());
        assertEquals(0, attTrace.getActiveGraph().getArcs().size());
        assertEquals(0, attTrace.getActiveGraph().getNodes().size());

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(0, oriLogSummary.getCaseCount());
        assertEquals(0, oriLogSummary.getActivityCount());
        assertEquals(0, oriLogSummary.getEventCount());
        assertEquals(0, oriLogSummary.getVariantCount());
        assertEquals(0, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(0, logSummary.getCaseCount());
        assertEquals(0, logSummary.getActivityCount());
        assertEquals(0, logSummary.getEventCount());
        assertEquals(0, logSummary.getVariantCount());
        assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);

    }

    @Test
    void test_SpecialLogWithOneTrace_OneCompleteEventOnly() {
        ALog log = new ALog(readLogWithOneTraceOneEvent());
        ATrace trace0 = log.getTraces().get(0);
        AActivity activity0 = trace0.getActivityFromIndex(0);
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);

        // ALog
        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getAttributes().size());
        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getTraces().size());
        assertEquals(5, log.getAttributeStore().getAttributeIndexes().length);
        assertEquals(1, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1", log.getAttributes().get("concept:name").toString());
        assertEquals(1, log.getOriginalNumberOfEvents());
        assertEquals(1, log.getNumberOfEvents());

        // ATrace
        assertEquals("Case1", log.getTraces().get(0).getTraceId());
        assertEquals(1, trace0.getOriginalActivities().size());
        assertEquals(1, trace0.getActivities().size());
        assertEquals(1, trace0.getEvents().size());
        assertEquals(1, trace0.getOriginalEvents().size());
        assertEquals(1, trace0.getAttributes().size());
        assertEquals(1, trace0.getOriginalEventStatus().cardinality());
        assertEquals(1, trace0.getOriginalActivityStatus().cardinality());

        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), trace0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), trace0.getEndTime());
        assertEquals(LongArrayList.newListWith(log.getTraces().get(0).getStartTime()), trace0.getStartTimeTrace());
        assertEquals(LongArrayList.newListWith(log.getTraces().get(0).getStartTime()), trace0.getEndTimeTrace());

        assertEquals("a", trace0.getEventFromIndex(0).getAttributes().get("concept:name").toString());
        assertEquals("complete", trace0.getEventFromIndex(0).getAttributes().get("lifecycle:transition").toString());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00"),
            dateFormatter.parseDateTime(trace0.getEventFromIndex(0).getAttributes().get("time:timestamp").toString()));

        // Activity
        assertEquals(0, activity0.getDuration());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            activity0.getStartTimestamp());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            activity0.getEndTimestamp());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00"), activity0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00"), activity0.getEndTime());
        assertEquals(true, activity0.isActive());
        assertEquals(true, activity0.isInstant());

        // AttributeLog
        assertEquals(1, attLog.getOriginalNumberOfEvents());
        assertEquals(1, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0), attLog.getAttributeValues());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals(0, attLog.getValueFromString("a"));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(1, attLog.getTraces().size());
        assertEquals(1, attLog.getNumberOfEvents());

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(1, oriLogSummary.getActivityCount());
        assertEquals(1, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(0, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(1, logSummary.getActivityCount());
        assertEquals(1, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);

        // Variants
        assertEquals(1, attLog.getVariantView().getActiveVariants().size());
        IntList variant0 = attLog.getVariantView().getActiveVariants().getVariantAtIndex(0);
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, attLog.getEndEvent()), variant0);
        assertEquals(1, attLog.getVariantView().getActiveVariants().getFrequency(variant0));
        assertEquals(Lists.mutable.of(attTrace0), attLog.getVariantView().getActiveVariants().getTraces(variant0));

        // AttributeTrace
        assertEquals("Case1", attTrace0.getTraceId());
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, attLog.getEndEvent()), attTrace0.getValueTrace());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            attTrace0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(), attTrace0.getEndTime());
        assertEquals(0, attTrace0.getDuration());
        assertEquals(3, attTrace0.getOriginalEventStatus().cardinality());
        assertEquals(0, attTrace0.getVariantIndex());
        assertEquals(0, attTrace0.getVariantRank());
        assertEquals(IntArrayList.newListWith(attLog.getStartEvent(), 0, attLog.getEndEvent()),
            attTrace0.getOriginalValueTrace());
        assertEquals(IntArrayList.newListWith(attLog.getStartEvent(), 0, attLog.getEndEvent()),
            attTrace0.getValueTrace());
        assertEquals(LongArrayList.newListWith(
            dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis()), attTrace0.getStartTimeTrace());
        assertEquals(LongArrayList.newListWith(
            dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis(),
            dateFormatter.parseDateTime("2010-10-27T22:31:19.495+10:00").getMillis()), attTrace0.getEndTimeTrace());
        assertEquals(false, attTrace0.isEmpty());

        assertEquals(IntSets.mutable.of(3, 2), attTrace0.getActiveGraph().getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2), attTrace0.getActiveGraph().getNodes());

        assertEquals(1, attTrace0.getActiveGraph().getNodeTotalFrequency(0));
        assertEquals(1, attTrace0.getActiveGraph().getNodeTotalFrequency(1));
        assertEquals(1, attTrace0.getActiveGraph().getNodeTotalFrequency(2));
        assertEquals(0, attTrace0.getActiveGraph().getNodeTotalDuration(0));
        assertEquals(0, attTrace0.getActiveGraph().getNodeMinDuration(0));
        assertEquals(0, attTrace0.getActiveGraph().getNodeMaxDuration(0));
        assertEquals(0, attTrace0.getActiveGraph().getNodeMinDuration(1));
        assertEquals(0, attTrace0.getActiveGraph().getNodeMaxDuration(1));
        assertEquals(0, attTrace0.getActiveGraph().getNodeMinDuration(2));
        assertEquals(0, attTrace0.getActiveGraph().getNodeMaxDuration(2));

        assertEquals(1, attTrace0.getActiveGraph().getArcTotalFrequency(2));
        assertEquals(1, attTrace0.getActiveGraph().getArcTotalFrequency(3));
        assertEquals(0, attTrace0.getActiveGraph().getArcMinDuration(2));
        assertEquals(0, attTrace0.getActiveGraph().getArcMaxDuration(2));
        assertEquals(0, attTrace0.getActiveGraph().getArcMinDuration(3));
        assertEquals(0, attTrace0.getActiveGraph().getArcMaxDuration(3));

    }

    @Test
    void test1_AttributesOf_ATrace_AActivity_AttributeTrace_And_Changing_Attribute() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);

        // ALog
        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getTraces().size());
        assertEquals(1, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1", log.getAttributes().get("concept:name").toString());
        assertEquals(11, log.getOriginalNumberOfEvents());
        assertEquals(11, log.getNumberOfEvents());
        assertEquals(Lists.mutable.of(trace0), log.getTraces());
        assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());

        // ATrace
        assertEquals("Case1", trace0.getTraceId());
        assertEquals(11, trace0.getOriginalActivities().size());
        assertEquals(11, trace0.getActivities().size());
        assertEquals(11, trace0.getEvents().size());
        assertEquals(11, trace0.getOriginalEvents().size());
        assertEquals(1, trace0.getAttributes().size());
        assertEquals(11, trace0.getOriginalEventStatus().cardinality());
        assertEquals(11, trace0.getOriginalActivityStatus().cardinality());


        // AttributeLog
        assertEquals(11, attLog.getOriginalNumberOfEvents());
        assertEquals(11, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getAttributeValues());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals("b", attLog.getStringFromValue(1));
        assertEquals("c", attLog.getStringFromValue(2));
        assertEquals("d", attLog.getStringFromValue(3));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());

        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(1, attLog.getTraces().size());
        assertEquals(11, attLog.getNumberOfEvents());

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(4, oriLogSummary.getActivityCount());
        assertEquals(11, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(4, logSummary.getActivityCount());
        assertEquals(11, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);

        // AttributeTrace
        assertEquals("Case1", attTrace0.getTraceId());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
            attTrace0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        assertEquals(3300000, attTrace0.getDuration());
        assertEquals(13, attTrace0.getOriginalEventStatus().cardinality());
        assertEquals(0, attTrace0.getVariantIndex());
        assertEquals(0, attTrace0.getVariantRank());

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            attTrace0.getValueTrace());
        assertEquals(
            IntArrayList.newListWith(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            attTrace0.getOriginalValueTrace());

        assertEquals(LongArrayList.newListWith(
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

        assertEquals(LongArrayList.newListWith(
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

        assertEquals(LongArrayList.newListWith(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), attTrace0.getDurationTrace());

        assertEquals(false, attTrace0.isEmpty());

        // Test Activity Attribute Map
        assertEquals(true, attTrace0.getAttributeMapAtIndex(0).isEmpty()); // start event

        Map<String, String> expectedAttMap1 = Stream.of(new Object[][] { // first activity
            {"org:resource", "R1"},
            {"concept:name", "a"},
            {"time:timestamp", "2010-10-27T22:00:19.308+10:00"},
            {"lifecycle:transition", "complete"}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
        Map<String, String> resultAttMap1 = attTrace0.getAttributeMapAtIndex(1);

        assertEquals(expectedAttMap1.get("org:resource"), resultAttMap1.get("org:resource"));
        assertEquals(expectedAttMap1.get("concept:name"), resultAttMap1.get("concept:name"));
        assertEquals(expectedAttMap1.get("lifecycle:transition"), resultAttMap1.get("lifecycle:transition"));
        assertEquals(ZonedDateTime.parse(expectedAttMap1.get("time:timestamp")).toInstant().toEpochMilli(),
            ZonedDateTime.parse(resultAttMap1.get("time:timestamp")).toInstant().toEpochMilli());


        Map<String, String> expectedAttMap2 = Stream.of(new Object[][] { // second activity
            {"org:resource", "R2"},
            {"concept:name", "a"},
            {"time:timestamp", "2010-10-27T22:01:19.308+10:00"},
            {"lifecycle:transition", "complete"}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
        Map<String, String> resultAttMap2 = attTrace0.getAttributeMapAtIndex(2);

        assertEquals(expectedAttMap2.get("org:resource"), resultAttMap2.get("org:resource"));
        assertEquals(expectedAttMap2.get("concept:name"), resultAttMap2.get("concept:name"));
        assertEquals(expectedAttMap2.get("lifecycle:transition"), resultAttMap2.get("lifecycle:transition"));
        assertEquals(ZonedDateTime.parse(expectedAttMap2.get("time:timestamp")).toInstant().toEpochMilli(),
            ZonedDateTime.parse(resultAttMap2.get("time:timestamp")).toInstant().toEpochMilli());

        AttributeTraceGraph traceGraph0 = attTrace0.getActiveGraph();
        assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), traceGraph0.getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), traceGraph0.getNodes());

        assertEquals(4, traceGraph0.getNodeTotalFrequency(0));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(1));
        assertEquals(4, traceGraph0.getNodeTotalFrequency(2));
        assertEquals(2, traceGraph0.getNodeTotalFrequency(3));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(4)); //-1
        assertEquals(1, traceGraph0.getNodeTotalFrequency(5)); //-2

        assertEquals(0, traceGraph0.getNodeTotalDuration(0));
        assertEquals(0, traceGraph0.getNodeMinDuration(0));
        assertEquals(0, traceGraph0.getNodeMaxDuration(0));

        assertEquals(0, traceGraph0.getNodeTotalDuration(1));
        assertEquals(0, traceGraph0.getNodeMinDuration(1));
        assertEquals(0, traceGraph0.getNodeMaxDuration(1));

        assertEquals(0, traceGraph0.getNodeTotalDuration(2));
        assertEquals(0, traceGraph0.getNodeMinDuration(2));
        assertEquals(0, traceGraph0.getNodeMaxDuration(2));

        assertEquals(0, traceGraph0.getNodeTotalDuration(3));
        assertEquals(0, traceGraph0.getNodeMinDuration(3));
        assertEquals(0, traceGraph0.getNodeMaxDuration(3));

        assertEquals(0, traceGraph0.getNodeTotalDuration(4));
        assertEquals(0, traceGraph0.getNodeMinDuration(4));
        assertEquals(0, traceGraph0.getNodeMaxDuration(4));

        assertEquals(0, traceGraph0.getNodeTotalDuration(5));
        assertEquals(0, traceGraph0.getNodeMinDuration(5));
        assertEquals(0, traceGraph0.getNodeMaxDuration(5));

        assertEquals(2, traceGraph0.getArcTotalFrequency(0)); //0,1,2,8,12,15,17,20,24
        assertEquals(1, traceGraph0.getArcTotalFrequency(1));
        assertEquals(1, traceGraph0.getArcTotalFrequency(2));
        assertEquals(1, traceGraph0.getArcTotalFrequency(8));
        assertEquals(1, traceGraph0.getArcTotalFrequency(12));
        assertEquals(2, traceGraph0.getArcTotalFrequency(15));
        assertEquals(1, traceGraph0.getArcTotalFrequency(17));
        assertEquals(2, traceGraph0.getArcTotalFrequency(20));
        assertEquals(1, traceGraph0.getArcTotalFrequency(24));

        assertEquals(60000, traceGraph0.getArcMinDuration(0));
        assertEquals(120000, traceGraph0.getArcMaxDuration(0));

        assertEquals(180000, traceGraph0.getArcMinDuration(1));
        assertEquals(180000, traceGraph0.getArcMaxDuration(1));

        assertEquals(600000, traceGraph0.getArcMinDuration(2));
        assertEquals(600000, traceGraph0.getArcMaxDuration(2));

        assertEquals(240000, traceGraph0.getArcMinDuration(8));
        assertEquals(240000, traceGraph0.getArcMaxDuration(8));

        assertEquals(540000, traceGraph0.getArcMinDuration(12));
        assertEquals(540000, traceGraph0.getArcMaxDuration(12));

        assertEquals(300000, traceGraph0.getArcMinDuration(15));
        assertEquals(420000, traceGraph0.getArcMaxDuration(15));

        assertEquals(0, traceGraph0.getArcMinDuration(17));
        assertEquals(0, traceGraph0.getArcMaxDuration(17));

        assertEquals(360000, traceGraph0.getArcMinDuration(20));
        assertEquals(480000, traceGraph0.getArcMaxDuration(20));

        assertEquals(0, traceGraph0.getArcMinDuration(24));
        assertEquals(0, traceGraph0.getArcMaxDuration(24));


        // Variants
        assertEquals(1, attLog.getVariantView().getActiveVariants().size());
        IntList variant0 = variants.getVariantAtIndex(0);

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            variant0);

        assertEquals(0, variants.getIndexOf(variant0));
        assertEquals(1, variants.getFrequency(variant0));
        assertEquals(0, variants.getRankOf(variant0));

        assertEquals(1.0, variants.getVariantRelativeFrequency(variant0), 0.01);

        assertEquals(Lists.mutable.of(attTrace0), variants.getTraces(variant0));
    }


    @Test
    void test_Changing_Attribute() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeTrace attTrace0 = attLog.getTraces().get(0);

        IndexableAttribute resAtt = log.getAttributeStore().getStandardEventResource();
        attLog.setAttribute(resAtt);

        // AttributeLog
        assertEquals(11, attLog.getOriginalNumberOfEvents());
        assertEquals(11, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3, 4),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3, 4),
            attLog.getAttributeValues());
        assertEquals("R1", attLog.getStringFromValue(0));
        assertEquals("R2", attLog.getStringFromValue(1));
        assertEquals("R3", attLog.getStringFromValue(2));
        assertEquals("R4", attLog.getStringFromValue(3));
        assertEquals("R5", attLog.getStringFromValue(4));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());
        assertEquals(attTrace0, attLog.getTraceFromTraceId("Case1"));

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(5, oriLogSummary.getActivityCount());
        assertEquals(11, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(5, logSummary.getActivityCount());
        assertEquals(11, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);

        // AttributeTrace
        assertEquals("Case1", attTrace0.getTraceId());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:00:19.308+10:00").getMillis(),
            attTrace0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        assertEquals(3300000, attTrace0.getDuration());
        assertEquals(13, attTrace0.getOriginalEventStatus().cardinality());
        assertEquals(0, attTrace0.getVariantIndex());
        assertEquals(0, attTrace0.getVariantRank());

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 1, 1, 1, 2, 3, 3, 2, 0, 1, 4, attLog.getEndEvent()),
            attTrace0.getValueTrace());
        assertEquals(
            IntArrayList.newListWith(attLog.getStartEvent(), 0, 1, 1, 1, 2, 3, 3, 2, 0, 1, 4, attLog.getEndEvent()),
            attTrace0.getOriginalValueTrace());

        assertEquals(LongArrayList.newListWith(
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

        assertEquals(LongArrayList.newListWith(
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

        assertEquals(LongArrayList.newListWith(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), attTrace0.getDurationTrace());

        assertEquals(false, attTrace0.isEmpty());

        AttributeTraceGraph traceGraph0 = attTrace0.getActiveGraph();
        assertEquals(IntSets.mutable.of(1, 8, 9, 11, 14, 17, 23, 24, 34, 35), traceGraph0.getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5, 6), traceGraph0.getNodes());

        assertEquals(2, traceGraph0.getNodeTotalFrequency(0));
        assertEquals(4, traceGraph0.getNodeTotalFrequency(1));
        assertEquals(2, traceGraph0.getNodeTotalFrequency(2));
        assertEquals(2, traceGraph0.getNodeTotalFrequency(3));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(4));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(5));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(6));

        assertEquals(0, traceGraph0.getNodeTotalDuration(0));
        assertEquals(0, traceGraph0.getNodeMinDuration(0));
        assertEquals(0, traceGraph0.getNodeMaxDuration(0));

        assertEquals(0, traceGraph0.getNodeTotalDuration(1));
        assertEquals(0, traceGraph0.getNodeMinDuration(1));
        assertEquals(0, traceGraph0.getNodeMaxDuration(1));

        assertEquals(0, traceGraph0.getNodeTotalDuration(2));
        assertEquals(0, traceGraph0.getNodeMinDuration(2));
        assertEquals(0, traceGraph0.getNodeMaxDuration(2));

        assertEquals(0, traceGraph0.getNodeTotalDuration(3));
        assertEquals(0, traceGraph0.getNodeMinDuration(3));
        assertEquals(0, traceGraph0.getNodeMaxDuration(3));

        assertEquals(0, traceGraph0.getNodeTotalDuration(4));
        assertEquals(0, traceGraph0.getNodeMinDuration(4));
        assertEquals(0, traceGraph0.getNodeMaxDuration(4));

        assertEquals(0, traceGraph0.getNodeTotalDuration(5));
        assertEquals(0, traceGraph0.getNodeMinDuration(5));
        assertEquals(0, traceGraph0.getNodeMaxDuration(5));

        assertEquals(0, traceGraph0.getNodeTotalDuration(6));
        assertEquals(0, traceGraph0.getNodeMinDuration(6));
        assertEquals(0, traceGraph0.getNodeMaxDuration(6));

        assertEquals(2, traceGraph0.getArcTotalFrequency(1)); //1,8,9,11,14,17,23,24,34,35
        assertEquals(2, traceGraph0.getArcTotalFrequency(8));
        assertEquals(1, traceGraph0.getArcTotalFrequency(9));
        assertEquals(1, traceGraph0.getArcTotalFrequency(11));
        assertEquals(1, traceGraph0.getArcTotalFrequency(14));
        assertEquals(1, traceGraph0.getArcTotalFrequency(17));
        assertEquals(1, traceGraph0.getArcTotalFrequency(23));
        assertEquals(1, traceGraph0.getArcTotalFrequency(24));
        assertEquals(1, traceGraph0.getArcTotalFrequency(34));
        assertEquals(1, traceGraph0.getArcTotalFrequency(35));

        assertEquals(60000, traceGraph0.getArcMinDuration(1));
        assertEquals(540000, traceGraph0.getArcMaxDuration(1));

        assertEquals(120000, traceGraph0.getArcMinDuration(8));
        assertEquals(180000, traceGraph0.getArcMaxDuration(8));

        assertEquals(240000, traceGraph0.getArcMinDuration(9));
        assertEquals(240000, traceGraph0.getArcMaxDuration(9));

        assertEquals(600000, traceGraph0.getArcMinDuration(11));
        assertEquals(600000, traceGraph0.getArcMaxDuration(11));

        assertEquals(480000, traceGraph0.getArcMinDuration(14));
        assertEquals(480000, traceGraph0.getArcMaxDuration(14));

        assertEquals(300000, traceGraph0.getArcMinDuration(17));
        assertEquals(300000, traceGraph0.getArcMaxDuration(17));

        assertEquals(420000, traceGraph0.getArcMinDuration(23));
        assertEquals(420000, traceGraph0.getArcMaxDuration(23));

        assertEquals(360000, traceGraph0.getArcMinDuration(24));
        assertEquals(360000, traceGraph0.getArcMaxDuration(24));

        assertEquals(0, traceGraph0.getArcMinDuration(34));
        assertEquals(0, traceGraph0.getArcMaxDuration(34));

        assertEquals(0, traceGraph0.getArcMinDuration(35));
        assertEquals(0, traceGraph0.getArcMaxDuration(35));
    }

    @Test
    void test_Resource_Attribute_EventMerging_Exclude_Resource() {
        ALog log = new ALog(readLogWithOneTrace_StartCompleteEvents_EventMerging_Exclude_R1());

        // Activity perspective
        IndexableAttribute actAtt = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, actAtt, getAllDayAllTimeCalendar());

        assertEquals(3, actAtt.getValueIndexes().length); // a, b, c
        assertEquals(5, attLog.getOriginalAttributeValues().size()); // a, b, c, start, end
        assertEquals(5, attLog.getAttributeValues().size()); // a, b, c, start, end

        // Change the perspective attribute to resource
        IndexableAttribute resAtt = log.getAttributeStore().getStandardEventResource();
        attLog.setAttribute(resAtt);

        assertEquals(4, resAtt.getValueSize()); // R1, R2, R3, R4
        assertEquals(5, attLog.getOriginalAttributeValues()
            .size()); //R2, R3, R4, start, end - R1 was excluded because of event merging
        assertEquals(5, attLog.getAttributeValues().size()); // R2, R3, R4, start, end
    }


    @Test
    void test_LogSummary_AfterChanging_Attribute() {
        ALog log = new ALog(readLogWithCompleteEventsOnly());
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());

        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(6, oriLogSummary.getCaseCount());
        assertEquals(5, oriLogSummary.getActivityCount());
        assertEquals(23, oriLogSummary.getEventCount());
        assertEquals(3, oriLogSummary.getVariantCount());
        assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(6, logSummary.getCaseCount());
        assertEquals(5, logSummary.getActivityCount());
        assertEquals(23, logSummary.getEventCount());
        assertEquals(3, logSummary.getVariantCount());
        assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);

        // Change perspective attribute
        IndexableAttribute resAtt = log.getAttributeStore().getStandardEventResource();
        attLog.setAttribute(resAtt);

        oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(6, oriLogSummary.getCaseCount());
        assertEquals(5, oriLogSummary.getActivityCount());
        assertEquals(23, oriLogSummary.getEventCount());
        assertEquals(5, oriLogSummary.getVariantCount());
        assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);

        logSummary = attLog.getLogSummary();
        assertEquals(6, logSummary.getCaseCount());
        assertEquals(5, logSummary.getActivityCount());
        assertEquals(23, logSummary.getEventCount());
        assertEquals(5, logSummary.getVariantCount());
        assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);
    }

    @Test
    void test_Trace_Filtering() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());

        // AttributeLogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(4, oriLogSummary.getActivityCount());
        assertEquals(11, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(4, logSummary.getActivityCount());
        assertEquals(11, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);

        // Filter traces of ALog
        LogBitMap logBitMap1 = null;
        try {
            logBitMap1 = new LogBitMap(log.getOriginalTraces().size());
            logBitMap1.setTraceBitSet(new BitSet(1), 1); // remove trace
            logBitMap1.addEventBitSet(LogBitMap.newBitSet(11), 11); // keep all events
            log.updateLogStatus(logBitMap1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(0, log.getTraces().size());
        assertEquals(0, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1", log.getAttributes().get("concept:name").toString());
        assertEquals(11, log.getOriginalNumberOfEvents());
        assertEquals(0, log.getNumberOfEvents());
        assertEquals(Lists.mutable.empty(), log.getTraces());
        assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());

        // Filter traces of AttributeLog
        try {
            attLog.refresh();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(11, attLog.getOriginalNumberOfEvents());
        assertEquals(0, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.empty(), attLog.getAttributeValues());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals("b", attLog.getStringFromValue(1));
        assertEquals("c", attLog.getStringFromValue(2));
        assertEquals("d", attLog.getStringFromValue(3));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());

        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(0, attLog.getTraces().size());
        assertEquals(0, attLog.getNumberOfEvents());

        // LogSummary
        oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(4, oriLogSummary.getActivityCount());
        assertEquals(11, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);

        logSummary = attLog.getLogSummary();
        assertEquals(0, logSummary.getCaseCount());
        assertEquals(0, logSummary.getActivityCount());
        assertEquals(0, logSummary.getEventCount());
        assertEquals(0, logSummary.getVariantCount());
        assertEquals(0, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(0, logSummary.getTraceDurationMedian(), 0.05);


        // Restore
        try {
            logBitMap1.clear();
            logBitMap1.setTraceBitSet(LogBitMap.newBitSet(1), 1); // re-add trace
            logBitMap1.addEventBitSet(LogBitMap.newBitSet(11), 11); // keep all events
            log.updateLogStatus(logBitMap1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // ALog
        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getTraces().size());
        assertEquals(1, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1", log.getAttributes().get("concept:name").toString());
        assertEquals(11, log.getOriginalNumberOfEvents());
        assertEquals(11, log.getNumberOfEvents());
        assertEquals(Lists.mutable.of(trace0), log.getTraces());
        assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());

        // Refresh AttributeLog
        try {
            attLog.refresh();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        assertEquals(11, attLog.getOriginalNumberOfEvents());
        assertEquals(11, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getAttributeValues());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals("b", attLog.getStringFromValue(1));
        assertEquals("c", attLog.getStringFromValue(2));
        assertEquals("d", attLog.getStringFromValue(3));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());

        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(1, attLog.getTraces().size());
        assertEquals(11, attLog.getNumberOfEvents());

        // LogSummary
        logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(4, logSummary.getActivityCount());
        assertEquals(11, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);

    }

    @Test
    void test_Event_Filtering() {
        ALog log = new ALog(readLogWithOneTraceAndCompleteEvents());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeTrace attTrace0 = attLog.getTraces().get(0);

        // AttributeLogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(4, oriLogSummary.getActivityCount());
        assertEquals(11, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(4, logSummary.getActivityCount());
        assertEquals(11, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(3300000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, logSummary.getTraceDurationMedian(), 0.05);

        // Filter traces of ALog
        LogBitMap logBitMap1 = null;
        try {
            logBitMap1 = new LogBitMap(log.getOriginalTraces().size());
            logBitMap1.setTraceBitSet(LogBitMap.newBitSet(1), 1); // keep trace
            logBitMap1.addEventBitSet(LogBitMap.newBitSet(11, 5, 11), 11);
            log.updateLogStatus(logBitMap1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getTraces().size());
        assertEquals(1, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1", log.getAttributes().get("concept:name").toString());
        assertEquals(11, log.getOriginalNumberOfEvents());
        assertEquals(6, log.getNumberOfEvents());
        assertEquals(Lists.mutable.of(trace0), log.getTraces());
        assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());

        // Filter traces of AttributeLog
        try {
            attLog.refresh();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(11, attLog.getOriginalNumberOfEvents());
        assertEquals(6, attLog.getNumberOfEvents());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals("c", attLog.getStringFromValue(2));
        assertEquals("d", attLog.getStringFromValue(3));
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 2, 3),
            attLog.getAttributeValues());
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());

        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(1, attLog.getTraces().size());
        assertEquals(6, attLog.getNumberOfEvents());

        // AttributeLogSummary
        oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(4, oriLogSummary.getActivityCount());
        assertEquals(11, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3300000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3300000, oriLogSummary.getTraceDurationMedian(), 0.05);

        logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(3, logSummary.getActivityCount());
        assertEquals(6, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(2400000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(2400000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(2400000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(2400000, logSummary.getTraceDurationMedian(), 0.05);

        // AttributeTrace
        assertEquals("Case1", attTrace0.getTraceId());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
            attTrace0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        assertEquals(2400000, attTrace0.getDuration());
        assertEquals(8, attTrace0.getOriginalEventStatus().cardinality());
        assertEquals(0, attTrace0.getVariantIndex());
        assertEquals(0, attTrace0.getVariantRank());

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            attTrace0.getValueTrace());
        assertEquals(
            IntArrayList.newListWith(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            attTrace0.getOriginalValueTrace());

        assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
            attTrace0.getStartTimeTrace());

        assertEquals(LongArrayList.newListWith(
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:15:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:21:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:28:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:36:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:45:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(),
                dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis()),
            attTrace0.getEndTimeTrace());

        assertEquals(LongArrayList.newListWith(0, 0, 0, 0, 0, 0, 0, 0), attTrace0.getDurationTrace());

        assertEquals(false, attTrace0.isEmpty());

        AttributeTraceGraph traceGraph0 = attTrace0.getActiveGraph();
        assertEquals(IntSets.mutable.of(2, 12, 15, 17, 20, 27), traceGraph0.getArcs());
        assertEquals(IntSets.mutable.of(0, 2, 3, 4, 5), traceGraph0.getNodes());

        assertEquals(1, traceGraph0.getNodeTotalFrequency(0));
        assertEquals(3, traceGraph0.getNodeTotalFrequency(2));
        assertEquals(2, traceGraph0.getNodeTotalFrequency(3));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(4)); //-1
        assertEquals(1, traceGraph0.getNodeTotalFrequency(5)); //-2

        assertEquals(0, traceGraph0.getNodeTotalDuration(0));
        assertEquals(0, traceGraph0.getNodeMinDuration(0));
        assertEquals(0, traceGraph0.getNodeMaxDuration(0));

        assertEquals(0, traceGraph0.getNodeTotalDuration(2));
        assertEquals(0, traceGraph0.getNodeMinDuration(2));
        assertEquals(0, traceGraph0.getNodeMaxDuration(2));

        assertEquals(0, traceGraph0.getNodeTotalDuration(3));
        assertEquals(0, traceGraph0.getNodeMinDuration(3));
        assertEquals(0, traceGraph0.getNodeMaxDuration(3));

        assertEquals(0, traceGraph0.getNodeTotalDuration(4));
        assertEquals(0, traceGraph0.getNodeMinDuration(4));
        assertEquals(0, traceGraph0.getNodeMaxDuration(4));

        assertEquals(0, traceGraph0.getNodeTotalDuration(5));
        assertEquals(0, traceGraph0.getNodeMinDuration(5));
        assertEquals(0, traceGraph0.getNodeMaxDuration(5));

        assertEquals(1, traceGraph0.getArcTotalFrequency(2)); //2,12,15,17,20,27
        assertEquals(1, traceGraph0.getArcTotalFrequency(12));
        assertEquals(1, traceGraph0.getArcTotalFrequency(15));
        assertEquals(1, traceGraph0.getArcTotalFrequency(17));
        assertEquals(2, traceGraph0.getArcTotalFrequency(20));
        assertEquals(1, traceGraph0.getArcTotalFrequency(27));

        assertEquals(600000, traceGraph0.getArcMinDuration(2));
        assertEquals(600000, traceGraph0.getArcMaxDuration(2));

        assertEquals(540000, traceGraph0.getArcMinDuration(12));
        assertEquals(540000, traceGraph0.getArcMaxDuration(12));

        assertEquals(420000, traceGraph0.getArcMinDuration(15));
        assertEquals(420000, traceGraph0.getArcMaxDuration(15));

        assertEquals(0, traceGraph0.getArcMinDuration(17));
        assertEquals(0, traceGraph0.getArcMaxDuration(17));

        assertEquals(360000, traceGraph0.getArcMinDuration(20));
        assertEquals(480000, traceGraph0.getArcMaxDuration(20));

        assertEquals(0, traceGraph0.getArcMinDuration(27));
        assertEquals(0, traceGraph0.getArcMaxDuration(27));
    }

    @Test
        // Similar to test1_AttributesOf_ATrace_AActivity_AttributeTrace_And_Changing_Attribute()
        // but for a log with start and complete events and these pairs are non-overlapping in time.
    void test2_AttributesOf_ATrace_AActivity_AttributeTrace() {
        ALog log = new ALog(readLogWithOneTrace_StartCompleteEvents_NonOverlapping());
        ATrace trace0 = log.getTraces().get(0);
        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();
        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);

        // ALog
        assertEquals(1, log.getOriginalTraces().size());
        assertEquals(1, log.getTraces().size());
        assertEquals(1, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1", log.getAttributes().get("concept:name").toString());
        assertEquals(22, log.getOriginalNumberOfEvents());
        assertEquals(22, log.getNumberOfEvents());
        assertEquals(Lists.mutable.of(trace0), log.getTraces());
        assertEquals(Lists.mutable.of(trace0), log.getOriginalTraces());

        // ATrace
        assertEquals("Case1", trace0.getTraceId());
        assertEquals(11, trace0.getOriginalActivities().size());
        assertEquals(11, trace0.getActivities().size());
        assertEquals(22, trace0.getEvents().size());
        assertEquals(22, trace0.getOriginalEvents().size());
        assertEquals(1, trace0.getAttributes().size());
        assertEquals(22, trace0.getOriginalEventStatus().cardinality());
        assertEquals(11, trace0.getOriginalActivityStatus().cardinality());


        // AttributeLog
        assertEquals(11, attLog.getOriginalNumberOfEvents());
        assertEquals(11, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3),
            attLog.getAttributeValues());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals("b", attLog.getStringFromValue(1));
        assertEquals("c", attLog.getStringFromValue(2));
        assertEquals("d", attLog.getStringFromValue(3));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());

        assertEquals(1, attLog.getOriginalTraces().size());
        assertEquals(1, attLog.getTraces().size());
        assertEquals(11, attLog.getNumberOfEvents());

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(1, oriLogSummary.getCaseCount());
        assertEquals(4, oriLogSummary.getActivityCount());
        assertEquals(22, oriLogSummary.getEventCount());
        assertEquals(1, oriLogSummary.getVariantCount());
        assertEquals(3360000, oriLogSummary.getTraceDurationMin(), 0.05);
        assertEquals(3360000, oriLogSummary.getTraceDurationMax(), 0.05);
        assertEquals(3360000, oriLogSummary.getTraceDurationMean(), 0.05);
        assertEquals(3360000, oriLogSummary.getTraceDurationMedian(), 0.05);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(1, logSummary.getCaseCount());
        assertEquals(4, logSummary.getActivityCount());
        assertEquals(22, logSummary.getEventCount());
        assertEquals(1, logSummary.getVariantCount());
        assertEquals(3360000, logSummary.getTraceDurationMin(), 0.05);
        assertEquals(3360000, logSummary.getTraceDurationMax(), 0.05);
        assertEquals(3360000, logSummary.getTraceDurationMean(), 0.05);
        assertEquals(3360000, logSummary.getTraceDurationMedian(), 0.05);

        // AttributeTrace
        assertEquals("Case1", attTrace0.getTraceId());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T21:59:19.308+10:00").getMillis(),
            attTrace0.getStartTime());
        assertEquals(dateFormatter.parseDateTime("2010-10-27T22:55:19.308+10:00").getMillis(), attTrace0.getEndTime());
        assertEquals(3360000, attTrace0.getDuration());
        assertEquals(13, attTrace0.getOriginalEventStatus().cardinality());
        assertEquals(0, attTrace0.getVariantIndex());
        assertEquals(0, attTrace0.getVariantRank());

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            attTrace0.getValueTrace());
        assertEquals(
            IntArrayList.newListWith(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            attTrace0.getOriginalValueTrace());

        assertEquals(LongArrayList.newListWith(
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

        assertEquals(LongArrayList.newListWith(
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

        assertEquals(
            LongArrayList.newListWith(0, 60000, 60000, 60000, 60000, 60000, 60000, 60000, 60000, 60000, 60000, 60000,
                0), attTrace0.getDurationTrace());

        AttributeTraceGraph traceGraph0 = attTrace0.getActiveGraph();
        assertEquals(IntSets.mutable.of(0, 1, 2, 8, 12, 15, 17, 20, 24), traceGraph0.getArcs());
        assertEquals(IntSets.mutable.of(0, 1, 2, 3, 4, 5), traceGraph0.getNodes());

        assertEquals(4, traceGraph0.getNodeTotalFrequency(0));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(1));
        assertEquals(4, traceGraph0.getNodeTotalFrequency(2));
        assertEquals(2, traceGraph0.getNodeTotalFrequency(3));
        assertEquals(1, traceGraph0.getNodeTotalFrequency(4)); //-1
        assertEquals(1, traceGraph0.getNodeTotalFrequency(5)); //-2

        assertEquals(240000, traceGraph0.getNodeTotalDuration(0));
        assertEquals(60000, traceGraph0.getNodeMinDuration(0));
        assertEquals(60000, traceGraph0.getNodeMaxDuration(0));

        assertEquals(60000, traceGraph0.getNodeTotalDuration(1));
        assertEquals(60000, traceGraph0.getNodeMinDuration(1));
        assertEquals(60000, traceGraph0.getNodeMaxDuration(1));

        assertEquals(240000, traceGraph0.getNodeTotalDuration(2));
        assertEquals(60000, traceGraph0.getNodeMinDuration(2));
        assertEquals(60000, traceGraph0.getNodeMaxDuration(2));

        assertEquals(120000, traceGraph0.getNodeTotalDuration(3));
        assertEquals(60000, traceGraph0.getNodeMinDuration(3));
        assertEquals(60000, traceGraph0.getNodeMaxDuration(3));

        assertEquals(0, traceGraph0.getNodeTotalDuration(4));
        assertEquals(0, traceGraph0.getNodeMinDuration(4));
        assertEquals(0, traceGraph0.getNodeMaxDuration(4));

        assertEquals(0, traceGraph0.getNodeTotalDuration(5));
        assertEquals(0, traceGraph0.getNodeMinDuration(5));
        assertEquals(0, traceGraph0.getNodeMaxDuration(5));

        assertEquals(2, traceGraph0.getArcTotalFrequency(0)); //0,1,2,8,12,15,17,20,24
        assertEquals(1, traceGraph0.getArcTotalFrequency(1));
        assertEquals(1, traceGraph0.getArcTotalFrequency(2));
        assertEquals(1, traceGraph0.getArcTotalFrequency(8));
        assertEquals(1, traceGraph0.getArcTotalFrequency(12));
        assertEquals(2, traceGraph0.getArcTotalFrequency(15));
        assertEquals(1, traceGraph0.getArcTotalFrequency(17));
        assertEquals(2, traceGraph0.getArcTotalFrequency(20));
        assertEquals(1, traceGraph0.getArcTotalFrequency(24));

        assertEquals(0, traceGraph0.getArcMinDuration(0));
        assertEquals(60000, traceGraph0.getArcMaxDuration(0));

        assertEquals(120000, traceGraph0.getArcMinDuration(1));
        assertEquals(120000, traceGraph0.getArcMaxDuration(1));

        assertEquals(540000, traceGraph0.getArcMinDuration(2));
        assertEquals(540000, traceGraph0.getArcMaxDuration(2));

        assertEquals(180000, traceGraph0.getArcMinDuration(8));
        assertEquals(180000, traceGraph0.getArcMaxDuration(8));

        assertEquals(480000, traceGraph0.getArcMinDuration(12));
        assertEquals(480000, traceGraph0.getArcMaxDuration(12));

        assertEquals(240000, traceGraph0.getArcMinDuration(15));
        assertEquals(360000, traceGraph0.getArcMaxDuration(15));

        assertEquals(0, traceGraph0.getArcMinDuration(17));
        assertEquals(0, traceGraph0.getArcMaxDuration(17));

        assertEquals(300000, traceGraph0.getArcMinDuration(20));
        assertEquals(420000, traceGraph0.getArcMaxDuration(20));

        assertEquals(0, traceGraph0.getArcMinDuration(24));
        assertEquals(0, traceGraph0.getArcMaxDuration(24));


        // Variants
        assertEquals(1, attLog.getVariantView().getActiveVariants().size());
        IntList variant0 = variants.getVariantAtIndex(0);

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 0, 0, 1, 2, 3, 2, 3, 2, 0, 2, attLog.getEndEvent()),
            variant0);

        assertEquals(0, variants.getIndexOf(variant0));
        assertEquals(1, variants.getFrequency(variant0));
        assertEquals(0, variants.getRankOf(variant0));

        assertEquals(1.0, variants.getVariantRelativeFrequency(variant0), 0.01);

        assertEquals(Lists.mutable.of(attTrace0), variants.getTraces(variant0));

    }

    @Test
    void test_AttributesOf_Alog_AttributeLog_And_AttributeTraceVariants() {
        ALog log = new ALog(readLogWithCompleteEventsOnly());

        AttributeLog attLog =
            new AttributeLog(log, log.getAttributeStore().getStandardEventConceptName(), getAllDayAllTimeCalendar());
        AttributeTraceVariants variants = attLog.getVariantView().getActiveVariants();

        AttributeTrace attTrace0 = attLog.getOriginalTraceFromIndex(0);
        AttributeTrace attTrace1 = attLog.getOriginalTraceFromIndex(1);
        AttributeTrace attTrace2 = attLog.getOriginalTraceFromIndex(2);
        AttributeTrace attTrace3 = attLog.getOriginalTraceFromIndex(3);
        AttributeTrace attTrace4 = attLog.getOriginalTraceFromIndex(4);
        AttributeTrace attTrace5 = attLog.getOriginalTraceFromIndex(5);

        // ALog
        assertEquals(6, log.getOriginalTraces().size());
        assertEquals(6, log.getTraces().size());
        assertEquals(6, log.getOriginalTraceStatus().cardinality());
        assertEquals("L1_complete_events_only_with_resources", log.getAttributes().get("concept:name").toString());
        assertEquals(23, log.getOriginalNumberOfEvents());
        assertEquals(23, log.getNumberOfEvents());

        // ATrace

        // AttributeLog
        assertEquals(23, attLog.getOriginalNumberOfEvents());
        assertEquals(23, attLog.getNumberOfEvents());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3, 4),
            attLog.getOriginalAttributeValues());
        assertEquals(IntSets.mutable.of(attLog.getEndEvent(), attLog.getStartEvent(), 0, 1, 2, 3, 4),
            attLog.getAttributeValues());
        assertEquals("a", attLog.getStringFromValue(0));
        assertEquals("e", attLog.getStringFromValue(1));
        assertEquals("d", attLog.getStringFromValue(2));
        assertEquals("c", attLog.getStringFromValue(3));
        assertEquals("b", attLog.getStringFromValue(4));
        assertEquals(Constants.START_NAME, attLog.getStringFromValue(attLog.getStartEvent()));
        assertEquals(Constants.END_NAME, attLog.getStringFromValue(attLog.getEndEvent()));
        assertEquals(attLog.getAttribute().getArtificialStartIndex(), attLog.getStartEvent());
        assertEquals(attLog.getAttribute().getArtificialEndIndex(), attLog.getEndEvent());

        // LogSummary
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        assertEquals(6, oriLogSummary.getCaseCount());
        assertEquals(5, oriLogSummary.getActivityCount());
        assertEquals(23, oriLogSummary.getEventCount());
        assertEquals(3, oriLogSummary.getVariantCount());
        assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);

        AttributeLogSummary logSummary = attLog.getLogSummary();
        assertEquals(6, logSummary.getCaseCount());
        assertEquals(5, logSummary.getActivityCount());
        assertEquals(23, logSummary.getEventCount());
        assertEquals(3, logSummary.getVariantCount());
        assertEquals(120000, oriLogSummary.getTraceDurationMin(), 0.005);
        assertEquals(240000, oriLogSummary.getTraceDurationMax(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMean(), 0.005);
        assertEquals(180000, oriLogSummary.getTraceDurationMedian(), 0.005);

        // AttributeTrace
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 1, 2, attLog.getEndEvent()),
            attTrace0.getValueTrace());
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 3, 4, 2, attLog.getEndEvent()),
            attTrace1.getValueTrace());
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 4, 3, 2, attLog.getEndEvent()),
            attTrace2.getValueTrace());
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 4, 3, 2, attLog.getEndEvent()),
            attTrace3.getValueTrace());
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 4, 3, 2, attLog.getEndEvent()),
            attTrace4.getValueTrace());
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 3, 4, 2, attLog.getEndEvent()),
            attTrace5.getValueTrace());

        // Variants
        assertEquals(3, attLog.getVariantView().getActiveVariants().size());

        IntList variant0 = variants.getVariantAtIndex(0);
        IntList variant1 = variants.getVariantAtIndex(1);
        IntList variant2 = variants.getVariantAtIndex(2);

        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 1, 2, attLog.getEndEvent()), variant0);
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 3, 4, 2, attLog.getEndEvent()), variant1);
        assertEquals(IntLists.mutable.of(attLog.getStartEvent(), 0, 4, 3, 2, attLog.getEndEvent()), variant2);

        assertEquals(0, variants.getIndexOf(variant0));
        assertEquals(1, variants.getIndexOf(variant1));
        assertEquals(2, variants.getIndexOf(variant2));

        assertEquals(1, variants.getFrequency(variant0));
        assertEquals(2, variants.getFrequency(variant1));
        assertEquals(3, variants.getFrequency(variant2));

        assertEquals(2, variants.getRankOf(variant0));
        assertEquals(1, variants.getRankOf(variant1));
        assertEquals(0, variants.getRankOf(variant2));

        assertEquals(0.166, variants.getVariantRelativeFrequency(variant0), 0.01);
        assertEquals(0.333, variants.getVariantRelativeFrequency(variant1), 0.01);
        assertEquals(0.5, variants.getVariantRelativeFrequency(variant2), 0.01);

        assertEquals(Lists.mutable.of(attTrace0), variants.getTraces(variant0));
        assertEquals(Lists.mutable.of(attTrace1, attTrace5), variants.getTraces(variant1));
        assertEquals(Lists.mutable.of(attTrace2, attTrace3, attTrace4), variants.getTraces(variant2));

    }

}
