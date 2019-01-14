/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package de.unihannover.se.infocup2008.bpmn.model;

import de.hpi.layouting.model.LayoutingAbstractDiagram;
import de.hpi.layouting.model.LayoutingElement;

import java.util.LinkedList;
import java.util.List;

public abstract class BPMNAbstractDiagram extends LayoutingAbstractDiagram<LayoutingElement> implements BPMNDiagram {

    /* (non-Javadoc)
      * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getStartEvents()
      */
    public List<LayoutingElement> getStartEvents() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (BPMNType.isAStartEvent(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

    /* (non-Javadoc)
      * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getConnectingElements()
      */
    public List<LayoutingElement> getConnectingElements() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (BPMNType.isAConnectingElement(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

    /* (non-Javadoc)
      * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getGateways()
      */
    public List<LayoutingElement> getGateways() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (BPMNType.isAGateWay(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

}
