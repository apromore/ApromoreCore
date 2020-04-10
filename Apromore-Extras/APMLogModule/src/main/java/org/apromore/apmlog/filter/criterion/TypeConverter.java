/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne. All Rights Reserved.
 *
 */
package org.apromore.apmlog.filter.criterion;


import org.apromore.apmlog.filter.criterion.types.CodeType;

import java.math.BigDecimal;

/**
 * @author Chii Chang (Last modified: 28/01/2020)
 */
public class TypeConverter {
    public static CodeType convertToCodeType(String code) {
        switch(code) {
            case "concept:name": return CodeType.CONCEPT_NAME;
            case "case:variant": return CodeType.VARIANT;
            case "direct:follow": return CodeType.D_FOLLOW;
            case "eventually:follow": return CodeType.E_FOLLOW;
            case "lifecycle:transition": return CodeType.LIFECYCLE;
            case "org:group": return CodeType.ORG_GROUP;
            case "org:resource": return CodeType.ORG_RESOURCE;
            case "org:role": return CodeType.ORG_ROLE;
            case "time:timestamp": return CodeType.TIMEFRAME;
            case "duration:range": return CodeType.DURATION_RANGE;
            case "time:startrange": return CodeType.START_TIME_RANGE;
            case "time:endrange": return CodeType.END_TIME_RANGE;
            case "case:id": return CodeType.CASE_NAME;
            case "duration:total_processing": return CodeType.TOTAL_PROCESSING_TIME;
            case "duration:average_processing": return CodeType.AVERAGE_PROCESSING_TIME;
            case "duration:max_processing": return CodeType.MAX_PROCESSING_TIME;
            case "duration:total_waiting": return CodeType.TOTAL_WAITING_TIME;
            case "duration:average_waiting": return CodeType.AVERAGE_WAITING_TIME;
            case "duration:max_waiting": return CodeType.MAX_WAITING_TIME;
            case "case:utilization": return CodeType.CASE_UTIL;
            case "case:caseattribute": return CodeType.CASE_CASE_ATTR;
            case "case:eventattribute": return CodeType.CASE_EVENT_ATTR;
            case "event:attribute": return CodeType.EVENT_ATTR;
            case "rework:repetition": return CodeType.REWORK_REPETITION;
            default: return CodeType.UNKNOWN;
        }
    }

    public static String convertToCode(CodeType codeType) {
        switch(codeType) {
            case CONCEPT_NAME: return "concept:name";
            case VARIANT: return "case:variant";
            case D_FOLLOW: return "direct:follow";
            case E_FOLLOW: return "eventually:follow";
            case LIFECYCLE: return "lifecycle:transition";
            case ORG_GROUP: return "org:group";
            case ORG_RESOURCE: return "org:resource";
            case ORG_ROLE: return "org:role";
            case TIMEFRAME: return "time:timestamp";
            case DURATION_RANGE: return "duration:range";
            case START_TIME_RANGE: return "time:startrange";
            case END_TIME_RANGE: return "time:endrange";
            case CASE_NAME: return "case:id";
            case TOTAL_PROCESSING_TIME: return "duration:total_processing";
            case AVERAGE_PROCESSING_TIME: return "duration:average_processing";
            case MAX_PROCESSING_TIME: return "duration:max_processing";
            case TOTAL_WAITING_TIME: return "duration:total_waiting";
            case AVERAGE_WAITING_TIME: return "duration:average_waiting";
            case MAX_WAITING_TIME: return "duration:max_waiting";
            case CASE_UTIL: return "case:utilization";
            case CASE_CASE_ATTR: return "case:caseattribute";
            case CASE_EVENT_ATTR: return "case:eventattribute";
            case EVENT_ATTR: return "event:attribute";
            case REWORK_REPETITION: return "rework:repetition";
            default: return "n:a";
        }
    }

    public static long toMillisecond(String s) {
        int spaceIndex = s.indexOf(" ");
        String numberString = s.substring(0, spaceIndex);
        BigDecimal bdNumValue = new BigDecimal(numberString);
        String unitString = s.substring(spaceIndex + 1);
        BigDecimal bdUnitValue = unitStringToBigDecimal(unitString);
        BigDecimal bdValue = bdNumValue.multiply(bdUnitValue);
        return bdValue.longValue();
    }

    public static BigDecimal unitStringToBigDecimal(String s) {
        switch(s.toLowerCase()) {
            case "years": return new BigDecimal("31536000000.0");
            case "months": return new BigDecimal("2678400000.0");
            case "weeks": return new BigDecimal("604800000.0");
            case "days": return new BigDecimal("86400000.0");
            case "hours": return new BigDecimal("3600000.0");
            case "minutes": return new BigDecimal("60000.0");
            case "seconds": return new BigDecimal("1000.0");
            default: return new BigDecimal("1.0");
        }
    }
}
