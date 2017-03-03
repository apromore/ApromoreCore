/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
package org.apromore.prodrift.config;

import java.util.Collections;
import java.util.Comparator;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;


public class LogStreamer {
	
	public static XLog logStreamer(XLog log) {

		XLog eventStream = new XLogImpl(log.getAttributes());
		
		// iterate through all the events of the log
		for (int i = 0; i < log.size(); i++) {
			
			XTrace t = log.get(i);
			
			for(int j = 0; j < t.size(); j++) {
				
				XEvent e = t.get(j);
				
//				if(isCompleteEvent(e))
//				{
					if(getEventAttr(e, XTimeExtension.KEY_TIMESTAMP) != null)
					{
						
						XAttributeMap attmap = t.getAttributes();
						
						XTraceImpl t1 = new XTraceImpl(attmap);
						
						t1.add(e);
						
						eventStream.add(t1);
						
					}
					
//				}
			}
			
		}
		
		Collections.sort(eventStream, new Comparator<XTrace>() {
			public int compare(XTrace o1, XTrace o2) {
				
				XAttributeTimestampImpl date1 = (XAttributeTimestampImpl) getEventTime(o1.get(0));
				XAttributeTimestampImpl date2 = (XAttributeTimestampImpl) getEventTime(o2.get(0));
				return date1.compareTo(date2);
				
			}
		});
		
		return (XLog)eventStream;
	}
	
	public static XAttribute getEventAttr(XEvent e, String attrKey)
	{
		
		return e.getAttributes().get(attrKey);
		
	}
	
	public static XAttributeTimestamp getEventTime(XEvent e)
	{
		
		return (XAttributeTimestamp) e.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
		
	}
	
	public static String getEventName(XEvent e) {
		
		return e.getAttributes().get(XConceptExtension.KEY_NAME).toString();
		
	}
	
	public static boolean isCompleteEvent(XEvent e) {
		
		return getEventType(e).compareToIgnoreCase("complete") == 0;
		
	}
	
	public static String getEventType(XEvent e) {
		
		return  e.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString().trim();
		
	}

}
