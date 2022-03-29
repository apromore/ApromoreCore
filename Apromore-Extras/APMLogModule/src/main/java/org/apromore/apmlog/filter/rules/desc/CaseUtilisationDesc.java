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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.Util;

import java.text.DecimalFormat;

public class CaseUtilisationDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) +
                " all cases with a case utilization between ";

        double utilRangeFrom = 0, utilRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) utilRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) utilRangeTo = ruleValue.getDoubleValue();
        }

        DecimalFormat df2 = Util.getDecimalFormat();
        String fromString = df2.format(utilRangeFrom * 100) + "%";
        String toString = df2.format(utilRangeTo * 100) + "%";

        desc += fromString + " to " + toString;
        return desc;
    }
}
