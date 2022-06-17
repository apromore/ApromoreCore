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
import org.apromore.apmlog.filter.rules.assemblers.CostOptions;
import org.apromore.apmlog.filter.rules.assemblers.NodeCostFilterRule;
import org.apromore.apmlog.util.AttributeCodes;
import org.apromore.apmlog.util.Util;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.Map;

class NodeCostDescTest {

    @Test
    void getDescription() {
        CostOptions costOptions = new CostOptions("JPY", AttributeCodes.ORG_ROLE,
                Map.of("Role1", 10.0, "Role2", 20.0), 0, 10);
        LogFilterRule rule = NodeCostFilterRule.of(true, true, AttributeCodes.CONCEPT_NAME,
                "A1", costOptions);
        String desc = rule.toString();

        String symbol = Util.getCurrencySymbol("JPY");
        String eMinStr = symbol + 0;
        String eMaxStr = symbol + 10;
        String expected = String.format("Retain all cases where Activity 'A1' has cost between [%s AND %s]", eMinStr, eMaxStr);

        assertEquals(expected, desc);
    }
}