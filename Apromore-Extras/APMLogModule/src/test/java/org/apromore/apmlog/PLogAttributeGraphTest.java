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
package org.apromore.apmlog;

import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.stats.DurSubGraph;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;


import static org.junit.Assert.assertTrue;

public class PLogAttributeGraphTest {

    public static void testArc(APMLog apmLog) {
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        PLog pLog = apmLogFilter.getPLog();

        // test without update stats
        proceedTestArc(pLog);

        // test after update stats
        pLog.updateStats();
        proceedTestArc(pLog);
    }

    private static void proceedTestArc(PLog pLog) {
        String attrKey = "concept:name";
        String val1 = "Warehouse check for the order";
        String val2 = "Prepare package";

        DurSubGraph subGraph = pLog.getAttributeGraph().getNextValueDurations(attrKey, val1, pLog);
        DoubleArrayList durList = subGraph.getValDurListMap().get(val2);

        assertTrue( Util.durationStringOf(durList.min()).equals("1.47 hrs") );
        assertTrue( Util.durationStringOf(durList.average()).equals("2.53 hrs") );
        assertTrue( Util.durationStringOf(durList.median()).equals("2.45 hrs") );
        assertTrue( Util.durationStringOf(durList.max()).equals("3.67 hrs") );
        assertTrue( Util.durationStringOf(durList.sum()).equals("7.58 hrs") );
    }
}
