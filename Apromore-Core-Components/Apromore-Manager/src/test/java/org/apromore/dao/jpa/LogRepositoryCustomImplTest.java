package org.apromore.dao.jpa;

import org.apromore.service.CanoniserService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.*;
import org.junit.Before;
import org.junit.Test;
import org.apromore.dao.jpa.LogRepositoryCustomImpl;

import javax.inject.Inject;

import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FilterInputStream;
import java.io.BufferedInputStream;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Random;

import org.apromore.dao.jpa.LogRepositoryCustomImpl;

import org.xeslite.external.XFactoryExternalStore;
import org.xeslite.external.XFactoryExternalStore.InMemoryStoreImpl;
import org.xeslite.lite.factory.XFactoryLiteImpl;

import static org.junit.Assert.*;

public class LogRepositoryCustomImplTest {

    LogRepositoryCustomImpl logRepo = new LogRepositoryCustomImpl();

    private Random random;
    private String[] randomPool;
    private String[] eventPool;

    protected void readSequentially(XLog log) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        long startTime = System.nanoTime();
        System.out.println("Reading all attributes via values(): ");
        long attributeCounter = 0;
        for (XTrace trace : log) {
            for (XEvent event : trace) {
                for (XAttribute attr : event.getAttributes().values()) {
                    assertNotNull(attr.getKey());
                    if (attr instanceof XAttributeBoolean) {
                        assertNotNull(((XAttributeBoolean) attr).getValue());
                    } else if (attr instanceof XAttributeLiteral) {
                        assertNotNull(((XAttributeLiteral) attr).getValue());
                    } else if (attr instanceof XAttributeContinuous) {
                        assertNotNull(((XAttributeContinuous) attr).getValue());
                    } else if (attr instanceof XAttributeDiscrete) {
                        assertNotNull(((XAttributeDiscrete) attr).getValue());
                    } else if (attr instanceof XAttributeTimestamp) {
                        assertNotNull(((XAttributeTimestamp) attr).getValue());
                    }
                    attributeCounter++;
                }
            }
        }
        long elapsedNanos = System.nanoTime() - startTime;
        System.out.println("Elapsed time: " + elapsedNanos / 1000000 + " ms");
        double elapsedSecond = (System.nanoTime() - startTime) / 1000000000.0;
        double attributesPerSecond = attributeCounter / elapsedSecond;
        System.out.println(numberFormat.format(attributesPerSecond) + " APS");
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        System.out.println("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
    }

    protected void readRandom(XLog log) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        long startTime = System.nanoTime();
        System.out.println("Reading all attributes via values() of random traces & events: ");
        long attributeCounter = 0;
        int traceCounter = 0;
        while (traceCounter < log.size()) {
            XTrace trace = log.get(random.nextInt(log.size()));
            int eventCounter = 0;
            while (eventCounter < trace.size()) {
                XEvent event = trace.get(random.nextInt(trace.size()));
                for (String key : event.getAttributes().keySet()) {
                    XAttribute attr = event.getAttributes().get(key);
                    assertNotNull(attr);
                    if (attr instanceof XAttributeBoolean) {
                        assertNotNull(((XAttributeBoolean) attr).getValue());
                    } else if (attr instanceof XAttributeLiteral) {
                        assertNotNull(((XAttributeLiteral) attr).getValue());
                    } else if (attr instanceof XAttributeContinuous) {
                        assertNotNull(((XAttributeContinuous) attr).getValue());
                    } else if (attr instanceof XAttributeDiscrete) {
                        assertNotNull(((XAttributeDiscrete) attr).getValue());
                    }
                    attributeCounter++;
                }
                eventCounter++;
            }
            traceCounter++;
        }
        long elapsedNanos = System.nanoTime() - startTime;
        System.out.println("Elapsed time: " + elapsedNanos / 1000000 + " ms");
        double elapsedSecond = (System.nanoTime() - startTime) / 1000000000.0;
        double attributesPerSecond = attributeCounter / elapsedSecond;
        System.out.println(numberFormat.format(attributesPerSecond) + " APS");
        System.gc();
        System.out.println("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
    }

    protected MemoryUsage getMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage();
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testImportFromFile() {



        System.out.println(ClassLoader.getSystemResource("XES_logs/SepsisCases.xes"));

        System.out.println(System.getProperty("user.dir"));

        try {
            String name = ClassLoader.getSystemResource("XES_logs/SepsisCases.xes").getPath();
            System.out.println("name: " + name);

//            FileInputStream fis = new FileInputStream(name);
//            System.out.println("fis: " + fis);

//            FilterInputStream filter = new BufferedInputStream(fis);// FilterInputStream是protected类型
//            while (filter.available() > 0) {
//                byte[] buf = new byte[1024];
//                int len = filter.read(buf);
//                String myStr = new String(buf, 0, len, "UTF-8");
//                System.out.print(myStr);
//            }
//            fis.close();

            XFactoryExternalStore.InMemoryStoreImpl factory = new XFactoryExternalStore.InMemoryStoreImpl();

//            XFactory factory = new XFactoryLiteImpl();
            XFactoryRegistry.instance().setCurrentDefault(factory);


            XLog log = logRepo.importFromFile(factory, name);

//            XFactory factory = new XFactoryNaiveImpl();
//            XLog log = logRepo.importFromInputStream(fis, new XesXmlParser(factory));


            System.out.println("log: " + log);

            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
            System.out.println("logInfo: " + logInfo);

            readSequentially(log);
//            readRandom(log);

//            XLog log = logRepo.importFromFile(new XFactoryNaiveImpl(), name);
//            System.out.println("xlog: " + log);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());

        }

    }



    @Test
    public void exportToFile() {
    }

    @Test
    public void importFromInputStream() {
    }

    @Test
    public void exportToInputStream() {
    }

    @Test
    public void getProcessLog() {

    }
}