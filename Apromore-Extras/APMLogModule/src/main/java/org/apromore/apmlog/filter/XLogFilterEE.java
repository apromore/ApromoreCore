package org.apromore.apmlog.filter;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;

public class XLogFilterEE {

    public static XLog filter(XLog xLog, PLog pLog) {
        List<PTrace> pTraceList = pLog.getCustomPTraceList();

        // filter event first
        for (int i = 0; i < xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);
            PTrace pTrace = pTraceList.get(i);
            BitSet validEventBS = pTrace.getValidEventIndexBitSet();

            List<XEvent> tobeRemovedXEvents = new ArrayList<>();
            for (int j = 0; j < xTrace.size(); j++) {
                XEvent xEvent = xTrace.get(j);
                if (!validEventBS.get(j)) tobeRemovedXEvents.add(xEvent);
            }
            if (tobeRemovedXEvents.size() > 0) xTrace.removeAll(tobeRemovedXEvents);
        }

        BitSet validTraceBS = pLog.getValidTraceIndexBS();

        UnifiedSet<XTrace> tobeRemovedXTraces = new UnifiedSet<>();
        for (int i = 0; i < xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);
            if (!validTraceBS.get(i)) tobeRemovedXTraces.add(xTrace);
        }

        ListIterator<XTrace> iter = xLog.listIterator();
        while (iter.hasNext()) {
            if (tobeRemovedXTraces.contains(iter.next())) {
                iter.remove();
            }
        }


        return xLog;
    }
}
