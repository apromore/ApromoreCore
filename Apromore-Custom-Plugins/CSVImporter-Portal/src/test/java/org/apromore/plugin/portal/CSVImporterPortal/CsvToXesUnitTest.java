package org.apromore.plugin.portal.CSVImporterPortal;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.impl.LogModel;
import org.deckfour.xes.model.XLog;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;

///** Test suite for {@link CsvToXes}. */
public class CsvToXesUnitTest {
    @Inject
    private CSVImporterLogic csvImporterLogic;
    /** Test with <code>CallcenterExample.csv</code> */
    @Ignore
    @Test
    public void testCreateXLog_CallcenterExample() throws IOException {
        testCreateXLog("CallcenterExample.csv");
    }

    /** Test with <code>PurchasingExample.csv</code> */
    @Ignore
    @Test
    public void testCreateXLog_PurchasingExample() throws IOException {
        testCreateXLog("PurchasingExample.csv");
    }

    private void testCreateXLog(String fileName) throws IOException {

//        csvImporterLogic c2x = new csvImporterLogic();

        // Read the header and first line to guess column types
        InputStream in = CsvToXesUnitTest.class.getClassLoader().getResourceAsStream(fileName);
        CSVReader csvReader = new CSVReader(new InputStreamReader(in));

        String[] header = csvReader.readNext();
        System.out.println("H: " + Arrays.toString(header));
        csvImporterLogic.setHeads(header);
        String[] line = csvReader.readNext();

        System.out.println("L: " + Arrays.toString(line));
//        c2x.setLine(line);
//        c2x.setOtherTimestamps();

        // Convert to XES
        in = CsvToXesUnitTest.class.getClassLoader().getResourceAsStream(fileName);
        List<LogModel> traces = csvImporterLogic.prepareXesModel(new InputStreamReader(in));
        XLog xlog = csvImporterLogic.createXLog(traces);
    }
}
