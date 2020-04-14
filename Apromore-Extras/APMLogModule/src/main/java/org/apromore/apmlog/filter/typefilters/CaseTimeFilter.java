package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.List;


public class CaseTimeFilter {
    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {
        long traceStartTime = trace.getStartTimeMilli(), traceEndTime = trace.getEndTimeMilli();


        FilterType filterType = logFilterRule.getFilterType();
        Inclusion inclusion = logFilterRule.getInclusion();
        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        switch (filterType) {
            case CASE_TIME:
                switch (inclusion) {
                    case ALL_VALUES:
                        return withinTimeRange(fromTime, toTime, traceStartTime, traceEndTime);
                    case ANY_VALUE:
                        return intersectTimeRange(fromTime, toTime, trace);
                        default: return false;
                }
            case STARTTIME:
                return startInTimeRange(fromTime, toTime, traceStartTime);
            case ENDTIME:
                return endInTimeRange(fromTime, toTime, traceEndTime);
                default: return false;
        }
    }

    private static boolean withinTimeRange(long fromTime, long toTime, long traceStartTime, long traceEndTime) {
        return traceStartTime >= fromTime && traceEndTime <= toTime;
    }

    private static boolean intersectTimeRange(long fromTime, long toTime, LaTrace trace) {
        List<AEvent> eventList = trace.getEventList();
        for (int i = 0; i < eventList.size(); i++) {
            AEvent event = eventList.get(i);
            long time = event.getTimestampMilli();
            if (time >= fromTime && time <= toTime) return true;
        }
        return false;
    }

    private static boolean startInTimeRange(long fromTime, long toTime, long traceStartTime) {
        return traceStartTime >= fromTime && traceStartTime <= toTime;
    }

    private static boolean endInTimeRange(long fromTime, long toTime, long traceEndTime) {
        return traceEndTime >= fromTime && traceEndTime <= toTime;
    }
}
