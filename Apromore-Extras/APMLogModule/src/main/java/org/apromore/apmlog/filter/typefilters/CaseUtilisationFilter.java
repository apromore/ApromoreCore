package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;

public class CaseUtilisationFilter {

    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {
        double traceUtilVal = trace.getCaseUtilization();

        double utilRangeFrom = 0, utilRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) utilRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) utilRangeTo = ruleValue.getDoubleValue();
        }

        return traceUtilVal >= utilRangeFrom && traceUtilVal <= utilRangeTo;
    }


}
