package org.apromore.service.csvimporter.impl;

import com.google.common.io.ByteStreams;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apromore.service.csvimporter.*;
import org.apromore.service.csvimporter.CSVImporterLogic.InvalidCSVException;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
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

    void setup(CSVReader reader) throws InvalidCSVException, IOException {
        List<String> header = new ArrayList<String>();
        List<String> line = new ArrayList<String>();
        ListModelList<String[]> result = new ListModelList<>();

        Collections.addAll(header, reader.readNext());

        line = Arrays.asList(reader.readNext());
        if (line.size() < 2 && line != null) {
            while (line.size() < 2 && line != null) {
                line = Arrays.asList(reader.readNext());
            }
        }

        if (line != null && header != null && !line.isEmpty() && !header.isEmpty() && line.size() > 1) {
            csvImporterLogic.setLine(line);
            csvImporterLogic.setHeads(header);
            csvImporterLogic.setOtherTimestamps(result);
        } else {
            throw new InvalidCSVException("Could not parse file!");
        }

        if (line.size() != header.size()) {
            reader.close();
            throw new InvalidCSVException("Number of columns in the header does not match number of columns in the data");
        } else {
            csvImporterLogic.setLists(line.size(), csvImporterLogic.getHeads(), AttribWidth - 20 + "px");
        }
    }


    // Test cases

    /** Test {@link CSVImporterLogic.prepareXesModel} against <code>test1.csv</code>. */
    @Test
    public void testPrepareXesModel_test1() throws Exception {

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test1.csv");
        setup(csvReader);
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test1.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertEquals(8, logModel.getLineCount());
        assertEquals(8, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }


    // Internal methods

    private static CSVReader newCSVReader(String filename) {
        return new CSVReaderBuilder(new InputStreamReader(CSVImporterLogicImplUnitTest.class.getResourceAsStream(filename), Charset.forName("utf-8")))
            .withSkipLines(0)
            .withCSVParser((new RFC4180ParserBuilder())
                               .withSeparator(',')
                               .build())
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .build();
    }

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }
}
