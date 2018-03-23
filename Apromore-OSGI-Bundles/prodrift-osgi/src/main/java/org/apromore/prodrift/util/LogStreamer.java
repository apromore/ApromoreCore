package org.apromore.prodrift.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;


public class LogStreamer {

	public static XLog logStreamer(XLog log, StringBuilder numOfActivities, StringBuilder winSizeStr, String logName) {

		XLog eventStream = new XLogImpl(log.getAttributes());

		// iterate through all the events of the log
		Set<String> activities = new HashSet<>();
		long sumTraceDuration = 0;
		for (int i = 0; i < log.size(); i++) {

			XTrace t = log.get(i);

			long traceStartTime = -1;
			long traceEndTime = -1;
			for(int j = 0; j < t.size(); j++)
			{

				XEvent e = t.get(j);

				if(getEventAttr(e, XTimeExtension.KEY_TIMESTAMP) != null)
				{
					XAttributeTimestampImpl date = (XAttributeTimestampImpl) XLogManager.getEventTime(e);
					if (traceStartTime == -1) traceStartTime = date.getValueMillis();
					traceEndTime = date.getValueMillis();

					if (logName.contains("bpi") && logName.contains("2013") || XLogManager.isCompleteEvent(e)) {


						XAttributeMap attmap = t.getAttributes();

						XTraceImpl t1 = new XTraceImpl(attmap);

						t1.add(e);

						eventStream.add(t1);

						activities.add(XLogManager.getEventName(e));

					}
				}
//				}
			}
			sumTraceDuration += (traceEndTime - traceStartTime);

		}

		Collections.sort(eventStream, new Comparator<XTrace>() {
			public int compare(XTrace o1, XTrace o2) {

				XAttributeTimestampImpl date1 = (XAttributeTimestampImpl) XLogManager.getEventTime(o1.get(0));
				XAttributeTimestampImpl date2 = (XAttributeTimestampImpl) XLogManager.getEventTime(o2.get(0));
				return date1.compareTo(date2);

			}
		});

		if(numOfActivities != null)
			numOfActivities.append(activities.size());

		long meanTraceDuration = sumTraceDuration / log.size();

		long stTime = -1;
		long endTime = -1;
		int eventCount = 0;
		int winSize = 0;
		for(int i = 0; i < eventStream.size(); i++)
		{

			XAttributeTimestampImpl date = (XAttributeTimestampImpl) XLogManager.getEventTime(eventStream.get(i).get(0));
			if(stTime == -1) stTime = date.getValueMillis();
			endTime = date.getValueMillis();

			if(endTime - stTime >= meanTraceDuration)
			{
				if(eventCount > winSize) winSize = eventCount;
				stTime = -1;
				endTime = -1;
				eventCount = 0;
			}else
				eventCount++;
		}

		if(eventCount > winSize) winSize = eventCount;

		if(winSizeStr == null)
			winSizeStr = new StringBuilder();
		winSizeStr.append(winSize);

//		for(String activity: activities)
//		{
//			System.out.println(activity);
//		}

//		System.out.println("# of activities: " + activities.size());

//		System.out.println("Log Streaming Done! It took(sec):" + (System.currentTimeMillis() - time1) / 1000);

		return (XLog)eventStream;
	}


	public static XLog logStreamer(XLog log, List<String> distinctActivityNames, StringBuilder winSizeStr) {

		XLog eventStream = new XLogImpl(log.getAttributes());

		// iterate through all the events of the log
		long sumTraceDuration = 0;
		for (int i = 0; i < log.size(); i++) {

			XTrace t = log.get(i);

			long traceStartTime = -1;
			long traceEndTime = -1;
			for(int j = 0; j < t.size(); j++)
			{

				XEvent e = t.get(j);e.clone();

				if(getEventAttr(e, XTimeExtension.KEY_TIMESTAMP) != null)
				{
					XAttributeTimestampImpl date = (XAttributeTimestampImpl) XLogManager.getEventTime(e);
					if (traceStartTime == -1) traceStartTime = date.getValueMillis();
					traceEndTime = date.getValueMillis();

					if (XLogManager.isCompleteEvent(e)) {


						XAttributeMap attmap = t.getAttributes();

						XTraceImpl t1 = new XTraceImpl(attmap);

						t1.add(e);

						eventStream.add(t1);

						String evName = XLogManager.getEventName(e);
						if (distinctActivityNames != null && !distinctActivityNames.contains(evName))
							distinctActivityNames.add(evName);

					}
				}
//				}
			}
			sumTraceDuration += (traceEndTime - traceStartTime);

		}

		Collections.sort(eventStream, new Comparator<XTrace>() {
			public int compare(XTrace o1, XTrace o2) {

				XAttributeTimestampImpl date1 = (XAttributeTimestampImpl) XLogManager.getEventTime(o1.get(0));
				XAttributeTimestampImpl date2 = (XAttributeTimestampImpl) XLogManager.getEventTime(o2.get(0));
				return date1.compareTo(date2);

			}
		});

		long meanTraceDuration = sumTraceDuration / log.size();

		long stTime = -1;
		long endTime = -1;
		int eventCount = 0;
		int winSize = 0;
		for(int i = 0; i < eventStream.size(); i++)
		{

			XAttributeTimestampImpl date = (XAttributeTimestampImpl) XLogManager.getEventTime(eventStream.get(i).get(0));
			if(stTime == -1) stTime = date.getValueMillis();
			endTime = date.getValueMillis();

			if(endTime - stTime >= meanTraceDuration)
			{
				if(eventCount > winSize) winSize = eventCount;
				stTime = -1;
				endTime = -1;
				eventCount = 0;
			}else
				eventCount++;
		}

		if(eventCount > winSize) winSize = eventCount;

		if(winSizeStr == null)
			winSizeStr = new StringBuilder();
		winSizeStr.append(winSize);
//		for(String activity: activities)
//		{
//			System.out.println(activity);
//		}

//		System.out.println("# of activities: " + activities.size());

//		System.out.println("Log Streaming Done! It took(sec):" + (System.currentTimeMillis() - time1) / 1000);

		return (XLog)eventStream;
	}

	public static XAttribute getEventAttr(XEvent e, String attrKey)
	{

		return e.getAttributes().get(attrKey);

	}


	public static void main(String args[]) throws IOException
	{

		Path path = Paths.get("./Detail_Incident_Activity_BPI2014.mxml");
		byte[] logByteArray = Files.readAllBytes(path);

		XLog xl = XLogManager.readLog(new ByteArrayInputStream(logByteArray), path.getFileName().toString());
		XLog ls = logStreamer(xl, null, null, "");

//		for(int i = 0; i < ls.size(); i++)
//		{
//			
//			System.out.println(XLogManager.getEventTime(ls.get(i).get(0)));
//			
//		}


	}
}
