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

import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingElementImpl;

import java.util.LinkedList;
import java.util.List;


public abstract class BPMNAbstractElement extends LayoutingElementImpl implements BPMNElement {


    @Override
    public List<LayoutingElement> getFollowingElements() {
        List<LayoutingElement> followingElements = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getOutgoingLinks()) {
            if (BPMNType.isAConnectingElement(element.getType())) {
                followingElements.addAll(element.getFollowingElements());
            } else if (BPMNType.isAActivity(getType())
                    && BPMNType.isACatchingIntermediateEvent(element.getType())) {
                followingElements.addAll(element.getFollowingElements());
            } else if (!BPMNType.isASwimlane(element.getType())) {
                followingElements.add(element);
            }
        }

        return followingElements;
    }

    @Override
    public List<LayoutingElement> getPrecedingElements() {
        List<LayoutingElement> precedingElements = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getIncomingLinks()) {
            if (BPMNType.isAConnectingElement(element.getType())) {
                precedingElements.addAll(element.getPrecedingElements());
            } else if (BPMNType.isACatchingIntermediateEvent(getType())
                    && BPMNType.isAActivity(element.getType())) {
                precedingElements.addAll(element.getPrecedingElements());
            } else if (element instanceof BPMNElement) {
                if (((BPMNElement) element).isADockedIntermediateEvent())
                    precedingElements.addAll(element.getIncomingLinks());
                else if (!BPMNType.isASwimlane(element.getType()))
                    precedingElements.add(element);
            } else if (!BPMNType.isASwimlane(element.getType())) {
                precedingElements.add(element);
            }
        }

        return precedingElements;
    }


    public boolean isADockedIntermediateEvent() {
        if (!BPMNType.isACatchingIntermediateEvent(getType())) {
            return false;
        }

        for (LayoutingElement element : getIncomingLinks()) {
            if (BPMNType.isAActivity(element.getType())) {
                return true;
            }
        }

        return false;
    }
}