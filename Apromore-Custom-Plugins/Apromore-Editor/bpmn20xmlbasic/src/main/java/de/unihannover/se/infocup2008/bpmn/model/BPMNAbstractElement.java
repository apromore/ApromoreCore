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

package de.unihannover.se.infocup2008.bpmn.model;

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

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
