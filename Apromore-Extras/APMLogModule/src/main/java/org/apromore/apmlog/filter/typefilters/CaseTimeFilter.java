/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;


public class CaseTimeFilter {
    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {
        long traceStartTime = trace.getStartTime(), traceEndTime = trace.getEndTime();


        FilterType filterType = logFilterRule.getFilterType();
        Inclusion inclusion = logFilterRule.getInclusion();
        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        switch (filterType) {
            case CASE_TIME:
                switch (inclusion) {
                    case ALL_VALUES:
                        return withinTimeRange(fromTime, toTime, traceStartTime, traceEndTime);
                    case ANY_VALUE:
                        return intersectTimeRange(fromTime, toTime, trace);
                        default: return false;
                }
            case STARTTIME:
                return startInTimeRange(fromTime, toTime, traceStartTime);
            case ENDTIME:
                return endInTimeRange(fromTime, toTime, traceEndTime);
                default: return false;
        }
    }

    private static boolean withinTimeRange(long fromTime, long toTime, long traceStartTime, long traceEndTime) {
        return traceStartTime >= fromTime && traceEndTime <= toTime;
    }

    private static boolean intersectTimeRange(long fromTime, long toTime, PTrace trace) {
        long traceST = trace.getStartTime();
        long traceET = trace.getEndTime();

        if (traceST <= fromTime && traceET >= toTime) return true;
        if (traceST <= fromTime && traceET >= fromTime) return true;
        if (traceST >= fromTime && traceET <= toTime) return true;
        if (traceST <= toTime && traceET >= toTime) return true;

        return false;
    }

    private static boolean startInTimeRange(long fromTime, long toTime, long traceStartTime) {
        return traceStartTime >= fromTime && traceStartTime <= toTime;
    }

    private static boolean endInTimeRange(long fromTime, long toTime, long traceEndTime) {
        return traceEndTime >= fromTime && traceEndTime <= toTime;
    }
}
