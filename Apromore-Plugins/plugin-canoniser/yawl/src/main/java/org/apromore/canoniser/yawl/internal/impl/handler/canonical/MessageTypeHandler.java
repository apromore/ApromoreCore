package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.DirectionType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.ResourcingExternalInteractionType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

public class MessageTypeHandler extends DecompositionHandler<MessageType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerTypeHandler.class.getName());

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
        return taskPredecessor instanceof MessageType && ((MessageType) taskPredecessor).getDirection() == DirectionType.INCOMING;
    }

    private boolean isTaskBeforeOutgoingMessage(final NodeType predecessor) {
        return predecessor instanceof TaskType && getObject().getDirection() == DirectionType.OUTGOING;
    }

    private boolean isTaskAfterIncomingMessage(final NodeType successor) {
        return successor instanceof TaskType && getObject().getDirection() == DirectionType.INCOMING;
    }

    private boolean isOutgoingMessage(final NodeType taskSuccessor) {
        return taskSuccessor instanceof MessageType && ((MessageType) taskSuccessor).getDirection() == DirectionType.OUTGOING;
    }

}
