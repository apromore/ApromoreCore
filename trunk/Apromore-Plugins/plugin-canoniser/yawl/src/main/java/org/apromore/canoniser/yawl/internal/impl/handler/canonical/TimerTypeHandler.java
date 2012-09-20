package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.ResourcingExternalInteractionType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

public class TimerTypeHandler extends DecompositionHandler<TimerType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerTypeHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (getContext().getPostSet(getObject().getId()).size() <= 1) {

            final NodeType successor = getContext().getFirstSuccessor(getObject().getId());

            if (successor instanceof TaskType) {
                // Ignore as Task will take care of this
                LOGGER.debug("Ignore this Timer {} as succeeding Task {} will recognise it.", getObject().getName(), successor.getName());
            } else {
                // Introduce new YAWL Timer Task
                final ExternalTaskFactsType task = createTask(getObject());
                task.setSplit(getDefaultSplitType());
                task.setJoin(getDefaultJoinType());
                final WebServiceGatewayFactsType d = createDecomposition(getObject());
                // Will be an automatic Task in YAWL that is doing nothing
                d.setExternalInteraction(ResourcingExternalInteractionType.AUTOMATED);
                task.setDecomposesTo(d);
                task.setTimer(createTimer(getObject()));
                LOGGER.debug("Added new (introduced) Task for Timer {}", getObject().getName());
                getConvertedParent().getProcessControlElements().getTaskOrCondition().add(task);
            }

        } else {
            throw new CanoniserException("Timer node should have not more than one predecessor!");
        }
    }

}
