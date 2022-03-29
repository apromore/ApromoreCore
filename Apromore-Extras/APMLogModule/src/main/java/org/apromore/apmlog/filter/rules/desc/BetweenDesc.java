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
import org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.END;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.FIRST_OCCURRENCE;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.INCLUDE_SELECTION;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.START;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.xes.XESAttributeCodes;

public class BetweenDesc {

    private BetweenDesc() {
        throw new IllegalStateException("Utility class");
    }

    public static String getDescription(LogFilterRule logFilterRule) {

        Choice choice = logFilterRule.getChoice();
        String attribute = XESAttributeCodes.getDisplayLabelForSingle(logFilterRule.getKey());
        RuleValue rvFrom = BetweenFilterSupport.findValue(OperationType.FROM, logFilterRule);
        RuleValue rvTo = BetweenFilterSupport.findValue(OperationType.TO, logFilterRule);
        boolean sourceFirstOccur = Boolean.parseBoolean(rvFrom.getCustomAttributes().get(FIRST_OCCURRENCE));
        boolean targetFirstOccur = Boolean.parseBoolean(rvTo.getCustomAttributes().get(FIRST_OCCURRENCE));
        boolean includeFrom = Boolean.parseBoolean(rvFrom.getCustomAttributes().get(INCLUDE_SELECTION));
        boolean includeTo = Boolean.parseBoolean(rvTo.getCustomAttributes().get(INCLUDE_SELECTION));

        StringBuilder sb = new StringBuilder();
        sb.append(choice == Choice.RETAIN ? "Retain " : "Remove ").append("all activity instances ");
        sb.append(buildDescription(attribute, rvFrom.getStringValue(), rvTo.getStringValue(),
                sourceFirstOccur, targetFirstOccur, includeFrom, includeTo));

        return sb.toString();
    }

    private static String buildDescription(String attribute, String source, String target,
                                           boolean sourceFirstOccur, boolean targetFirstOccur,
                                           boolean includeFrom, boolean includeTo) {
        return  " between " +
                getValueString(attribute, source, sourceFirstOccur, includeFrom) +
                " and " +
                getValueString(attribute, target, targetFirstOccur, includeTo);
    }

    private static String getValueString(String attribute, String value, boolean firstOccur, boolean include) {
        if (value.equals(START) || value.equals(END))
            return value;

        return String.format("the %s occurrence of %s [%s (%s)]",
                firstOccur ? "first" : "last",
                attribute,
                value,
                include ? "included" : "excluded");
    }
}
