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
package de.unihannover.se.infocup2008.bpmn.layouter;

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

import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingDockers;
import de.hpi.layouting.model.LayoutingElement;
import de.unihannover.se.infocup2008.bpmn.layouter.decorator.DocketEventDecorator;
import de.unihannover.se.infocup2008.bpmn.layouter.decorator.LayoutConstants;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

/**
 * This class positions the catching intermediate events which are docked at a
 * task
 *
 * @author Team Royal Fawn
 */
public class CatchingIntermediateEventLayouter {

    public static void setCatchingIntermediateEvents(BPMNDiagram diagram) {
        for (String id : diagram.getElements().keySet()) {
            BPMNElement element = (BPMNElement) diagram.getElement(id);
            if (BPMNType.isAActivity(element.getType())) {
                int count = 0;
                for (LayoutingElement connectedElement : element.getOutgoingLinks()) {
                    if (BPMNType.isACatchingIntermediateEvent(connectedElement
                            .getType())) {
                        LayoutingBounds relativeGeometry = element.getGeometry();
                        LayoutingBounds newGeometry = new DocketEventDecorator(
                                connectedElement.getGeometry(),
                                relativeGeometry, count);

                        connectedElement.setGeometry(newGeometry);

                        LayoutingDockers dockers = connectedElement.getDockers();
                        if (dockers != null) {
                            double dockerX = newGeometry.getX()
                                    - relativeGeometry.getX()
                                    + (LayoutConstants.EVENT_DIAMETER / 2);
                            double dockerY = relativeGeometry.getHeight() - 8;

                            //System.out.println(dockerX + "," + dockerY);
                            dockers.setPoints(dockerX, dockerY);
                        } else {
                            System.err.println("Fehler beim dockersnode");
                        }
                        count++;
                    }
                }
            }
        }
    }

}
