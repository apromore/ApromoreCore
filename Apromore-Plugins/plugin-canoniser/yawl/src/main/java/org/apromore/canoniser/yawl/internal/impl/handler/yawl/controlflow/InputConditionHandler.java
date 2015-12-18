/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;

/**
 * Converts the YAWL InputCondition.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class InputConditionHandler extends BaseConditionHandler<ExternalConditionFactsType> {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.yawl.simple.SimpleConditionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // Each YAWL Net will always start with an Event
        final NodeType event = createEvent(getObject(), getObject().getName());

        if (checkSingleExit(getObject())) {
            // This is safe as we're single exit
            connectToSuccessors(event, getObject().getFlowsInto());
        } else {
            // We are the Input Condition -> Insert a State
            final StateType state = createState();
            // Connect ourself with the state node
            createSimpleEdge(event, state);
            // Connect State with our successors
            connectToSuccessors(state, getObject().getFlowsInto());
        }

        convertAnnotations();
    }

    protected void convertAnnotations() throws CanoniserException {
        if (getObject().getDocumentation() != null) {
            createDocumentation(getObject(), getObject().getDocumentation());
        }
        createGraphics(getObject());
    }

}
