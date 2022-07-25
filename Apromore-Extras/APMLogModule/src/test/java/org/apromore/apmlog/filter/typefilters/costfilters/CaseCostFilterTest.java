package org.apromore.apmlog.filter.typefilters.costfilters;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.assemblers.CaseCostFilterRule;
import org.apromore.apmlog.filter.rules.assemblers.CostOptions;
import org.apromore.apmlog.util.AttributeCodes;
import static org.junit.jupiter.api.Assertions.*;
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