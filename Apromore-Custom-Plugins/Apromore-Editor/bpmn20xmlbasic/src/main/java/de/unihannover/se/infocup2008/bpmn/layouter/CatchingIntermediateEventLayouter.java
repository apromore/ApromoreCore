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
package de.unihannover.se.infocup2008.bpmn.layouter;

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
