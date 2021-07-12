/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.service.loganimation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;

public class CreateTestLog {
	private Map<XTrace,Integer> traceCopyCountMap = new HashMap<>();
	
    public static void main(String[] args) {
        CreateTestLog creator = new CreateTestLog(); 
        
        final int COPY_TRACE_NUM = 199;
        try (FileInputStream is = new FileInputStream("Sepsis.xes.gz")) {
            XFactory factory = XFactoryRegistry.instance().currentDefault();
            XParser parser = new XesXmlGZIPParser(factory);
            List<XLog> logs = parser.parse(is);
            
            if (logs != null && !logs.isEmpty()) {
                XLog log = logs.iterator().next();
                XLog newLog = factory.createLog(log.getAttributes());
                
                for (XTrace trace : log) {
                    for (int i=0; i<=COPY_TRACE_NUM; i++) {
                        XTrace copyTrace = creator.copyTrace(trace, factory);
                        newLog.add(copyTrace);
                    }
                }
                
                try (FileOutputStream outputStream = new FileOutputStream("Sepsis_big.xes.gz")) {
                    XSerializer serializer = new XesXmlGZIPSerializer();
                    serializer.serialize(newLog, outputStream);
                }
            }
            else {
                System.out.println("Error: cannot import log");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }
    
    private int getCopyCount(XTrace trace) {
    	return traceCopyCountMap.containsKey(trace) ? traceCopyCountMap.get(trace) : 0;
    }
    
    private int getNextCopyNumber(XTrace trace) {
    	int nextCopyNumber = getCopyCount(trace) + 1;
    	traceCopyCountMap.put(trace, nextCopyNumber);
    	return nextCopyNumber;
    }
    
    private XTrace copyTrace(XTrace trace, XFactory factory) {
    	String traceId = XConceptExtension.instance().extractName(trace);
        XTrace newTrace = factory.createTrace((XAttributeMap)trace.getAttributes().clone());
        XConceptExtension.instance().assignName(newTrace, traceId + "_" + getNextCopyNumber(trace)); 
        int random = getRandomNumberInRange(4*3600*1000, 10*24*3600*1000); //4h->10days
        for (XEvent event : trace) {
            XEvent newEvent = factory.createEvent((XAttributeMap)event.getAttributes().clone());
            long eventTimestamp = XTimeExtension.instance().extractTimestamp(event).getTime();
            XTimeExtension.instance().assignTimestamp(newEvent, eventTimestamp + random); 
            newTrace.add(newEvent);
        }
        return newTrace;
    }
    
    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }
    
    
}
