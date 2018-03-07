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

package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.DirectionEnum;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.ResourcingExternalInteractionType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

/**
 * TODO
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class MessageTypeHandler extends DecompositionHandler<MessageType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTypeHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        if (getContext().getPostSet(getObject().getId()).size() <= 1 && getContext().getPostSet(getObject().getId()).size() <= 1) {

            final NodeType successor = getContext().getFirstSuccessor(getObject().getId());
            final NodeType predecessor = getContext().getFirstPredecessor(getObject().getId());

            if (isTaskAfterIncomingMessage(successor)) {
                if (isOutgoingMessage(getContext().getFirstSuccessor(successor.getId()))) {
                    // Task between two Message Events one incoming one outgoing -> YAWL Web-Service call
                    LOGGER.debug("Ignore two  Messages as succeeding Task {} will recognise it.", successor.getName());
                } else {
                    // Message Event before Task
                    createMessageTask();
                    LOGGER.debug("Single Message Event before Task, added introduced Task.");
                }
            } else if (isTaskBeforeOutgoingMessage(predecessor)) {
                if (isIncomingMessage(getContext().getFirstPredecessor(predecessor.getId()))) {
                    // Task between two Message Events one incoming one outgoing -> YAWL Web-Service call
                    LOGGER.debug("Ignore two  Messages as succeeding Task {} will recognise it.", successor.getName());
                } else {
                    // Message Event succeeding Task
                    createMessageTask();
                    LOGGER.debug("Single Message Event after Task, added introduced Task.");
                }

            } else {
                // No Task after or before this Message Event, introduce a new Task as there is not Message Event in YAWL
                createMessageTask();
            }

        } else {
            throw new CanoniserException("Timer node should have not more than one predecessor!");
        }
    }

    private void createMessageTask() {
        final ExternalTaskFactsType task = createTask(getObject());
        task.setSplit(getDefaultSplitType());
        task.setJoin(getDefaultJoinType());
        final WebServiceGatewayFactsType d = createDecomposition(getObject());
        // Will be an automatic Task in YAWL that is doing nothing
        d.setExternalInteraction(ResourcingExternalInteractionType.AUTOMATED);
        task.setDecomposesTo(d);
        LOGGER.debug("Added new (introduced) Task for Message {}", getObject().getName());
        getConvertedParent().getProcessControlElements().getTaskOrCondition().add(task);
    }

    private boolean isIncomingMessage(final NodeType taskPredecessor) {
        return taskPredecessor instanceof MessageType && ((MessageType) taskPredecessor).getDirection() == DirectionEnum.INCOMING;
    }

    private boolean isTaskBeforeOutgoingMessage(final NodeType predecessor) {
        return predecessor instanceof TaskType && getObject().getDirection() == DirectionEnum.OUTGOING;
    }

    private boolean isTaskAfterIncomingMessage(final NodeType successor) {
        return successor instanceof TaskType && getObject().getDirection() == DirectionEnum.INCOMING;
    }

    private boolean isOutgoingMessage(final NodeType taskSuccessor) {
        return taskSuccessor instanceof MessageType && ((MessageType) taskSuccessor).getDirection() == DirectionEnum.OUTGOING;
    }

}
