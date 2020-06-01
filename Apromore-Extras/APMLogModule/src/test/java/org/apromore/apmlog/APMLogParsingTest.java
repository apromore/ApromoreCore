package org.apromore.apmlog;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.deckfour.xes.model.XLog;

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
}
