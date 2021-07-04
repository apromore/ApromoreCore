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
import org.apromore.apmlog.filter.types.Inclusion;

import java.util.Set;
import java.util.stream.Collectors;

public class CaseSectionEventAttributeFilter {

    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();
        Set<RuleValue> primRV = logFilterRule.getPrimaryValues();

        if (primRV == null || primRV.isEmpty()) return false;

        final Set<String> values = (Set<String>) primRV.iterator().next().getObjectVal();

        Set<String> containedVals = trace.getActivityInstances().stream()
                .filter(x -> values.contains(x.getAttributeValue(attributeKey)))
                .map(x -> x.getAttributeValue(attributeKey))
                .collect(Collectors.toSet());

        if (logFilterRule.getInclusion() == Inclusion.ALL_VALUES) return containedVals.size() >= values.size();
        else return containedVals.size() > 0;
    }
}
