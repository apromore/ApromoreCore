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
package org.apromore.apmlog.filter.typefilters;


import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;

public class DurationFilter {

    public static boolean toKeep(ATrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(ATrace trace, LogFilterRule logFilterRule) {


        double durRangeFrom = 0, durRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getDoubleValue();
        }


        FilterType filterType = logFilterRule.getFilterType();

        double dur = 0;

        switch (filterType) {
            case DURATION:
                dur = trace.getDuration(); break;
            case MAX_PROCESSING_TIME:
                dur = trace.getMaxProcessingTime(); break;
            case AVERAGE_PROCESSING_TIME:
                dur = trace.getAverageProcessingTime(); break;
            case TOTAL_PROCESSING_TIME:
                dur = trace.getTotalProcessingTime(); break;
            case TOTAL_WAITING_TIME:
                dur = trace.getTotalWaitingTime(); break;
            case AVERAGE_WAITING_TIME:
                dur = trace.getAverageWaitingTime(); break;
            case MAX_WAITING_TIME:
                dur = trace.getMaxWaitingTime(); break;
                default: break;
        }

        return dur >= durRangeFrom && dur <= durRangeTo;
    }


}
