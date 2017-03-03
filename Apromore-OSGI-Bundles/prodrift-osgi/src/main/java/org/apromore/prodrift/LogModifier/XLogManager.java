/*
 * Copyright � 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.prodrift.LogModifier;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.AbstractMap.SimpleEntry;

import org.apromore.prodrift.im.BlockStructure;
import org.apromore.prodrift.util.Utils;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XMxmlGZIPSerializer;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
//import org.processmining.processtree.Block;
//import org.processmining.processtree.Node;
//import org.processmining.processtree.ProcessTree;

import com.google.common.collect.Multimap;


public class XLogManager {
	
	
	public static XLog openLog(InputStream inputLogFile, String name) throws Exception {
		XLog log = null;

		
		try{
			
			if(name.toLowerCase().endsWith("mxml.gz")){
				XMxmlGZIPParser parser = new XMxmlGZIPParser();
	//			if(parser.canParse()){
	//				try {
						log = parser.parse(inputLogFile).get(0);
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//			}
			}else if(name.toLowerCase().endsWith("mxml") || 
					name.toLowerCase().endsWith("xml")){
				XMxmlParser parser = new XMxmlParser();
	//			if(parser.canParse(inputLogFile)){
	//				try {
						log = parser.parse(inputLogFile).get(0);
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//			}
			}
	
			if(name.toLowerCase().endsWith("xes.gz")){
				XesXmlGZIPParser parser = new XesXmlGZIPParser();
	//			if(parser.canParse(inputLogFile)){
	//				try {
						log = parser.parse(inputLogFile).get(0);
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//			}
			}else if(name.toLowerCase().endsWith("xes")){
				XesXmlParser parser = new XesXmlParser();
	//			if(parser.canParse(inputLogFile)){
	//				try {
						log = parser.parse(inputLogFile).get(0);
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//			}
			}

		}catch(Exception e)
		{
			throw new Exception("File invalid");
		}

		if(log == null)
			throw new Exception("Oops ...");
		
		return log;
	}
	
	public static ByteArrayOutputStream saveLogInMemory(XLog log, String logFilePath) {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			XSerializer serializer = getSerializer(logFilePath);
			serializer.serialize(log, os);
			
		} catch (Exception e) {
			System.out.println("Exception when writing sublog in stream "+e.toString());
		}
		
		return os;

}
	
	public static void saveLogInDisk(XLog log, String logFilePath) {
		
		try {
			XSerializer serializer = getSerializer(logFilePath);
			OutputStream os = new FileOutputStream(new File(logFilePath));
			serializer.serialize(log, os);
		} catch (Exception e) {
			System.out.println("Exception when writing file "+e.toString());
		}
		
	}
	
	
	public static XSerializer getSerializer(String logName)
	{
		
		XSerializer xs = null;
		
		if(logName.toLowerCase().endsWith("mxml.gz")){
			
			xs = new XMxmlGZIPSerializer();
			
		}else if(logName.toLowerCase().endsWith("mxml") || 
				logName.toLowerCase().endsWith("xml")){
			
			xs = new XMxmlSerializer();
			
		}

		if(logName.toLowerCase().endsWith("xes.gz")){
			
			xs = new XesXmlGZIPSerializer();
			
		}else if(logName.toLowerCase().endsWith("xes")){
			
			xs = new XesXmlSerializer();
			
		}
		    
		return xs;
		
	}
	
	public static String getExtension(String logName)
	{
		
		String ext = null;
		
		if(logName.toLowerCase().endsWith("mxml.gz")){
			
			ext = logName.substring(logName.indexOf(".") + 1);
			
		}else if(logName.toLowerCase().endsWith("mxml") || 
				logName.toLowerCase().endsWith("xml")){
			
			ext = logName.substring(logName.indexOf(".") + 1);
			
		}

		if(logName.toLowerCase().endsWith("xes.gz")){
			
			ext = logName.substring(logName.indexOf(".") + 1);
			
		}else if(logName.toLowerCase().endsWith("xes")){
			
			ext = logName.substring(logName.indexOf(".") + 1);
			
		}
		    
		return ext;
		
		
	}
	
	
	
	public static XLog readLog(InputStream logFile, String name)
	{
		XLog lg = null;
		try {
			lg = XLogManager.openLog(logFile, name);
		} catch (Exception e) {
			return null;
		}
		
		return lg;
		
	}
	
	public static boolean validateLog(InputStream logFile, String name)
	{
		XLog lg = null;
		try {
			lg = XLogManager.openLog(logFile, name);
		} catch (Exception e) {
			return false;
		}
		
		return true;
		
	}
	
	public static List<ByteArrayOutputStream> getSubLogs(byte[] log, String logName, List<BigInteger> startOfTransitionPoints,	
			List<BigInteger> endOfTransitionPoints) throws Exception
	{
		
		XLog lg = null;
		try {
			lg = XLogManager.openLog(new ByteArrayInputStream(log), logName);
		} catch (Exception e) {
			throw e;
		}
	
		List<ByteArrayOutputStream> eventLogList = null;
		try{
			
			eventLogList = new ArrayList<ByteArrayOutputStream>();		
			for(int i = 0; i < endOfTransitionPoints.size(); i++)
			{
				
				int start = endOfTransitionPoints.get(i).intValue();
				int end  = startOfTransitionPoints.get(i).intValue();
				if(start < end)
				{
					ByteArrayOutputStream baos = XLogManager.saveLogInMemory(getSubLog(lg, start, end), 
							logName.substring(0, logName.indexOf(".")) + "_sublog" + "_" + start+"_" + end + "." + XLogManager.getExtension(logName));
//					System.out.println(logName.substring(0, logName.indexOf(".")) + "_sublog" + "_" + start+"_" + end + "." + XLogReader.getExtension(logName));
					eventLogList.add(baos);
//					System.out.println(baos.toByteArray().length);
				
				}
				
			}
			
		}catch(Exception e)
		{
			
			throw e;
			
		}
		
		return eventLogList;
		
		
	}
	
	public static XLog getSubLog(XLog log, int start, int end){
		
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttributeMap attributeMap;
		List<XTrace> chunkTraceList;
		XLog chunkLog;

		int noEventsInLog;
		
		noEventsInLog = 0;
		chunkLog = factory.createLog();
		for (XExtension extension : log.getExtensions())
			chunkLog.getExtensions().add(extension);

		attributeMap = (XAttributeMap)log.getAttributes().clone();
		XAttribute conceptNameAttribute = factory.createAttributeLiteral("concept:name", attributeMap.get("concept:name").clone().toString()+"_"+start+"_"+end, XConceptExtension.instance());
		attributeMap.put("concept:name", conceptNameAttribute);
		
		chunkLog.setAttributes(attributeMap);
		

		chunkTraceList = log.subList(start, end);

		for(XTrace trace : chunkTraceList){
			chunkLog.add((XTrace)trace.clone());
			noEventsInLog += trace.size();
		}

//		System.out.println("No. Events in Log: "+noEventsInLog);
		
		
		return (XLog)chunkLog.clone();

	}
	
	public static XLog getSubLogByStartDate(XLog log, Date fromDate, Date toDate){
		
		XLog orderedLog = orderByTraceStartTimeStamp(log);
		XLog subLog = (XLog)orderedLog.clone();
		subLog.clear();
		
		long fromDate_ms = fromDate.getTime();
		long toDate_ms = toDate.getTime();
		
		for(int i = 0; i < orderedLog.size(); i++)
		{
			
			XTrace t = orderedLog.get(i);
			long traceTime_ms = getEventTime(t.get(0)).getValueMillis();
			if(traceTime_ms >= fromDate_ms && traceTime_ms < toDate_ms)
			{
				
				subLog.add((XTrace)t.clone());
				
			}
			
		}
		
		return subLog;
		
	}
	
public static XLog getSubLogByCompletiontDate(XLog log, Date fromDate, Date toDate){
		
		XLog orderedLog = orderByTraceCompletionTimeStamp(log);
		XLog subLog = (XLog)orderedLog.clone();
		subLog.clear();
		
		long fromDate_ms = fromDate.getTime();
		long toDate_ms = toDate.getTime();
		
		for(int i = 0; i < orderedLog.size(); i++)
		{
			
			XTrace t = orderedLog.get(i);
			long traceTime_ms = getEventTime(t.get(t.size()-1)).getValueMillis();
			if(traceTime_ms >= fromDate_ms && traceTime_ms < toDate_ms)
			{
				
				subLog.add((XTrace)t.clone());
				
			}
			
		}
		
		return subLog;
		
	}


// the result will be 45 working days from the date specified that have at least 1 trace completed
public static XLog getSubLogByCompletiontDate_ByEffectiveDays(XLog log, Date fromDate, int numOfDays){
	
	XLog orderedLog = orderByTraceCompletionTimeStamp(log);
	XLog subLog = (XLog)orderedLog.clone();
	subLog.clear();
	
	Calendar fromDate_gc = GregorianCalendar.getInstance();
	fromDate_gc.setTime(fromDate);
	
	Calendar currentDate_gc = GregorianCalendar.getInstance();
	
	int numOfDaysIndex = 0;
	boolean firstDay = true;
	
	for(int i = 0; i < orderedLog.size(); i++)
	{
		
		XTrace curTrace = orderedLog.get(i);
		Date traceTime = XLogManager.getEventTime(curTrace.get(curTrace.size()-1)).getValue();
		Calendar traceTime_gc = GregorianCalendar.getInstance();
		traceTime_gc.setTime(traceTime);
		
		
		if(traceTime_gc.compareTo(fromDate_gc) >= 0)
		{
			
			if(firstDay)
			{
				
				currentDate_gc = traceTime_gc;
				firstDay = false;
				
			}
			
			if(traceTime_gc.get(Calendar.YEAR) != currentDate_gc.get(Calendar.YEAR) ||
					traceTime_gc.get(Calendar.MONTH) != currentDate_gc.get(Calendar.MONTH) ||
					traceTime_gc.get(Calendar.DAY_OF_MONTH) != currentDate_gc.get(Calendar.DAY_OF_MONTH))
			{
				
				numOfDaysIndex++;
				currentDate_gc = traceTime_gc;
				
			}
			
			if(numOfDaysIndex < numOfDays)
			{
				
				subLog.add((XTrace)curTrace.clone());
				
			}
			
		}
		
	}
	
	return subLog;	
	
}

	public static List<XEvent> getDirectPrecedingEvents(XTrace trace, XEvent event)
	{
		
		List<XEvent> precedingEvents = new ArrayList<>();
		for(int i = 0; i < trace.size(); i++)
		{
			
			XEvent curEvent = trace.get(i);
			if(XLogManager.getEventName(curEvent).compareToIgnoreCase(XLogManager.getEventName(event)) == 0)
			{
				
				if(i > 0)
				{
					
					precedingEvents.add(trace.get(i - 1));
					
				}
				
			}
			
		}
		
		return precedingEvents;
		
	}

public static int countDitinctCompletionDays(XLog log)
{
	
	XLog orderedLog = orderByTraceCompletionTimeStamp(log);
		
	Calendar currentDate_gc = GregorianCalendar.getInstance();
	XTrace firstTrace = orderedLog.get(0);
	currentDate_gc.setTime(XLogManager.getEventTime(firstTrace.get(firstTrace.size()-1)).getValue());
	
	
	int numOfDays = 1;
	
	for(int i = 0; i < orderedLog.size(); i++)
	{
		
		XTrace curTrace = orderedLog.get(i);
		Date traceTime = XLogManager.getEventTime(curTrace.get(curTrace.size()-1)).getValue();
		Calendar traceTime_gc = GregorianCalendar.getInstance();
		traceTime_gc.setTime(traceTime);
		
		if(traceTime_gc.get(Calendar.YEAR) != currentDate_gc.get(Calendar.YEAR) ||
				traceTime_gc.get(Calendar.MONTH) != currentDate_gc.get(Calendar.MONTH) ||
				traceTime_gc.get(Calendar.DAY_OF_MONTH) != currentDate_gc.get(Calendar.DAY_OF_MONTH))
		{
			
			numOfDays++;
			currentDate_gc = traceTime_gc;
			
		}
		
	}
	
	return numOfDays;	
	
}
	
	public void splitLogIntoChunks(XLog log, int splitSize){
		int noTraces = log.size();
		int noChunks = (int)Math.ceil((1.0*noTraces)/splitSize);
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttributeMap attributeMap;
		List<XTrace> chunkTraceList;
		XLog chunkLog;
		int sumChunks = 0;
		int noEventsInLog;
		for(int i = 0; i < noChunks; i++){
			noEventsInLog = 0;
			chunkLog = factory.createLog();
			for (XExtension extension : log.getExtensions())
				chunkLog.getExtensions().add(extension);

			attributeMap = (XAttributeMap)log.getAttributes().clone();
			XAttribute conceptNameAttribute = factory.createAttributeLiteral("concept:name", attributeMap.get("concept:name").clone().toString()+"_Split_"+i, XConceptExtension.instance());
			attributeMap.put("concept:name", conceptNameAttribute);
			
			chunkLog.setAttributes(attributeMap);
			
			if(i < noChunks-1)
				chunkTraceList = log.subList(i*splitSize, (i+1)*splitSize);
			else
				chunkTraceList = log.subList((noChunks-1)*splitSize, noTraces);
			for(XTrace trace : chunkTraceList){
				chunkLog.add((XTrace)trace.clone());
				noEventsInLog += trace.size();
			}
			sumChunks += chunkLog.size();
			System.out.println("No. Events in Log: "+noEventsInLog);
		}
		
		System.out.println("Original No. Traces: "+noTraces);
		System.out.println("No. Chunks: "+noChunks);
		System.out.println("Sum of Chunks: "+sumChunks);
	}
	
	public static String getEventName(XEvent e) {
		
		return e.getAttributes().get(XConceptExtension.KEY_NAME).toString().trim();
		
	}
	
	public static String getResource(XEvent e) {
		
		if(e.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE) != null)
			return e.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE).toString().trim();
		else
			return null;
		
	}
	
	public static String getEventType(XEvent e) {
		
		return  e.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString().trim();
		
	}
	
	public static XAttributeTimestamp getEventTime(XEvent e)
	{
		
		return (XAttributeTimestamp) e.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
		
	}
	
	public static boolean isCompleteEvent(XEvent e) {
		
		return getEventType(e).compareToIgnoreCase("complete") == 0;
		
	}
	
	public static XEvent addAttrToEvent(XEvent e, String attrKey, String value) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute Attr = factory.createAttributeLiteral(attrKey, value, XConceptExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(attrKey, Attr);
		
		return e;
		
	}
	
	public static XEvent addAttrToTrace(XEvent e, String attrKey, String value) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute Attr = factory.createAttributeLiteral(attrKey, value, XConceptExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(attrKey, Attr);
		
		return e;
		
	}
	
	public static XEvent addResourceToEvent(XEvent e, String resourceName) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute resAttr = factory.createAttributeLiteral(XOrganizationalExtension.KEY_RESOURCE, resourceName, XConceptExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(XOrganizationalExtension.KEY_RESOURCE, resAttr);
		
		return e;
		
	}
	
	
	public static XLog orderByTraceCompletionTimeStamp(XLog log) {
        
		Map<Integer, String> treeMap = new TreeMap<Integer, String>(
				new Comparator<Integer>() {
	 
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
	 
			});
		
		Map<Integer, Long> timeKeyMap = new HashMap<Integer, Long>();
		
        for (int i = 0; i < log.size(); i++) {
        	
            XTrace t = log.get(i);
            XAttributeTimestamp time = getEventTime(t.get(t.size() - 1));
            timeKeyMap.put(i, time.getValueMillis());
           
        }
        
        System.out.println("*********************************************");
        
        Map<Integer, Long> timeKeyMap_sorted = sortByComparator(timeKeyMap);
        
        XLog orderedLog = (XLog)log.clone();
        
        orderedLog.clear();
        
        Set<Integer> keySet = timeKeyMap_sorted.keySet();
        Iterator<Integer> it = keySet.iterator();
        
        while (it.hasNext()) {
        	
            int traceId = it.next();
            orderedLog.add((XTrace)log.get(traceId).clone());            
           
        }
                
//        for (int i = 0; i < orderedLog.size(); i++) {
//        	
//            XTrace t = orderedLog.get(i);
//            XAttributeTimestamp time = getEventTime(t.get(0));
//        }
        
       
        return orderedLog;
	}
	
	public static XLog orderByTraceStartTimeStamp(XLog log) {
        
		Map<Integer, String> treeMap = new TreeMap<Integer, String>(
				new Comparator<Integer>() {
	 
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
	 
			});
		
		Map<Integer, Long> timeKeyMap = new HashMap<Integer, Long>();
		
        for (int i = 0; i < log.size(); i++) {
        	
            XTrace t = log.get(i);
            XAttributeTimestamp time = getEventTime(t.get(0));
            timeKeyMap.put(i, time.getValueMillis());
           
        }
        
        System.out.println("*********************************************");
        
        Map<Integer, Long> timeKeyMap_sorted = sortByComparator(timeKeyMap);
        
        XLog orderedLog = (XLog)log.clone();
        
        orderedLog.clear();
        
        Set<Integer> keySet = timeKeyMap_sorted.keySet();
        Iterator<Integer> it = keySet.iterator();
        
        while (it.hasNext()) {
        	
            int traceId = it.next();
            orderedLog.add((XTrace)log.get(traceId).clone());          
           
        }
                
//        for (int i = 0; i < orderedLog.size(); i++) {
//        	
//            XTrace t = orderedLog.get(i);
//            XAttributeTimestamp time = getEventTime(t.get(0));
//        }
        
       
        return orderedLog;
	}
	
	private static Map<Integer, Long> sortByComparator(Map<Integer, Long> unsortMap) {
		 
		// Convert Map to List
		List<Entry<Integer, Long>> list =
			new LinkedList<Entry<Integer, Long>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<Integer, Long>>() {
			public int compare(Entry<Integer, Long> o1,
                                           Entry<Integer, Long> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Long> sortedMap = new LinkedHashMap<Integer, Long>();
		for (Iterator<Entry<Integer, Long>> it = list.iterator(); it.hasNext();) {
			Entry<Integer, Long> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static int getNumberOfTracesContainingEitherAorB(XLog log, String a_name, String b_name)
	{
		
		int count = 0;
		boolean contains_a = false;
		boolean contains_b = false;
		for (int i = 0; i < log.size(); i++) {
        	
            XTrace trace = log.get(i);
            for(XEvent event: trace)
    		{
    			
    			if(getEventName(event).compareTo(a_name) == 0)
    				contains_a = true;
    			if(getEventName(event).compareTo(b_name) == 0)
    				contains_b = true;
    			
    			if(contains_a && contains_b)
    				break;
    			
    		}
            
            if((contains_a && !contains_b) || (contains_b && !contains_a))
            	count++;
           
        }
		
		return count;
		
	}
	
	public static int countEvent(XTrace trace, String eventName) {
		int count = 0;
		for(XEvent event: trace)
		{
			
			if(getEventName(event).compareTo(eventName) == 0)
				count++;
			
		}
		
		return count;
	}
	
	public static void printCompletionDatesOfTraces(XLog log)
	{
		
		for (int i = 0; i < log.size(); i++) {
        	
            XTrace t = log.get(i);
            System.out.print("<Trace>");
        	
        	XAttributeTimestamp time = (XAttributeTimestamp) t.get(t.size() - 1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
        	System.out.print(time.getValue());
        	
        	System.out.println("</Trace>");
           
        }
		
	}
	
	public static void printStartDatesOfTraces(XLog log)
	{
		
		for (int i = 0; i < log.size(); i++) {
        	
            XTrace t = log.get(i);
            System.out.print("<Trace>");
        	
        	XAttributeTimestamp time = (XAttributeTimestamp) t.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
        	System.out.print(time.getValue());
        	
        	System.out.println("</Trace>");
           
        }
		
	}
	
	
	public static void main(String[] args) {
//        String logfile = "BPIC15_1.xes";
//        Path path = Paths.get(logfile);
//        byte[] logByteArray;
//        try {
//                        logByteArray = Files.readAllBytes(path);
//                        XLog xl = readLog(new ByteArrayInputStream(logByteArray), logfile);
//                        XLog xl_ordered = orderByTraceTimeStamp(xl);
//                        
//                        saveLogInDisk(xl_ordered, "BPIC15_1_ordered.xes", logfile);
//        } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//        }
		
		
		HashMap<String, String> hm = new HashMap<>();
		String k1 = new String("k1");
		hm.put(k1, "v1");
		
		String k11 = new String("k1");
		
		System.out.println(hm.containsKey(k11));
		
		
	}


}
