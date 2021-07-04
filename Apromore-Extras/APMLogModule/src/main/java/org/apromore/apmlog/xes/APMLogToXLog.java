/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.apmlog.xes;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.List;


public class APMLogToXLog {

    public static XLog getXLog(APMLog apmLog) {
        XFactory xFactory = new XFactoryNaiveImpl();
        XLog xLog = xFactory.createLog();

        XConceptExtension xConceptExtension = XConceptExtension.instance();
        XLifecycleExtension xLifecycleExtension = XLifecycleExtension.instance();
        XTimeExtension xTimeExtension = XTimeExtension.instance();
        XOrganizationalExtension xOrganizationalExtension = XOrganizationalExtension.instance();

        xLog.getExtensions().add(xConceptExtension);
        xLog.getExtensions().add(xLifecycleExtension);
        xLog.getExtensions().add(xTimeExtension);
        xLog.getExtensions().add(xOrganizationalExtension);

        xLifecycleExtension.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);

        List<ATrace> aTraceList = apmLog.getTraces();

        List<XTrace> xTraceList = new ArrayList<>();

        for (int i = 0; i < aTraceList.size(); i++) {
            ATrace aTrace = aTraceList.get(i);
            String caseId = aTrace.getCaseId();

            XTrace xTrace = xFactory.createTrace();
            xConceptExtension.assignName(xTrace, caseId);

            UnifiedMap<String, String> aTraceAttributes = aTrace.getAttributes();
            for (String key : aTraceAttributes.keySet()) {
                XAttribute attribute = new XAttributeLiteralImpl(key, aTraceAttributes.get(key));
                xTrace.getAttributes().put(key, attribute);
            }

            for (ActivityInstance a : aTrace.getActivityInstances()) {
                if (a.getImmutableEventIndexes().size() > 1) {
                    xTrace.add(getXEvent(a.getStartTime(), "start", a, xFactory));
                }

                xTrace.add(getXEvent(a.getEndTime(), "complete", a, xFactory));
            }

            xTraceList.add(xTrace);
        }

        xLog.addAll(xTraceList);

        return xLog;
    }

    private static XEvent getXEvent(long timestamp,
                                    String lifecycle,
                                    ActivityInstance activityInstance, XFactory xFactory) {
        XEvent xEvent = xFactory.createEvent();

        XAttribute timestampAttribute = new XAttributeTimestampImpl("time:timestamp", timestamp);
        xEvent.getAttributes().put("time:timestamp", timestampAttribute);

        XAttribute lifecycleAttribute = new XAttributeLiteralImpl("lifecycle:transition", lifecycle);
        xEvent.getAttributes().put("lifecycle:transition", lifecycleAttribute);

        UnifiedMap<String, String> attributes = activityInstance.getAttributes();
        for (String key : attributes.keySet()) {
            XAttribute xAttribute = new XAttributeLiteralImpl(key, attributes.get(key));
            xEvent.getAttributes().put(key, xAttribute);
        }
        return xEvent;
    }
}
