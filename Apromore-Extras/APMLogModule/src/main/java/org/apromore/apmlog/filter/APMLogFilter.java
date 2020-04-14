/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.apmlog.filter;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.typefilters.*;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Section;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This class handles log filtering mechanisms for APMLog.
 * It creates PLog (pointer log) of APMLog for performing filtering without modifying the original APMLog.
 * @author Chii Chang
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (12/03/2020)
 * Modified: Chii Chang (10/04/2020)
 */
public class APMLogFilter {

    private APMLog apmLogNotForChange;
    private PLog pLog;

    private static final Logger LOGGER = LoggerFactory.getLogger(APMLogFilter.class);

    public APMLogFilter(APMLog apmLog) {
        this.apmLogNotForChange = apmLog;
        LOGGER.info("Create new PLog");
        this.pLog = new PLog(apmLog);
        LOGGER.info("Create new PLog complete");
//        System.out.println("");
    }

    public PLog getPLog() {
        return pLog;
    }


    APMLog finalAPMLog;

    public APMLog getFinalAPMLog() {
        return this.finalAPMLog;
    }

    public APMLog getApmLog() {
        finalAPMLog = this.pLog.toAPMLog();
        return finalAPMLog;
    }

    public void reset() {
        this.pLog.reset();
    }

    public void initPrevious() {
        // Set previous values
        this.pLog.previousValidTraceIndexBS = new BitSet(this.pLog.getValidTraceIndexBS().size());
        for(int i=0; i < this.pLog.getValidTraceIndexBS().size(); i++) {
            this.pLog.previousValidTraceIndexBS.set(i, this.pLog.getValidTraceIndexBS().get(i));
        }
        this.pLog.previousPTraceList = new ArrayList<>();
        for(int i=0; i<this.pLog.getPTraceList().size(); i++) {
            this.pLog.previousPTraceList.add(this.pLog.getPTraceList().get(i));
        }
        this.pLog.previousCaseVariantSize = this.pLog.getCaseVariantSize();
        this.pLog.previousEventSize = this.pLog.getEventSize();
        this.pLog.previousMinDuration = this.pLog.getMinDuration();
        this.pLog.previousMaxDuration = this.pLog.getMaxDuration();
        this.pLog.previousMedianDuration = this.pLog.getMedianDuration();
        this.pLog.previousAverageDuration = this.pLog.getAverageDuration();
        this.pLog.previousStartTime = this.pLog.getStartTime();
        this.pLog.previousEndTime = this.pLog.getEndTime();
        this.pLog.previousVariantIdFreqMap = this.pLog.getVariantIdFreqMap();
    }

    public void resetPrevious() {
        this.pLog.resetPrevious();
    }

    public void updatePrevious() {
        this.pLog.updatePrevious();
    }


    public void filter(List<LogFilterRule> logFilterRuleList) {

        List<PTrace> filteredPTraceList = new ArrayList<>();

        for (int i = 0; i < pLog.size(); i++) {

            PTrace pTrace = pLog.get(i);

            PTrace filteredPTrace = getFilteredPTrace(pTrace, logFilterRuleList);

            if (filteredPTrace != null) {
                if (filteredPTrace.getEventSize() > 0) filteredPTraceList.add(filteredPTrace);
            }
        }

        updatePLogStats(filteredPTraceList);
    }

    private PTrace getFilteredPTrace(PTrace pTrace, List<LogFilterRule> logFilterRules) {

        PTrace filteredTrace = pTrace;

        for (int i = 0; i < logFilterRules.size(); i++) {
            LogFilterRule logFilterRule = logFilterRules.get(i);

            Section section = logFilterRule.getSection();

            if (section == Section.CASE) {
                boolean keepTrace = toKeep(pTrace, logFilterRule);
                if (keepTrace) {
                    filteredTrace = pTrace;
                } else {
                    filteredTrace = null;
                    break;
                }
            } else { //Event section

                BitSet validEventBS = pTrace.getValidEventIndexBitSet();

                List<AEvent> eventList = pTrace.getEventList();
                for (int j = 0; j < eventList.size(); j++) {
                    if (validEventBS.get(j)) {
                        AEvent event = eventList.get(j);
                        if (!toKeep(event, logFilterRule)) {
                            validEventBS.set(j, false);
                        }
                    }
                }

                if (validEventBS.cardinality() == 0) {
                    filteredTrace = null;
                    break;
                } else {
                    pTrace.setValidEventIndexBS(validEventBS);
                    filteredTrace = pTrace;
                }
            }
        }

        if (filteredTrace!= null) {
            if (filteredTrace.getValidEventIndexBitSet().cardinality() == 0) {
                filteredTrace = null;
            } else {
                filteredTrace.update();
            }
        }

        return filteredTrace;
    }

    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        FilterType filterType = logFilterRule.getFilterType();
        switch (filterType) {
            case CASE_VARIANT:
            case CASE_ID:
            case CASE_CASE_ATTRIBUTE:
                return CaseSectionCaseAttributeFilter.toKeep(trace, logFilterRule);
            case CASE_EVENT_ATTRIBUTE:
                return CaseSectionEventAttributeFilter.toKeep(trace, logFilterRule);
            case CASE_TIME:
            case STARTTIME:
            case ENDTIME:
                return CaseTimeFilter.toKeep(trace, logFilterRule);
            case DURATION:
            case AVERAGE_PROCESSING_TIME:
            case MAX_PROCESSING_TIME:
            case TOTAL_PROCESSING_TIME:
            case AVERAGE_WAITING_TIME:
            case MAX_WAITING_TIME:
            case TOTAL_WAITING_TIME:
                return DurationFilter.toKeep(trace, logFilterRule);
            case CASE_UTILISATION:
                return CaseUtilisationFilter.toKeep(trace, logFilterRule);
            case DIRECT_FOLLOW:
            case EVENTUAL_FOLLOW:
                return PathFilter.toKeep(trace, logFilterRule);
            case REWORK_REPETITION:
                return ReworkFilter.toKeep(trace, logFilterRule);
            default:
                return false;
        }
    }

    public static boolean toKeep(AEvent event, LogFilterRule logFilterRule) {
        FilterType filterType = logFilterRule.getFilterType();
        switch (filterType) {
            case EVENT_EVENT_ATTRIBUTE:
                return EventSectionAttributeFilter.toKeep(event, logFilterRule);
            case EVENT_TIME:
                return EventTimeFilter.toKeep(event, logFilterRule);
            default:
                return false;
        }
    }

    private void updatePLogStats(List<PTrace> pTraceList) {
        this.pLog.setPTraceList(pTraceList);
        this.pLog.updateActivityOccurMaxMap();

        this.pLog.setMinDuration(0);
        this.pLog.setMaxDuration(0);
        this.pLog.setStartTime(-1);
        this.pLog.setEndTime(-1);

        UnifiedSet<Integer> variSet = new UnifiedSet<>();
        for(PTrace pTrace : pTraceList) {
            long dur = pTrace.getDuration();
            long st = pTrace.getStartTimeMilli();
            long et = pTrace.getEndTimeMilli();

            if(this.pLog.getMinDuration() == 0 || dur < this.pLog.getMinDuration()) this.pLog.setMinDuration(dur);
            if(this.pLog.getMaxDuration() == 0 || dur > this.pLog.getMaxDuration()) this.pLog.setMaxDuration(dur);
            if(this.pLog.getStartTime() == -1 || st < this.pLog.getStartTime()) this.pLog.setStartTime(st);
            if(this.pLog.getEndTime() == -1 || et > this.pLog.getEndTime()) this.pLog.setEndTime(et);

            if(!variSet.contains(pTrace.getCaseVariantId())) {
                variSet.put(pTrace.getCaseVariantId());
            }
        }

        this.pLog.setCaseVariantSize(variSet.size());

        int newEventSize = 0;

        for(int i=0; i < this.pLog.getPTraceList().size(); i++) {
            newEventSize += this.pLog.getPTraceList().get(i).getEventSize();
        }

        this.pLog.setEventSize(newEventSize);


        resetDuration();
        updateCaseVariants();
    }



//    public void filter(List<LogFilterRule> criteria) {
//
//        List<PTrace> originalPTraceList = this.pLog.getOriginalPTraceList();
//
//        //Reset
//        for(int i=0; i< originalPTraceList.size(); i++) {
//            originalPTraceList.get(i).reset();
//            this.pLog.getValidTraceIndexBS().set(i, true);
//        }
//
//        List<Integer> removeIndexList = new ArrayList<>();
//
//        if(criteria != null) {
//
//            for(int i=0; i<criteria.size(); i++) {
//
//                LogFilterRule filterRule = criteria.get(i);
//
//                switch (filterRule.getSection()) {
//                    case EVENT:
//                        for(int j=0; j<originalPTraceList.size(); j++) {
//                            PTrace pTrace = originalPTraceList.get(j);
//
//                            BitSet validEventIndex = pTrace.getValidEventIndexBitSet();
//
//                            List<AEvent> originalEventList = pTrace.getOriginalEventList();
//
//                            for (int k = 0; k < originalEventList.size(); k++) {
//                                AEvent aEvent = originalEventList.get(k);
//                                if (detach(filterRule, aEvent)) {
//                                    validEventIndex.set(k, false);
//                                }
//                            }
//                            if (validEventIndex.cardinality() == 0) {
//                                removeIndexList.add(j);
//                            } else {
//                                pTrace.update();
//                            }
//                        }
//                        break;
//                    default: // CASE
//                        for(int j=0; j<originalPTraceList.size(); j++) {
//                            PTrace pTrace = originalPTraceList.get(j);
//                            if(detach(filterRule, pTrace)) {
//                                this.pLog.getValidTraceIndexBS().set(j, false);
//                                removeIndexList.add(j);
//                            }
//                        }
//
//                        break;
//                }
//            }
//        }
//
//
//        List<PTrace> pTraceList = new ArrayList<>();
//        for(int i=0; i<originalPTraceList.size(); i++) {
//            if(this.pLog.getValidTraceIndexBS().get(i) == true) {
//                PTrace pTrace = originalPTraceList.get(i);
//                if(pTrace.getEventList().size() > 0) {
//                    pTraceList.add(pTrace);
//                }
//            }
//        }
//
//
//        this.pLog.setPTraceList(pTraceList);
//        this.pLog.updateActivityOccurMaxMap();
//
//        this.pLog.setMinDuration(0);
//        this.pLog.setMaxDuration(0);
//        this.pLog.setStartTime(-1);
//        this.pLog.setEndTime(-1);
//
//        UnifiedSet<Integer> variSet = new UnifiedSet<>();
//        for(PTrace pTrace : pTraceList) {
//            long dur = pTrace.getDuration();
//            long st = pTrace.getStartTimeMilli();
//            long et = pTrace.getEndTimeMilli();
//
//            if(this.pLog.getMinDuration() == 0 || dur < this.pLog.getMinDuration()) this.pLog.setMinDuration(dur);
//            if(this.pLog.getMaxDuration() == 0 || dur > this.pLog.getMaxDuration()) this.pLog.setMaxDuration(dur);
//            if(this.pLog.getStartTime() == -1 || st < this.pLog.getStartTime()) this.pLog.setStartTime(st);
//            if(this.pLog.getEndTime() == -1 || et > this.pLog.getEndTime()) this.pLog.setEndTime(et);
//
//            if(!variSet.contains(pTrace.getCaseVariantId())) {
//                variSet.put(pTrace.getCaseVariantId());
//            }
//        }
//
//        this.pLog.setCaseVariantSize(variSet.size());
//
//        int newEventSize = 0;
//
//        for(int i=0; i < this.pLog.getPTraceList().size(); i++) {
//            newEventSize += this.pLog.getPTraceList().get(i).getEventSize();
//        }
//
//        this.pLog.setEventSize(newEventSize);
//
//
//        resetDuration();
//        updateCaseVariants();
//    }

    private void resetDuration() {
        this.pLog.setMinDuration(0);
        this.pLog.setMaxDuration(0);
        for(int i=0; i<this.pLog.getPTraceList().size(); i++) {
            PTrace pTrace = this.pLog.getPTraceList().get(i);
            if(this.pLog.getMinDuration() == 0 || pTrace.getDuration() < this.pLog.getMinDuration()) {
                this.pLog.setMinDuration(pTrace.getDuration());
            }
            if(pTrace.getDuration() > this.pLog.getMaxDuration()) {
                this.pLog.setMaxDuration(pTrace.getDuration());
            }
        }
    }


    private void updateCaseVariants() {

        UnifiedSet<Integer> existVariants = new UnifiedSet<>();
        for(PTrace pTrace : this.pLog.getPTraceList()) {
            existVariants.put(pTrace.getCaseVariantId());
        }

        UnifiedMap<Integer, Integer> variantIdFreqMap = new UnifiedMap<>();
        for(int key : this.pLog.getOriginalVariantIdFreqMap().keySet()) {
            if(existVariants.contains(key)) {
                variantIdFreqMap.put(key, this.pLog.getOriginalVariantIdFreqMap().get(key));
            }
        }

        this.pLog.setVariantIdFreqMap(variantIdFreqMap);
    }

//    private boolean detach(APMLogFilterCriterion criterionEE, AEvent aEvent) {
//        if (criterionEE.retainment == Retainment.REMOVE) return conform(criterionEE, aEvent);
//        else return !conform(criterionEE, aEvent);
//    }
//
//    private boolean conform(APMLogFilterCriterion criterionEE, AEvent aEvent) {
//        switch (criterionEE.codeType) {
//            case TIMEFRAME:
//                long eventTime = aEvent.getTimestampMilli();
//                return eventTime >= criterionEE.fromTime && eventTime <= criterionEE.toTime;
//            case CONCEPT_NAME:
//                for (String s : criterionEE.criterionValues) {
//                    if (aEvent.getName().equals(s)) return true;
//                }
//                break;
//            case ORG_RESOURCE:
//                for (String s : criterionEE.criterionValues) {
//                    if (aEvent.getResource().equals(s)) return true;
//                }
//                break;
//            case LIFECYCLE:
//                for (String s : criterionEE.criterionValues) {
//                    if (aEvent.getLifecycle().toLowerCase().equals(s.toLowerCase())) return true;
//                }
//                break;
//            default:
//                String attrKey = criterionEE.attributeKey;
//                if(aEvent.getAttributeMap().containsKey(attrKey)) {
//                    return criterionEE.criterionValues.contains(aEvent.getAttributeMap().get(attrKey));
//                }else return false;
//        }
//        return false;
//    }
//
//    private boolean detach(APMLogFilterCriterion criterionEE, PTrace pTrace) {
//        if (criterionEE.retainment == Retainment.REMOVE) {
//            return conform(criterionEE, pTrace);
//        } else { // KEEP
//            return !conform(criterionEE, pTrace);
//        }
//    }
//
//    private boolean conform(APMLogFilterCriterion criterionEE, PTrace pTrace) {
//
//        List<AEvent> aEventList = pTrace.getEventList();
//
//        switch(criterionEE.codeType) {
//            case CASE_NAME: return criterionEE.criterionValues.contains(pTrace.getCaseId());
//            case VARIANT: return criterionEE.criterionValues.contains(pTrace.getCaseVariantId() + "");
//            case TIMEFRAME:
//                if(criterionEE.section == Section.CASE) return conformTraceTime(pTrace, criterionEE);
//                else {
//                    for (int i = 0; i < aEventList.size(); i++) {
//                        AEvent aEvent = aEventList.get(i);
//                        if(criterionEE.inclusion == Inclusion.ANY) {
//                            return conformEventTime(aEvent, criterionEE.fromTime, criterionEE.toTime);
//                        } else {
//                            return pTrace.getStartTimeMilli() >= criterionEE.fromTime &&
//                                    pTrace.getEndTimeMilli() <= criterionEE.toTime;
//                        }
//                    }
//                    if (criterionEE.inclusion == Inclusion.ANY) return false;
//                    else return criterionEE.inclusion == Inclusion.ALL;
//                }
//            case START_TIME_RANGE: return withinTimeframe(criterionEE, pTrace.getStartTimeMilli());
//            case END_TIME_RANGE: return withinTimeframe(criterionEE, pTrace.getEndTimeMilli());
//            case DURATION_RANGE: return conformDuration(criterionEE, pTrace, CodeType.DURATION_RANGE);
//            case TOTAL_PROCESSING_TIME:
//                return conformDuration(criterionEE, pTrace, CodeType.TOTAL_PROCESSING_TIME);
//            case AVERAGE_PROCESSING_TIME:
//                return conformDuration(criterionEE, pTrace, CodeType.AVERAGE_PROCESSING_TIME);
//            case MAX_PROCESSING_TIME:
//                return conformDuration(criterionEE, pTrace, CodeType.MAX_PROCESSING_TIME);
//            case TOTAL_WAITING_TIME:
//                return conformDuration(criterionEE, pTrace, CodeType.TOTAL_WAITING_TIME);
//            case AVERAGE_WAITING_TIME:
//                return conformDuration(criterionEE, pTrace, CodeType.AVERAGE_WAITING_TIME);
//            case MAX_WAITING_TIME:
//                return conformDuration(criterionEE, pTrace, CodeType.MAX_WAITING_TIME);
//            case CASE_UTIL:
//                double cu = pTrace.getCaseUtilization();
//                return cu >= criterionEE.utilGreater && cu <= criterionEE.utilLess;
//            case E_FOLLOW:
//            case D_FOLLOW:
//                if (aEventList.size() < 1){
//                    return false;
//                }else {
//                    AEvent sEvent = aEventList.get(0), eEvent = aEventList.get(aEventList.size() - 1);
//                    String s, e;
//                    String attribute = criterionEE.requirementMap.get(ReqType.ATTRIBUTE);
//                    if (attribute == null) attribute = criterionEE.attributeKey;
//                    switch (attribute) {
//                        case "concept:name":
//                            s = sEvent.getName();
//                            e = eEvent.getName();
//                            break;
//                        case "org:resource":
//                            s = sEvent.getResource();
//                            e = eEvent.getResource();
//                            break;
//                        case "lifecycle:transition":
//                            s = sEvent.getLifecycle();
//                            e = eEvent.getLifecycle();
//                            break;
//                        default:
//                            s = sEvent.getAttributeValue(attribute);
//                            e = eEvent.getAttributeValue(attribute);
//                            break;
//                    }
//
//                    if (criterionEE.codeType == CodeType.D_FOLLOW) {
//                        if (criterionEE.criterionValues.contains("[Start] => " + s)) {
//                            return true;
//                        }
//                        if (criterionEE.criterionValues.contains(e + " => [End]")) {
//                            return true;
//                        }
//                    }
//
//                    List<AActivity> ptActList = pTrace.getActivityList();
//
//                    for (int i = 0; i < ptActList.size(); i++) {
//                        AActivity act1 = ptActList.get(i);
//                        String act1Name = act1.getName();
//                        AEvent e1 = act1.getEventList().get(0);
//                        if (criterionEE.codeType == CodeType.D_FOLLOW) {
//                            if ( (i + 1) < ptActList.size()) {
//                                AActivity act2 = ptActList.get(i + 1);
//                                AEvent e2 = act2.getEventList().get(0);
//                                if (conformOptionalRequirement(criterionEE, e1, e2)) {
//                                    return true;
//                                }
//                            }
//                        } else {
//                            for (int j = (i + 1); j < ptActList.size(); j++) {
//                                AActivity act2 = ptActList.get(j);
//                                String act2Name = act2.getName();
//                                if (conformEFollowOptionalRequirement(criterionEE, act1, act2)) {
//                                    return true;
//                                }
//                            }
//                        }
//                    }
//
//
//                    return false;
//                }
//            case REWORK_REPETITION:
//                return conformReworkRepetition(criterionEE, pTrace);
//            case CASE_CASE_ATTR:
//                String caseAttrKey = criterionEE.attributeKey;
//                return conformCaseAttribute(pTrace, caseAttrKey, criterionEE.criterionValues);
//            case CASE_EVENT_ATTR:
//            case ORG_GROUP:
//            case ORG_RESOURCE:
//                String eventAttrKey = criterionEE.attributeKey;
//                int conformCount = 0;
//
//
//                for(int i=0; i<aEventList.size(); i++) {
//
//
//
//                    AEvent aEvent = aEventList.get(i);
//
//                    if (criterionEE.inclusion == Inclusion.ANY) {
//                        for(String v : criterionEE.criterionValues) {
//                            if(conformEventAttribute(aEvent, eventAttrKey, v)) return true;
//                        }
//                    } else { // ALL
//                        for(String v : criterionEE.criterionValues) {
//                            if(conformEventAttribute(aEvent, eventAttrKey, v)) {
//                                conformCount += 1;
//                            }
//                        }
//                    }
//                }
//                if (criterionEE.inclusion == Inclusion.ALL)  return conformCount >= aEventList.size();
//                return false;
//            default:
//                return false;
//        }
//    }
//
//
//    private boolean conformOptionalRequirement(APMLogFilterCriterion criterionEE, AEvent e1, AEvent e2) {
//        String e1V, e2V;
//        String attribute2 = criterionEE.requirementMap.get(ReqType.ATTRIBUTE);
//        if (attribute2 == null) attribute2 = criterionEE.attributeKey;
//        switch (attribute2) {
//            case "concept:name": e1V = e1.getName();  e2V = e2.getName(); break;
//            case "org:resource": e1V = e1.getResource(); e2V = e2.getResource(); break;
//            case "lifecycle:transition": e1V = e1.getLifecycle(); e2V = e2.getLifecycle(); break;
//            default:
//                e1V = e1.getAttributeValue(attribute2);
//                e2V = e2.getAttributeValue(attribute2);
//                break;
//        }
//
//        if (criterionEE.criterionValues.contains(e1V + " => " + e2V)) {
//            if (criterionEE.requirementMap.containsKey(ReqType.SAME_ATTR)) {
//                if (!haveSameAttributeValue( e1, e2, criterionEE.requirementMap.get(
//                        ReqType.SAME_ATTR))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.DIFF_ATTR)) {
//                if (haveSameAttributeValue( e1, e2, criterionEE.requirementMap.get(
//                        ReqType.DIFF_ATTR))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.LESS_TIME)) {
//                if (!intervalLessThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.LESS_TIME))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.LESS_EQUAL_TIME)) {
//                if (!intervalLessEqualThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.LESS_EQUAL_TIME))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.GREATER_TIME)) {
//                if (!intervalGreaterThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.GREATER_TIME))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.GREATER_EQUAL_TIME)) {
//                if (!intervalGreaterEqualThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.GREATER_EQUAL_TIME))) return false;
//            }
//            return true;
//        } else return false;
//    }
//
//    private boolean conformEFollowOptionalRequirement(APMLogFilterCriterion criterionEE,
//                                                      AActivity act1, AActivity act2) {
//        AEvent e1 = act1.getEventList().get(0), e2 = act2.getEventList().get(0);
//        String e1V, e2V;
//        String attribute2 = criterionEE.requirementMap.get(ReqType.ATTRIBUTE);
//        if (attribute2 == null) attribute2 = criterionEE.attributeKey;
//        switch (attribute2) {
//            case "concept:name": e1V = e1.getName();  e2V = e2.getName(); break;
//            case "org:resource": e1V = e1.getResource(); e2V = e2.getResource(); break;
//            case "lifecycle:transition": e1V = e1.getLifecycle(); e2V = e2.getLifecycle(); break;
//            default:
//                e1V = e1.getAttributeValue(attribute2);
//                e2V = e2.getAttributeValue(attribute2);
//                break;
//        }
//
//        if (criterionEE.criterionValues.contains(e1V + " => " + e2V)) {
//            if (criterionEE.requirementMap.containsKey(ReqType.SAME_ATTR)) {
//                if (!haveSameAttributeValue( e1, e2, criterionEE.requirementMap.get(ReqType.SAME_ATTR))) {
//                    return false;
//                }
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.DIFF_ATTR)) {
//                if (haveSameAttributeValue( e1, e2, criterionEE.requirementMap.get(
//                        ReqType.DIFF_ATTR))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.LESS_TIME)) {
//                if (!intervalLessThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.LESS_TIME))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.LESS_EQUAL_TIME)) {
//                if (!intervalLessEqualThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.LESS_EQUAL_TIME))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.GREATER_TIME)) {
//                if (!intervalGreaterThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.GREATER_TIME))) return false;
//            }
//            if (criterionEE.requirementMap.containsKey(ReqType.GREATER_EQUAL_TIME)) {
//                if (!intervalGreaterEqualThan(e1, e2, criterionEE.requirementMap.get(
//                        ReqType.GREATER_EQUAL_TIME))) return false;
//            }
//            return true;
//        } else return false;
//    }
//
//    private boolean conformTraceTime(PTrace pTrace, APMLogFilterCriterion criterionEE) { //2019-10-24
//        long traceST = pTrace.getStartTimeMilli(), traceET = pTrace.getEndTimeMilli();
//
//        if(criterionEE.inclusion == Inclusion.ALL) {
//            return (traceST >= criterionEE.fromTime && traceET <= criterionEE.toTime);
//        } else {
//            if(traceST >= criterionEE.fromTime && traceET <= criterionEE.toTime) return true;
//            if(traceST <= criterionEE.fromTime && traceET >= criterionEE.toTime) return true;
//            if(traceST <= criterionEE.fromTime && traceET >= criterionEE.fromTime) return true;
//            if(traceST <= criterionEE.toTime && traceET >= criterionEE.toTime) return true;
//        }
//        return false;
//    }
//
//    private boolean haveSameAttributeValue(AEvent event1, AEvent event2, String attributeKey) {
//        switch (attributeKey) {
//            case "concept:name": return event1.getName().equals(event2.getName());
//            case "org:resource": return event1.getResource().equals(event2.getResource());
//            case "lifecycle:transition": return event1.getLifecycle().equals(event2.getLifecycle());
//            default:
//                if(event1.getAttributeValue(attributeKey) != null && event2.getAttributeValue(attributeKey) != null) {
//                    return event1.getAttributeValue(attributeKey).equals(event2.getAttributeValue(attributeKey));
//                }
//                return false;
//        }
//
//    }
//
//    private boolean intervalLessThan(AEvent e1, AEvent e2, String intervalString) {
//        long intervalVal = getMillisecond(intervalString);
//        long intervalE1E2 = e2.getTimestampMilli() - e1.getTimestampMilli();
//        return intervalE1E2 < intervalVal;
//    }
//
//    private boolean intervalLessEqualThan(AEvent e1, AEvent e2, String intervalString) {
//        long intervalVal = getMillisecond(intervalString);
//        long intervalE1E2 = e2.getTimestampMilli() - e1.getTimestampMilli();
//        return intervalE1E2 <= intervalVal;
//    }
//
//    private boolean intervalGreaterThan(AEvent e1, AEvent e2, String intervalString) {
//        long intervalVal = getMillisecond(intervalString);
//        long intervalE1E2 = e2.getTimestampMilli() - e1.getTimestampMilli();
//        return intervalE1E2 > intervalVal;
//    }
//
//    private boolean intervalGreaterEqualThan(AEvent e1, AEvent e2, String intervalString) {
//        long intervalVal = getMillisecond(intervalString);
//        long intervalE1E2 = e2.getTimestampMilli() - e1.getTimestampMilli();
//        return intervalE1E2 >= intervalVal;
//    }
//
//    private long getMillisecond(String intervalText) {
//        String timeUnitStr = intervalText.substring(intervalText.indexOf(" ") + 1);
//        String inputStr = intervalText.substring(0, intervalText.indexOf(" "));
//        BigDecimal inVal = convertDecimalTimeInput(inputStr, timeUnitStr);
//        return inVal.longValue();
//    }
//
//    private BigDecimal convertDecimalTimeInput(String inputString, String unit) {
//        BigDecimal bdYear = new BigDecimal(new Long(1000 * 60 * 60 * 24 * 365));
//        BigDecimal bdMonth = new BigDecimal(new Long(1000 * 60 * 60 * 24 * 31));
//        BigDecimal bdWeek = new BigDecimal(new Long(1000 * 60 * 60 * 24 * 7));
//        BigDecimal bdDay = new BigDecimal(new Long(1000 * 60 * 60 * 24));
//        BigDecimal bdHour = new BigDecimal(new Long(1000 * 60 * 60));
//        BigDecimal bdMinute = new BigDecimal(new Long(1000 * 60));
//        BigDecimal bdSecond = new BigDecimal(new Long(1000));
//        BigDecimal inVal = new BigDecimal(inputString);
//        switch (unit) {
//            case "years": return inVal.multiply(bdYear);
//            case "months": return inVal.multiply(bdMonth);
//            case "weeks": return inVal.multiply(bdWeek);
//            case "days": return inVal.multiply(bdDay);
//            case "hours": return inVal.multiply(bdHour);
//            case "minutes": return inVal.multiply(bdMinute);
//            case "seconds": return inVal.multiply(bdSecond);
//            default: return inVal;
//        }
//    }
//
//    private boolean conformEventTime(AEvent aEvent, long fromTime, long toTime) {
//        long eventTime = aEvent.getTimestampMilli();
//        return eventTime >= fromTime && eventTime <= toTime;
//    }
//
//    private boolean conformDuration(APMLogFilterCriterion criterionEE, PTrace pTrace, CodeType codeType) {
//        long dur = 0;
//        switch(codeType) {
//            case DURATION_RANGE: dur = pTrace.getDuration(); break;
//            case TOTAL_PROCESSING_TIME: dur = pTrace.getTotalProcessingTime(); break;
//            case AVERAGE_PROCESSING_TIME: dur = pTrace.getAverageProcessingTime(); break;
//            case MAX_PROCESSING_TIME: dur = pTrace.getMaxProcessingTime(); break;
//            case TOTAL_WAITING_TIME: dur = pTrace.getTotalWaitingTime(); break;
//            case AVERAGE_WAITING_TIME: dur = pTrace.getAverageWaitingTime(); break;
//            case MAX_WAITING_TIME: dur = pTrace.getMaxWaitingTime(); break;
//            default:  break;
//        }
//        return dur > criterionEE.fromTime && dur < criterionEE.toTime;
//    }
//
//    private boolean conformCaseAttribute(PTrace pTrace, String attributeKey, UnifiedSet<String> criterionValues) {
//        if(pTrace.getAttributeMap().containsKey(attributeKey)) {
//            return criterionValues.contains(pTrace.getAttributeMap().get(attributeKey));
//        }else return false;
//    }
//
//    private boolean conformEventAttribute(AEvent aEvent, String attributeKey, String attributeValue) {
//        switch (attributeKey) {
//            case "concept:name": return (aEvent.getName().equals(attributeValue));
//            case "org:resource": return (aEvent.getResource().equals(attributeValue));
//            case "lifecycle:transition": return (aEvent.getLifecycle().equals(attributeValue));
//            default:
//                if(aEvent.getAttributeMap().containsKey(attributeKey)) {
//                    if(aEvent.getAttributeMap().get(attributeKey).equals(attributeValue)) return true;
//                    else return false;
//                }else return false;
//        }
//    }
//
//    private boolean withinTimeframe(APMLogFilterCriterion criterionEE, long tt) {
//        return tt >= criterionEE.fromTime && tt <= criterionEE.toTime;
//    }

//    private boolean conformReworkRepetition(APMLogFilterCriterion criterionEE, PTrace pTrace) {
//
//        UnifiedSet<String> actNameConformSet = new UnifiedSet<>();
//        UnifiedMap<String, Integer> activityOccurCountMap = new UnifiedMap<>();
//        List<AActivity> aActivityList = pTrace.getActivityList();
//        UnifiedSet<String> selectedValues = criterionEE.reworkOption.selectedValues;
//
//        for (AActivity activity : aActivityList) {
//            String activityName = activity.getName();
//            if (selectedValues.contains(activityName)) {
//                actNameConformSet.add(activityName);
//                if (!activityOccurCountMap.containsKey(activityName)) activityOccurCountMap.put(activityName, 1);
//                else {
//                    int count = activityOccurCountMap.get(activityName) + 1;
//                    activityOccurCountMap.put(activityName, count);
//                }
//            }
//        }
//
//        if (criterionEE.inclusion == Inclusion.ALL) {
//            if (actNameConformSet.size() != selectedValues.size() || activityOccurCountMap.size() < 1) {
//                return false;
//            } else {
//                for (String aName : activityOccurCountMap.keySet()) {
//                    int frequency = activityOccurCountMap.get(aName);
//                    if (criterionEE.hasFrequencyGreaterEqualOption(aName)) {
//                        if (frequency < criterionEE.reworkOption.frequencyGreaterEqualMap.get(aName)) return false;
//                    }
//                    if (criterionEE.hasFrequencyGreaterOption(aName)) {
//                        if (frequency <= criterionEE.reworkOption.frequencyGreaterMap.get(aName)) return false;
//                    }
//                    if (criterionEE.hasFrequencyLessEqualOption(aName)) {
//                        if (frequency > criterionEE.reworkOption.frequencyLessEqualMap.get(aName)) return false;
//                    }
//                    if (criterionEE.hasFrequencyLessOption(aName)) {
//                        if (frequency >= criterionEE.reworkOption.frequencyLessMap.get(aName)) return false;
//                    }
//                }
//            }
//        } else {
//            if (actNameConformSet.size() < 1) return false;
//            else {
//                for (String aName : activityOccurCountMap.keySet()) {
//                    int frequency = activityOccurCountMap.get(aName);
//                    if (criterionEE.hasFrequencyGreaterEqualOption(aName)) {
//                        if (frequency < criterionEE.reworkOption.frequencyGreaterEqualMap.get(aName)) return false;
//                    }
//                    if (criterionEE.hasFrequencyGreaterOption(aName)) {
//                        if (frequency <= criterionEE.reworkOption.frequencyGreaterMap.get(aName)) return false;
//                    }
//                    if (criterionEE.hasFrequencyLessEqualOption(aName)) {
//                        if (frequency > criterionEE.reworkOption.frequencyLessEqualMap.get(aName)) return false;
//                    }
//                    if (criterionEE.hasFrequencyLessOption(aName)) {
//                        if (frequency >= criterionEE.reworkOption.frequencyLessMap.get(aName)) return false;
//                    }
//                }
//            }
//        }
//
//        return true;
//    }

}
