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

public class DirectFollowFilterTest {

    public static void runTest1(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException {
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

//        System.out.println("\n\ntrace size = " + traceList.size() + "\n\n");

        for (ATrace trace : traceList) {
//            System.out.println(trace.getCaseId());
            if (trace.getCaseId().equals("c1")) hasC1 = true;
            if (trace.getCaseId().equals("c2")) hasC2 = true;
        }



        if (!hasC1 || !hasC2) {
            String message = "TEST FAILED. RESULT TRACE LIST MISMATCH.";
            throw new AssertionError(message);
        } else {
            parent.printString("'Direct Follow' Test 1 PASS.");
        }
    }

    public static void runTest2(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException {
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

//        System.out.println("\n\ntrace size = " + traceList.size() + "\n\n");

        for (ATrace trace : traceList) {
//            System.out.println(trace.getCaseId());
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
            parent.printString("'Direct Follow' Test 2 PASS.");
        }
    }
}
