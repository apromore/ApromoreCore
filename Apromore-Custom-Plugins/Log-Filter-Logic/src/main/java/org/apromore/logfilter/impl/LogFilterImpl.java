/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.logfilter.impl;

import org.apromore.logfilter.LogFilterService;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Bruce Nguyen: comments, restructure
 */
public class LogFilterImpl implements LogFilterService {
    private final XFactory factory = new XFactoryNaiveImpl();

    @Override
    public XLog filter(XLog log, List<LogFilterCriterion> criteria) {
    	if (criteria == null || criteria.isEmpty()) return log;
    	
        log = cloneLog(log);
        Iterator<XTrace> traceIterator = log.iterator();
        while (traceIterator.hasNext()) { // Object order OO
            XTrace trace = traceIterator.next();
            if(criteria != null) {
                for (int i = 0; i < criteria.size(); i++) { // Criterion order OC
                    LogFilterCriterion criterion = criteria.get(i);
                    if (criterion.getLevel() == Level.TRACE) {
                        if(criterion.isToRemove(trace)) { //matching & action
                            traceIterator.remove();
                            break;
                        }
                    } else { //down-level shift
                        Iterator<XEvent> eventIterator = trace.iterator(); //Object order OO
                        while (eventIterator.hasNext()) {
                            XEvent event = eventIterator.next();
                            for (int j = i; j < criteria.size(); j++) {  // Criterion order OC
                                LogFilterCriterion criterion1 = criteria.get(j);
                                if (criterion1.getLevel() == Level.TRACE) break; // up-level shift
                                if (criterion1.isToRemove(event)) { //matching & action
                                    eventIterator.remove();
                                    break;
                                }
                            }
                        }
                        if(trace.size() == 0) {
                        	traceIterator.remove();
                        	break; //Bruce added 
                        }
                    }
                }
            }
        }
        return log;
    }

    private XLog cloneLog(XLog log) {
        XLog newLog = factory.createLog(log.getAttributes());
        for (XTrace trace : log) {
            XTrace newTrace = getXTrace(trace);
            newLog.add(newTrace);
        }

        return newLog;
    }

    private XTrace getXTrace(XTrace trace) {
        XTrace newTrace = factory.createTrace(trace.getAttributes());
        newTrace.addAll(trace);
        return newTrace;
    }
}
