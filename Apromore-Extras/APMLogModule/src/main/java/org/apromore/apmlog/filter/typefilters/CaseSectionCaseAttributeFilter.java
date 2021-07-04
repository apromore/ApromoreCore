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
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;

import java.util.BitSet;
import java.util.Set;

public class CaseSectionCaseAttributeFilter {

    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();

        FilterType filterType = logFilterRule.getFilterType();

        if (logFilterRule.getPrimaryValues() == null || logFilterRule.getPrimaryValues().isEmpty()) {
            return false;
        }

        switch (filterType) {
            case CASE_ID:
                int immutableIndex = trace.getImmutableIndex();
                BitSet bitSet = (BitSet) logFilterRule.getPrimaryValues().iterator().next().getObjectVal();
                return bitSet.get(immutableIndex);
            case CASE_VARIANT:
                String caseVariant = trace.getCaseVariantId() + "";
                Set<String> variants = logFilterRule.getPrimaryValuesInString();
                return variants.contains(caseVariant);
            default:
                if (!trace.getAttributes().keySet().contains(attributeKey)) return false;
                String value = trace.getAttributes().get(attributeKey);
                Set<String> ruleVals = (Set<String>) logFilterRule.getPrimaryValues().iterator().next().getObjectVal();
                return ruleVals.contains(value);
        }

    }
}
