/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.deckfour.xes.model.XLog;

import java.util.List;

public class APMLogParsingTest {

    public static void testConcurrentStartCompleteEvents(XLog xLog, APMLogUnitTest parent)
            throws Exception {
        parent.printString("\n(/ 'o')/ ~ Test 'ConcurrentStartCompleteEvents'");
        APMLog apmLog = new APMLog(xLog);
        if (apmLog.getTraceList().get(0).getActivityList().size() != 10) {
            throw new AssertionError("TEST FAILED. OUTPUT ACTIVITY LIST SIZE MISMATCH.");
        } else {
            parent.printString("'ConcurrentStartCompleteEvents' test PASS.");
        }
    }

    public static void testStartCompleteNoOverlap(XLog xLog, APMLogUnitTest parent)
            throws Exception {
        parent.printString("\n(/ 'o')/ ~ Test 'StartCompleteNoOverlap'");
        APMLog apmLog = new APMLog(xLog);
        if (apmLog.getTraceList().get(0).getEventList().size() != 22) {
            throw new AssertionError("TEST FAILED. OUTPUT EVENT LIST SIZE MISMATCH.");
        } else {
            parent.printString("'StartCompleteNoOverlap' test PASS.");
        }
    }

    public static void testActivityStartCompleteEventsOnly(XLog xLog, APMLogUnitTest parent)
            throws Exception {
        parent.printString("\n(/ 'o')/ ~ Test 'ActivityStartCompleteEventsOnly'");
        APMLog apmLog = new APMLog(xLog);

//        parent.printString("event size = " + apmLog.getEventSize());
//        parent.printString("trace 0 event size = " + apmLog.getTraceList().get(0).getEventList().size());

        parent.printString("variant size = " + apmLog.getCaseVariantSize());


        if (apmLog.getTraceList().size() != 6) {
            throw new AssertionError("TEST FAILED. OUTPUT TRACE LIST SIZE MISMATCH.");
        } else if (apmLog.getCaseVariantSize() != 3) {
            throw new AssertionError("TEST FAILED. OUTPUT CASE VARIANT SIZE MISMATCH.");
        } else if (apmLog.getEventSize() != 23) {
            throw new AssertionError("TEST FAILED. OUTPUT TOTAL EVENTS SIZE MISMATCH.");
        } else if (apmLog.getUniqueActivitySize() != 5) {
            throw new AssertionError("TEST FAILED. OUTPUT UNIQUE ACTIVITY SIZE MISMATCH.");
        } else {
            parent.printString("'ActivityStartCompleteEventsOnly' test PASS.");
        }
    }

    public static void testMissingTimestamp(XLog xLog, APMLogUnitTest parent) throws Exception {
        parent.printString("\n(/ 'o')/ ~ Test 'MissingTimestamp'");
        APMLog apmLog = new APMLog(xLog);
        if (apmLog.getTraceList().size() != 6) {
            throw new AssertionError("TEST FAILED. OUTPUT TRACE LIST SIZE MISMATCH.");
        } else if (apmLog.getCaseVariantSize() != 3) {
            throw new AssertionError("TEST FAILED. OUTPUT CASE VARIANT SIZE MISMATCH.");
        } else if (apmLog.getEventSize() != 23) {
            throw new AssertionError("TEST FAILED. OUTPUT TOTAL EVENTS SIZE MISMATCH.");
        } else if (apmLog.getUniqueActivitySize() != 5) {
            throw new AssertionError("TEST FAILED. OUTPUT UNIQUE ACTIVITY SIZE MISMATCH.");
        } else {
            parent.printString("'MissingTimestamp' test PASS.");
        }
    }

    public static void testCompleteOnlyWithResources(XLog xLog, APMLogUnitTest parent) throws Exception {
        parent.printString("\n(/ 'o')/ ~ Test 'CompleteOnlyWithResources'");
        APMLog apmLog = new APMLog(xLog);
        if (apmLog.getTraceList().size() != 6) {
            throw new AssertionError("TEST FAILED. OUTPUT TRACE LIST SIZE MISMATCH.");
        } else if (apmLog.getCaseVariantSize() != 3) {
            throw new AssertionError("TEST FAILED. OUTPUT CASE VARIANT SIZE MISMATCH.");
        } else if (apmLog.getEventSize() != 23) {
            throw new AssertionError("TEST FAILED. OUTPUT TOTAL EVENTS SIZE MISMATCH.");
        } else if (apmLog.getUniqueActivitySize() != 5) {
            throw new AssertionError("TEST FAILED. OUTPUT UNIQUE ACTIVITY SIZE MISMATCH.");
        } else {
            parent.printString("'CompleteOnlyWithResources' test PASS.");
        }
    }

    public static void testCountAsSameActivityEvenResourcesAreDifferent(XLog xLog, APMLogUnitTest parent)
            throws Exception {
        parent.printString("\n(/ 'o')/ ~ Test 'CountAsSameActivityEvenResourcesAreDifferent'");
        APMLog apmLog = new APMLog(xLog);
        List<AActivity> activityList = apmLog.getTraceList().get(0).getActivityList();
        String lastActivity = activityList.get(activityList.size()-1).getName();

        if (apmLog.getTraceList().size() != 1) {
            throw new AssertionError("TEST FAILED. OUTPUT TRACE LIST SIZE MISMATCH.");
        } else if (apmLog.getCaseVariantSize() != 1) {
            throw new AssertionError("TEST FAILED. OUTPUT CASE VARIANT SIZE MISMATCH.");
        } else if (apmLog.getEventSize() != 27) {
            throw new AssertionError("TEST FAILED. OUTPUT TOTAL EVENTS SIZE MISMATCH.");
        } else if (apmLog.getUniqueActivitySize() != 14) {
            throw new AssertionError("TEST FAILED. OUTPUT UNIQUE ACTIVITY SIZE MISMATCH.");
        } else if (!lastActivity.equals("O_Refused")) {
            throw new AssertionError("TEST FAILED. THE LAST ACTIVITY MISMATCH.");
        } else {
            parent.printString("'CountAsSameActivityEvenResourcesAreDifferent' test PASS.");
        }
    }
}
