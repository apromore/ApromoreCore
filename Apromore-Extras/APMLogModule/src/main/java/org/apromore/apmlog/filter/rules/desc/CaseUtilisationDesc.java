package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.OperationType;

import java.text.DecimalFormat;

public class CaseUtilisationDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) +
                " all cases with a case utilization between ";

        double utilRangeFrom = 0, utilRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) utilRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) utilRangeTo = ruleValue.getDoubleValue();
        }

        DecimalFormat df2 = new DecimalFormat("###############.##");
        String fromString = df2.format(utilRangeFrom) + "%";
        String toString = df2.format(utilRangeTo) + "%";

        desc += fromString + " to " + toString;
        return desc;
    }
}
