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
package org.deckfour.xes;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.util.XTimer;
import org.deckfour.xes.xstream.XesXStreamPersistency;

import com.thoughtworks.xstream.XStream;

/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class StressTest {
	
	public static final Random random = new Random();
	public static XFactory factory = XFactoryRegistry.instance().currentDefault();
	
	public static final char CHARACTERS[] = new char[] {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
	};
	
	public static long NUM_TRACES = 0;
	public static long NUM_EVENTS = 0;
	public static long NUM_ATTRIBUTES = 0;

	public static String generateString(int minLength, int maxLength) {
		StringBuilder sb = new StringBuilder();
		int length = minLength + random.nextInt(maxLength - minLength);
		for(int i=0; i<length; i++) {
			sb.append(CHARACTERS[random.nextInt(CHARACTERS.length)]);
		}
		return sb.toString();
	}
	
	public static XAttribute generateAttribute() {
		String key = generateString(4, 15);
		XAttribute attribute;
		int typeIndex = random.nextInt(5);
		switch(typeIndex) {
		case 0:
			attribute = factory.createAttributeBoolean(key, random.nextBoolean(), null);
			break;
		case 1:
			attribute = factory.createAttributeContinuous(key, random.nextDouble(), null);
			break;
		case 2:
			attribute = factory.createAttributeDiscrete(key, random.nextLong(), null);
			break;
		case 3:
			attribute = factory.createAttributeLiteral(key, generateString(10, 30), null);
			break;
		default:
			attribute = factory.createAttributeTimestamp(key, random.nextLong(), null);
			break;
		}
		addAttributes(attribute, 0.05, 1, 5);
		NUM_ATTRIBUTES++;
		return attribute;
	}
	
	public static void addAttributes(XAttributable attributable, double chance, int minNr, int maxNr) {
		if(random.nextDouble() < chance) {
			XAttribute childAttr;
			for(int i=random.nextInt(maxNr - minNr) + minNr; i>0; i--) {
				childAttr = generateAttribute();
				attributable.getAttributes().put(childAttr.getKey(), childAttr);
			}
		}
	}
	
	public static XEvent createEvent() {
		XEvent event = factory.createEvent();
		addAttributes(event, 1.0, 2, 20);
		NUM_EVENTS++;
		return event;
	}
	
	public static XTrace createTrace(int minLength, int maxLength) {
		XTrace trace = factory.createTrace();
		addAttributes(trace, 0.9, 3, 50);
		int length = minLength + random.nextInt(maxLength - minLength);
		for(int i=0; i<length; i++) {
			trace.add(createEvent());
		}
		NUM_TRACES++;
		return trace;
	}
	
	public static XLog createLog(int minTraces, int maxTraces, int minTraceLength, int maxTraceLength) {
		XLog log = factory.createLog();
		addAttributes(log, 0.9, 3, 50);
		int length = minTraces + random.nextInt(maxTraces - minTraces);
		for(int i=0; i<length; i++) {
			log.add(createTrace(minTraceLength, maxTraceLength));
		}
		return log;
	}
	
	public static void walkLog(XLog log) {
		walkAttributes(log);
		for(XTrace trace : log) {
			walkTrace(trace);
		}
	}
	
	public static void walkTrace(XTrace trace) {
		walkAttributes(trace);
		for(XEvent event : trace) {
			walkAttributes(event);
		}
	}
	
	public static void walkAttributes(XAttributable attributable) {
		XAttributeMap attributeMap = attributable.getAttributes();
		for(XAttribute attribute : attributeMap.values()) {
			String key = attribute.getKey();
			String value = attribute.toString();
			key.trim();
			value.trim();
			walkAttributes(attribute);
		}
	}
	
	public static void runTest() {
		Thread testRunner = new Thread() {

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				XTimer timer = new XTimer();
				XLog log;
				List<XLog> parsedLog = null;
				// create log
				// XLog log = createLog(20, 3000, 100, 2000);//createLog(150, 151, 500, 1000);
				// import log
				XFactory factory = new XFactoryNaiveImpl();
				XesXmlParser parser = new XesXmlParser(factory);
				try {
					parsedLog = parser.parse(new GZIPInputStream(new FileInputStream("/Users/frank/Projects/OpenXES 2.26/test/procmin20180612_F2_5M.xes.gz")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				log = parsedLog.iterator().next();
				timer.stop();
				System.out.println("Imported log:");
//				System.out.println("Created log:");
//				System.out.println("  Traces: " + NUM_TRACES);
//				System.out.println("  Events: " + NUM_EVENTS);
//				System.out.println("  Attributes: " + NUM_ATTRIBUTES);
				System.out.println("Duration: " + timer.getDurationString());
				System.gc();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				System.out.println("Memory Used: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
				// walk log
				timer.start();
				walkLog(log);
				timer.stop();
				System.out.println("Walked log.");
				System.out.println("Duration: " + timer.getDurationString());
				// serializing to GZIPped file

				System.out.println("Serializing log.");
				timer.start();
				try {
					OutputStream os = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(new File("/Users/frank/Desktop/stresstest.xes.gz"))));
					(new XesXmlSerializer()).serialize(log, os);
					timer.stop();
					os.flush();
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Duration: " + timer.getDurationString());

				File sFile = new File("/Users/frank/Desktop/testxstream.xml");
				if(sFile.exists()) {
					sFile.delete();
				}
				try {
					sFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				XStream xstream = new XStream();
				XesXStreamPersistency.register(xstream);
				try {
					System.out.println("Serializing log with XStream");
					timer.start();
					OutputStream oStream = new BufferedOutputStream(new FileOutputStream(sFile));
					xstream.toXML(log, oStream);
					timer.stop();
					System.out.println("Duration: " + timer.getDurationString());
					System.out.println("Deserializing log with XStream");
					timer.start();
					XLog log2 = (XLog)xstream.fromXML(new BufferedInputStream(new FileInputStream(sFile)));
					System.out.println("Duration: " + timer.getDurationString());
					// walk log
					timer.start();
					walkLog(log2);
					timer.stop();
					System.out.println("Walked deserialized log.");
					System.out.println("Duration: " + timer.getDurationString());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		};
		
		testRunner.start();
	}

	public static void main(String[] args) {
		runTest();
	}

}
