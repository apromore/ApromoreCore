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

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.assemblers.CaseCostFilterRule;
import org.apromore.apmlog.filter.rules.assemblers.CostOptions;
import org.apromore.apmlog.util.AttributeCodes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class CaseCostFilterTest {

    @Test
    void filter() throws Exception {
        APMLog apmLog = APMLogUnitTest.getImmutableLog("TwoCases05", "files/TwoCases05.xes");
        CostOptions costOptions = new CostOptions("JPY", AttributeCodes.ORG_ROLE,
                Map.of("Role1", 10.0, "Role2", 20.0), 0, 20);
        LogFilterRule logFilterRule = CaseCostFilterRule.of(true, costOptions);
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(List.of(logFilterRule));
        PLog filteredLog = apmLogFilter.getPLog();
        assertEquals(1, filteredLog.size());
        assertEquals("C1", filteredLog.getPTraces().get(0).getCaseId());
    }
}