package org.apromore.service.impl;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.junit.Before;
import org.junit.Test;
import org.xeslite.external.XFactoryExternalStore;

import static org.junit.Assert.*;

public class EventLogServiceImplTest {

    @Before
    public void setUp() throws Exception {

        XFactoryExternalStore.InMemoryStoreImpl factory = new XFactoryExternalStore.InMemoryStoreImpl();
        XFactoryRegistry.instance().setCurrentDefault(factory);
    }

    @Test
    public void readLogSummaries() {
    }

    @Test
    public void importLog() {
    }

    @Test
    public void updateLogMetaData() {
    }

    @Test
    public void exportLog() {
    }

    @Test
    public void getXLog() {
    }

    @Test
    public void deleteLogs() {
    }

    @Test
    public void importFromStream() {
    }

    @Test
    public void exportToStream() {
    }
}