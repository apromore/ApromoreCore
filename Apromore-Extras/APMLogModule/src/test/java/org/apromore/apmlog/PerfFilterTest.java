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
package org.apromore.apmlog;

import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerfFilterTest {

    // (1)
    public static void testDuration(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {

        System.out.println("Trace size: " + apmLog.getTraces().size());

        for (ATrace aTrace : apmLog.getTraces()) {
            System.out.println(aTrace.getCaseId() + ":" + aTrace.getDuration());
        }

        Set<RuleValue> primaryValues = new HashSet<>();

        double val1 = Double.valueOf("10800000");
        double val2 = Double.valueOf("10800001");

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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (2)
    public static void testTotalProcessTime(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {

        double val1 = Double.valueOf("3600000");
        double val2 = Double.valueOf("3600001");

        System.out.println("Trace size: " + apmLog.getTraces().size());

        for (ATrace aTrace : apmLog.getTraces()) {

            double ttlProcTime = LogStatsAnalyzer.getProcessingTimesOf(aTrace).sum();
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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (3)
    public static void testAverageProcessTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {

        double val1 = Double.valueOf("1800000");
        double val2 = Double.valueOf("1800001");

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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (4)
    public static void testMaxProcessTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {

        double val1 = Double.valueOf("1800000");
        double val2 = Double.valueOf("1800001");

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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (5)
    public static void testTotalWaitTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {

        double val1 = Double.valueOf(10800000d);
        double val2 = Double.valueOf(10800001d);

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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (6)
    public static void testAverageWaitTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {

        double val1 = Double.valueOf("7200000");
        double val2 = Double.valueOf("7200001");


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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (7)
    public static void testMaxWaitTime(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {

        double val1 = Double.valueOf("10777000");
        double val2 = Double.valueOf("10800000");

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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (8)
    public static void testUtilization(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {

        double val1 = 0.297;
        double val2 = 0.379;


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

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
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

    // (9)
    public static void testCaseLength(APMLog apmLog) throws EmptyInputException {

        long val1 = 1;
        long val2 = 2;

        String attrKey = "case:length";

        FilterType filterType = FilterType.CASE_LENGTH;

        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, val1);
        RuleValue rv2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, val2);

        Set<RuleValue> primaryValues = new HashSet<>(Arrays.asList(rv1, rv2));

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = Arrays.asList(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();

        int[] result = new int[]{0, 0};
        int[] expected = new int[]{1, 0};

        System.out.println("Result size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) result[0] = 1;
            if (trace.getCaseId().equals("c2")) result[1] = 1;
        }

        assertArrayEquals(expected, result);
    }
}
