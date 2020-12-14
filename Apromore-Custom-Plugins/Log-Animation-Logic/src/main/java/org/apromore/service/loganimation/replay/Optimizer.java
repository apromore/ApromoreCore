/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
* Created by Raffaele Conforti on 21/10/14.
* Modified by Bruce 10/11/2014: added optimizeProcessModel
* Modified by Bruce Nguyen 26/11/2020: simplified optimizeLog for concept:name attribute only.
*/
public class Optimizer {

    private ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<Object, Object>();
    private XFactory factory = new XFactoryNaiveImpl();

    /**
     * Replace all concept:name values with reference to the same String instance value.
     * @param log
     * @return new XLog object
     */
    public XLog optimizeLog(XLog log) {
        XLog newLog = factory.createLog();
        copyCachedAttribute(log.getAttributes().get(XConceptExtension.KEY_NAME), newLog);
        for(XTrace trace : log) {
            XTrace newTrace = factory.createTrace();
            copyCachedAttribute(trace.getAttributes().get(XConceptExtension.KEY_NAME), newTrace);
            for(XEvent event : trace) {
                XAttribute activityAtt = event.getAttributes().get(XConceptExtension.KEY_NAME);
                XAttribute timestampAtt = event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
                if (activityAtt != null && timestampAtt != null) {
                    XEvent newEvent = factory.createEvent();
                    copyCachedAttribute(activityAtt, newEvent);
                    copyCachedAttribute(timestampAtt, newEvent);
                    newTrace.insertOrdered(newEvent);
                }
            }
            if (!newTrace.isEmpty()) newLog.add(newTrace);
        }
        return newLog;
    }
    
    private boolean copyCachedAttribute(XAttribute attribute, XAttributable newElement) {
        Object currentAttValue = getAttributeValue(attribute);
        String cachedKey = (String) cacheObject(attribute.getKey()); // cache key
        if (currentAttValue != null) {
            Object cachedValue = cacheObject(currentAttValue); // cache value
            XAttribute newAtt = createXAttribute(cachedKey, cachedValue, attribute);
            newElement.getAttributes().put(cachedKey, newAtt);
            return true;
        }
        return false;
    }
    
    /**
     * Update a process definition to make every node containing a name ref
     * with reference to the name value (stored in map field of LogOptimizer)
     * @param definitions: original process definition
     * @return process definition after being modified
     */
    public Definitions optimizeProcessModel(Definitions definitions) {
        /*
        Map<FlowNode, FlowNode2> nodeMap = new HashMap();
        FlowNode source;
        FlowNode2 source2;
        FlowNode target;
        FlowNode2 target2;
        */
        
        List<BaseElement> rootElements = definitions.getRootElement();
        Process process = (Process)rootElements.get(0);        
        for (FlowElement element : process.getFlowElement()) {
            if (element instanceof FlowNode) {
                ((FlowNode)element).setNameRef(cacheObject(element.getName()));
            }
        }
        
        return definitions;
    }

    public ConcurrentHashMap<Object, Object> getReductionMap() {
        return map;
    }

    private Object cacheObject(Object o) {
        Object result = null;
        if(o instanceof Date) return o;
        if((result = map.get(o)) == null) {
            map.put(o, o);
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
