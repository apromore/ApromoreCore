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
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Bruce Nguyen: comments, restructure
 * Modified by Chii Chang (20/01/2020)
 */
public class LogFilterImpl implements LogFilterService {
    private final XFactory factory = new XFactoryNaiveImpl();
    public XLog filter(XLog theLog, List<LogFilterCriterion> criteria) {
        if (criteria == null || criteria.isEmpty()) return theLog;
        XLog log = (XLog) theLog.clone();

        List<XTrace> traceToBeRemoved = new ArrayList<>();

        for(XTrace xTrace : log) {
            if(criteria != null) {
                for (int i = 0; i < criteria.size(); i++) { // Criterion order OC
                    LogFilterCriterion criterion = criteria.get(i);
                    if (criterion.getLevel() == Level.TRACE) {
                        if(criterion.isToRemove(xTrace)) { //matching & action
                            traceToBeRemoved.add(xTrace);
                            break;
                        }
                    } else { //down-level shift
                        List<XEvent> eventToBeRemoved = new ArrayList<>();
                        for(XEvent xEvent : xTrace) {
                            for (int j = i; j < criteria.size(); j++) {  // Criterion order OC
                                LogFilterCriterion criterion1 = criteria.get(j);
                                if (criterion1.getLevel() == Level.TRACE) break; // up-level shift
                                if (criterion1.isToRemove(xEvent)) { //matching & action
                                    eventToBeRemoved.add(xEvent);
                                    break;
                                }
                            }
                        }

                        if(eventToBeRemoved.size() > 0) xTrace.removeAll(eventToBeRemoved);

                        if(xTrace.size() == 0) {
                            traceToBeRemoved.add(xTrace);
                            break;
                        }
                    }
                }
            }
        }

        if(traceToBeRemoved.size() > 0) log.removeAll(traceToBeRemoved);

        return log;
    }


    private XTrace getXTrace(XTrace trace) {
        XTrace newTrace = factory.createTrace(trace.getAttributes());
        newTrace.addAll(trace);
        return newTrace;
    }
}
