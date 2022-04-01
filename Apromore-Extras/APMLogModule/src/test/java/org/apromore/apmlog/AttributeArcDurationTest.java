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
import static org.apromore.apmlog.filter.types.FilterType.EVENT_EVENT_ATTRIBUTE;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttributeArcDurationTest {
    public static void testRetain1(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.ATTRIBUTE_ARC_DURATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        double lowBoundVal = 1000 * 60 * 60 * 24d;
        double upBoundVal = 1000 * 60 * 60 * 36d;

        String lowBoundUnit = "days";
        String upBoundUnit = "days";

        Set<RuleValue> primaryValues = new HashSet<RuleValue>();

        String attrKey = "concept:name";
        String attrVal1 = "A";
        String attrVal2 = "C";

        RuleValue priRuleValue1 = new RuleValue(filterType, OperationType.FROM, attrKey, attrVal1);
        RuleValue priRuleValue2 = new RuleValue(filterType, OperationType.TO, attrKey, attrVal2);

        priRuleValue1.getCustomAttributes().put("base", "true");

        primaryValues.add(priRuleValue1);
        primaryValues.add(priRuleValue2);

        String secRuleKey = attrVal2;

        RuleValue secRuleVal1 = new RuleValue(filterType, OperationType.GREATER_EQUAL,
                secRuleKey, lowBoundVal);
        secRuleVal1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue secRuleVal2 = new RuleValue(filterType, OperationType.LESS_EQUAL,
                secRuleKey, upBoundVal);
        secRuleVal2.getCustomAttributes().put("unit", upBoundUnit);

        Set<RuleValue> secValues = new HashSet<RuleValue>();

        secValues.add(secRuleVal1);
        secValues.add(secRuleVal2);


        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey,
                primaryValues, secValues);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;
        boolean hasC4 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("1")) hasC1 = true;
            if (trace.getCaseId().equals("2")) hasC2 = true;
            if (trace.getCaseId().equals("3")) hasC3 = true;
            if (trace.getCaseId().equals("4")) hasC4 = true;
            System.out.println(trace.getCaseId());
        }

        assert !hasC1;
        assert !hasC2;
        assert !hasC3;
        assert hasC4;
    }

    public static void testRetain2(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.ATTRIBUTE_ARC_DURATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        double lowBoundVal = 1000 * 60 * 60 * 24d * 1.2;
        double upBoundVal = 1000 * 60 * 60 * 24d * 2.2;

        String lowBoundUnit = "days";
        String upBoundUnit = "days";

        Set<RuleValue> primaryValues = new HashSet<RuleValue>();

        String attrKey = "org:resource";
        String attrVal1 = "R1";
        String attrVal2 = "R2";

        RuleValue priRuleValue1 = new RuleValue(filterType, OperationType.FROM, attrKey, attrVal1);
        RuleValue priRuleValue2 = new RuleValue(filterType, OperationType.TO, attrKey, attrVal2);

        priRuleValue1.getCustomAttributes().put("base", "true");

        primaryValues.add(priRuleValue1);
        primaryValues.add(priRuleValue2);

        String secRuleKey = attrVal2;

        RuleValue secRuleVal1 = new RuleValue(filterType, OperationType.GREATER_EQUAL,
                secRuleKey, lowBoundVal);
        secRuleVal1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue secRuleVal2 = new RuleValue(filterType, OperationType.LESS_EQUAL,
                secRuleKey, upBoundVal);
        secRuleVal2.getCustomAttributes().put("unit", upBoundUnit);

        Set<RuleValue> secValues = new HashSet<RuleValue>();

        secValues.add(secRuleVal1);
        secValues.add(secRuleVal2);


        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey,
                primaryValues, secValues);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;
        boolean hasC4 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("1")) hasC1 = true;
            if (trace.getCaseId().equals("2")) hasC2 = true;
            if (trace.getCaseId().equals("3")) hasC3 = true;
            if (trace.getCaseId().equals("4")) hasC4 = true;
            System.out.println(trace.getCaseId());
        }

        assert !hasC1;
        assert hasC2;
        assert !hasC3;
        assert hasC4;
    }

    public static void testRetain3(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.ATTRIBUTE_ARC_DURATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        double lowBoundVal = 1000 * 60 * 60 * 24d;
        double upBoundVal = 1000 * 60 * 60 * 24d * 1.5;

        String lowBoundUnit = "days";
        String upBoundUnit = "days";


        String attrKey = "concept:name";
        String attrVal1 = "A";
        String attrVal2 = "C";

        Set<RuleValue> primaryValues = new HashSet<RuleValue>();

        RuleValue priRuleValue1 = new RuleValue(filterType, OperationType.FROM,
                attrKey, attrVal1);
        RuleValue priRuleValue2 = new RuleValue(filterType, OperationType.TO,
                attrKey, attrVal2);

        priRuleValue2.getCustomAttributes().put("base", "true");

        primaryValues.add(priRuleValue1);
        primaryValues.add(priRuleValue2);

        String secRuleKey = attrVal1;

        RuleValue secRuleVal1 = new RuleValue(filterType, OperationType.GREATER_EQUAL,
                secRuleKey, lowBoundVal);
        secRuleVal1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue secRuleVal2 = new RuleValue(filterType, OperationType.LESS_EQUAL,
                secRuleKey, upBoundVal);
        secRuleVal2.getCustomAttributes().put("unit", upBoundUnit);

        Set<RuleValue> secValues = new HashSet<RuleValue>();

        secValues.add(secRuleVal1);
        secValues.add(secRuleVal2);


        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, secValues);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;
        boolean hasC4 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("1")) hasC1 = true;
            if (trace.getCaseId().equals("2")) hasC2 = true;
            if (trace.getCaseId().equals("3")) hasC3 = true;
            if (trace.getCaseId().equals("4")) hasC4 = true;
            System.out.println(trace.getCaseId());
        }

        assert !hasC1;
        assert !hasC2;
        assert !hasC3;
        assert hasC4;
    }

    /**
     * When the prior filter rule leads the Filter to remove the medium event/activity from the trace
     * (i.e. the 'b' of {'a', 'b', 'c'}),
     * if the next filter rule is the Arc duration filter, it should compute the arc duration between the rest
     * activities (i.e. 'a' and 'c') without considering the medium activity - 'b' which is no longer exist.
     * @param originalLog
     */
    public static void testSequencialFiltering01(APMLog originalLog) throws EmptyInputException {

        /** First rule **/
        Set<String> r1V = new HashSet<>(Collections.singletonList("b"));
        RuleValue rv1 = new RuleValue(EVENT_EVENT_ATTRIBUTE, OperationType.EQUAL, "concept:name", r1V);
        Set<RuleValue> primaryValues = new HashSet<>();
        primaryValues.add(rv1);
        LogFilterRule rule1 = new LogFilterRuleImpl(Choice.REMOVE, Inclusion.ANY_VALUE, Section.EVENT,
                FilterType.EVENT_EVENT_ATTRIBUTE, "concept:name", primaryValues, null);


        /** Second rule **/
        FilterType filterType = FilterType.ATTRIBUTE_ARC_DURATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        double lowBoundVal = 0;
        double upBoundVal = 1000 * 60 * 12;

        String lowBoundUnit = "minutes";
        String upBoundUnit = "minutes";

        Set<RuleValue> pvSet2 = new HashSet<>();

        String attrKey = "concept:name";
        String attrVal1 = "a";
        String attrVal2 = "c";

        RuleValue priRuleValue1 = new RuleValue(filterType, OperationType.FROM, attrKey, attrVal1);
        RuleValue priRuleValue2 = new RuleValue(filterType, OperationType.TO, attrKey, attrVal2);

        priRuleValue1.getCustomAttributes().put("base", "true");

        pvSet2.add(priRuleValue1);
        pvSet2.add(priRuleValue2);

        String secRuleKey = attrVal2;

        RuleValue secRuleVal1 = new RuleValue(filterType, OperationType.GREATER_EQUAL,
                secRuleKey, lowBoundVal);
        secRuleVal1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue secRuleVal2 = new RuleValue(filterType, OperationType.LESS_EQUAL,
                secRuleKey, upBoundVal);
        secRuleVal2.getCustomAttributes().put("unit", upBoundUnit);

        Set<RuleValue> secValues = new HashSet<>();

        secValues.add(secRuleVal1);
        secValues.add(secRuleVal2);

        LogFilterRule rule2 = new LogFilterRuleImpl(choice, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey,
                pvSet2, secValues);

        List<LogFilterRule> rules = Arrays.asList(rule1, rule2);

        /** Filter by first rule **/
        APMLogFilter apmLogFilter = new APMLogFilter(originalLog);
        apmLogFilter.filter(rules);
        APMLog filteredLog = apmLogFilter.getAPMLog();

        assertEquals("c1", filteredLog.getTraces().get(0).getCaseId());
    }
}
