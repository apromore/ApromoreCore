/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.apmlog.filter.typefilters.costfilters;

/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.assemblers.CostOptions;
import org.apromore.apmlog.filter.rules.assemblers.NodeCostFilterRule;
import org.apromore.apmlog.util.AttributeCodes;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class NodeCostFilterTest {

    @Test
    void filter() throws Exception {
        APMLog apmLog = APMLogUnitTest.getImmutableLog("TwoCases05", "files/TwoCases05.xes");
        CostOptions costOptions = new CostOptions("JPY", AttributeCodes.ORG_ROLE,
                Map.of("Role1", 10.0, "Role2", 20.0), 0, 10);
        LogFilterRule rule = NodeCostFilterRule.of(true, true, AttributeCodes.CONCEPT_NAME,
                "A1", costOptions);
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(List.of(rule));
        PLog filteredLog = apmLogFilter.getPLog();
        assertEquals(1, filteredLog.size());
        assertEquals("C1", filteredLog.getPTraces().get(0).getCaseId());

        rule = NodeCostFilterRule.of(true, false, AttributeCodes.CONCEPT_NAME,
                "A1", costOptions);
        apmLogFilter.filter(List.of(rule));
        filteredLog = apmLogFilter.getPLog();
        assertEquals(2, filteredLog.size());
    }
}