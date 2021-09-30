/**
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
package org.apromore.apmlog.filter.typefilters.between;

import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class BetweenFilterTest {

    private PLog get5casesEF2Log() throws Exception {
        return new PLog(APMLogUnitTest.getImmutableLog("FiveCases", "files/5 cases EFollow (2).xes"));
    }

    @Test
    public void testNoMatchKeep() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                "Order modification", "Prepare package",
                true, false, true, false);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        List<PTrace> containOrderModification = output.stream()
                .filter(x -> containsActivity(x, "Order modification"))
                .collect(Collectors.toList());
        List<PTrace> notContainOrderModification = output.stream()
                .filter(x -> !containsActivity(x, "Order modification"))
                .collect(Collectors.toList());

        assertEquals(1, containOrderModification.size());
        assertEquals(4, notContainOrderModification.size());
    }

    private boolean containsActivity(PTrace trace, String name) {
        return null != trace.getActivityInstances().stream()
                .filter(x -> x.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Test
    public void testSourceFirstOccurIncludedToTargetExcluded() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                "Order modification", "Prepare package",
                true, false, true, false);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Order modification", output.get(4).getActivityInstances().get(0).getName());
        assertEquals("Warehouse check for the order", output.get(4).getActivityInstances().get(1).getName());
        assertEquals(2, output.get(4).getActivityInstances().size());
    }

    @Test
    public void testStartToTargetExcluded() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                BetweenFilterSupport.START, "Warehouse check for the order",
                true, false, true, false);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Proceed order",
                output.get(2).getActivityInstances().get(output.get(2).getActivityInstances().size()-1).getName());
    }

    @Test
    public void testStartToTargetIncluded() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                BetweenFilterSupport.START, "Warehouse check for the order",
                true, false, true, true);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Warehouse check for the order",
                output.get(2).getActivityInstances().get(output.get(2).getActivityInstances().size()-1).getName());
    }

    @Test
    public void testSourceExcludedToEnd() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                "Warehouse check for the order", BetweenFilterSupport.END,
                true, false, false, true);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Tranfer items from warehouse 2", output.get(2).getActivityInstances().get(0).getName());
    }

    @Test
    public void testSourceIncludedToEnd() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                "Warehouse check for the order", BetweenFilterSupport.END,
                true, false, true, true);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Warehouse check for the order", output.get(2).getActivityInstances().get(0).getName());
    }

    @Test
    public void testSourceFirstOccurrence() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                "Prepare package", BetweenFilterSupport.END,
                true, false, true, true);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Prepare package", output.get(1).getActivityInstances().get(0).getName());
        assertEquals("Report package damaged", output.get(1).getActivityInstances().get(1).getName());
        assertEquals("Prepare package", output.get(1).getActivityInstances().get(2).getName());
    }

    @Test
    public void testSourceLastOccurrence() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                "Warehouse check for the order", "Warehouse check for the order",
                true, true, false, false);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals(1, output.size());
        assertEquals("Order modification", output.get(0).getActivityInstances().get(0).getName());
    }

    @Test
    public void testTargetFirstOccurrence() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                BetweenFilterSupport.START, "Warehouse check for the order",
                true, false, true, true);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Warehouse check for the order", output.get(4).getActivityInstances().get(1).getName());
        assertEquals(2, output.get(4).getActivityInstances().size());
    }

    @Test
    public void testTargetLastOccurrence() throws Exception {
        PLog log = get5casesEF2Log();
        LogFilterRule rule = BetweenFilterSupport.createRule(Choice.RETAIN, "concept:name",
                BetweenFilterSupport.START, "Warehouse check for the order",
                true, true, true, true);
        List<PTrace> output = BetweenFilter.filter(log.getPTraces(), rule);
        assertEquals("Warehouse check for the order", output.get(4).getActivityInstances().get(1).getName());
        assertEquals("Warehouse check for the order",
                output.get(4).getActivityInstances().get(output.get(4).getActivityInstances().size()-1).getName());
    }

}