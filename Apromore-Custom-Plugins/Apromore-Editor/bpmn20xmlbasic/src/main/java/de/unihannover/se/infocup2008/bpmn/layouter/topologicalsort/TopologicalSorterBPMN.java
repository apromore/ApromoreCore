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
package de.unihannover.se.infocup2008.bpmn.layouter.topologicalsort;

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

import de.hpi.layouting.model.LayoutingDiagram;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.topologicalsort.BackwardsEdge;
import de.hpi.layouting.topologicalsort.SortableLayoutingElement;
import de.hpi.layouting.topologicalsort.TopologicalSorter;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

import java.util.List;

/**
 * This class does a slight modified topological sort
 *
 * @author Team Royal Fawn
 */
public class TopologicalSorterBPMN extends TopologicalSorter {


    public TopologicalSorterBPMN(LayoutingDiagram diagram,
                                 LayoutingElement parent) {
        super(diagram, parent);
    }

//	@Override
//	protected void prepareDataAndSort(LayoutingElement parent, boolean shouldBackpatch) {
//		sortetElements = new LinkedList<LayoutingElement>();
//		elementsToSort = new HashMap<String, SortableLayoutingElement>();
//		backwardsEdges = new LinkedList<BackwardsEdge>();
//
//		// create global start
//		LayoutingElement globalStartDummyElement = new LayoutingElementImpl();
//		globalStartDummyElement.setId("#####Global-Start#####");
//		globalStartDummyElement.setType(BPMNType.StartEvent);
//		for (LayoutingElement startElement : this.diagram.getStartEvents()) {
//			globalStartDummyElement.addOutgoingLink(startElement);
//			startElement.addIncomingLink(globalStartDummyElement);
//		}
//		elementsToSort.put(globalStartDummyElement.getId(),
//				new SortableLayoutingElement(globalStartDummyElement));
//
//		addAllChilds(parent);
//
//		topologicalSort();
//
//		if (shouldBackpatch) {
//			backpatchBackwardsEdges();
//		}
//		// write backwards edges in diagram
//		reverseBackwardsEdges();
//		// remove global start
//		for (LayoutingElement startElement : this.diagram.getStartEvents()) {
//			globalStartDummyElement.removeOutgoingLink(startElement);
//			startElement.removeIncomingLink(globalStartDummyElement);
//		}
//		this.sortetElements.remove(globalStartDummyElement);
//	}

    @Override
    /**
     * @param parent
     */
    protected void addAllChilds(LayoutingElement parent) {
        for (LayoutingElement el : diagram.getChildElementsOf(parent)) {
            BPMNElement element = (BPMNElement) el;
            // LayoutingElement element = diagram.getElement(id);
            if (!BPMNType.isAConnectingElement(element.getType())
                    && !element.isADockedIntermediateEvent()
                    && !BPMNType.isASwimlane(element.getType())) {
                elementsToSort.put(element.getId(), new SortableLayoutingElement(
                        element));
            } else if (BPMNType.isASwimlane(element.getType())) {
                addAllChilds(element);
            }
        }
    }

    @Override
    protected void reverseBackwardsEdges() {
        List<LayoutingElement> edges = this.diagram.getConnectingElements();
        for (BackwardsEdge backwardsEdge : this.backwardsEdges) {
            String sourceId = backwardsEdge.getSource();
            String targetId = backwardsEdge.getTarget();
            LayoutingElement sourceElement = (LayoutingElement) this.diagram.getElement(sourceId);
            LayoutingElement targetElement = (LayoutingElement) this.diagram.getElement(targetId);

            LayoutingElement edge = getEdge(edges, (LayoutingElement) sourceElement, (LayoutingElement) targetElement);

            boolean elementSkipped = (edge == null);
            if (elementSkipped) {
                // catching intermediate events skipped
                for (LayoutingElement ol : sourceElement
                        .getOutgoingLinks()) {
                    BPMNElement outgoingLink = (BPMNElement) ol;
                    if (outgoingLink.isADockedIntermediateEvent()) {
                        edge = getEdge(edges, outgoingLink, targetElement);
                        if (edge != null) {
                            System.err.println("found");
                            break;
                        }
                    }
                }

            }

            backwardsEdge.setEdge(edge);

            // remove edge
            sourceElement.removeOutgoingLink(edge);
            targetElement.removeIncomingLink(edge);

            // add direct back link
            targetElement.addOutgoingLink(sourceElement);
            sourceElement.addIncomingLink(targetElement);
        }

    }
}
