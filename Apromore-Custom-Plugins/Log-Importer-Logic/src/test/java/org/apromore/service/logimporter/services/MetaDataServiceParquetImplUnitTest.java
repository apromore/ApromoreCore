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
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
 * #L%
 */

package org.apromore.service.logimporter.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.service.logimporter.model.LogMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MetaDataServiceParquetImplUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataServiceParquetImplUnitTest.class);
    private MetaDataService metaDataService;

    @BeforeEach
    void init() {
        metaDataService = new MetaDataServiceParquetImpl();
    }

    /**
     * Test {@link MetaDataService} with column header mapping in <code>test1-valid.parquet</code>.
     */
    @Test
    void testColumnHeaderMapping() throws Exception {

        LOGGER.info("\n************************************\ntest column header mapping");

        String testFile = "/test1-valid.parquet";
        Map<String, String> columnHeaderMap = new HashMap<>();
        columnHeaderMap.put("Non-existing column name", "test");
        columnHeaderMap.put("case_id", "Case ID");
        columnHeaderMap.put("activity", "Activity Instance");
        columnHeaderMap.put("start_date", "Start Date");
        columnHeaderMap.put("completion_time", "End Time");

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", columnHeaderMap);

        List<String> expectedMappedHeader =
            Arrays.asList("Case ID", "Activity Instance", "Start Date", "End Time", "process_type");
        // Validate result
        assertEquals(expectedMappedHeader, logMetaData.getHeader());
    }

    @Test
    void testGenerateSampleLog() throws Exception {
        List<String> row1 =
            Arrays.asList("case2", "activity1", "2019-09-23 15:13:05.071", "2019-09-23 15:13:05.132", "1");
        List<String> row2 =
            Arrays.asList("case1", "activity1", "2019-09-23 15:13:05.114", "2019-09-23 15:13:05.132", "1");
        List<String> row3 =
            Arrays.asList("case2", "activity2", "2019-09-23 15:13:05.133", "2019-09-23 15:13:05.133", "1");

        List<List<String>> expectedSampleLog = Arrays.asList(row1, row2, row3);
        String testFile = "/test1-valid.parquet";

        assertEquals(expectedSampleLog, metaDataService.generateSampleLog(this.getClass().getResourceAsStream(testFile),
            50, "UTF-8"));
    }
}
