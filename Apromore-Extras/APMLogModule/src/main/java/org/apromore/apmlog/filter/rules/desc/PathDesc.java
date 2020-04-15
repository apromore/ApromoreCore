package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;

import java.util.Set;

public class PathDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) +
                " all cases where their events contain the ";
        FilterType filterType = logFilterRule.getFilterType();
        if (filterType == FilterType.DIRECT_FOLLOW) {
            desc += "Direct-follows relation ";
        } else {
            desc += "Eventually-follows relation ";
        }

        String mainAttrKey = getDisplayAttributeKey(logFilterRule.getKey());

        desc += "of the \"" + mainAttrKey + "\" equal to [" ;

        String fromVal = "", toVal = "";
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.FROM) fromVal = ruleValue.getStringValue();
            if (operationType == OperationType.TO) toVal = ruleValue.getStringValue();
        }

        desc += fromVal + " => " + toVal + "] ";

        Set<RuleValue> secondaryValues = logFilterRule.getSecondaryValues();

        if (logFilterRule.getSecondaryValues() != null) {
            OperationType requireOpt = OperationType.UNKNOWN;
            String requireAttrKey = "";
            OperationType lowerBoundOpt = OperationType.UNKNOWN;
            OperationType upperBoundOpt = OperationType.UNKNOWN;
            long lowerBoundVal = -1;
            long upperBoundVal = -1;
            for (RuleValue ruleValue : secondaryValues) {
                if (ruleValue.getOperationType() == OperationType.EQUAL) {
                    requireOpt = OperationType.EQUAL;
                    requireAttrKey = ruleValue.getKey();
                }
                if (ruleValue.getOperationType() == OperationType.NOT_EQUAL) {
                    requireOpt = OperationType.NOT_EQUAL;
                    requireAttrKey = ruleValue.getKey();
                }
                if (ruleValue.getOperationType() == OperationType.GREATER) {
                    lowerBoundOpt = OperationType.GREATER;
                    lowerBoundVal = ruleValue.getLongValue();
                }
                if (ruleValue.getOperationType() == OperationType.GREATER_EQUAL) {
                    lowerBoundOpt = OperationType.GREATER_EQUAL;
                    lowerBoundVal = ruleValue.getLongValue();
                }
                if (ruleValue.getOperationType() == OperationType.LESS) {
                    upperBoundOpt = OperationType.LESS;
                    upperBoundVal = ruleValue.getLongValue();
                }
                if (ruleValue.getOperationType() == OperationType.LESS_EQUAL) {
                    upperBoundOpt = OperationType.LESS_EQUAL;
                    upperBoundVal = ruleValue.getLongValue();
                }
            }

            if (requireOpt == OperationType.EQUAL) {
                desc += "and have the same \"" + getDisplayAttributeKey(requireAttrKey) + "\" ";
            }
            if (requireOpt == OperationType.NOT_EQUAL) {
                desc += "and have the different \"" + getDisplayAttributeKey(requireAttrKey) + "\" ";
            }
            if (lowerBoundOpt != OperationType.UNKNOWN || upperBoundOpt != OperationType.UNKNOWN) {
                desc += "and time interval ";
            }
            if (lowerBoundOpt == OperationType.GREATER) {
                desc += "is greater than " + TimeUtil.durationStringOf(lowerBoundVal) + " ";
                if (upperBoundOpt != OperationType.UNKNOWN) desc += "and ";
            }
            if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                desc += "is at least " + TimeUtil.durationStringOf(lowerBoundVal) + " ";
                if (upperBoundOpt != OperationType.UNKNOWN) desc += "and ";
            }
            if (upperBoundOpt == OperationType.LESS) {
                desc += "is less than " + TimeUtil.durationStringOf(upperBoundVal) + " ";
            }
            if (upperBoundOpt == OperationType.LESS_EQUAL) {
                desc += "is up to " + TimeUtil.durationStringOf(upperBoundVal) + " ";
            }
        }

        return desc;
    }

    private static String getDisplayAttributeKey(String attributeKey) {
        switch (attributeKey) {
            case "concept:name": return "Activity";
            case "org:resource": return "Resource";
            case "org:group": return "Resource group";
            case "org:role": return "Role";
            case "lifecycle:transition": return "Status";
            default: return attributeKey;
        }
    }
}
