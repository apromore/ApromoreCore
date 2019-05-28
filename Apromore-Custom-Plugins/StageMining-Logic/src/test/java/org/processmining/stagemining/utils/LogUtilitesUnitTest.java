package org.processmining.stagemining.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xeslite.external.XFactoryExternalStore;
import org.xeslite.parser.XesLiteXmlParser;

public class LogUtilitesUnitTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore("Test fails because Guava's Ints.fromBytes method is missing.")
    @Test
    public void addStartEndEvents() throws Exception {

        XFactoryExternalStore.InMemoryStoreImpl factory = new XFactoryExternalStore.InMemoryStoreImpl();
        XesLiteXmlParser parser = new XesLiteXmlParser(factory, false);
        List<XLog> parsedLog = parser.parse(new GZIPInputStream(LogUtilitesUnitTest.class.getClassLoader().getResourceAsStream("BPI13.xes.gz")));

        XLog log = parsedLog.iterator().next();

        LogUtilites.addStartEndEvents(log);

    }
}
