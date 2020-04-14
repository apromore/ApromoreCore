package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;


public class CaseTimeDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        FilterType filterType = logFilterRule.getFilterType();
        Inclusion inclusion = logFilterRule.getInclusion();

        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " ";


        if (filterType == FilterType.CASE_TIME) {
            desc += "all cases where timestamp ";
            if (inclusion == Inclusion.ALL_VALUES) {
                desc += "is from " + TimeUtil.convertTimestamp(fromTime);
            } else {
                desc += "intersect from " + TimeUtil.convertTimestamp(fromTime);
            }
            desc += " to " + TimeUtil.convertTimestamp(toTime);
        } else {
            if (filterType == FilterType.STARTTIME) {
                desc += "cases that contain start event in the timestamp range between ";
            } else if (filterType == FilterType.ENDTIME) {
                desc += "cases that contain end event in the timestamp range between ";
            }
            desc += TimeUtil.convertTimestamp(fromTime) + " and ";
            desc += TimeUtil.convertTimestamp(toTime);
        }

        return desc;
    }
}
