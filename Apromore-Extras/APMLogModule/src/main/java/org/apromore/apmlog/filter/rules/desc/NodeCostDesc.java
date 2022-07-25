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

import lombok.experimental.UtilityClass;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.Util;

import java.text.DecimalFormat;

@UtilityClass
public class NodeCostDesc {
    public static String getDescription(LogFilterRule logFilterRule) {

        StringBuilder sb = new StringBuilder();

        String attributeKey = logFilterRule.getKey();
        String attributeVal = logFilterRule.getPrimaryValues().iterator().next().getKey();

        sb.append(logFilterRule.getChoice() == Choice.RETAIN ? "Retain" : "Remove");
        sb.append(" all cases where ");
        sb.append(Util.getDisplayAttributeKey(attributeKey)).append(" '").append(attributeVal).append("' has cost " +
                "between [");

        double minCost = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.GREATER_EQUAL).doubleValue();
        double maxCost = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.LESS_EQUAL).doubleValue();

        String symbol = Util.getCurrencySymbol(logFilterRule.getCurrency());
        DecimalFormat df = Util.getDecimalFormat();
        String fromStr = symbol + df.format(minCost);
        String toStr = symbol + df.format(maxCost);

        sb.append(fromStr).append(" AND ").append(toStr);
        sb.append("]");

        return sb.toString();
    }
}
