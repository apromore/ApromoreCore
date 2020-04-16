/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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


public class CaseTimeDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        FilterType filterType = logFilterRule.getFilterType();
        Inclusion inclusion = logFilterRule.getInclusion();

        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " ";


        if (filterType == FilterType.CASE_TIME) {
            desc += "all cases where timestamp ";
            if (inclusion == Inclusion.ALL_VALUES) {
                desc += "is from " + TimeUtil.convertTimestamp(fromTime);
            } else {
                desc += "intersect from " + TimeUtil.convertTimestamp(fromTime);
            }
            desc += " to " + TimeUtil.convertTimestamp(toTime);
        } else {
            if (filterType == FilterType.STARTTIME) {
                desc += "cases that contain start event in the timestamp range between ";
            } else if (filterType == FilterType.ENDTIME) {
                desc += "cases that contain end event in the timestamp range between ";
            }
            desc += TimeUtil.convertTimestamp(fromTime) + " and ";
            desc += TimeUtil.convertTimestamp(toTime);
        }

        return desc;
    }
}
