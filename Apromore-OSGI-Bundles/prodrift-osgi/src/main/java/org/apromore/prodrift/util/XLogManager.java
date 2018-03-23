/*
 * Copyright 2009-2018 The Apromore Initiative.
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
package org.apromore.prodrift.util;




import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apromore.prodrift.im.BlockStructure;
import org.apromore.prodrift.model.DriftPoint;
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
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.deckfour.xes.out.XMxmlGZIPSerializer;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
//import org.processmining.processtree.Block;
//import org.processmining.processtree.Node;
//import org.processmining.processtree.ProcessTree;


public class XLogManager {
	
	
	public static XLog openLog(InputStream inputLogFile, String name) throws Exception {
		XLog log = null;

		
//		try{
			
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
			
			

//		}catch(Exception e)
//		{
//			throw new Exception("File invalid");
//		}

		if(log == null)
			throw new Exception("Oops could not open the log file!");
		
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
			os.close();
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
			
			ext = "mxml.gz";
			
		}else if(logName.toLowerCase().endsWith("mxml") || 
				logName.toLowerCase().endsWith("xml")){
			
			ext = logName.substring(logName.lastIndexOf(".") + 1);
			
		}

		if(logName.toLowerCase().endsWith("xes.gz")){
			
			ext = "xes.gz";
			
		}else if(logName.toLowerCase().endsWith("xes")){
			
			ext = logName.substring(logName.lastIndexOf(".") + 1);
			
		}
		    
		return ext;
		
		
	}
	
	public static void GzipLogAndSaveInDisk(ByteArrayOutputStream baos, String logFilePath)
	{
		
        try
        {

        	GZIPOutputStream gzos =	new GZIPOutputStream(new FileOutputStream(logFilePath));
            gzos.write(baos.toByteArray());            
            gzos.flush();
            gzos.close();

        }catch (IOException e)
        {
            System.out.println("Failed to Gzip and write to disk!!");
        }
		
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
	
	public static XLog validateLog(InputStream logFile, String name)
	{
		
		XLog xlog = null;
		try {
			xlog = XLogManager.openLog(logFile, name);
		} catch (Exception e) {
			return null;	
		}
		
		
		return xlog;
		
	}
	

	public static List<XLog> getSubLogsAsXlogs(XLog lg, String logName, List<BigInteger> startOfTransitionPoints,	
			List<BigInteger> endOfTransitionPoints, boolean isEventBased) throws Exception
	{
		
		List<XLog> eventLogList = null;
		XLog eventStream = LogStreamer.logStreamer(lg, null, null);
		removeEventFromEventStream(eventStream, "DRIFT_PO");
		try{
			
			eventLogList = new ArrayList<XLog>();		
			for(int i = 0; i < endOfTransitionPoints.size(); i++)
			{
				
				int start = endOfTransitionPoints.get(i).intValue();
				int end  = startOfTransitionPoints.get(i).intValue();
				if(start < end)
				{
					
					XLog sublog = null;
					if(isEventBased)
						sublog = getSubLog_eventBased(eventStream, start, end);
					else
						sublog = getSubLog(lg, start, end);

					eventLogList.add(sublog);
//					System.out.println(baos.toByteArray().length);
				
				}
				
			}
			
		}catch(Exception e)
		{
			
			throw e;
			
		}
		
		return eventLogList;
		
		
	}
	
	public static List<ByteArrayOutputStream> getSubLogs(XLog xlog, String logName, List<BigInteger> startOfTransitionPoints,	
			List<BigInteger> endOfTransitionPoints, boolean isEventBased) throws Exception
	{
		
		XLog lg = xlog;
	
		List<ByteArrayOutputStream> eventLogList = null;
		XLog eventStream = LogStreamer.logStreamer(lg, null, null);
		try{
			
			eventLogList = new ArrayList<ByteArrayOutputStream>();		
			for(int i = 0; i < endOfTransitionPoints.size(); i++)
			{
				
				int start = endOfTransitionPoints.get(i).intValue();
				int end  = startOfTransitionPoints.get(i).intValue();
				if(start < end)
				{
					
					XLog sublog = null;
					if(isEventBased)
						sublog = getSubLog_eventBased(eventStream, start, end);
					else
						sublog = getSubLog(lg, start, end);
					ByteArrayOutputStream baos = XLogManager.saveLogInMemory(sublog, 
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
	
	public static XLog getSubLog_eventBased(XLog eventStream, int start, int end){
		
		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map = new HashMap<>();
		XLog subLog = new XLogImpl(eventStream.getAttributes());
		
		for(int i = start; i < end; i++)
		{
			
			XTrace curTrace = eventStream.get(i);
			String traceID = XLogManager.getTraceID(curTrace);
			XEvent curEvent = curTrace.get(0);
			Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
			if(TraceIndex_InSubLog == null)
			{
				
				XTrace t1 = new XTraceImpl((XAttributeMap)curTrace.getAttributes().clone());
				t1.add(curEvent);
				subLog.add(t1);
				TraceIndex_InSubLog = subLog.size() - 1;
				TraceId_TraceIndex_InSubLog_Map.put(traceID, TraceIndex_InSubLog);
				
			}else
			{
				
				XTrace t1 = subLog.get(TraceIndex_InSubLog);
				t1.add(curEvent);
				
			}
		}
		
		return subLog;

	}
	
	public static XLog getCompleteTracesSubLogFromSubTraceSubLog(XLog subLog, String[] startEventNames, 
			String[] endEventNames){
		
		List<String> startEventNamesList = Arrays.asList(startEventNames);
		List<String> endEventNamesList = Arrays.asList(endEventNames);
		
		XLog newsubLog = new XLogImpl(subLog.getAttributes());
		
		for(XTrace trace : subLog)
		{
			XEvent startEv = trace.get(0);
			XEvent endEv = trace.get(trace.size() - 1);
			if(startEventNamesList.contains(XLogManager.getEventName(startEv))
					&& endEventNamesList.contains(XLogManager.getEventName(endEv)))
			{
				newsubLog.add(trace);				
			}
		}
		
		return newsubLog;

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

//the result will be 45 working days from the date specified that have at least 1 trace completed
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
	
	private static void tracesName(XLog log) {
		
		String output=""; 
		for (int i = 0; i < log.size(); i++) {
			if (i == 480) {
				XTrace tr1 = log.get(i);
				System.out.println((tr1.getAttributes().get("concept:name")));
//						.toString().substring(0, 2));
			}
		}
	}

	public static String getTraceID(XTrace t) {
		
		return t.getAttributes().get(XConceptExtension.KEY_NAME).toString();
		
	}
	
	public static XAttributeTimestamp getTraceCompletionTime(XTrace t){
		XAttributeTimestamp lastPossibleTimeStamp = null; 
		for (int i = t.size(); i != 0; i--) {
			lastPossibleTimeStamp = getEventTime(t.get(i - 1));
			if (lastPossibleTimeStamp!=null)
				break;
	//		else
	//			System.out.println("trace without completion timestamp");
		}				
		return lastPossibleTimeStamp;
	}
	
	public static Boolean getBooleanAttrFromTrace(XTrace trace, String attrKey) {
		if(trace.getAttributes().containsKey(attrKey))
			return Boolean.valueOf(trace.getAttributes().get(attrKey).toString().trim());
		else
			return null;
	}
	public static String getEventName(XEvent e) {
		
		return e.getAttributes().get(XConceptExtension.KEY_NAME).toString();
		
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
	
	public static XAttribute getEventAttr(XEvent e, String attrKey)
	{
		
		return e.getAttributes().get(attrKey);
		
	}
	
	public static String getEventAttrAsString(XEvent e, String attrKey)
	{
		
		if(e.getAttributes().get(attrKey) != null)
			return e.getAttributes().get(attrKey).toString().trim();
		else
			return null;
		
	}
	
	/* Adds identical start and end activities to traces if needed */
	public static List<Set<String>> addStartAndEndActivities(XLog log)
	{
		
		Set<String> startActivities = new HashSet<>();
		Set<String> endActivities = new HashSet<>();
		
		boolean needsFakeStartActivity = false;
		boolean needsFakeEndActivity = false;
		
		String startActivity = null;
		String endActivity = null;
		for(XTrace trace: log)
		{
			if(trace.size() > 0)
			{
				XEvent startEvent = trace.get(0);
				XEvent endEvent = trace.get(trace.size() - 1);
				if(!needsFakeStartActivity)
				{
					if(startActivity != null)
					{
						if(XLogManager.getEventName(startEvent).compareTo(startActivity) != 0)
							needsFakeStartActivity = true;
					}else
						startActivity = XLogManager.getEventName(startEvent);
				}
				
				if(!needsFakeEndActivity)
				{
					if(endActivity != null)
					{
						if(XLogManager.getEventName(endEvent).compareTo(endActivity) != 0)
							needsFakeEndActivity = true;
					}else
						endActivity = XLogManager.getEventName(endEvent);
				}
			}
			
			if(needsFakeStartActivity && needsFakeEndActivity)
				break;
			
		}
		
		if(needsFakeStartActivity)
		{
			startActivity = "START";
			for(XTrace trace: log)
			{
				if(trace.size() > 0)
				{
					
					XAttributeTimestampImpl curStartTimeStamp = (XAttributeTimestampImpl) XLogManager.getEventTime(trace.get(0));
					XEvent startEvent = createEvent(startActivity, curStartTimeStamp.getValueMillis() - 1, "Complete");
					
					trace.add(0, startEvent);
				}
			}
		}
		
		if(needsFakeEndActivity)
		{
			endActivity = "END";
			for(XTrace trace: log)
			{
				if(trace.size() > 0)
				{
					
					XAttributeTimestampImpl curEndTimeStamp = (XAttributeTimestampImpl) XLogManager.getEventTime(trace.get(trace.size() - 1));
					XEvent endEvent = createEvent(endActivity, curEndTimeStamp.getValueMillis() + 1, "Complete");
					
					trace.add(trace.size(), endEvent);
				}
			}
		}
		
		startActivities.add(startActivity);
		endActivities.add(endActivity);
		
		List<Set<String>> startAndEndActivities = new ArrayList<>();
		startAndEndActivities.add(startActivities);
		startAndEndActivities.add(endActivities);
		
		return startAndEndActivities;		
		
	}
	
	
	public static boolean isCompleteEvent(XEvent e) {
		
		return getEventType(e).compareToIgnoreCase("complete") == 0;
		
	}
	
	public static XEvent addConceptAttrToEvent(XEvent e, String attrKey, String value) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute Attr = factory.createAttributeLiteral(attrKey, value, XConceptExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(attrKey, Attr);
		e.setAttributes(attrMap);
		
		return e;
		
	}
	
	public static XEvent addLifeCycleAttrToEvent(XEvent e, String attrKey, String value) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute Attr = factory.createAttributeLiteral(attrKey, value, XLifecycleExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(attrKey, Attr);
		e.setAttributes(attrMap);
		
		return e;
		
	}
	
	public static XEvent addTimeAttrToEvent(XEvent e, String attrKey, long value) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute Attr = factory.createAttributeTimestamp(attrKey, value, XTimeExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(attrKey, Attr);
		e.setAttributes(attrMap);
		
		return e;
		
	}
	
	public static XEvent addResourceToEvent(XEvent e, String resourceName) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute resAttr = factory.createAttributeLiteral(XOrganizationalExtension.KEY_RESOURCE, resourceName, XOrganizationalExtension.instance());
		
		XAttributeMap attrMap = e.getAttributes();
		attrMap.put(XOrganizationalExtension.KEY_RESOURCE, resAttr);
		
		return e;
		
	}
	
	public static XTrace addBooleanAttrToTrace(XTrace trace, String attrKey, Boolean value) {
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute Attr = factory.createAttributeBoolean(attrKey, value, null);
		
		XAttributeMap attrMap = trace.getAttributes();
		attrMap.put(attrKey, Attr);
		
		return trace;
		
	}
	
	
	
	public static List<Set<String>> getStartAndEndActivities(XLog log)
	{
		
		Set<String> startActivities = new HashSet<>();
		Set<String> endActivities = new HashSet<>();
		for(XTrace trace: log)
		{
			if(trace.size() > 0)
			{
				XEvent startEvent = trace.get(0);
				XEvent endEvent = trace.get(trace.size() - 1);
				startActivities.add(XLogManager.getEventName(startEvent));
				endActivities.add(XLogManager.getEventName(endEvent));
			}
			
		}
		
		List<Set<String>> startAndEndActivities = new ArrayList<>();
		startAndEndActivities.add(startActivities);
		startAndEndActivities.add(endActivities);
		
		return startAndEndActivities;		
		
	}
	
	public static XEvent createEvent(String eventName, long eventTimeStamp_milis, String lifeCycle)
	{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent();
		XLogManager.addConceptAttrToEvent(event, XConceptExtension.KEY_NAME, eventName);
		XLogManager.addTimeAttrToEvent(event, XTimeExtension.KEY_TIMESTAMP, eventTimeStamp_milis);
		XLogManager.addLifeCycleAttrToEvent(event, XLifecycleExtension.KEY_TRANSITION, lifeCycle);
		
		return event;
	}
	
	public static int getEventIndex(XLog eventStream, XEvent event)
	{
		
		XAttributeMap attmap = event.getAttributes();
		for(int i = 0; i < eventStream.size(); i++)
		{
			
			XEvent curEvent = eventStream.get(i).get(0);
			if(curEvent.getAttributes() == event.getAttributes())
			{
				
				return i;
				
			}
			
		}
		
		return -1;
		
	}
	
	public static void printActualDriftPoints(XLog eventStream, List<DriftPoint> DriftPointsList)
	{
		
		Boolean lowerBranch = null;
		int index = 1;
		int counter = -1;
		int overlappingArea_stIndex = -1;
		int overlappingArea_endIndex = -1;
		DriftPoint esddr = null;
		for(int i = 0; i < eventStream.size(); i++)
		{
			
			String lowerBranchString = XLogManager.getEventAttrAsString(eventStream.get(i).get(0), "lowerBranch");
			if(lowerBranchString != null)
			{
				
				if(lowerBranch == null)
					lowerBranch = Boolean.valueOf(lowerBranchString);
				
				if(lowerBranchString.compareToIgnoreCase(String.valueOf(lowerBranch)) != 0)
				{
					
					esddr = new DriftPoint();
					DriftPointsList.add(esddr);
					
					if(counter != -1)
					{
						
						System.out.println("Intra-trace drift area from event " + overlappingArea_stIndex + " to event " + overlappingArea_endIndex);
					
					}
					
					System.out.println("Drift(" + index++ + ") at event: " + i);
					esddr.setDriftPointActual(i);
					esddr.setDriftTimeActual(XLogManager.getEventTime(eventStream.get(i).get(0)).getValueMillis());
					lowerBranch = !lowerBranch;
					
					String counterStr = XLogManager.getEventAttrAsString(eventStream.get(i).get(0), "n");
					counter = Integer.valueOf(counterStr);	
					overlappingArea_stIndex = i;
					esddr.setIntraTraceDriftAreaStartPoint(i);
					overlappingArea_endIndex = i;
					
				}
				
				if(XLogManager.getEventName(eventStream.get(i).get(0)).compareTo("DRIFT_PO") == 0)
				{
					
					eventStream.remove(i);
					i--;
					continue;
					
				}
				
			}
			
			if(counter != -1)
			{
				
				String traceId = XLogManager.getTraceID(eventStream.get(i));
				if(counter > Integer.valueOf(traceId))
				{
					
					overlappingArea_endIndex = i;
					esddr.setIntraTraceDriftAreaEndPoint(i);
					
				}
				
			}
			
		}
		
		if(counter != -1)
		{
			
			System.out.println("Intra-trace drift area from event " + overlappingArea_stIndex + " to event " + overlappingArea_endIndex);
			
		}
		
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
//            XAttributeTimestamp time = getEventTime(t.get(t.size() - 1));
            XAttributeTimestamp time = getTraceCompletionTime(t);
			if (time!=null)
				timeKeyMap.put(i, time.getValueMillis());
			else 
				continue;
           
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
	
	public static void removeTrace(XLog log, String traceID)
	{
		
		for (int i = 0; i < log.size(); i++) {
        	
            XTrace trace = log.get(i);
            if(XLogManager.getTraceID(trace).compareToIgnoreCase(traceID) == 0)
            {
            	
            	log.remove(i);
            	i--;
       
            }
            
        }
		
	}
	
	public static void removeSilentTransitionInCPNLog(String filePath)
	{
		
		BufferedReader reader;
		List<String> lines = new ArrayList<String>();
		
		try {

			reader = new BufferedReader(new FileReader(new File(filePath)));
			String currentLine;
	         
	        while ((currentLine = reader.readLine()) != null) {
	            
	        	lines.add(currentLine);
	            
	        }
	        
	        reader.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < lines.size(); i++)
		{
			
			String line = lines.get(i);
			String splits[] = line.split("\\s+");
			
//			for(String split : splits)
//				System.out.print(split + ",");
//			
//			System.out.println();
			
			if(splits.length > 2 && splits[2].compareToIgnoreCase("@") == 0)
			{
				
				lines.remove(i); // remove transaction line
				while(i < lines.size())
				{
					
					line = lines.get(i);
					if(line.startsWith(" - "))
						lines.remove(i); // remove bindings line
					else
						break;
					
				}
				i--;
				
			}
			
			
		}
		
		BufferedWriter writer;
		
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));
	         
			for(int i = 0; i < lines.size(); i++)
			{
	            
	        	writer.write(lines.get(i) + "\n");
	            
	        }
	        
	        writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void removeEventFromEventStream(XLog eventStream, String eventName)
	{
		
		for(int i = 0; i < eventStream.size(); i++)
		{
			
			if(XLogManager.getEventName(eventStream.get(i).get(0)).compareTo(eventName) == 0)
			{
				
				eventStream.remove(i);
				i--;
				
			}
			
		}
		
	}
	
	public static void convertCPNLogToMXML(String logFile_path)
	{
		
		String logName = logFile_path.substring(logFile_path.lastIndexOf("/") + 1, logFile_path.lastIndexOf(".")); 
		
		removeSilentTransitionInCPNLog(logFile_path);
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		
		XAttributeMap logAttributeMap = factory.createAttributeMap();
		XAttribute logConceptNameAttribute = factory.createAttributeLiteral(XConceptExtension.KEY_NAME, logName, XConceptExtension.instance());
		logAttributeMap.put(logName, logConceptNameAttribute);
				
		XLog log = factory.createLog(logAttributeMap);
		
		HashMap<String, Integer> TraceId_TraceIndex_InLog = new HashMap<>();
		
		BufferedReader reader;
		List<String> lines = new ArrayList<String>();
		
		try {

			reader = new BufferedReader(new FileReader(new File(logFile_path)));
			String currentLine;
	         
	        while ((currentLine = reader.readLine()) != null) {
	            
	        	lines.add(currentLine);
	            
	        }
	        
	        reader.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			String lineSplits[] = line.split("\\s");
			
			if(Utils.isLong(lineSplits[0]) && Utils.isDouble(lineSplits[1]))
			{
				
				HashMap<String, String> block = new HashMap<>();
				block.put(XConceptExtension.KEY_NAME, lineSplits[2]);
				String timeStamp = lineSplits[1].indexOf('.') != -1 ? lineSplits[1].substring(0, lineSplits[1].indexOf('.')) : lineSplits[1];
				block.put(XTimeExtension.KEY_TIMESTAMP, timeStamp);
				int j = i + 1;
				for(;j < lines.size() ;j++)
				{
					
					line = lines.get(j);
					if(line.startsWith(" - "))
					{
						
						lineSplits = line.split("\\s");
						block.put(lineSplits[2], lineSplits[4]);
						
					}else
						break;
					
				}
				i = j - 1;
				
				
				if(block.containsKey("i")){
					
					XTrace trace = null;
					XAttributeMap eventAttributeMap = factory.createAttributeMap();
					
					XAttribute eventTransitionAttribute = factory.createAttributeLiteral(XLifecycleExtension.KEY_TRANSITION, "complete", XLifecycleExtension.instance());
					eventAttributeMap.put(XLifecycleExtension.KEY_TRANSITION, eventTransitionAttribute);
					
					Iterator<String> it = block.keySet().iterator();
					while(it.hasNext())
					{
						
						String key = it.next();
						if(key.compareToIgnoreCase("i") == 0)
						{
							
							String traceID = block.get(key);
							Integer TraceIndex_InSubLog = TraceId_TraceIndex_InLog.get(traceID);
							if(TraceIndex_InSubLog == null)
							{
								
								XAttributeMap traceAttributeMap = factory.createAttributeMap();
								XAttribute traceConceptNameAttribute = factory.createAttributeLiteral(XConceptExtension.KEY_NAME, block.get(key), XConceptExtension.instance());
								traceAttributeMap.put(XConceptExtension.KEY_NAME, traceConceptNameAttribute);
								
								trace = factory.createTrace(traceAttributeMap);
								
								log.add(trace);
								TraceIndex_InSubLog = log.size() - 1;
								TraceId_TraceIndex_InLog.put(traceID, TraceIndex_InSubLog);
								
							}else
							{
								
								trace = log.get(TraceIndex_InSubLog);
								
							}
							
							
						}else if(key.compareToIgnoreCase(XConceptExtension.KEY_NAME) == 0)
						{
							
							XAttribute eventConceptNameAttribute = factory.createAttributeLiteral(XConceptExtension.KEY_NAME, block.get(key), XConceptExtension.instance());
							eventAttributeMap.put(XConceptExtension.KEY_NAME, eventConceptNameAttribute);
							
						}else if(key.compareToIgnoreCase(XTimeExtension.KEY_TIMESTAMP) == 0)
						{
							
							XAttribute eventTimestampAttribute = factory.createAttributeTimestamp(XTimeExtension.KEY_TIMESTAMP, Long.valueOf(block.get(key)), XTimeExtension.instance());
							eventAttributeMap.put(XTimeExtension.KEY_TIMESTAMP, eventTimestampAttribute);
						
						}else
						{
							
							XAttribute eventOtherAttribute = factory.createAttributeLiteral(key, block.get(key), null);
							eventAttributeMap.put(key, eventOtherAttribute);
							
						}
						
					}
					
					XEvent event = factory.createEvent(eventAttributeMap);
					trace.add(event);
					
				}
				
			}
			
		}

		String mxmlLogPath = logFile_path.substring(0, logFile_path.lastIndexOf(".")) + ".mxml";
		ByteArrayOutputStream baos = saveLogInMemory(log, mxmlLogPath);
		mxmlLogPath += ".gz";
		GzipLogAndSaveInDisk(baos, mxmlLogPath);
		
	}
	
	public static void printTrace(XTrace trace)
	{
		
		System.out.print(XLogManager.getTraceID(trace) + ": ");
		for (int i = 0; i < trace.size(); i++) {
        	
            System.out.print(XLogManager.getEventName(trace.get(i)) + "->");
            
        }
		System.out.println();
		
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
	
	//get all activites (event classes)
		public static List<String> getActivities(XLog log) {
			List<String> activities = new ArrayList<String>();
			for (XTrace xTrace : log) {
				for (XEvent xEvent : xTrace) {
					if (!activities.contains(XLogManager.getEventName(xEvent)))
						activities.add(XLogManager.getEventName(xEvent));
				}
			}
			System.out.println(activities);
			return activities;
		}
		
		public static XLog simplifyActivityNames(XLog xlog)
		{
			Map<String, String> activities_labels_map = new HashMap<String, String>();
			int labelIndex = 1;
			for (XTrace xTrace : xlog) {
				for (XEvent xEvent : xTrace) {
					if (XLogManager.getEventName(xEvent).compareToIgnoreCase("start") != 0 &&
							XLogManager.getEventName(xEvent).compareToIgnoreCase("end") != 0)
					{
						if (!activities_labels_map.containsKey(XLogManager.getEventName(xEvent)))
						{
							activities_labels_map.put(XLogManager.getEventName(xEvent), ("a"+ labelIndex));
							System.out.println(XLogManager.getEventName(xEvent) + ": " + ("a"+ labelIndex));
							labelIndex++;
						}
						
						String label = activities_labels_map.get(XLogManager.getEventName(xEvent));
						XLogManager.addConceptAttrToEvent(xEvent, XConceptExtension.KEY_NAME, label);
					}
				}
				
			}
			
			return xlog;
		}
		
		public static void labelTraceCompletionStatus(XLog log, Set<String> startActivities, Set<String> endActivities)
		{
			for(XTrace trace : log)
			{
				
				XEvent startEvent = trace.get(0);
				XEvent endEvent = trace.get(trace.size() - 1);
				
				if(startActivities.contains(getEventName(startEvent)))
					addBooleanAttrToTrace(trace, "startcomplete", true);
				else
					addBooleanAttrToTrace(trace, "startcomplete", false);
				
				if(endActivities.contains(getEventName(endEvent)))
					addBooleanAttrToTrace(trace, "endcomplete", true);
				else
					addBooleanAttrToTrace(trace, "endcomplete", false);
			}
		}
		
		public static void labelTraceCompletionStatus(XTrace trace, Set<String> startActivities, Set<String> endActivities)
		{
				
			XEvent startEvent = trace.get(0);
			XEvent endEvent = trace.get(trace.size() - 1);
			
			if(startActivities.contains(getEventName(startEvent)))
				addBooleanAttrToTrace(trace, "startcomplete", true);
			else
				addBooleanAttrToTrace(trace, "startcomplete", false);
			
			if(endActivities.contains(getEventName(endEvent)))
				addBooleanAttrToTrace(trace, "endcomplete", true);
			else
				addBooleanAttrToTrace(trace, "endcomplete", false);
		}
		
		
		
		
		public static Map<String, String> assingFragmentsToActivitites(
				HashMap<String, Entry<BlockStructure, Integer>> distinctFragments)
		{
			Map<String, String> activityName_fragmentID = new HashMap<>();
			Iterator<Entry<String, Entry<BlockStructure, Integer>>>it = distinctFragments.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, Entry<BlockStructure, Integer>> fragment = it.next();
				String frgmentId = fragment.getKey();
				BlockStructure fragmentBS = fragment.getValue().getKey();
				for(BlockStructure leaf : fragmentBS.getLeaves())
				{
					activityName_fragmentID.put(leaf.getBlockName(), frgmentId);
				}
			}
			
			return activityName_fragmentID;
					
		}

	
	public static void main(String[] args) {
        String logfile = "./Loan_baseline_Replace10000.mxml";
        Path path = Paths.get(logfile);
        byte[] logByteArray;
        try {
                        logByteArray = Files.readAllBytes(path);
                        XLog xl = readLog(new ByteArrayInputStream(logByteArray), logfile);
                        printTrace(xl.get(0));
//                        XLog xl_ordered = orderByTraceTimeStamp(xl);
//                        
//                        saveLogInDisk(xl_ordered, "BPIC15_1_ordered.xes", logfile);
        } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
        }
		
		
		
	}
}
