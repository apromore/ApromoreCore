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
import static org.junit.Assert.*;
import org.xeslite.external.XFactoryExternalStore;
import org.xeslite.parser.XesLiteXmlParser;

public class LogUtilitesTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void addStartEndEvents() throws Exception {

        XFactoryExternalStore.InMemoryStoreImpl factory = new XFactoryExternalStore.InMemoryStoreImpl();
        XesLiteXmlParser parser =  new XesLiteXmlParser(factory, false);
        List<XLog> parsedLog = parser.parse(new GZIPInputStream(new FileInputStream("./BPI13.xes.gz")));

        XLog log = parsedLog.iterator().next();

        LogUtilites.addStartEndEvents(log);

    }
}