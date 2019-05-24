package org.apromore.plugin.portal.CSVImporterPortal;

import com.opencsv.CSVReader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.sun.xml.xsom.impl.scd.Iterators;
import org.deckfour.xes.model.XLog;
import org.junit.Ignore;
import org.junit.Test;

/** Test suite for {@link CsvToXes}. */
public class CsvToXesUnitTest {

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

        CsvToXes c2x = new CsvToXes();

        // Read the header and first line to guess column types
        InputStream in = CsvToXesUnitTest.class.getClassLoader().getResourceAsStream(fileName);
        CSVReader csvReader = new CSVReader(new InputStreamReader(in));

        String[] header = csvReader.readNext();
        System.out.println("H: " + Arrays.toString(header));
        c2x.setHeads(header);
        String[] line = csvReader.readNext();

        System.out.println("L: " + Arrays.toString(line));
//        c2x.setLine(line);
//        c2x.setOtherTimestamps();
        
        // Convert to XES
        in = CsvToXesUnitTest.class.getClassLoader().getResourceAsStream(fileName);
        List<LogModel> traces = c2x.prepareXesModel(new InputStreamReader(in));
        XLog xlog = c2x.createXLog(traces);
    }
}
