package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;

import java.util.Set;

public class EventSectionAttributeFilter {

    public static boolean toKeep(AEvent event, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(event, logFilterRule);
            default: return !conformRule(event, logFilterRule);
        }
    }

    private static boolean conformRule(AEvent event, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey().toLowerCase();
        Set<String> values = logFilterRule.getPrimaryValuesInString();

        switch (attributeKey) {
            case "concept:name":
                if (values.contains(event.getName())) return true;
                break;
            case "org:resource":
                if (values.contains(event.getResource())) return true;
                break;
            case "lifecycle:transition":
                if (values.contains(event.getLifecycle())) return true;
                break;
            default:
                if (!event.getAttributeMap().keySet().contains(attributeKey)) return false;

                String val = event.getAttributeValue(attributeKey);
                if (values.contains(val)) return true;

                break;
        }

        return false;
    }
}
