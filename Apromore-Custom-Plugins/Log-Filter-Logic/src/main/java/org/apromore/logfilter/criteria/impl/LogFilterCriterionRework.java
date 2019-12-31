/*
 * Copyright © 2020 Apromore, The University of Melbourne.
 *
 * This software is provided by Apromore. All rights reserved.
 *
 * author: Chii Chang
 *
 */

package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LogFilterCriterionRework extends AbstractLogFilterCriterion {

    private UnifiedSet<String> valueNameSet;
    private UnifiedMap<String, Integer> greaterMap, greaterEqualMap, lessMap, lessEqualMap;

    public LogFilterCriterionRework(Action action, Containment containment, Level level, String label,
                                    String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);

        valueNameSet = new UnifiedSet<>();
        greaterMap = new UnifiedMap<>();
        greaterEqualMap = new UnifiedMap<>();
        lessMap = new UnifiedMap<>();
        lessEqualMap = new UnifiedMap<>();
        for (String s : value) {
            String attrValueName = "";
            if (!s.contains("@")) attrValueName = s;
            else {
                attrValueName = s.substring(0, s.indexOf("@"));
                String boundString = s.substring(s.indexOf("@"));
                setBoundValues(attrValueName, boundString);
            }
            valueNameSet.add(attrValueName);
        }
    }

    private void setBoundValues(String valueName, String s) {
        String lowerBoundValueString = "1", upperBoundValueString = "1";

        if (s.contains("@>")) {
            // has lower bound
            if (s.contains("@>=") && s.contains("@<")) {
                lowerBoundValueString = s.substring(3, s.indexOf("@<"));
            } else if (s.contains("@>=") && !s.contains("@<")) {
                lowerBoundValueString = s.substring(3);
            } else {
                if (s.contains("@<")) {
                    lowerBoundValueString = s.substring(2, s.indexOf("@<"));
                } else {
                    lowerBoundValueString = s.substring(2);
                }
            }
        }

        if (s.contains("@<")) {
            // has upper bound
            if (s.contains("@<=") && s.contains("@>")) {
                upperBoundValueString = s.substring(s.indexOf("@<=") + 3);
            } else if (s.contains("@<=") && !s.contains("@>")) {
                upperBoundValueString = s.substring(3);
            } else {
                upperBoundValueString = s.substring(s.indexOf("@<") + 2);
            }
        }

        int lbValue = new Integer(lowerBoundValueString);
        int ubValue = new Integer(upperBoundValueString);

        if (s.contains("@>")) {
            if (s.contains("@>=")) greaterEqualMap.put(valueName, lbValue);
            else greaterMap.put(valueName, lbValue);
        }

        if (s.contains("@<")) {
            if (s.contains("@<=")) lessEqualMap.put(valueName, ubValue);
            else lessMap.put(valueName, ubValue);
        }
    }

    @Override
    protected boolean matchesCriterion(XTrace trace) {

        UnifiedMap<String, Boolean> matchMap = new UnifiedMap<>();

        UnifiedSet<String> matchedTraceActNames = new UnifiedSet<>();

        for (String s : valueNameSet) {
            matchMap.put(s, true);
        }

        UnifiedMap<String, Integer> actOccurMap = new UnifiedMap<>();

        List<String> activities = getActivityNames(trace);

        for (int i=0; i < activities.size(); i++) {
            String actName = activities.get(i);
            if (valueNameSet.contains(actName)) {
                matchedTraceActNames.add(actName);
                if (actOccurMap.containsKey(actName)) {
                    int occurCount = actOccurMap.get(actName) + 1;
                    actOccurMap.put(actName, occurCount);
                } else {
                    actOccurMap.put(actName, 1);
                }
            }
        }

        if (matchedTraceActNames.size() != valueNameSet.size()) return false;

//        System.out.println(actOccurMap);

        if (actOccurMap.size() > 0) {
            for (String actName : actOccurMap.keySet()) {
                int occur = actOccurMap.get(actName);
                if (lessMap.containsKey(actName)) {
                    if (occur >= lessMap.get(actName)) return false;
                }
                if (lessEqualMap.containsKey(actName)) {
                    if (occur > lessEqualMap.get(actName)) return false;
                }
                if (greaterMap.containsKey(actName)) {
                    if (occur <= greaterMap.get(actName)) return false;
                }
                if (greaterEqualMap.containsKey(actName)) {
                    if (occur < greaterEqualMap.get(actName)) return false;
                }
            }
        }

        return true;
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false;
    }

    @Override
    public String getAttribute() {
        return "rework:repetition";
    }

    @Override
    public String toString() {
        String displayString = super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " all traces where their activities contain [";// + value.toString();

        int count = 1;

        for (String valueName : valueNameSet) {
            boolean hasGreaterCondition = false;
            boolean hasLessCondition = false;

            displayString += valueName;
            if (greaterMap.containsKey(valueName)) {
                displayString += " (occur more than " + greaterMap.get(valueName) + " times";
                hasGreaterCondition = true;
            }
            if (greaterEqualMap.containsKey(valueName)) {
                displayString += " (occur at least " + greaterEqualMap.get(valueName) + " times";
                hasGreaterCondition = true;
            }
            if (lessMap.containsKey(valueName)) {
                if (hasGreaterCondition) displayString += " and less than ";
                else displayString += " (occur less than ";
                displayString += lessMap.get(valueName) + " times";
                hasLessCondition = true;
            }
            if (lessEqualMap.containsKey(valueName)) {
                if (hasGreaterCondition) displayString += " and no more than ";
                else displayString += " (occur no more than ";
                displayString += lessEqualMap.get(valueName) + " times";
                hasLessCondition = true;
            }


            if (count < valueNameSet.size()) {
                if(hasGreaterCondition || hasLessCondition) displayString += "), ";
                else displayString += ", ";

            } else {
                if(hasGreaterCondition || hasLessCondition) displayString += ")";
            }

            count +=1;
        }

        displayString += "]";
        return displayString;
    }

    private List<String> getActivityNames(XTrace xTrace) {
        List<String> activityNameList = new ArrayList<>();

        UnifiedSet<XEvent> markedXEvent = new UnifiedSet<>();

        for(int i=0; i<xTrace.size(); i++) {
            XEvent xEvent = xTrace.get(i);
            if (xEvent.getAttributes().containsKey("concept:name")) {
                String eventName = xEvent.getAttributes().get("concept:name").toString();

                if (xEvent.getAttributes().containsKey("lifecycle:transition")) {
                    String lifecycle = xEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase();

                    if (lifecycle.equals("start")) {
                        markedXEvent.put(xEvent);
                        if((i+1) <= (xTrace.size()-1)) {
                            for(int j=(i+1); j < xTrace.size(); j++) {
                                XEvent jEvent = xTrace.get(j);
                                if (jEvent.getAttributes().containsKey("concept:name")) {
                                    String jEventName = jEvent.getAttributes().get("concept:name").toString();
                                    if (jEventName.equals(eventName)) {
                                        if (jEvent.getAttributes().containsKey("lifecycle:transition")) {
                                            String jLifecycle = jEvent.getAttributes().get("lifecycle:transition").toString();
                                            if (jLifecycle.toLowerCase().equals("complete")) {
                                                activityNameList.add(eventName);
                                                markedXEvent.add(jEvent);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if ( !markedXEvent.contains(xEvent) && lifecycle.equals("complete")) {
                        activityNameList.add(eventName);
                        markedXEvent.add(xEvent);
                    }
                }

            }
        }

        return activityNameList;
    }
}
