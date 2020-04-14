package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;


public class EventTimeFilter {

    public static boolean toKeep(AEvent event, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(event, logFilterRule);
            default: return !conformRule(event, logFilterRule);
        }
    }

    private static boolean conformRule(AEvent event, LogFilterRule logFilterRule) {

        if (event.getLifecycle().equals("")) return false;

        long eventEpochMilli = event.getTimestampMilli();

        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        return withinTimeRange(fromTime, toTime, eventEpochMilli);

    }

    private static boolean withinTimeRange(long fromTime, long toTime, long eventEpochMilli) {
        return eventEpochMilli >= fromTime && eventEpochMilli <= toTime;
    }
}
