package de.unihannover.se.infocup2008.bpmn.layouter;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
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
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

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
