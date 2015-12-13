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
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;

/**
 * Converts a YAWL Condition to the canonical format.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class ConditionHandler extends BaseConditionHandler<ExternalConditionFactsType> {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.yawl.simple.SimpleConditionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        NodeType eventOrState;

        if (!checkSingleEntry(getObject()) || !checkSingleExit(getObject())) {
            // Create State if we are non single entry
            eventOrState = createState(getObject());
        } else {
            eventOrState = createEvent(getObject(), getObject().getName());
        }

        if (hasIncomingQueue(getObject())) {
            connectFromPredecessors(getObject(), eventOrState);
        } else {
            getContext().addIntroducedPredecessor(getObject(), eventOrState);
        }

        connectToSuccessors(eventOrState, getObject().getFlowsInto());

        convertAnnotations();
    }

    /**
     * Convert Layout and Documenattion
     * 
     * @throws CanoniserException
     */
    protected void convertAnnotations() throws CanoniserException {
        if (getObject().getDocumentation() != null) {
            createDocumentation(getObject(), getObject().getDocumentation());
        }
        createGraphics(getObject());
    }

}
