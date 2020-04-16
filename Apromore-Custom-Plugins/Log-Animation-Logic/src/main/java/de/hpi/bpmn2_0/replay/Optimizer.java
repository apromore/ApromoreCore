/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Created by Raffaele Conforti on 21/10/14.
* Modified by Bruce 10/11/2014: added optimizeProcessModel
*/
public class Optimizer {

    private ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<Object, Object>();
    private XFactory factory = new XFactoryNaiveImpl();

    public XLog optimizeLog(XLog log) {

        XLog result = factory.createLog();

        for(Map.Entry<String, XAttribute> entry : log.getAttributes().entrySet()) {
            String key = (String) getObject(entry.getKey());
            Object value = getObject(getAttributeValue(entry.getValue()));
            XAttribute attribute = createXAttribute(key, value, entry.getValue());

            result.getAttributes().put(key, attribute);
        }

        for(XTrace trace : log) {
            XTrace newTrace = factory.createTrace();

            for(Map.Entry<String, XAttribute> entry : trace.getAttributes().entrySet()) {
                String key = (String) getObject(entry.getKey());
                Object value = getObject(getAttributeValue(entry.getValue()));
                XAttribute attribute = createXAttribute(key, value, entry.getValue());

                newTrace.getAttributes().put(key, attribute);
            }

            for(XEvent event : trace) {
                XEvent newEvent = factory.createEvent();

                for(Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {
                    String key = (String) getObject(entry.getKey());
                    Object value = getObject(getAttributeValue(entry.getValue()));
                    XAttribute attribute = createXAttribute(key, value, entry.getValue());

                    newEvent.getAttributes().put(key, attribute);
                }
                
                newTrace.insertOrdered(newEvent); //Bruce
            }

            result.add(newTrace);
        }

        return result;
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
                ((FlowNode)element).setNameRef(getObject(element.getName()));
            }
            /*
            if (element instanceof SequenceFlow) {
                //------------------------------------
                // Copy source and target node of every sequence flow
                //------------------------------------
                source = (FlowNode)((SequenceFlow)element).getSourceRef();
                if (nodeMap.containsKey(source)) {
                    source2 = nodeMap.get(source);
                } else {
                    source2 = new FlowNode2(source);
                    source2.setNameRef(getObject(source2.getName()));
                    nodeMap.put(source, source2);
                }
                target = (FlowNode)((SequenceFlow)element).getTargetRef();
                if (nodeMap.containsKey(target)) {
                    target2 = nodeMap.get(target);
                } else {
                    target2 = new FlowNode2(target);
                    target2.setNameRef(getObject(target2.getName()));
                    nodeMap.put(target, target2);
                }

                //---------------------------------------
                // Adjust sequence flow source and target
                //---------------------------------------
                ((SequenceFlow)element).setSourceRef(source2);
                ((SequenceFlow)element).setTargetRef(target2);
            } 
            */
        }
        
        return definitions;
    }

    public ConcurrentHashMap<Object, Object> getReductionMap() {
        return map;
    }

    private Object getObject(Object o) {
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
