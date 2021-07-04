/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.apmlog;

import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.apromore.apmlog.xes.XESAttributeCodes;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.immutable.ImmutableUnifiedMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class APMLogParsingTest {

    public static void testConcurrentStartCompleteEvents(XLog xLog, APMLogUnitTest parent) throws EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 10);
    }

    public static void testStartCompleteNoOverlap(XLog xLog, APMLogUnitTest parent) throws EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 11);
    }

    public static void testActivityStartCompleteEventsOnly(XLog xLog, APMLogUnitTest parent) throws EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);

        assertTrue(apmLog.getTraces().size() == 6);
        assertTrue(apmLog.getCaseVariantGroupMap().size() == 3);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 23);
        assertTrue(getUniqueActivitySize(apmLog) == 5);
    }

    public static void testMissingTimestamp(XLog xLog, APMLogUnitTest parent) throws EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);

        assertTrue(apmLog.getTraces().size() == 6);
        assertTrue(apmLog.getCaseVariantGroupMap().size() == 3);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 22);
        assertTrue(getUniqueActivitySize(apmLog) == 4);
    }

    public static void testCompleteOnlyWithResources(XLog xLog, APMLogUnitTest parent) throws EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);

        assertTrue(apmLog.getTraces().size() == 6);
        assertTrue(apmLog.getCaseVariantGroupMap().size() == 3);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 23);
        assertTrue(getUniqueActivitySize(apmLog) == 5);
    }

    public static void testCountAsSameActivityEvenResourcesAreDifferent(XLog xLog, APMLogUnitTest parent) throws EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);
        List<ActivityInstance> activityList = apmLog.getTraces().get(0).getActivityInstances();
        String lastActivity = activityList.get(activityList.size()-1).getName();

        assertTrue(apmLog.getTraces().size() == 1);
        assertTrue(apmLog.getCaseVariantGroupMap().size() == 1);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 14);
        assertTrue(getUniqueActivitySize(apmLog) == 14);
        assertTrue(lastActivity.equals("O_Refused"));
    }

    public static void testMixedLifecycleNoOverlapping(XLog xLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        APMLog apmLog = XLogToImmutableLog.convertXLog("Process Log", xLog);

        assertTrue(apmLog.getTraces().size() == 2);
        assertTrue(apmLog.getCaseVariantGroupMap().size() == 2);
        assertTrue(apmLog.getTraces().stream().flatMap(x->x.getActivityInstances().stream())
                .collect(Collectors.toList()).size() == 6);
        assertTrue(getUniqueActivitySize(apmLog) == 5);

        List<String> conceptNames = Arrays.asList("a", "b", "c", "d", "e");
        List<Long> sizes = Arrays.asList(1L, 1L, 1L, 2L, 1L);

        for (int i = 0; i < conceptNames.size(); i++) {
            EventAttributeValue eav = getEventAttributeValueByConceptName(conceptNames.get(i), apmLog);
            assertTrue(eav.getCases() == sizes.get(i));
        }
    }

    private static int getUniqueActivitySize(APMLog apmLog) {
        ImmutableUnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = apmLog.getImmutableEventAttributeValues();
        return eavMap.get(XESAttributeCodes.CONCEPT_NAME).size();
    }

    private static EventAttributeValue getEventAttributeValueByConceptName(String conceptName, APMLog apmLog) {
        return apmLog.getImmutableEventAttributeValues().get("concept:name").stream()
                .filter(x -> x.getValue().equals(conceptName))
                .findFirst()
                .orElse(null);
    }
}
