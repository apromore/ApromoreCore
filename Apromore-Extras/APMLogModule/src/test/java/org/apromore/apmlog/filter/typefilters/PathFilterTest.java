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
package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.logobjects.ImmutableLog;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PathFilterTest {

    @Test
    void toKeep() throws Exception{
        ImmutableLog immutableLog = APMLogUnitTest.getImmutableLog("testlog", "files/EFollowTest01.xes");
        APMLogFilter apmLogFilter = new APMLogFilter(immutableLog);
        apmLogFilter.filter(List.of(getEFollowRule1()));

        PLog pLog = apmLogFilter.getPLog();
        assertEquals(1, pLog.size());
    }

    private LogFilterRule getEFollowRule1() {
        Choice choice = Choice.RETAIN;
        FilterType filterType = FilterType.EVENTUAL_FOLLOW;
        String mainKey = "concept:name";

        Set<RuleValue> primaryValues = Set.of(
                new RuleValue(filterType, OperationType.FROM, mainKey, "b"),
                new RuleValue(filterType, OperationType.TO, mainKey, "b")
        );

        return new LogFilterRuleImpl(choice, Inclusion.ANY_VALUE, Section.CASE,
                filterType, mainKey,
                primaryValues, new HashSet<>());
    }
}