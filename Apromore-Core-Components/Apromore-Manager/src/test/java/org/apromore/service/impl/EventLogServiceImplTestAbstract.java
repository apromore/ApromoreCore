/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.*;
import org.junit.Before;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;

public class EventLogServiceImplTestAbstract {



    protected static final int TEST_SIZE = 1000;

    private static final int RANDOM_POOL_SIZE = 65536;
    private static final int EVENT_POOL_SIZE = 32;

    private Random random;
    private String[] randomPool;
    private String[] eventPool;

    interface AttributeNamingStrategy {
        String createAttributeName();
    }

    @Before
    public void setUp() {
        random = new Random(42);
        randomPool = new String[RANDOM_POOL_SIZE];
        for (int i = 0; i < RANDOM_POOL_SIZE; i++) {
            randomPool[i] = randomString(random, 20);
        }
        eventPool = new String[EVENT_POOL_SIZE];
        for (int i = 0; i < EVENT_POOL_SIZE; i++) {
            eventPool[i] = "Event "+i;
        }
    }

    private String randomString(Random random, final int length) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = (char)(random.nextInt((int)(Character.MAX_VALUE)));
            sb.append(c);
        }
        return sb.toString();
    }

    private String getRandomString() {
        return randomPool[random.nextInt(RANDOM_POOL_SIZE)];
    }

    private String getRandomEvent() {
        return eventPool[random.nextInt(EVENT_POOL_SIZE)];
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

    protected void readSequentiallyCommon(XLog log) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        long startTime = System.nanoTime();
        System.out.println("Reading concept:name, lifecycle:transition, org:resource: ");
        long attributeCounter = 0;
        for (XTrace trace : log) {
            for (XEvent event : trace) {
                XAttributeMap attributes = event.getAttributes();
                assertNotNull(attributes.get(XConceptExtension.KEY_NAME));
                assertNotNull(attributes.get(XLifecycleExtension.KEY_TRANSITION));
                assertNotNull(attributes.get(XTimeExtension.KEY_TIMESTAMP));
                attributeCounter = attributeCounter + 3;
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

    protected void changeAttributes(XLog log) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        long startTime = System.nanoTime();
        System.out.println("Changing all attributes via entrySet(): ");
        long attributeCounter = 0;
        for (XTrace trace : log) {
            for (XEvent event : trace) {
                for (Map.Entry<String, XAttribute> attrEntry : event.getAttributes().entrySet()) {
                    if (attrEntry.getValue() instanceof XAttributeBoolean) {
                        attrEntry.setValue(new XAttributeBooleanImpl(attrEntry.getKey(),
                                !((XAttributeBoolean) attrEntry.getValue()).getValue(),
                                attrEntry.getValue().getExtension()));
                    } else if (attrEntry.getValue() instanceof XAttributeLiteral) {
                        if (attrEntry.getKey().equals(XConceptExtension.KEY_NAME)) {
                            attrEntry.setValue(new XAttributeLiteralImpl(attrEntry.getKey(),
                                    ((XAttributeLiteral) attrEntry.getValue()).getValue().concat(" NEW"),
                                    attrEntry.getValue().getExtension()));
                        } else {
                            attrEntry.setValue(new XAttributeLiteralImpl(attrEntry.getKey(), getRandomString(),
                                    attrEntry.getValue().getExtension()));
                        }
                    } else if (attrEntry.getValue() instanceof XAttributeContinuous) {
                        attrEntry.setValue(new XAttributeContinuousImpl(attrEntry.getKey(), random.nextDouble(),
                                attrEntry.getValue().getExtension()));
                    } else if (attrEntry.getValue() instanceof XAttributeDiscrete) {
                        attrEntry.setValue(new XAttributeDiscreteImpl(attrEntry.getKey(), random.nextInt(),
                                attrEntry.getValue().getExtension()));
                    } else if (attrEntry.getValue() instanceof XAttributeTimestamp) {
                        attrEntry.setValue(new XAttributeTimestampImpl(attrEntry.getKey(), Math.abs(random.nextLong()),
                                attrEntry.getValue().getExtension()));
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

    protected XLog createRandomLog(XFactory factory, int traces) {
        return createLog(factory, 40, traces, 2);
    }

    public XLog createLog(XFactory factory, int events, int traces, int attributes) {
        return createLog(factory, events, traces, attributes, false);
    }

    public XLog createLog(XFactory factory, int events, int traces, int attributes, boolean minimalAttributes) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        long startTime = System.nanoTime();
        System.out.println("Creating with " + factory.getClass().getSimpleName() + ": ");
        System.out.println(integerFormat.format(traces) + " traces");
        System.out.println(integerFormat.format(events * traces) + " events");
        int standardAttributes = minimalAttributes ? 1 : 4;
        long totalAttributes = events * traces * ((attributes * 4) + standardAttributes);
        System.out.println(integerFormat.format(totalAttributes) + " attributes");
        XLog testLog = factory.createLog();
        int traceCounter = 0;
        while (traceCounter++ < traces) {
            XTrace t = factory.createTrace();
            if (!minimalAttributes) {
                addAttribute(t, factory.createAttributeLiteral(XConceptExtension.KEY_NAME, "Trace " + traceCounter,
                        XConceptExtension.instance()));
            }
            createAttributes(factory, t, attributes, traceCounter);

            int eventCounter = 0;
            List<XEvent> eventList = new ArrayList<>();
            while (eventCounter++ < events) {
                XEvent e = factory.createEvent();
                createAttributes(factory, e, attributes, eventCounter);
                if (!minimalAttributes) {
                    addAttribute(e, factory.createAttributeTimestamp(XTimeExtension.KEY_TIMESTAMP,
                            (new Date().getTime() + eventCounter + traceCounter), XTimeExtension.instance()));
                    addAttribute(e, factory.createAttributeLiteral(XLifecycleExtension.KEY_TRANSITION,
                            XLifecycleExtension.StandardModel.COMPLETE.getEncoding(), XLifecycleExtension.instance()));
                    addAttribute(e, factory.createAttributeLiteral(XOrganizationalExtension.KEY_RESOURCE,
                            "Resource " + eventCounter, XOrganizationalExtension.instance()));
                }
                addAttribute(e, factory.createAttributeLiteral(XConceptExtension.KEY_NAME, getRandomEvent(),
                        XConceptExtension.instance()));
                eventList.add(e);
            }
            t.addAll(eventList);
            testLog.add(t);
        }
        long elapsedNanos = System.nanoTime() - startTime;
        System.out.println("Elapsed time: " + elapsedNanos / 1000000 + " ms");
        double elapsedSecond = (System.nanoTime() - startTime) / 1000000000.0;
        double attributesPerSecond = (traceCounter * events * (attributes * 4 + 3)) / elapsedSecond;
        System.out.println(numberFormat.format(attributesPerSecond) + " APS");
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        System.out.println("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
        return testLog;
    }

    protected MemoryUsage getMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage();
    }

    private void createAttributes(XFactory factory, XAttributable a, int attributes, int currentIndex) {
        int attributesCounter = 0;
        while (attributesCounter++ < attributes) {
            addAttribute(a, factory.createAttributeBoolean("boolean" + attributesCounter, random.nextBoolean(), null));
            addAttribute(a,
                    factory.createAttributeDiscrete("discrete" + attributesCounter, random.nextInt(100_000), null));
            addAttribute(a,
                    factory.createAttributeContinuous("continuous" + attributesCounter, random.nextDouble(), null));
            addAttribute(a, factory.createAttributeLiteral("literal" + attributesCounter, getRandomString(), null));
        }
    }

    private void addAttribute(XAttributable a, XAttribute attr) {
        a.getAttributes().put(attr.getKey(), attr);
    }

}
