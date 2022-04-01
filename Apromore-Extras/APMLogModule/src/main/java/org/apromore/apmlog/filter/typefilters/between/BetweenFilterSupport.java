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
package org.apromore.apmlog.filter.typefilters.between;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;

import java.util.Set;

public class BetweenFilterSupport {

    public static final String START = "[Start]";
    public static final String END = "[End]";
    public static final String INCLUDE_SELECTION = "includeSelection";
    public static final String FIRST_OCCURRENCE = "firstOccurrence";

    private BetweenFilterSupport() {
        throw new IllegalStateException("Utility class");
    }

    public static LogFilterRule createRule(Choice choice,
                                           String attribute,
                                           String source,
                                           String target,
                                           boolean sourceFirstOccurrence,
                                           boolean targetLastOccurrence,
                                           boolean includeSource,
                                           boolean includeTarget) {

        RuleValue val1 = new RuleValue(FilterType.BETWEEN, OperationType.FROM, attribute, source);
        val1.getCustomAttributes().put(INCLUDE_SELECTION, String.valueOf(includeSource));
        val1.getCustomAttributes().put(FIRST_OCCURRENCE, String.valueOf(sourceFirstOccurrence));

        RuleValue val2 = new RuleValue(FilterType.BETWEEN, OperationType.TO, attribute, target);
        val2.getCustomAttributes().put(INCLUDE_SELECTION, String.valueOf(includeTarget));
        val2.getCustomAttributes().put(FIRST_OCCURRENCE, String.valueOf(!targetLastOccurrence));

        return new LogFilterRuleImpl(choice, Inclusion.ALL_VALUES, Section.EVENT, FilterType.BETWEEN, attribute,
                Set.of(val1, val2), null);
    }

    public static RuleValue findValue(OperationType operationType, LogFilterRule logFilterRule) {
        return logFilterRule.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == operationType).findFirst().orElse(null);
    }

}
