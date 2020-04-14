package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaseSectionEventAttributeFilter {

    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();
        Set<String> values = logFilterRule.getPrimaryValuesInString();

        List<AEvent> eventList = trace.getEventList();

        if (logFilterRule.getInclusion() == Inclusion.ALL_VALUES) {

            Set<String> matchedValues = new HashSet<>();

            for (int i = 0; i < eventList.size(); i++) {
                AEvent event = eventList.get(i);
                String matchedVal = getConformdValue(event, attributeKey, values);
                if (matchedVal != null) matchedValues.add(matchedVal);
            }

            if (matchedValues.size() == values.size()) return true;
            else return false;
        } else {
            for (int i = 0; i < eventList.size(); i++) {
                AEvent event = eventList.get(i);
                String matchedVal = getConformdValue(event, attributeKey, values);
                if (matchedVal != null) return true;
            }
            return false;
        }
    }

    private static String getConformdValue(AEvent event, String attributeKey, Set<String> values) {

        switch (attributeKey) {
            case "concept:name":
                if (values.contains(event.getName())) return event.getName();
                break;
            case "org:resource":
                if (values.contains(event.getResource())) return event.getResource();
                break;
            case "lifecycle:transition":
                if (values.contains(event.getLifecycle())) return event.getLifecycle();
                break;
                default:
                    if (!event.getAttributeMap().keySet().contains(attributeKey)) return null;

                    String val = event.getAttributeValue(attributeKey);
                    if (values.contains(val)) return val;

                    break;
        }

        return null;
    }
}