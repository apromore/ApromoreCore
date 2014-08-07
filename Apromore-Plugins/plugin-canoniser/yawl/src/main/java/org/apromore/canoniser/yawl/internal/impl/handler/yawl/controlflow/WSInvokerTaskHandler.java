/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.DirectionEnum;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NodeType;

/**
 * Converts a synchronous Web Service call of YAWL.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class WSInvokerTaskHandler extends BaseTaskHandler {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.BaseTaskHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        // Convert to CPF Message Event -> CPF Task -> CPF Message Event
        final NodeType taskNode = createTask(getObject());

        final MessageType preceedingEvent = createMessage(DirectionEnum.OUTGOING);
        createSimpleEdge(preceedingEvent, taskNode);
        final MessageType succeedingEvent = createMessage(DirectionEnum.INCOMING);
        createSimpleEdge(taskNode, succeedingEvent);

        // Link correctly to predecessor and successors separating the routing behavior from the task.
        linkToPredecessors(preceedingEvent);
        linkToSucessors(succeedingEvent);

        super.convert();
    }

}
