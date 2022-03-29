/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;

import static org.apromore.apmlog.filter.types.FilterType.CASE_TIME;
import static org.apromore.apmlog.filter.types.FilterType.STARTTIME;
import static org.apromore.apmlog.filter.types.Inclusion.ALL_VALUES;
import static org.apromore.apmlog.filter.types.Inclusion.ANY_VALUE;


public class CaseTimeDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        StringBuilder sb = new StringBuilder();

        String choice = logFilterRule.getChoice().toString().toLowerCase();
        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        FilterType filterType = logFilterRule.getFilterType();
        Inclusion inclusion = logFilterRule.getInclusion();

        sb.append(choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases that ");

        if (filterType == CASE_TIME && inclusion == ALL_VALUES) {
            sb.append("are contained between " + getText(fromTime, toTime));
        } else  if (filterType == CASE_TIME && inclusion == ANY_VALUE) {
            sb.append("are active between " + getText(fromTime, toTime));
        } else if (filterType == STARTTIME) {
            sb.append("start between " + getText(fromTime, toTime));
        } else {
            sb.append("end between " + getText(fromTime, toTime));
        }

        return sb.toString();
    }

    private static String getText(long from, long to) {
        return TimeUtil.convertTimestamp(from) + " and " + TimeUtil.convertTimestamp(to);
    }
}
