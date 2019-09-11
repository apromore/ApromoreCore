package org.apromore.logman.utils;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;

public class LogUtils {

	////////////////////////// Attribute utilities //////////////////////////
	public static String getConceptName(XAttributable attrib) {
		String value = XConceptExtension.instance().extractName(attrib);
		return (value != null ? value : "");
	}

	public static void setConceptName(XAttributable attrib, String name) {
		XConceptExtension.instance().assignName(attrib, name);
	}

	public static String getLifecycleTransition(XEvent event) {
		String value = XLifecycleExtension.instance().extractTransition(event);
		return (value != null ? value : "");
	}

	public static void setLifecycleTransition(XEvent event, String transition) {
		XLifecycleExtension.instance().assignTransition(event, transition);
	}
	
	public static String getValue(XAttribute attr) {
		if (attr instanceof XAttributeBoolean) {
			Boolean b = ((XAttributeBoolean) attr).getValue();
			return b.toString();
		} else if (attr instanceof XAttributeContinuous) {
			Double d = ((XAttributeContinuous) attr).getValue();
			return d.toString();
		} else if (attr instanceof XAttributeDiscrete) {
			Long l = ((XAttributeDiscrete) attr).getValue();
			return l.toString();
		} else if (attr instanceof XAttributeLiteral) {
			String s = ((XAttributeLiteral) attr).getValue();
			return s;
		} else if (attr instanceof XAttributeTimestamp) {
			Date d = ((XAttributeTimestamp) attr).getValue();
			return d.toString();
		}
		return "";
	}
	
	////////////////////////////// Event utilities //////////////////////////

	public static void setTimestamp(XEvent event, Date timestamp) {
		XTimeExtension.instance().assignTimestamp(event, timestamp);
	}

	public static DateTime getDateTime(XEvent event) {
		return new DateTime(XTimeExtension.instance().extractTimestamp(event));
	}
	
    public static long getTimestamp(XEvent event) {
        return XTimeExtension.instance().extractTimestamp(event).toInstant().toEpochMilli();
    }	
    
    /////////////////// XTrace utilities ////////////////////////////////
    
    public static long getStartTimestamp(XTrace trace) {
    	return (trace.isEmpty() ? 0 : getTimestamp(trace.get(0)));
    }
    
    public static long getEndTimestamp(XTrace trace) {
    	return (trace.isEmpty() ? 0 : getTimestamp(trace.get(trace.size()-1)));
    }
    
    public static long getDuration(XTrace trace) {
    	if (trace.isEmpty() || trace.size()==1) {
    		return 0;
    	}
    	else {
    		return (getEndTimestamp(trace) - getStartTimestamp(trace));
    	}
    }
    
    public static DateTime getStartDate(XTrace trace) {
    	return (trace.isEmpty() ? null : getDateTime(trace.get(0)));
    }
    
    public static DateTime getEndDate(XTrace trace) {
    	return (trace.isEmpty() ? null : getDateTime(trace.get(trace.size()-1)));
    }

	public static String getOrganizationalResource(XEvent event) {
		String value = XOrganizationalExtension.instance().extractResource(event);
		return (value != null ? value : "");
	}

	
}