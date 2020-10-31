/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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
//            case ACTIVITY_DURATION:
//                return ActivityDurationDesc.getDescription(logFilterRule);
//            case RESOURCE_DURATION:
//                return ResourceDurationDesc.getDescription(logFilterRule);
            case EVENT_ATTRIBUTE_DURATION:
                return EventAttributeDurationDesc.getDescription(logFilterRule);
            case CASE_SECTION_ATTRIBUTE_COMBINATION:
                return CaseSectionEventAttributeCombinationDesc.getDescription(logFilterRule);
            case ATTRIBUTE_ARC_DURATION:
                return AttributeArcDurationDesc.getDescription(logFilterRule);
            default:
                break;
        }
        return null;
    }
}
