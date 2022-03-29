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

package org.apromore.processdiscoverer.bpmn;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;

/**
 * SimpleBPMNDiagram is a <b>BPMNDiagram</b> created based on events in the log.
 * 
 * @author Bruce Nguyen
 *
 */
public class SimpleBPMNDiagram extends BPMNDiagramImpl {
    protected AttributeLog log;
    
    public SimpleBPMNDiagram(AttributeLog log) {
        super("");
        this.log = log;
    }
    
    public BPMNNode addNode(int nodeTraceValue) {
        String nodeLabel = log.getStringFromValue(nodeTraceValue);
        if (log.isArtificialStart(nodeTraceValue)) {
            return this.addEvent(nodeLabel, Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, true, null);
        }
        else if (log.isArtificialEnd(nodeTraceValue)) {
            return this.addEvent(nodeLabel, Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, true, null);
        }
        else {
            return this.addActivity(nodeLabel, false, false, false, false, false);
        }
    }
    
}
