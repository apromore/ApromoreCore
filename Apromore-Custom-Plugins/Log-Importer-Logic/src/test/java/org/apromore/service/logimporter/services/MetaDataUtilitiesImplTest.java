package org.apromore.service.logimporter.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MetaDataUtilitiesImplTest {

    private final MetaDataUtilities metaDataUtilities = new MetaDataUtilitiesImpl();

    @Test
    void isTimestampTest() {

        List<List<String>> sampleLog = new ArrayList<>();
        List<String> line = new ArrayList<>();
        line.add("1");
        line.add("19-12-2019 15:13:05.9");
        sampleLog.add(line);

        String format_1 = "y";
        String format_2 = "dd-MM-yyyy HH:mm:ss.S";

        assertFalse(metaDataUtilities.isTimestamp(0, format_1, sampleLog));
        assertTrue((metaDataUtilities.isTimestamp(1, format_2, sampleLog)));
    }
}