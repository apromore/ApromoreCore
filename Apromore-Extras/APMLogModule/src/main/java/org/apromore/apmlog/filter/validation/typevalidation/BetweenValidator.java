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

package org.apromore.apmlog.filter.validation.typevalidation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import org.apromore.apmlog.stats.LogStatsAnalyzer;

import java.util.Set;
import java.util.stream.Collectors;

public class BetweenValidator {
    private BetweenValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidatedFilterRule validate(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule validatedRule = originalRule.deepClone();

        String attrKey = validatedRule.getKey();

        Set<String> validVals =
                LogStatsAnalyzer.getUniqueEventAttributeValues(apmLog.getActivityInstances(), attrKey);

        Set<RuleValue> validValues = validatedRule.getPrimaryValues().stream()
                .filter(x -> validVals.contains(x.getStringValue()))
                .collect(Collectors.toSet());

        if (validValues.isEmpty() || validValues.size() != validatedRule.getPrimaryValues().size()) {
            return null;
        }

        validatedRule.setPrimaryValues(validValues);
        return new ValidatedFilterRule(originalRule, validatedRule, true, false);
    }
}
