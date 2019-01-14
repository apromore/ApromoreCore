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

import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingElementImpl;

import java.util.LinkedList;
import java.util.List;

public class EPCElementImpl extends LayoutingElementImpl implements
        EPCElement {

    @Override
    public List<LayoutingElement> getFollowingElements() {
        List<LayoutingElement> followingElements = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getOutgoingLinks()) {
            if (EPCType.isAConnectingElement(element.getType())) {
                followingElements.addAll(element.getFollowingElements());
            } else {
                followingElements.add(element);
            }
        }

        return followingElements;
    }

    @Override
    public List<LayoutingElement> getPrecedingElements() {
        List<LayoutingElement> precedingElements = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getIncomingLinks()) {
            if (EPCType.isAConnectingElement(element.getType())) {
                precedingElements.addAll(element.getPrecedingElements());
            } else {
                precedingElements.add(element);
            }
        }

        return precedingElements;
    }

}
