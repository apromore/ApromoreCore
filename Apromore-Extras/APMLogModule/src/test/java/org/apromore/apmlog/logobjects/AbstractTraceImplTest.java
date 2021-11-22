package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.Arrays;

public class AbstractTraceImplTest {

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
    public void getActivityInstancesIndicator() throws Exception {
        APMLog apmLog = APMLogUnitTest.getImmutableLog("5casesMode", "files/5casesMOD.xes");
        String expected = Arrays.toString(getActIndicatorArray(apmLog));
        assertEquals(expected, apmLog.get(0).getActivityInstancesIndicator());
    }

    @Test
    public void getActivityInstancesIndicatorArray() throws Exception {
        APMLog apmLog = APMLogUnitTest.getImmutableLog("5casesMode", "files/5casesMOD.xes");
        int[] trace1acts = getActIndicatorArray(apmLog);
        assertArrayEquals(trace1acts, apmLog.get(0).getActivityInstancesIndicatorArray());
    }
}