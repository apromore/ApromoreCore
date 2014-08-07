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

import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.TimerTriggerType;

/**
 * Rewrites the canonical way of representing an OnEnablement Timer to a single YAWL Timer. In CPF it looks like this (& = parallel Nodes):
 *
 * ANDSplit -> (Timer & Task) -> XORJoin <br />
 *
 * In YAWL it is just a single Timer with attribute onEnablement set!
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class TimerOnStartMacro extends AbstractTimerMacro {

    static final Logger LOGGER = LoggerFactory.getLogger(TimerOnStartMacro.class);

    public TimerOnStartMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AbstractTimerMacro#checkCondition(org.apromore.cpf.NodeType)
     */
    @Override
    protected boolean checkCondition(final NodeType node) {
        return node instanceof ANDSplitType;
    }


    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AbstractTimerMacro#rewriteTimer(org.apromore.cpf.NodeType, org.apromore.cpf.NetType)
     */
    @Override
    protected boolean rewriteTimer(final NodeType node, final NetType net) throws CanoniserException {
        final ANDSplitType splitNode = (ANDSplitType) node;
        final TimerType timer = testFollowedByTimer(splitNode);
        final TaskType task = testFollowedByTask(splitNode);
        final XORJoinType xorJoin1 = testFollowedByXORJoin(timer);
        final XORJoinType xorJoin2 = testFollowedByXORJoin(task);

        if (timer == null || task == null || xorJoin1 == null || xorJoin2 == null) {
            return false;
        }

        if (!xorJoin1.getId().equals(xorJoin2.getId())) {
            return false;
        }

        if (!testMutuallyCancelling(timer, task)) {
            return false;
        }

        LOGGER.debug("Rewriting Timer (onStart)");

        // Remove all not needed Nodes
        deleteNodeLater(splitNode);
        deleteNodeLater(timer);
        deleteNodeLater(xorJoin1);

        // Set the correct YAWL Timer
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = createTimer(timer);
        yawlTimer.setTrigger(TimerTriggerType.ON_EXECUTING);
        getContext().getControlFlowContext().getElementInfo(task.getId()).setTimer(yawlTimer);

        LOGGER.debug("Added YAWL Timer to Task {}", task.getId());

        // Connect Task properly
        addEdgeLater(createEdge(getContext().getFirstPredecessor(splitNode.getId()), task));
        addEdgeLater(createEdge(task, getContext().getFirstSuccessor(xorJoin1.getId())));

        // Do the changes and update our Maps
        cleanupNet(net);

        return true;
    }

    private boolean testMutuallyCancelling(final TimerType timer, final TaskType task) {
        if (!(timer.getCancelEdgeId().isEmpty() && task.getCancelEdgeId().isEmpty())) {
            return false;
        }

        final List<CancellationRefType> cSetT = timer.getCancelNodeId();
        final List<CancellationRefType> cSetM = task.getCancelNodeId();

        if (cSetT.size() != 1 || cSetM.size() != 1) {
            return false;
        }

        if (!cSetM.get(0).getRefId().equals(timer.getId()) || !cSetT.get(0).getRefId().equals(task.getId())) {
            return false;
        }

        return true;
    }

}
