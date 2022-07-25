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

package org.apromore.apmlog.filter.typefilters.costfilters;

import lombok.Getter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;

import java.util.Map;

@Getter
public class CostFilterRuleData {

    private final boolean retain;
    private final String costPerspective;
    private final Map<String, Double> costRates;
    private final boolean invalid;

    public CostFilterRuleData(LogFilterRule logFilterRule) {
        retain = logFilterRule.getChoice() == Choice.RETAIN;
        costPerspective = logFilterRule.getCostPerspective();
        costRates = logFilterRule.getCostRates();
        invalid = costPerspective == null || costPerspective.isEmpty() || costRates == null || costRates.isEmpty();
    }
}
