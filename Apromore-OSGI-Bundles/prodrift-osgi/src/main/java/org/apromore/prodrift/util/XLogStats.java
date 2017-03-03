/*
 * Copyright  2009-2017 The Apromore Initiative.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import jsc.independentsamples.SmirnovTest;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.*;
import org.jbpt.utils.IOUtils;
import org.processmining.framework.util.Pair;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;


public class XLogStats {

	public static void main(String[] args) throws Exception {
		String logfile = "";
//		logfile = "real/SuncorpManual.xes.gz";
//		XLog log = null;
//		try {
//			log = XLogReader.openLog(logfile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		

		
//		tracesName(log);
//		XLogReader.saveLogInDisk(orderByTraceTimeStamp(log), "ordered.mxml");
		
		//interarrival
//		String outPut="";
//		
//		for (int i = 5; i < 6; i++) {
//			logfile = "logs/BPIC15_"+i+".xes";
//			XLog log = XLogReader.openLog(logfile);
//			log = XLogManager.orderByTraceStartTimeStamp(log);
//			//		outPut += "\n"+ interArrivalTwoSlidingWinTest(log);
//			outPut += "\n" + logfile + "_HOURS" + ","
//					+ getInterArrivalTimes(log, TimeUnit.HOURS)
//							.toString().replace("[", "").replace("]", "");
//			outPut += "\n" + logfile + "_DAYS" + ","
//					+ getInterArrivalTimes(log, TimeUnit.DAYS)
//							.toString().replace("[", "").replace("]", "");
//		}
//
//		IOUtils.toFile(logfile.replaceAll("[^\\p{L}\\p{Z}]","")+"_interArrivalTime.csv", outPut);
//		System.out.println("FINISH");
	}
	
	public static double[] toArrayIntegerdouble(List<Integer> list){
		  double[] ret = new double[list.size()];
		  for(int i = 0;i < ret.length;i++)
		    ret[i] = (double)list.get(i);
		  return ret;
	}
	
	public static double[] toArraydoubleLong(List<Long> list){
		  double[] ret = new double[list.size()];
		  for(int i = 0;i < ret.length;i++)
		    ret[i] = (double)list.get(i);
		  return ret;
	}
	

	
	//compute activity frequency per time slot (days, week, month)
	private static Map<String,List<Integer>> activities_freq_time(XLog log, TimeUnit timeslot) throws ParseException {
		Map<String,List<Integer>> histos = new HashMap<String, List<Integer>>();
		List<String> activities = XLogManager.getActivities(log);
//		System.out.println(specificEventHistogram(log, activities.get(0)));
		for (int i = 0; i < activities.size(); i++) {
			histos.put(activities.get(i), specificEventHistogram(log, activities.get(i),timeslot));
		}
		System.out.println(histos);
		return histos;
	}
	
//compute activity duration per trace
	public static Map<String,List<Long>> activities_duration_trace(XLog log, TimeUnit timeunit) throws ParseException {
		
		Map<String,List<Long>> histos = new HashMap<String, List<Long>>();
//		Map<String,Integer> histos = new HashMap<String, Integer>();
		List<String> activities = XLogManager.getActivities(log);
//		System.out.println(specificEventHistogram(log, activities.get(0)));
		for (int i = 0; i < activities.size(); i++) {
			histos.put(activities.get(i), new ArrayList(Arrays.asList(new Long[log.size()])));
//			System.out.println( histos.get(activities.get(i)).size());
		}
		
		Long act_duration;
		
		for (int i = 0; i < log.size(); i++) {
			XTrace tr = log.get(i);
			for (int j = 0; j < tr.size(); j++) {
				String actName = XLogManager.getEventName(tr.get(j));
//				if (histos.get(actName).get(i)==null)
//					histos.get(actName).set(i, 1);
//				else 
//					histos.get(actName).set(i,histos.get(actName).get(i)+1);
//				System.out.println("tr "+i+" event "+j+" "+histos.get(actName).get(i));
				if (j==0)
					act_duration = new Long(0);
				else
					act_duration = getEventDuration(tr.get(j),tr.get(j-1));
				act_duration = timeunit.convert(act_duration, TimeUnit.MILLISECONDS);
				if (histos.get(actName).get(i)==null)
					histos.get(actName).set(i, act_duration);
				else 
					histos.get(actName).set(i,histos.get(actName).get(i)+act_duration);
			}
		}
		
//		System.out.println(histos);
		return histos;
	}	

	//compute activity duration postEvent-preEvent
	private static Long getEventDuration(XEvent postEvent, XEvent preEvent) {		
		Long diff = null;
		XLogManager.getEventTime(preEvent);
		XAttributeTimestamp t_start = XLogManager.getEventTime(preEvent);
		XAttributeTimestamp t_end = XLogManager.getEventTime(postEvent);
		if (t_start != null && t_end!=null)
			diff = t_end.getValue().getTime() - t_start.getValue().getTime();	
		if (diff < 0)
			diff = new Long(0);
		return diff;
	}

	//compute average event per active case in a time slot (days, week, month)
	private static List<Integer> avg_event_activecase_time(XLog log) throws ParseException {
		List<Integer> event_histo = eventHistogram(log);
		List<Integer> case_histo = activeCaseHistogram(log, null);
		List<Integer> histo = new ArrayList<Integer>();
		for (int i = 0; i < event_histo.size(); i++) {
			histo.add(event_histo.get(i)/case_histo.get(i));
		}
		System.out.println(histo);
		return histo;
	}
	
	//compute events per time slot (day, week, month, ...)
	private static List<Integer> eventHistogram(XLog log) throws ParseException {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		log = LogStreamer.logStreamer(log, null);
		
		int count = 0, dateindex = 0;
		
		List<Integer> histo = new ArrayList<Integer>();
		List<LocalDateTime> dates = new ArrayList<LocalDateTime>();
		
		Date startDate = ((XAttributeTimestamp)(log.get(0).get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
		Date endDate = ((XAttributeTimestamp)(log.get(log.size()-1).get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
		
		System.out.println(startDate);
		System.out.println(endDate);
		
		
		LocalDateTime start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		
		for (LocalDateTime date = start; date.isBefore(end); date = date.plusDays(1)) {
			dates.add(date);
			System.out.println(date);
		}
		dates.add(end.plusDays(1));
		
		for (int i = 0; i < log.size(); i++) {
			
			XTrace tr1 = log.get(i);
			Date t1 = ((XAttributeTimestamp)(tr1.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
			
//			if (i==54374) System.out.println("DATE event "+i+" = "+t1);
			
			LocalDateTime local = t1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			
			
			if (local.isBefore(dates.get(dateindex+1)))
				count++;
			else{
				histo.add(count);
				count = 1;
				dateindex++;
			}
		}
		System.out.println(histo);
		return histo;
	}
	
	//compute events of class eventName per time slot (day, week, month, ...)
	public static List<Integer> specificEventHistogram(XLog log, String eventName, TimeUnit timeslot) throws ParseException {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		log = LogStreamer.logStreamer(log, null);
		
		int count = 0, dateindex = 0;
		
		List<Integer> histo = new ArrayList<Integer>();
		List<LocalDateTime> dates = new ArrayList<LocalDateTime>();
		
		Date startDate = ((XAttributeTimestamp)(log.get(0).get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
		Date endDate = ((XAttributeTimestamp)(log.get(log.size()-1).get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
		
//		System.out.println(startDate);
//		System.out.println(endDate);
		
		
		LocalDateTime start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		
		for (LocalDateTime date = start; date.isBefore(end); date = date.plusMonths(1)) {//define time slot (day, week, month, ...)
			dates.add(date);
			
		}
		dates.add(end.plusMonths(1));
//		System.out.println(dates);
		
		for (int i = 0; i < log.size(); i++) {
			
			XTrace tr1 = log.get(i);
			Date t1 = ((XAttributeTimestamp)(tr1.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
			
//				if (i==54374) System.out.println("DATE event "+i+" = "+t1);
			
			LocalDateTime local = t1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			
			
			if (local.isBefore(dates.get(dateindex+1))){
				if (XLogManager.getEventName(tr1.get(0)).compareTo(eventName) == 0)
					count++;
				}
			else{
				System.out.println(dates.get(dateindex+1));
				histo.add(count);
				if (XLogManager.getEventName(tr1.get(0)).compareTo(eventName) == 0)
					count = 1;
				else
					count = 0;
				dateindex++;
				
			}
		}
//		System.out.println(histo);
		return histo;
	}
		
	
	//compute average active case in time slot (day, week, month, ...)
	private static List<Integer> activeCaseHistogram(XLog log, TimeUnit timeslot) throws ParseException {
		
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		XLog logStream = LogStreamer.logStreamer(log, null);
		
		List<Integer> histo = new ArrayList<Integer>();
		List<LocalDateTime> dates = new ArrayList<LocalDateTime>();
		
		Date startDate = ((XAttributeTimestamp)(logStream.get(0).get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
		Date endDate = ((XAttributeTimestamp)(logStream.get(logStream.size()-1).get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
		
		System.out.println(startDate);
		System.out.println(endDate);
		

		LocalDateTime start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		for (LocalDateTime date = start; date.isBefore(end); date = date.plus(1, (TemporalUnit) timeslot)) {//define time slot (day, week, month, ...)
			dates.add(date);
		    System.out.println(date.format(formatter));
		}
		dates.add(end.plusYears(1));
		
		for (int i = 0; i < dates.size()-1; i++) {
			int number_case = 0;
			for (int j = 0; j < log.size(); j++) {
				XTrace tr1 = log.get(j);
				Date t_start = ((XAttributeTimestamp)(tr1.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
//				Date t_end = ((XAttributeTimestamp)(tr1.get(tr1.size()-1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
				Date t_end = null;
				if (XLogManager.getTraceCompletionTime(tr1)!=null)
					t_end = XLogManager.getTraceCompletionTime(tr1).getValue();
				else 
					continue;
//				if (i==54374) System.out.println("DATE event "+i+" = "+t1);
				
//				//case duration in days
//				long diff = t_end.getTime() - t_start.getTime();
//			    System.out.print (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+",");
				
				LocalDateTime case_local_start = t_start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				LocalDateTime case_local_end = t_end.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

				if ( ( case_local_start.isAfter(dates.get(i))&& (case_local_start.isBefore(dates.get(i+1))) )||
						( case_local_end.isAfter(dates.get(i))&& (case_local_end.isBefore(dates.get(i+1))) )||
						( case_local_start.isBefore(dates.get(i))&& (case_local_end.isAfter(dates.get(i+1))) ) )
				{
					number_case++;
				}

			}
			histo.add(number_case);
		}
		
		
		System.out.println(histo);
		return histo;
	}

	
	//compute case duration 
	public static List<Long> caseDurations(XLog log, TimeUnit timeunit) throws ParseException {
		
		
		List<Long> durations = new ArrayList<Long>();

//		System.out.println("log size "+log.size());
		for (int j = 0; j < log.size(); j++) {
			XTrace tr1 = log.get(j);
			Date t_start = ((XAttributeTimestamp)(tr1.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
//				Date t_end = ((XAttributeTimestamp)(tr1.get(tr1.size()-1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getValue();
			Date t_end = null;
			if (XLogManager.getTraceCompletionTime(tr1)!=null)
				t_end = XLogManager.getTraceCompletionTime(tr1).getValue();
			else 
				continue;
//				if (i==54374) System.out.println("DATE event "+i+" = "+t1);
			
			//case duration in days
			long diff = t_end.getTime() - t_start.getTime();
//		    System.out.print (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+",");
			
			durations.add(timeunit.convert(diff, TimeUnit.MILLISECONDS));

		}

//		System.out.println(durations);
		return durations;
	}
	
	private static String interArrivalTwoSlidingWinTest(XLog xl_non_ordered) {
		XLog log = XLogManager.orderByTraceStartTimeStamp(xl_non_ordered);
		
		ArrayList<Long> intArr = getInterArrivalTimes(log, TimeUnit.HOURS);
				
		int windowSize = 100;
		double pValue=0; 
		String pvalString = "";
		for (int index = 0; index < intArr.size() - 2 * windowSize + 1; index++) {
			
			
			double[] spl1 = getElts(intArr, index, windowSize);
			double[] spl2 = getElts(intArr, index + windowSize, windowSize);
			SmirnovTest KSTest = new SmirnovTest(spl1, spl2);
			pValue = KSTest.getSP(); // MWTest.getSP();
//			System.out.println(pValue);
			pvalString+= pValue +",";
		}
		return pvalString;
	}

	private static ArrayList<Long> getInterArrivalTimes(XLog log, TimeUnit timeunit) {
		
		ArrayList<Long> intArr = new ArrayList<Long>();
		
		for (int i = 0; i < log.size()-1; i++) {
			XTrace tr1 = log.get(i), tr2 = log.get(i+1);
			XAttributeTimestamp t1 = (XAttributeTimestamp)(tr1.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP));
			XAttributeTimestamp t2 = (XAttributeTimestamp)(tr2.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP));
			long diff =t2.getValueMillis() - t1.getValueMillis(); 
			intArr.add(timeunit.convert(diff, TimeUnit.MILLISECONDS));
			}
		return intArr;
	}
	

	private static double[] getElts(ArrayList<Long> listLong, int index, int windowSize) {
		ArrayList<Long> subList = new ArrayList<Long>(listLong.subList(index, index+windowSize));
		double[] array = new double[subList.size()];
		for (int i = 0; i < subList.size(); i++) {
			array[i] = subList.get(i);
		}
		return array;
	}


	private static Map<Integer, Long> sortByComparator(Map<Integer, Long> unsortMap) {

		// Convert Map to List
		List<Map.Entry<Integer, Long>> list = new LinkedList<Map.Entry<Integer, Long>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Long>>() {
			public int compare(Map.Entry<Integer, Long> o1,
					Map.Entry<Integer, Long> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<Integer, Long> sortedMap = new LinkedHashMap<Integer, Long>();
		for (Iterator<Map.Entry<Integer, Long>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<Integer, Long> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

}
