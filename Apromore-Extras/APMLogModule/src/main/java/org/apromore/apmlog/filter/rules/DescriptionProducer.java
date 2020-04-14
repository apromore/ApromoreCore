package org.apromore.apmlog.filter.rules;

import org.apromore.apmlog.filter.rules.desc.*;

public class DescriptionProducer {

    public static String getDescription(LogFilterRule logFilterRule) {
        switch (logFilterRule.getFilterType()) {
            case CASE_VARIANT:
                return CaseVariantDesc.getDescription(logFilterRule);
            case CASE_ID:
                return CaseIDDesc.getDescription(logFilterRule);
            case CASE_CASE_ATTRIBUTE:
                return CaseSectionCaseAttributeDesc.getDescription(logFilterRule);
            case CASE_EVENT_ATTRIBUTE:
                return CaseSectionEventAttributeDesc.getDescription(logFilterRule);
            case CASE_TIME:
            case STARTTIME:
            case ENDTIME:
                return CaseTimeDesc.getDescription(logFilterRule);
            case DURATION:
            case TOTAL_PROCESSING_TIME:
            case AVERAGE_PROCESSING_TIME:
            case MAX_PROCESSING_TIME:
            case TOTAL_WAITING_TIME:
            case AVERAGE_WAITING_TIME:
            case MAX_WAITING_TIME:
                return DurationDesc.getDescription(logFilterRule);
            case CASE_UTILISATION:
                return CaseUtilisationDesc.getDescription(logFilterRule);
            case DIRECT_FOLLOW:
            case EVENTUAL_FOLLOW:
                return PathDesc.getDescription(logFilterRule);
            case REWORK_REPETITION:
                return ReworkDesc.getDescription(logFilterRule);
            case EVENT_EVENT_ATTRIBUTE:
                return EventSectionAttributeDesc.getDescription(logFilterRule);
            case EVENT_TIME:
                return EventTimeDesc.getDescription(logFilterRule);
            default:
                break;
        }
        return null;
    }
}
