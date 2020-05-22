/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.*;
import org.apromore.apmlog.filter.types.Choice;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;


/**
 * Test suite for {@link APMLog}.
 */
public class APMLogUnitTest {

    private XLog bpi2013;

    @Before
    public void before() throws Exception {
        bpi2013 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/BPI Challenge 2013 closed problems.xes.gz")).get(0);

    }

    @Ignore("This test demonstrates the defect AP-1037")
    @Test
    public void testConstructor_BPIC13() {
        APMLog apmLog = new APMLog(bpi2013);
    }

    @Test
    public void testDirectFollowFilter1() throws Exception {
        printString("\n(/ 'o')/ ~ Test 'Direct Follow' Filter 1");

        XLog sample5 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/_sample5.xes.gz")).get(0);

        APMLog apmLog = new APMLog(sample5);

//        for (int i = 0; i < apmLog.size(); i++) {
//            System.out.println(apmLog.get(i).getCaseId());
//        }

        FilterType filterType = FilterType.DIRECT_FOLLOW;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "concept:name";
        RuleValue rv1 = new RuleValue(filterType, OperationType.FROM, attrKey, "[Start]");
        RuleValue rv2 = new RuleValue(filterType, OperationType.TO, attrKey, "a");
        RuleValue rv3 = new RuleValue(filterType, OperationType.TO, attrKey, "b");

        primaryValues.add(rv1);
        primaryValues.add(rv2);
        primaryValues.add(rv3);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;

        System.out.println("\n\ntrace size = " + traceList.size() + "\n\n");

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }



        if (!hasC1 || !hasC2) {
            String message = "TEST FAILED. RESULT TRACE LIST MISMATCH.";
            throw new AssertionError(message);
        } else {
            printString("'Direct Follow' Test 1 OK.");
        }
    }

    @Test
    public void testDirectFollowFilter2() throws Exception {
        printString("\n(/ 'o')/ ~ Test 'Direct Follow' Filter 2");

        XLog sample5 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/_sample5.xes.gz")).get(0);

        APMLog apmLog = new APMLog(sample5);

//        for (int i = 0; i < apmLog.size(); i++) {
//            System.out.println(apmLog.get(i).getCaseId());
//        }

        FilterType filterType = FilterType.DIRECT_FOLLOW;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = "concept:name";
        RuleValue rv1 = new RuleValue(filterType, OperationType.FROM, attrKey, "a");
        RuleValue rv2 = new RuleValue(filterType, OperationType.FROM, attrKey, "b");

        RuleValue rv3 = new RuleValue(filterType, OperationType.TO, attrKey, "a2");
        RuleValue rv4 = new RuleValue(filterType, OperationType.TO, attrKey, "b2");

        primaryValues.add(rv1);
        primaryValues.add(rv2);
        primaryValues.add(rv3);
        primaryValues.add(rv4);

        Set<RuleValue> secondaryValues = new HashSet<>();

        OperationType optType5 = OperationType.EQUAL;
        String reqKey = "org:resource";
        RuleValue rv5 = new RuleValue(filterType, optType5, reqKey, reqKey);
        secondaryValues.add(rv5);

        OperationType optType6 = OperationType.GREATER;
        long fromVal = 1000 * 60 * 10;
        RuleValue rv6 = new RuleValue(filterType, optType6, attrKey, fromVal);
        secondaryValues.add(rv6);

        OperationType optType7 = OperationType.LESS_EQUAL;
        long toVal = 1000 * 60 * 60 * 2;
        RuleValue rv7 = new RuleValue(filterType, optType7, attrKey, toVal);
        secondaryValues.add(rv7);



        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, secondaryValues);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getApmLog().getTraceList();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;
        boolean hasC4 = false;
        boolean hasC5 = false;

        System.out.println("\n\ntrace size = " + traceList.size() + "\n\n");

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
            if (trace.getCaseId().equals("c3")) hasC3 = true;
            if (trace.getCaseId().equals("c4")) hasC4 = true;
            if (trace.getCaseId().equals("c5")) hasC5 = true;
        }

        if (hasC2 || hasC3 || hasC4 || hasC5) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        }

        if (!hasC1) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            printString("'Direct Follow' Test 2 OK.");
        }

    }

    private void printString(String unicodeMessage) throws UnsupportedEncodingException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.println(unicodeMessage);
    }

    public static List<XLog> parseXLogFile(File xLogFile) throws Exception {
        String fileName = xLogFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        XesXmlParser parser  = extension.equals(".gz") ? new XesXmlGZIPParser() : new XesXmlParser();
        return parser.parse(xLogFile);
    }
}
