package org.apromore.apmlog;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DurationFilterTest {

    public static void testDuration(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException {

        System.out.println("Trace size: " + apmLog.getTraceList().size());

        for (ATrace aTrace : apmLog.getTraceList()) {
            System.out.println(aTrace.getCaseId() + ":" + aTrace.getDuration());
        }

        Set<RuleValue> primaryValues = new HashSet<>();

        long val1 = Long.valueOf("10800000");
        long val2 = Long.valueOf("10800001");

        String attrKey = "duration:range";
        RuleValue rv1 = new RuleValue(FilterType.DURATION, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(FilterType.DURATION, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                FilterType.DURATION, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (!hasC1 || hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Duration' Test PASS.");
        }
    }

    public static void testTotalProcessTime(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException {

        long val1 = Long.valueOf("3600000");
        long val2 = Long.valueOf("3600001");

        System.out.println("Trace size: " + apmLog.getTraceList().size());

        for (ATrace aTrace : apmLog.getTraceList()) {
            long ttlProcTime = aTrace.getTotalProcessingTime();
            System.out.println(aTrace.getCaseId() + ":" + ttlProcTime);
            System.out.println(ttlProcTime >= val1 && ttlProcTime <= val2);
        }

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "duration:total_processing";

        FilterType filterType = FilterType.TOTAL_PROCESSING_TIME;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Total Processing Time' Test PASS.");
        }
    }

    public static void testAverageProcessTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException {

        long val1 = Long.valueOf("1800000");
        long val2 = Long.valueOf("1800001");

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "duration:average_processing";

        FilterType filterType = FilterType.AVERAGE_PROCESSING_TIME;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Average Processing Time' Test PASS.");
        }
    }

    public static void testMaxProcessTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException {

        long val1 = Long.valueOf("1800000");
        long val2 = Long.valueOf("1800001");

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "duration:max_processing";

        FilterType filterType = FilterType.MAX_PROCESSING_TIME;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Max Processing Time' Test PASS.");
        }
    }

    public static void testTotalWaitTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException {

        long val1 = Long.valueOf("10800000");
        long val2 = Long.valueOf("10800001");

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "duration:total_waiting";

        FilterType filterType = FilterType.TOTAL_WAITING_TIME;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Total Waiting Time' Test PASS.");
        }
    }

    public static void testAverageWaitTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException {

        long val1 = Long.valueOf("7200000");
        long val2 = Long.valueOf("7200001");


//        for (ATrace aTrace : apmLog.getTraceList()) {
//            long avgWT = aTrace.getAverageWaitingTime();
//            System.out.println(aTrace.getCaseId() + ":" + avgWT);
//            System.out.println(avgWT >= val1 && avgWT <= val2);
//        }

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "duration:average_waiting";

        FilterType filterType = FilterType.AVERAGE_WAITING_TIME;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (!hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Average Waiting Time' Test PASS.");
        }
    }

    public static void testMaxWaitTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException {

        long val1 = Long.valueOf("10777000");
        long val2 = Long.valueOf("10800000");


//        for (ATrace aTrace : apmLog.getTraceList()) {
//            long avgWT = aTrace.getAverageWaitingTime();
//            System.out.println(aTrace.getCaseId() + ":" + avgWT);
//            System.out.println(avgWT >= val1 && avgWT <= val2);
//        }

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "duration:max_waiting";

        FilterType filterType = FilterType.MAX_WAITING_TIME;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (!hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Max Waiting Time' Test PASS.");
        }
    }

    public static void testUtilization(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException {

        double val1 = 0.297;
        double val2 = 0.379;


//        for (ATrace aTrace : apmLog.getTraceList()) {
//            long avgWT = aTrace.getAverageWaitingTime();
//            System.out.println(aTrace.getCaseId() + ":" + avgWT);
//            System.out.println(avgWT >= val1 && avgWT <= val2);
//        }

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "case:utilization";

        FilterType filterType = FilterType.CASE_UTILISATION;


        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv3 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        primaryValues.add(rv1);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }

        if (hasC1 || !hasC2) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'Case Utilization' Test PASS.");
        }
    }
}
