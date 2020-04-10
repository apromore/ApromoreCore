/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne. All Rights Reserved.
 *
 */
package org.apromore.apmlog.filter.criterion;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.apmlog.filter.criterion.types.*;
import org.apromore.apmlog.filter.criterion.options.ReworkOption;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Set;

/**
 * This class is an exclusive support class for APMLogFilter to perform the filtering
 * By utilising this class, APMLogFilter does not need to deal with the criterion values (String)
 * directly in most situations.
 * @author Chii Chang (Last modified: 28/01/2020)
 */
public class APMLogFilterCriterion {

    public String attributeKey, label;
    public Retainment retainment;
    public Section section;
    public CodeType codeType;
    public Inclusion inclusion;
    public UnifiedSet<String> criterionValues;
    public long fromTime = 0, toTime = Long.MAX_VALUE;
    public UnifiedMap<ReqType, String> requirementMap;
    public double utilGreater, utilLess;
    public ReworkOption reworkOption;
    public LogFilterCriterion logFilterCriterion;

    /**
     * Initiate this from logFilterCriterion
     * @param logFilterCriterion
     */
    public APMLogFilterCriterion(LogFilterCriterion logFilterCriterion) {

        this.logFilterCriterion = logFilterCriterion;

        this.attributeKey = logFilterCriterion.getAttribute();

        if(!logFilterCriterion.getLabel().equals("")) this.label = logFilterCriterion.getLabel();

        this.retainment = logFilterCriterion.getAction() == Action.RETAIN ? Retainment.KEEP : Retainment.REMOVE;

        this.section = logFilterCriterion.getLevel() == Level.TRACE ? Section.CASE : Section.EVENT;

        String code = logFilterCriterion.getAttribute();

        this.codeType = code == null ? TypeConverter.convertToCodeType(label) : TypeConverter.convertToCodeType(code);

        if (this.section == Section.CASE && (codeType == CodeType.CONCEPT_NAME ||
                codeType == codeType.UNKNOWN)) {
            if (label.equals("case:eventattribute") || label.equals("concept:name")) {
                codeType = CodeType.CASE_EVENT_ATTR;
            } else codeType = CodeType.CASE_CASE_ATTR;
        }


        this.inclusion = logFilterCriterion.getContainment() == Containment.CONTAIN_ALL ? Inclusion.ALL : Inclusion.ANY;

        criterionValues = new UnifiedSet<>(logFilterCriterion.getValue());

        if (codeType == CodeType.START_TIME_RANGE || codeType == CodeType.END_TIME_RANGE ||
            codeType == CodeType.TIMEFRAME) setTime();
        if (codeType == CodeType.DURATION_RANGE ||
            codeType == CodeType.TOTAL_PROCESSING_TIME ||
            codeType == CodeType.AVERAGE_PROCESSING_TIME ||
            codeType == CodeType.MAX_PROCESSING_TIME ||
            codeType == CodeType.TOTAL_WAITING_TIME ||
            codeType == CodeType.AVERAGE_WAITING_TIME ||
            codeType == CodeType.MAX_WAITING_TIME) setDuration();
        if (codeType == CodeType.CASE_UTIL) setUtil();
        if (codeType == CodeType.D_FOLLOW || codeType == CodeType.E_FOLLOW) setPathRequirement();
        if (codeType == CodeType.REWORK_REPETITION) setReworkRequirement(logFilterCriterion.getValue());

    }

    private void setTime() {
        for(String critValue : criterionValues) {
            if(critValue.startsWith(">")) fromTime = new Long(critValue.substring(1));
            if(critValue.startsWith("<")) toTime = new Long(critValue.substring(1));
        }
    }

    private void setDuration() {
        for(String critValue : criterionValues) {
            if(critValue.startsWith(">")) fromTime = TypeConverter.toMillisecond(critValue.substring(1));
            if(critValue.startsWith("<")) toTime = TypeConverter.toMillisecond(critValue.substring(1));
        }
    }

    private void setUtil() {
        for(String critValue : criterionValues) {
            if(critValue.startsWith(">")) utilGreater = new Double(critValue.substring(1));
            if(critValue.startsWith("<")) utilLess = new Double(critValue.substring(1));
        }
    }

    private void setPathRequirement() {
        requirementMap = new UnifiedMap<>();
        for (String s : criterionValues) {
            if (s.contains("@$")) this.requirementMap.put(ReqType.ATTRIBUTE, s.substring(2));
            if (s.contains("@&")) this.requirementMap.put(ReqType.SAME_ATTR, s.substring(2));
            if (s.contains("@!")) this.requirementMap.put(ReqType.DIFF_ATTR, s.substring(2));
            if (s.contains("@<|")) this.requirementMap.put(ReqType.LESS_TIME, s.substring(3));
            if (s.contains("@<=")) this.requirementMap.put(ReqType.LESS_EQUAL_TIME, s.substring(3));
            if (s.contains("@>|")) this.requirementMap.put(ReqType.GREATER_TIME, s.substring(3));
            if (s.contains("@>=")) this.requirementMap.put(ReqType.GREATER_EQUAL_TIME, s.substring(3));
        }

        if (!this.requirementMap.containsKey(ReqType.ATTRIBUTE)) {
            this.requirementMap.put(ReqType.ATTRIBUTE, this.label);
        }
    }


    private void setReworkRequirement(Set<String> criterionValues) {
        reworkOption = new ReworkOption(new UnifiedSet<>(criterionValues));
    }

    public boolean hasFrequencyGreaterOption(String valueId) {
        return reworkOption.frequencyGreaterMap.containsKey(valueId);
    }

    public boolean hasFrequencyGreaterEqualOption(String valueId) {
        return reworkOption.frequencyGreaterEqualMap.containsKey(valueId);
    }

    public boolean hasFrequencyLessOption(String valueId) {
        return reworkOption.frequencyLessMap.containsKey(valueId);
    }

    public boolean hasFrequencyLessEqualOption(String valueId) {
        return reworkOption.frequencyLessEqualMap.containsKey(valueId);
    }


    /**
     * Convert this LogFilterCriterionEE to LogFilterCriterion of Apromore-Core
     * @return
     */
    public LogFilterCriterion toLogFilterCriterion() {
        return null;
    }

}
