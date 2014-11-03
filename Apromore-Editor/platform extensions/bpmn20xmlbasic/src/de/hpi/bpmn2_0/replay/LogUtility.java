package de.hpi.bpmn2_0.replay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.deckfour.xes.model.buffered.XTraceBufferedImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

public class LogUtility {

    public static String getConceptName(XAttributable attrib) {
        String name = XConceptExtension.instance().extractName(attrib);
        return (name != null ? name : "<no name>");
    }

    public static void setConceptName(XAttributable attrib, String name) {
        XConceptExtension.instance().assignName(attrib, name);
    }

    public static String getLifecycleTransition(XEvent event) {
        String name = XLifecycleExtension.instance().extractTransition(event);
        return (name != null ? name : "<no transition>");
    }

    public static void setLifecycleTransition(XEvent event, String transition) {
        XLifecycleExtension.instance().assignTransition(event, transition);
    }

    public static void setTimestamp(XEvent event, Date timestamp) {
        XTimeExtension.instance().assignTimestamp(event, timestamp);
    }

    public static Date getTimestamp(XEvent event) {
        Date date = XTimeExtension.instance().extractTimestamp(event);
        return date;
    }

    public static String getOrganizationalResource(XEvent event) {
        String name = XOrganizationalExtension.instance().extractResource(event);
        return (name != null ? name : "<no resource>");
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
        
    /*
    * Divide a trace into multiple subtraces, with maximum lenth of each sub-trace
    * is defined in the "length" input variable
    * Get the whole input trace if the trace is shorter than the input length
    */
    public static List<XTrace> divide(XTrace trace, int length) {
        List<XTrace> list = new ArrayList<>();
        XTrace subtrace=null;
        if (trace.size() <= length) {
            list.add(trace);
        }
        else {
            for (int i=0; i<=(trace.size()-1); i++) {
                if (subtrace == null) {
                    subtrace = new XTraceImpl(trace.getAttributes());
                }
                subtrace.insertOrdered(trace.get(i));
                if (subtrace.size() >= length || i==(trace.size()-1)) {
                    list.add(subtrace);
                    subtrace = null;
                }
            }
        }
        return list;
    }
}