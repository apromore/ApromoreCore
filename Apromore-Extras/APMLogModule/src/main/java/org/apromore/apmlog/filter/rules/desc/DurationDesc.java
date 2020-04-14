package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;


public class DurationDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        long fromDur = 0, toDur = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromDur = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toDur = ruleValue.getLongValue();
        }

        FilterType filterType = logFilterRule.getFilterType();

        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases where all events have ";

        switch (filterType) {
            case DURATION:
                desc += "duration range equal to ["; break;
            case MAX_PROCESSING_TIME:
                desc += "max processing time between ["; break;
            case AVERAGE_PROCESSING_TIME:
                desc += "average processing time between ["; break;
            case TOTAL_PROCESSING_TIME:
                desc += "total processing time between ["; break;
            case TOTAL_WAITING_TIME:
                desc += "total waiting time between ["; break;
            case AVERAGE_WAITING_TIME:
                desc += "average waiting time between ["; break;
            case MAX_WAITING_TIME:
                desc += "max waiting time between ["; break;
            default: break;
        }

        desc += TimeUtil.durationStringOf(fromDur) + " AND " + TimeUtil.durationStringOf(toDur) + "]";


        return desc;
    }
}
