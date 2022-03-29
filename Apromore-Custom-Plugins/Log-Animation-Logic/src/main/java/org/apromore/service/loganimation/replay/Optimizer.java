/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.service.loganimation.replay;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;

/**
 * Optimizer is used to set all attributes keys/values of the same values to point to the same
 * object reference. This is to improve the performance of equals comparison. For example, if the 
 * log has activity "This is an activity" and the model has an activity "This is an activity", the 
 * comparison would be instant if they are the same object reference. String comparison between log 
 * and model is common in log-model alignment.
 * 
 * Created by Raffaele Conforti on 21/10/14.
 * Modified by Bruce 10/11/2014: added optimizeProcessModel
 * Modified by Bruce Nguyen 26/11/2020: simplified optimizeLog for concept:name and timestamp attributes only
 * Modified by Bruce Nguyen 17/12/2020: fix bug, clean up
*/
public class Optimizer {

    private ConcurrentHashMap<Object, Object> cache = new ConcurrentHashMap<Object, Object>();
    
    private XFactory factory = new XFactoryNaiveImpl();

    /**
     * Create a new log from an existing log. In the new log, all attributes of log/traces/events
     * if having the same key or value will point to the same reference object
     * Log and trace: only have concept:name attribute
     * Events: have concept:name, timestamp and transition attributes
     * @param log: original XLog
     * @return new XLog object
     */
    public XLog optimizeLog(XLog log) {
        final String CONCEPTNAME = XConceptExtension.KEY_NAME;
        final String TIMESTAMP = XTimeExtension.KEY_TIMESTAMP;
        final String TRANSITION = XLifecycleExtension.KEY_TRANSITION;
        
        XLog newLog = factory.createLog();
        copyCachedAttribute(CONCEPTNAME, log.getAttributes().get(CONCEPTNAME), newLog);
        for(XTrace trace : log) {
            XTrace newTrace = factory.createTrace();
            copyCachedAttribute(CONCEPTNAME, trace.getAttributes().get(CONCEPTNAME), newTrace);
            for(XEvent event : trace) {
                XAttribute activityAtt = event.getAttributes().get(CONCEPTNAME);
                XAttribute timestampAtt = event.getAttributes().get(TIMESTAMP);
                XAttribute transitionAtt = event.getAttributes().get(TRANSITION);
                if (activityAtt != null && timestampAtt != null && transitionAtt != null) {
                    XEvent newEvent = factory.createEvent();
                    copyCachedAttribute(CONCEPTNAME, activityAtt, newEvent);
                    copyCachedAttribute(TIMESTAMP, timestampAtt, newEvent);
                    copyCachedAttribute(TRANSITION, transitionAtt, newEvent);
                    newTrace.insertOrdered(newEvent);
                }
            }
            if (!newTrace.isEmpty()) newLog.add(newTrace);
        }
        return newLog;
    }
    
    /**
     * Update a process definition to make every node containing a name ref
     * with reference to the name value (stored in map field of LogOptimizer)
     * @param definitions: original process definition
     * @return process definition after being modified
     */
    public Definitions optimizeProcessModel(Definitions definitions) {        
        List<BaseElement> rootElements = definitions.getRootElement();
        Process process = (Process)rootElements.get(0);        
        for (FlowElement element : process.getFlowElement()) {
            if (element instanceof FlowNode) {
                if (element.getName() == null) element.setName(""); //BPMN.io saves new gateway without name attribute (it is null)
                ((FlowNode)element).setNameRef(cacheObject(element.getName()));
            }
        }
        return definitions;
    }
    
    /**
     * Copy an attribute to an element. Cache the attribute in memory if it is not yet cached.
     * @param key: the key of the attribute
     * @param attribute: the attribute to be copied
     * @param newElement: the element which will contain a copy of the attribute
     * @return true: the attribute has been copied, false otherwise.
     */
    private void copyCachedAttribute(String key, XAttribute attribute, XAttributable newElement) {
        if (attribute == null || newElement == null || key == null || key.isEmpty()) return ;
        Object currentAttValue = getAttributeValue(attribute);
        if (currentAttValue != null) {
            String cachedKey = (String) cacheObject(key); // cache key
            Object cachedValue = cacheObject(currentAttValue); // cache value
            XAttribute newAtt = createXAttribute(cachedKey, cachedValue, attribute);
            newElement.getAttributes().put(cachedKey, newAtt);
        }
    }

    /**
     * Cache an object
     * @param o: an object
     * @return a reference to an already cached object with the same value as o, 
     *          or a reference to o if no cache exists.
     */
    private Object cacheObject(Object o) {
        Object result = null;
        if(o instanceof Date) return o;
        if((result = cache.get(o)) == null) {
            cache.put(o, o);
            result = o;
        }
        return result;
    }

    private Object getAttributeValue(XAttribute attribute) {
        if(attribute instanceof XAttributeLiteral) return ((XAttributeLiteral) attribute).getValue();
        else if(attribute instanceof XAttributeBoolean) return ((XAttributeBoolean) attribute).getValue();
        else if(attribute instanceof XAttributeDiscrete) return ((XAttributeDiscrete) attribute).getValue();
        else if(attribute instanceof XAttributeContinuous) return ((XAttributeContinuous) attribute).getValue();
        else if(attribute instanceof XAttributeTimestamp) return ((XAttributeTimestamp) attribute).getValue();
        else {
            System.out.println(attribute);
            System.out.println("Error getAttributeValue");
            return null;
        }
    }

    private XAttribute createXAttribute(String key, Object value, XAttribute originalAttribute) {
        XAttribute result = null;
        if(originalAttribute instanceof XAttributeLiteral) result = factory.createAttributeLiteral(key, (String) value, null);
        else if(originalAttribute instanceof XAttributeBoolean) result = factory.createAttributeBoolean(key, (Boolean) value, null);
        else if(originalAttribute instanceof XAttributeDiscrete) result = factory.createAttributeDiscrete(key, (Long) value, null);
        else if(originalAttribute instanceof XAttributeContinuous) result = factory.createAttributeContinuous(key, (Double) value, null);
        else if(originalAttribute instanceof XAttributeTimestamp) result = factory.createAttributeTimestamp(key, (Date) value, null);
        else {
            System.out.println("Error");
            return null;
        }
        return result;
    }
}
