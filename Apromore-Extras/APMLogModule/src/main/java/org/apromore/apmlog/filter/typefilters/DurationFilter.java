package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;

public class DurationFilter {

    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {


        long durRangeFrom = 0, durRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getLongValue();
        }


        FilterType filterType = logFilterRule.getFilterType();

        long dur = 0;

        switch (filterType) {
            case DURATION:
                dur = trace.getDuration(); break;
            case MAX_PROCESSING_TIME:
                dur = trace.getMaxProcessingTime(); break;
            case AVERAGE_PROCESSING_TIME:
                dur = trace.getAverageProcessingTime(); break;
            case TOTAL_PROCESSING_TIME:
                dur = trace.getTotalProcessingTime(); break;
            case TOTAL_WAITING_TIME:
                dur = trace.getTotalWaitingTime(); break;
            case AVERAGE_WAITING_TIME:
                dur = trace.getAverageWaitingTime(); break;
            case MAX_WAITING_TIME:
                dur = trace.getMaxWaitingTime(); break;
                default: break;
        }

        return dur > durRangeFrom && dur <= durRangeTo;
    }


}
