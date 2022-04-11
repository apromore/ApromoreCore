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
package org.apromore.portal.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apromore.portal.common.VersionSummaryTypes;
import org.apromore.portal.model.VersionSummaryType;
import org.junit.jupiter.api.Test;

class VersionSummaryComparatorUnitTest {

    @Test
    public void versionNumberTestCase() {
        List<VersionSummaryType> list = Arrays.asList(
            createVersionSummaryWithVersionNo("3"),
            createVersionSummaryWithVersionNo("2.0"),
            createVersionSummaryWithVersionNo("5"),
            createVersionSummaryWithVersionNo("10"),
            createVersionSummaryWithVersionNo("1"),
            createVersionSummaryWithVersionNo("2")
        );

        VersionSummaryComparator comparator = new VersionSummaryComparator(true, VersionSummaryTypes.BY_VERSION);
        list.sort(comparator);
        assertEquals(list.size(), 6);
        String[] expected = {"1","2","2.0","3","5","10"};

        List<String> actual=new ArrayList<>();
        for(VersionSummaryType var:list){
            actual.add(var.getVersionNumber());
        }
        assertArrayEquals(expected, actual.toArray());

    }

    private VersionSummaryType createVersionSummaryWithVersionNo(String versionNo) {
        VersionSummaryType var= new VersionSummaryType();
        var.setVersionNumber(versionNo);
        return var;
    }

}
