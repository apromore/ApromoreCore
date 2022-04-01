/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.service.loganimation.impl;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;

/**
 * Used to map from the name of nodes and sequence flows to their corresponding ID.
 * SequenceFlow name is the concatenation of the source name, a delimiter, and the sink name.
 * 
 * @author Bruce Nguyen
 *
 */
public class ElementIDMapper {
    public static final String UNFOUND = "@@NotFound@@";
    
    private Map<String,String> nameToIDMapping = new HashMap<>();
    
    private final String nameDelimiter = "@";
    
    public ElementIDMapper(Definitions diagram) {
        Process process = (Process)diagram.getRootElement().get(0);
        for (FlowElement ele: process.getFlowElement()) {
            if (ele instanceof FlowNode) {
                FlowNode node = (FlowNode)ele;
                nameToIDMapping.put(node.getName(), node.getId());
            }
            else if (ele instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow)ele;
                nameToIDMapping.put(flow.getSourceRef().getName() + nameDelimiter + flow.getTargetRef().getName(), 
                                    flow.getId());             
            }
        }
    }
    
    public String getId(String name) {
        return nameToIDMapping.getOrDefault(name, ElementIDMapper.UNFOUND);
    }
    
    public String getId(FlowNode node) {
        return this.getId(node.getName());
    }
    
    public String getId(SequenceFlow flow) {
        return this.getId(flow.getSourceRef().getName() + nameDelimiter + flow.getTargetRef().getName());
    }
}
