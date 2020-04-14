package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Inclusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CaseSectionEventAttributeDesc {

    public static String getDescription(LogFilterRule logFilterRule) {

        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases containing events where ";

        String attributeKey = logFilterRule.getKey();

        desc += "'" + getDisplayAttributeKey(attributeKey) + "' equal to [";

        Set<RuleValue> ruleValues = logFilterRule.getPrimaryValues();
        List<RuleValue> ruleValueList = new ArrayList<RuleValue>(ruleValues);
        Collections.sort(ruleValueList);

        for (int i = 0; i < ruleValueList.size(); i++) {
            desc += ruleValueList.get(i).getStringValue();
            if (i < ruleValueList.size() -1) {
                if (logFilterRule.getInclusion() == Inclusion.ANY_VALUE) desc += " OR ";
                else desc += " AND ";
            }
        }

        desc += "]";

        return desc;
    }

    private static String getDisplayAttributeKey(String attributeKey) {
        switch (attributeKey) {
            case "concept:name": return "Activity";
            case "org:resource": return "Resource";
            case "org:group": return "Group";
            case "org:role": return "Role";
            case "lifecycle:transition": return "Status";
            default: return attributeKey;
        }
    }
}