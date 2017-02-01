/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.util.List;

import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Abstract base class for all NetElement conversion
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class OryxNetElementHandler extends OryxDecompositionHandler {

    public OryxNetElementHandler(final OryxConversionContext context, final BasicShape shape) {
        super(context, shape);
    }

    /**
     * Converting the incoming flowShapes to YFlows
     * 
     * @param element
     *            with incoming flows, should have been added to YNet already!
     */
    protected void rememberIncomings() {
        List<BasicShape> incomings = getShape().getIncomingsReadOnly();
        addFlows(incomings);
    }

    /**
     * Converting the outgoing flowShapes to YFlows
     * 
     * @param element
     *            with incoming flows, should have been added to YNet already!
     */
    protected void rememberOutgoings() {
        List<BasicShape> outgoings = getShape().getOutgoingsReadOnly();
        addFlows(outgoings);
    }

    private void addFlows(final List<BasicShape> outgoings) {
        for (BasicShape flowShape : outgoings) {
            if (flowShape instanceof BasicEdge) {
                getContext().addFlow(getShape().getParent(), (BasicEdge) flowShape);
            } else {
                // TODO check if these Shapes are still needed
                getContext().addConversionWarnings("Edge was not added to FlowSet " + flowShape.toString(), null);
            }
        }
    }

}
