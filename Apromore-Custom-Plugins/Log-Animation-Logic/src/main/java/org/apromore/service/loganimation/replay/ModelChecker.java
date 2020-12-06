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

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.transformation.AbstractVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

            
            //Check one element cannot be its own source or target
            //LOGGER.info("Checking: element is not its own source");
            FlowElement flowElement = (FlowElement)that;
            for(Edge edge : flowElement.getIncoming()) {
                if (flowElement == edge.getSourceRef()) {
                    faultMessages.add("Element " + flowElement.getId() + " is its own source.");
                    break;
                }
            }
            
            //LOGGER.info("Checking: element is not its own target");
            for(Edge edge : flowElement.getOutgoing()) {
                if (flowElement == edge.getTargetRef()) {
                    faultMessages.add("Element " + flowElement.getId() + " is its own target.");
                    break;
                }
            }
            
            //LOGGER.info("Checking: Activity does not have more than one target");
            if (that instanceof Activity) {
                if (flowElement.getOutgoing().size() > 1) {
                    faultMessages.add("Task " + flowElement.getId() + " has more than one target.");
                }
                
                if (flowElement.getIncoming().size() > 1) {
                    faultMessages.add("Task " + flowElement.getId() + " has more than one source.");
                }
            }
            
            //LOGGER.info("Checking: StartEvent does not have more than one target");
            if (that instanceof StartEvent) {
                if (flowElement.getOutgoing().size() > 1) {
                    faultMessages.add("Start Event " + flowElement.getId() + " has more than one target.");
                }
            }
            
            //LOGGER.info("Checking: EndEvent does not have more than one source");
            if (that instanceof EndEvent) {
                if (flowElement.getIncoming().size() > 1) {
                    faultMessages.add("End Event " + flowElement.getId() + " has more than one source.");
                }
            }
            
            //LOGGER.info("Checking gateways: merge only one target, split only one source");
            if (that instanceof Gateway) {
                Gateway gw = (Gateway)that;
                GatewayDirection direction = gw.getGatewayDirection();
                
                if (direction.equals(GatewayDirection.CONVERGING)) {
                    if (!(gw.getIncomingSequenceFlows().size() > 1 && gw
                            .getOutgoingSequenceFlows().size() == 1)) {
                        faultMessages.add("Merge Gateway " + gw.getId() + " has more than one target");
                    }
                } else if (direction.equals(GatewayDirection.DIVERGING)) {
                    if (!(gw.getIncomingSequenceFlows().size() == 1 && gw
                        .getOutgoingSequenceFlows().size() > 1)) {
                        faultMessages.add("Split Gateway " + gw.getId() + " has more than one source");
                    }
                }
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
