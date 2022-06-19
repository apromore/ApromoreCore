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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.Util;

import java.text.DecimalFormat;

@UtilityClass
public class CaseCostDesc {
    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) +
                " all cases with a case cost between ";

        double from = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.GREATER_EQUAL).doubleValue();
        double to = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.LESS_EQUAL).doubleValue();

        String symbol = Util.getCurrencySymbol(logFilterRule.getCurrency());

        DecimalFormat df = Util.getDecimalFormat();
        String fromStr = symbol + df.format(from);
        String toStr = symbol + df.format(to);

        desc += fromStr + " to " + toStr;
        return desc;
    }
}
