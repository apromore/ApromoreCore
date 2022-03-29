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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;


public class DurationDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        StringBuilder sb = new StringBuilder();
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        long fromDur = 0, toDur = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromDur = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toDur = ruleValue.getLongValue();
        }

        FilterType filterType = logFilterRule.getFilterType();

        sb.append(choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases with ");

        switch (filterType) {
            case DURATION:
                sb.append("duration between ["); break;
            case MAX_PROCESSING_TIME:
                sb.append("max processing time between ["); break;
            case AVERAGE_PROCESSING_TIME:
                sb.append("average processing time between ["); break;
            case TOTAL_PROCESSING_TIME:
                sb.append("total processing time between ["); break;
            case TOTAL_WAITING_TIME:
                sb.append("total waiting time between ["); break;
            case AVERAGE_WAITING_TIME:
                sb.append("average waiting time between ["); break;
            case MAX_WAITING_TIME:
                sb.append("max waiting time between ["); break;
            default: break;
        }

        sb.append(TimeUtil.durationStringOf(fromDur) + " AND " + TimeUtil.durationStringOf(toDur) + "]");


        return sb.toString();
    }
}
