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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.TimerTriggerType;

/**
 * Rewrite all other Timers that does not match a special category by introducing an artificial automated Task with an attached onEnablement Timer.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class MiscTimerMacro extends AbstractTimerMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiscTimerMacro.class);

    public MiscTimerMacro(final CanonicalConversionContext context) {
        super(context);
    }


    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AbstractTimerMacro#checkCondition(org.apromore.cpf.NodeType)
     */
    @Override
    protected boolean checkCondition(final NodeType node) {
        return node instanceof TimerType;
    }


    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AbstractTimerMacro#rewriteTimer(org.apromore.cpf.NodeType, org.apromore.cpf.NetType)
     */
    @Override
    protected boolean rewriteTimer(final NodeType node, final NetType net) throws CanoniserException {
        LOGGER.debug("Rewriting Timer (Misc)");

        final ObjectFactory oF = new ObjectFactory();
        final TaskType task = oF.createTaskType();
        task.setId(generateUUID());
        task.setName(node.getName());

        addNodeLater(task);
        deleteNodeLater(node);

        // Set the correct YAWL Timer
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = createTimer((TimerType) node);
        yawlTimer.setTrigger(TimerTriggerType.ON_ENABLED);
        getContext().getControlFlowContext().getElementInfo(task.getId()).setTimer(yawlTimer);
        // Remember that this Task should be automatic
        getContext().getControlFlowContext().getElementInfo(task.getId()).setAutomatic(true);

        LOGGER.debug("Added YAWL Timer to introduced Task {}", task.getId());

        // Connect the Task correctly
        addEdgeLater(createEdge(getContext().getFirstPredecessor(node.getId()), task));
        addEdgeLater(createEdge(task, getContext().getFirstSuccessor(node.getId())));

        // Do the changes and update our Maps
        cleanupNet(net);

        return true;
    }

}
