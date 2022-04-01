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
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.impl;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.util.Random;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogRepositoryCustomImplTest {

    TemporaryCacheService temporaryCacheService=new TemporaryCacheService();

    @Test
    void testImportFromFile() {

        System.out.println(ClassLoader.getSystemResource("XES_logs/SepsisCases.xes"));

        System.out.println(System.getProperty("user.dir"));

        try {
            String name = ClassLoader.getSystemResource("XES_logs/SepsisCases.xes").getPath();
            System.out.println("name: " + name);

            XFactory factory = XFactoryRegistry.instance().currentDefault();

            XLog log = temporaryCacheService.importFromFile(factory,new FileInputStream(new File(name)), name);

            System.out.println("log: " + log);

            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
            System.out.println("logInfo: " + logInfo);

            readSequentially(log);

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    protected void readSequentially(XLog log) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        long startTime = System.nanoTime();
        System.out.println("Reading all attributes via values(): ");
        long attributeCounter = 0;
        for (XTrace trace : log) {
            for (XEvent event : trace) {
                for (XAttribute attr : event.getAttributes().values()) {
                    assertNotNull(attr.getKey());
                    if (attr instanceof XAttributeLiteral) {
                        assertNotNull(((XAttributeLiteral) attr).getValue());
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
        Random random = new Random();
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
                    if (attr instanceof XAttributeLiteral) {
                        assertNotNull(((XAttributeLiteral) attr).getValue());
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
}
