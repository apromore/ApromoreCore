package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.List;
import java.util.Set;

public class PathFilter {
    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {

        FilterType filterType = logFilterRule.getFilterType();

        String attributeKey = logFilterRule.getKey();
        String fromVal = "", toVal = "";
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.FROM) fromVal = ruleValue.getStringValue();
            if (operationType == OperationType.TO) toVal = ruleValue.getStringValue();
        }

        List<AEvent> eventList = trace.getEventList();

        for (int i = 0; i < eventList.size(); i++) {
            AEvent event = eventList.get(i);

            String attrVal = "";
            switch (attributeKey) {
                case "concept:name": attrVal = event.getName(); break;
                case "org:resource": attrVal = event.getResource(); break;
                case "lifecycle:transition": attrVal = event.getLifecycle(); break;
                default:
                    if (event.getAttributeMap().keySet().contains(attributeKey)) {
                        attrVal = event.getAttributeMap().get(attributeKey);
                    }
                    break;
            }

            if (attrVal.equals(fromVal) && event.getLifecycle().equals("complete")) {
//            if (attrVal.equals(fromVal)) {
                boolean conform;
                if (filterType == FilterType.DIRECT_FOLLOW) {
                    conform = conformDirectFollowVal(trace, i, attributeKey, toVal, logFilterRule);
                } else {
                    conform = conformEventualFollowVal(trace, i, attributeKey, toVal, logFilterRule);
                }
                if (conform){
                    return true; // if not conform, continus the loop
                }
            }
        }

        return false;
    }

    private static boolean conformDirectFollowVal(LaTrace trace, int fromIndex, String attributeKey, String followedVal,
                                                  LogFilterRule logFilterRule) {

        List<AEvent> eventList = trace.getEventList();

        if (fromIndex < eventList.size()-1) {
            AEvent nextEvent = eventList.get(fromIndex + 1);
            String nextAttrVal = "";

            switch (attributeKey) {
                case "concept:name": nextAttrVal = nextEvent.getName(); break;
                case "org:resource": nextAttrVal = nextEvent.getResource(); break;
                case "lifecycle:transition": nextAttrVal = nextEvent.getLifecycle(); break;
                default:
                    if (nextEvent.getAttributeMap().keySet().contains(attributeKey)) {
                        nextAttrVal = nextEvent.getAttributeMap().get(attributeKey);
                    }
                    break;
            }

            if (nextAttrVal.equals(followedVal)) {
                return conformRequirement(trace.getEventList().get(fromIndex),
                        nextEvent, logFilterRule);
            } else {
//                System.out.println(nextAttrVal + "!=" + followedVal);
            }
        }
        return false;
    }

    private static boolean conformEventualFollowVal(LaTrace trace,
                                                    int fromIndex,
                                                    String attributeKey,
                                                    String followedVal,
                                                    LogFilterRule logFilterRule) {

        List<AEvent> eventList = trace.getEventList();

        if (fromIndex < eventList.size()-1) {
            for (int i = fromIndex + 1; i < eventList.size(); i++) {
                AEvent nextEvent = eventList.get(i);
                String nextAttrVal = "";
                switch (attributeKey) {
                    case "concept:name": nextAttrVal = nextEvent.getName(); break;
                    case "org:resource": nextAttrVal = nextEvent.getResource(); break;
                    case "lifecycle:transition": nextAttrVal = nextEvent.getLifecycle(); break;
                    default:
                        if (nextEvent.getAttributeMap().keySet().contains(attributeKey)) {
                            nextAttrVal = nextEvent.getAttributeMap().get(attributeKey);
                        }
                        break;
                }


                if (nextAttrVal.equals(followedVal)) {
                    return conformRequirement(trace.getEventList().get(fromIndex),
                            nextEvent, logFilterRule);
                }
            }
        }
        return false;
    }

    private static boolean conformRequirement(AEvent event1, AEvent event2, LogFilterRule logFilterRule) {
        Set<RuleValue> secondaryValues = logFilterRule.getSecondaryValues();
        if (secondaryValues != null) {
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
                if (!haveSameAttributeValue(event1, event2, requireAttrKey)) return false;
                else {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!haveIntervalGreater(event1, event2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!haveIntervalGreaterEqual(event1, event2, lowerBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!haveIntervalLess(event1, event2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!haveIntervalLessEqual(event1, event2, upperBoundVal)) return false;
                    }
                }
            }

            if (requireOpt == OperationType.NOT_EQUAL) {
                if (haveSameAttributeValue(event1, event2, requireAttrKey)) return false;
                else {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!haveIntervalGreater(event1, event2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!haveIntervalGreaterEqual(event1, event2, lowerBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!haveIntervalLess(event1, event2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!haveIntervalLessEqual(event1, event2, upperBoundVal)) return false;
                    }
                }
            }

            return true;
        } else {
            return true;
        }
    }


    private static boolean haveSameAttributeValue(AEvent event1, AEvent event2, String attributeKey) {
        switch (attributeKey) {
            case "concept:name":
                if (event1.getName().equals(event2.getName())) return true;
                break;
            case "org:resource":
                if (event1.getResource().equals(event2.getResource())) return true;
                break;
            case "lifecycle:transition":
                if (event1.getLifecycle().equals(event2.getLifecycle())) return true;
                break;
            default:
                if (event1.getAttributeMap().keySet().contains(attributeKey) &&
                        event2.getAttributeMap().keySet().contains(attributeKey)) {
                    String val1 = event1.getAttributeValue(attributeKey);
                    String val2 = event2.getAttributeValue(attributeKey);
                    if (val1.equals(val2)) return true;
                }
                break;
        }

        return false;
    }

    private static boolean haveIntervalGreater(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval > interval) return true;
        else return false;
    }

    private static boolean haveIntervalGreaterEqual(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval >= interval) return true;
        else return false;
    }

    private static boolean haveIntervalLess(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval < interval) return true;
        else return false;
    }

    private static boolean haveIntervalLessEqual(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval <= interval) return true;
        else return false;
    }

}
