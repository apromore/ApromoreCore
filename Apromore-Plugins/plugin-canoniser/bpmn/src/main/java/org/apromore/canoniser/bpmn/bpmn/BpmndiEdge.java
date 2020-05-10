/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages

import javax.xml.namespace.QName;

import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDataAssociation;
import org.omg.spec.bpmn._20100524.model.TMessageFlow;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.dd._20100524.dc.Point;

// Local packages

/**
 * BPMNDI Edge element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmndiEdge extends BPMNEdge {

    private static final double SCALE = 1;

    /** No-arg constructor. */
    public BpmndiEdge() { }


    /**
     * Construct a BPMNDI Edge corresponding to an ANF Graphics annotation.
     * @param graphics  an ANF graphics annotation, never <code>null</code>
     * @param initializer  BPMN document construction state
     * @throws CanoniserException if the edge can't be constructed
     */
    public BpmndiEdge(final GraphicsType graphics, final Initializer initializer) throws CanoniserException {
        initializer.populateDiagramElement(this, graphics);

        // Validate the graphics parameter: requires two or more waypoints
        if (graphics.getPosition().size() < 2) {
            //throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF edge " +
            //                             graphics.getCpfId() + " should contain at least two positions");
            initializer.warn("ANF Graphics annotation " + graphics.getId() + " for CPF edge " +
                    graphics.getCpfId() + " should contain at least two positions");

            // TODO - remove this brazen hack, which exists only to humor the OrderFulfillment.anf test case
            if (graphics.getPosition().size() == 0) {
                initializer.warn("Inserting fake waypoints to ANF Graphics annotation " + graphics.getId());
                getWaypoint().add(new Point());
                getWaypoint().add(new Point());
            }
        }

        // Validate the cpfId: must reference a BPMN flow node or lane
        TBaseElement bpmnElement = initializer.findElement(graphics.getCpfId());
        if (!(bpmnElement instanceof TDataAssociation || bpmnElement instanceof TMessageFlow || bpmnElement instanceof TSequenceFlow)) {
            throw new CanoniserException(graphics.getCpfId() + " isn't a BPMN element with an Edge");
        }

        // Handle @bpmnElement
        setBpmnElement(new QName(initializer.getTargetNamespace(), bpmnElement.getId()));

        // add each ANF position as a BPMNDI waypoint
        for (PositionType position : graphics.getPosition()) {
            Point point = new Point();
            point.setX(SCALE * position.getX().doubleValue());
            point.setY(SCALE * position.getY().doubleValue());
            getWaypoint().add(point);
        }
    }
}
