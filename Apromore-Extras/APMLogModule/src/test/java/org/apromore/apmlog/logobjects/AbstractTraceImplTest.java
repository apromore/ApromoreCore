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
package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class AbstractTraceImplTest {

    private int[] getActIndicatorArray(APMLog apmLog) {
        int[] array = new int[5];
        String[] actNameArray = new String[]{"Proceed order", "Cancel order", "Proceed order",
                "Warehouse check for the order", "Prepare package"};
        for (int i = 0; i < array.length; i++) {
            array[i] = apmLog.getActivityNameIndicatorMap().get(actNameArray[i]);
        }

        return array;
    }

    @Test
    void getActivityInstancesIndicator() throws Exception {
        APMLog apmLog = APMLogUnitTest.getImmutableLog("5casesMode", "files/5casesMOD.xes");
        String expected = Arrays.toString(getActIndicatorArray(apmLog));
        assertEquals(expected, apmLog.get(0).getActivityInstancesIndicator());
    }

    @Test
    void getActivityInstancesIndicatorArray() throws Exception {
        APMLog apmLog = APMLogUnitTest.getImmutableLog("5casesMode", "files/5casesMOD.xes");
        int[] trace1acts = getActIndicatorArray(apmLog);
        assertArrayEquals(trace1acts, apmLog.get(0).getActivityInstancesIndicatorArray());
    }
}