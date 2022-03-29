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

package org.apromore.logman.utils;

import java.util.Date;

import org.apromore.logman.AActivity;
import org.apromore.logman.Constants;
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
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;

public class LogUtils {

	////////////////////////// Attribute utilities //////////////////////////
	public static String getConceptName(XAttributable attrib) {
		String value = XConceptExtension.instance().extractName(attrib);
		return (value != null ? value : Constants.MISSING_STRING_VALUE);
	}

	public static void setConceptName(XAttributable attrib, String name) {
		XConceptExtension.instance().assignName(attrib, name);
	}

	public static String getLifecycleTransition(XEvent event) {
		String value = XLifecycleExtension.instance().extractTransition(event);
		return (value != null ? value : Constants.MISSING_STRING_VALUE);
	}

	public static void setLifecycleTransition(XEvent event, String transition) {
		XLifecycleExtension.instance().assignTransition(event, transition);
	}
	
    public static long getDuration(XEvent event) {
        if (event instanceof AActivity) {
            return ((AActivity)event).getDuration();
        }
        else {
            return 0;
        }
    }
	
    // This method should not be used if the other getXXX can be used
    // Because the returning value is any string value which is ad-hoc
    // It does not differentiate missing value from present one, just return any string.
	public static String getValueString(XAttribute attr) {
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
		return Constants.MISSING_STRING_VALUE;
	}
	
	////////////////////////////// Event utilities //////////////////////////

	public static void setTimestamp(XEvent event, Date timestamp) {
		XTimeExtension.instance().assignTimestamp(event, timestamp);
	}

	public static DateTime getDateTime(XEvent event) {
	    Date eventDate = XTimeExtension.instance().extractTimestamp(event);
	    if (eventDate != null) {
	        return new DateTime(XTimeExtension.instance().extractTimestamp(event));
	    }
	    else {
	        return new DateTime(Constants.MISSING_TIMESTAMP);
	    }
	}
	
    public static long getTimestamp(XEvent event) {
        Date eventDate = XTimeExtension.instance().extractTimestamp(event);
        if (eventDate != null) {
            return XTimeExtension.instance().extractTimestamp(event).toInstant().toEpochMilli();
        }
        else {
            return Constants.MISSING_TIMESTAMP;
        }
    }	
    
//    public static long getStartTimestamp(XEvent event) {
//        XAttribute att = event.getAttributes().get(Constants.ATT_KEY_START_TIME);
//        if (att != null ) {
//            if (att instanceof XAttributeTimestamp) {
//                return ((XAttributeTimestamp)att).getValueMillis();
//            }
//            else {
//                return Constants.MISSING_TIMESTAMP;
//            }
//        }
//        else {
//            return LogUtils.getTimestamp(event);
//        }
//    }
//    
//    public static long getEndTimestamp(XEvent event) {
//        XAttribute att = event.getAttributes().get(Constants.ATT_KEY_END_TIME);
//        if (att != null ) {
//            if (att instanceof XAttributeTimestamp) {
//                return ((XAttributeTimestamp)att).getValueMillis();
//            }
//            else {
//                return Constants.MISSING_TIMESTAMP;
//            }
//        }
//        else {
//            return LogUtils.getTimestamp(event);
//        }
//    }
    
    /////////////////// XTrace utilities ////////////////////////////////
    
    public static long getStartTimestamp(XTrace trace) {
    	return (trace.isEmpty() ? Constants.MISSING_TIMESTAMP: getTimestamp(trace.get(0)));
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
		return (value != null ? value : Constants.MISSING_STRING_VALUE);
	}
	


	
}
