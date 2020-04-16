/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages

import javax.xml.namespace.QName;

import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataObjectReference;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.dd._20100524.dc.Bounds;

// Local packages

/**
 * BPMNDI Shape element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmndiShape extends BPMNShape {

    private static final double SCALE = 1;

    /** No-arg constructor. */
    public BpmndiShape() { }

    /**
     * Construct a BPMNDI Shape corresponding to an ANF Graphics annotation.
     * @param graphics  an ANF graphics annotation, never <code>null</code>
     * @param initializer  BPMN document construction state
     * @throws CanoniserException if the shape can't be constructed
     */
    public BpmndiShape(final GraphicsType graphics, final Initializer initializer) throws CanoniserException {
        initializer.populateDiagramElement(this, graphics);

        // Validate the graphics parameter: requires a bounding box, defined by a top-left position and a size (width and height)
        if (graphics.getPosition().size() != 1) {
            throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF shape " +
                    graphics.getCpfId() + " should have just one origin position");
        }
        if (graphics.getSize() == null) {
            throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF shape " +
                    graphics.getCpfId() + " should specify a size");
        }

        // Validate the cpfId: must reference a BPMN element that has a bounding box
        TBaseElement bpmnElement = initializer.findElement(graphics.getCpfId());
        if (bpmnElement instanceof TProcess) {
            java.util.logging.Logger.getAnonymousLogger().warning(
                    "ANF graphics " + graphics.getId() + " references CPF element " + graphics.getCpfId() + " which corresponds to a BPMN process " +
                            bpmnElement.getId()
            );
        } else if (!(bpmnElement instanceof TDataObject     ||
                bpmnElement instanceof TDataObjectReference ||
                bpmnElement instanceof TDataStoreReference  ||
                bpmnElement instanceof TFlowNode            ||
                bpmnElement instanceof TLane                ||
                bpmnElement instanceof TParticipant         ||
                bpmnElement instanceof TProcess)) {  // TODO - decide whether TProcess really is legitimate to have a BPMNShape

            throw new CanoniserException(graphics.getCpfId() + " isn't a BPMN element with a Shape");
        }

        // Handle @bpmnElement
        setBpmnElement(new QName(initializer.getTargetNamespace(),
                initializer.findElement(graphics.getCpfId()).getId()));

        // Handle @isExpanded
        if (bpmnElement instanceof TSubProcess) {
            setIsExpanded(true);  // TODO - this should dereference graphics.getCpfId() to determine a proper value
        }

        // add the ANF position and size as a BPMNDI bounds
        Bounds bounds = new Bounds();
        bounds.setHeight(SCALE * graphics.getSize().getHeight().doubleValue());
        bounds.setWidth(SCALE * graphics.getSize().getWidth().doubleValue());
        bounds.setX(SCALE * graphics.getPosition().get(0).getX().doubleValue());
        bounds.setY(SCALE * graphics.getPosition().get(0).getY().doubleValue());
        setBounds(bounds);
    }
}
