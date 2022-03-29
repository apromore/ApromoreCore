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

import java.util.ArrayList;
import java.util.logging.Logger;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.transformation.AbstractVisitor;

public class ModelChecker extends AbstractVisitor {
    int numberOfStartEvent = 0;
    int numberOfEndEvent = 0;
    private BaseElement rootElement = null;
    private Definitions definitions;
    private ArrayList<String> faultMessages = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(ModelChecker.class.getCanonicalName());
    
    @Override
    public void visitBaseElement(BaseElement that) {
        
        if (that instanceof FlowNode) {
            FlowElement flowElement = (FlowElement)that;
            
            for(Edge edge : flowElement.getIncoming()) {
                if (flowElement == edge.getSourceRef()) {
                    faultMessages.add("The element " + flowElement.getName() + " has self-loop");
                    break;
                }
            }
            
            for(Edge edge : flowElement.getOutgoing()) {
                if (flowElement == edge.getTargetRef()) {
                    faultMessages.add("The element " + flowElement.getName() + " has self-loop");
                    break;
                }
            }
            
            if (that instanceof Activity) {
                if (flowElement.getOutgoing().size() > 1) {
                    faultMessages.add("The task " + flowElement.getName() + " has more than one outgoing arc");
                }
                
                if (flowElement.getIncoming().size() > 1) {
                    faultMessages.add("The task " + flowElement.getName() + " has more than one incoming arc");
                }
                
                if (flowElement.getOutgoing().isEmpty() || flowElement.getIncoming().isEmpty()) {
                    faultMessages.add("The task " + flowElement.getName() + " has missing incoming or outgoing arcs");
                }
            }
            
            if (that instanceof StartEvent) {
                if (flowElement.getOutgoing().size() > 1) {
                    faultMessages.add("The Start Event has more than one outgoing arc");
                }
                else if (flowElement.getOutgoing().isEmpty()) {
                    faultMessages.add("The Start Event has a missing outgoing arc");
                }
                
                if (!flowElement.getIncoming().isEmpty()) {
                    faultMessages.add("The Start Event has incoming arc(s)");
                }
            }
            
            if (that instanceof EndEvent) {
                if (flowElement.getIncoming().size() > 1) {
                    faultMessages.add("The End Event has more than one incoming arc");
                }
                else if (flowElement.getIncoming().isEmpty()) {
                    faultMessages.add("The End Event has a missing incoming arc");
                }
                
                if (!flowElement.getOutgoing().isEmpty()) {
                    faultMessages.add("The End Event has outgoing arc(s)");
                }
            }
            
            if (that instanceof Gateway) {
                if (flowElement.getOutgoing().isEmpty() || flowElement.getIncoming().isEmpty()) {
                    faultMessages.add("The gateway " + flowElement.getId() + " has missing incoming or outgoing arcs");
                }
            }
        }
        else if (that instanceof SequenceFlow) {
            Edge edge = (Edge)that;
            String disconnectedMsg = "The model has disconnected arcs";
            if (edge.getSourceRef() == null || edge.getTargetRef() == null) {
                if (!faultMessages.contains(disconnectedMsg)) faultMessages.add(disconnectedMsg);
            }
        }

    }
    
    public boolean isValid() {
        return (faultMessages.size() == 0);
    }
    
    public String getFaultMessage() {
        String faultMsg="";
        if (faultMessages.size() > 0) {
            for (String msg : faultMessages) {
                faultMsg += msg + System.getProperty("line.separator");
            }
        }
        return faultMsg;
    }
}
