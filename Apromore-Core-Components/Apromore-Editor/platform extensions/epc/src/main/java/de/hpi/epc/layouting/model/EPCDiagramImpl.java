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

package de.hpi.epc.layouting.model;

import de.hpi.layouting.model.LayoutingAbstractDiagram;
import de.hpi.layouting.model.LayoutingElement;

import java.util.LinkedList;
import java.util.List;

public class EPCDiagramImpl extends LayoutingAbstractDiagram<LayoutingElement> implements EPCDiagram {

    @Override
    protected EPCElement newElement() {
        return new EPCElementJSON();
    }

    public List<LayoutingElement> getStartEvents() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (!EPCType.isAnEvent(element.getType()))
                continue;
            if (element.getIncomingLinks().size() == 0)
                resultList.add((LayoutingElement) element);
        }

        return resultList;
    }

    public List<LayoutingElement> getConnectingElements() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (EPCType.isAConnectingElement(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

    public List<LayoutingElement> getGateways() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (EPCType.isAConnector(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }


}
