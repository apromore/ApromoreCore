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

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.*;
import org.apromore.apmlog.util.Util;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaseTimeFilterTest {

    public static void testActiveIn(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.CASE_TIME;

        Inclusion inclusion = Inclusion.ANY_VALUE;

        Choice choice =  Choice.RETAIN;
        ZonedDateTime zdtST = ZonedDateTime.parse("2020-01-15T22:02:29.000+11:00");
        ZonedDateTime zdtET = ZonedDateTime.parse("2020-01-15T22:05:30.000+11:00");
        long st = Util.epochMilliOf(zdtST);
        long et = Util.epochMilliOf(zdtET);

        String attrKey = "case:timeframe";

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, st);
        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, et);
        Set<RuleValue> primaryValues = new HashSet<RuleValue>();
        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
            if (trace.getCaseId().equals("c3")) hasC3 = true;
            System.out.println(trace.getCaseId());
        }

        if (hasC1 || !hasC2 || !hasC3) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'CaseTime:ActiveIn' test PASS.");
        }
    }

    public static void testContainIn(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.CASE_TIME;

        Inclusion inclusion = Inclusion.ALL_VALUES;

        Choice choice =  Choice.RETAIN;
        ZonedDateTime zdtST = ZonedDateTime.parse("2020-01-15T22:02:29.000+11:00");
        ZonedDateTime zdtET = ZonedDateTime.parse("2020-01-15T22:05:31.000+11:00");
        long st = Util.epochMilliOf(zdtST);
        long et = Util.epochMilliOf(zdtET);

        String attrKey = "case:timeframe";

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, st);
        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, et);
        Set<RuleValue> primaryValues = new HashSet<RuleValue>();
        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;

        System.out.println("trace size: " + traceList.size());

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
            if (trace.getCaseId().equals("c3")) hasC3 = true;
            System.out.println(trace.getCaseId());
        }

        if (hasC1 || !hasC2 || hasC3) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'CaseTime:ContainIn' test PASS.");
        }
    }

    public static void testStartIn(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.STARTTIME;

        Inclusion inclusion = Inclusion.ALL_VALUES;

        Choice choice =  Choice.RETAIN;
        ZonedDateTime zdtST = ZonedDateTime.parse("2020-01-15T21:01:30.922+11:00");
        ZonedDateTime zdtET = ZonedDateTime.parse("2020-01-15T21:30:30.922+11:00");
        long st = Util.epochMilliOf(zdtST);
        long et = Util.epochMilliOf(zdtET);

        String attrKey = "case:timeframe";

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, st);
        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, et);
        Set<RuleValue> primaryValues = new HashSet<RuleValue>();
        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;

        System.out.println("trace size: " + traceList.size());

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
            if (trace.getCaseId().equals("c3")) hasC3 = true;
            System.out.println(trace.getCaseId());
        }

        if (!hasC1 || hasC2 || !hasC3) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'CaseTime:StartIn' test PASS.");
        }
    }

    public static void testEndIn(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.ENDTIME;

        Inclusion inclusion = Inclusion.ALL_VALUES;

        Choice choice =  Choice.RETAIN;
        ZonedDateTime zdtST = ZonedDateTime.parse("2020-01-15T22:04:30.922+11:00");
        ZonedDateTime zdtET = ZonedDateTime.parse("2020-01-15T22:05:30.922+11:00");
        long st = Util.epochMilliOf(zdtST);
        long et = Util.epochMilliOf(zdtET);

        String attrKey = "case:timeframe";

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, st);
        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, et);
        Set<RuleValue> primaryValues = new HashSet<RuleValue>();
        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;

        System.out.println("trace size: " + traceList.size());

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
            if (trace.getCaseId().equals("c3")) hasC3 = true;
            System.out.println(trace.getCaseId());
        }

        if (hasC1 || !hasC2 || !hasC3) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            parent.printString("'CaseTime:EndIn' test PASS.");
        }
    }

}
