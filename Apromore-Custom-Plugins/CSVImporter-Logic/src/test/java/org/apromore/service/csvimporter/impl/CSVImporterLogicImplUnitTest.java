package org.apromore.service.csvimporter.impl;

import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apromore.service.csvimporter.*;
import org.apromore.service.csvimporter.CSVImporterLogic.InvalidCSVException;
import org.deckfour.xes.model.XLog;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.zkoss.zul.ListModelList;

/** Test suite for {@link CSVImporterLogicImpl}. */
public class CSVImporterLogicImplUnitTest {

    private static Integer AttribWidth = 150;

    /** Test instance. */
    private CSVImporterLogic csvImporterLogic = new CSVImporterLogicImpl();


    // Test cases

    @Test
    @Ignore("CSVImporterLogic setup not yet implemented")
    public void testPrepareXesModel_test1() throws Exception {

        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader("/test1.csv"));

        // Validate result
        assertEquals(8, logModel.getLineCount());
        assertEquals(8, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
    }


    // Internal methods

    private static CSVReader csvReader(String filename) {
        return new CSVReaderBuilder(new InputStreamReader(CSVImporterLogicImplUnitTest.class.getResourceAsStream(filename), Charset.forName("utf-8")))
            .withSkipLines(0)
            .withCSVParser((new RFC4180ParserBuilder())
                               .withSeparator(',')
                               .build())
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .build();
    }
}
