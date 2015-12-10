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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.TimerTriggerType;

/**
 * Rewrites the canonical way of representing an OnEnablement Timer of a automatic YAWL Task. In CPF it looks like this:
 *
 * Timer -> Task <br />
 *
 * In YAWL it is just a single Task with an attached Timer and attribute onEnablement set!
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class AutomaticTimerMacro extends AbstractTimerMacro {

    static final Logger LOGGER = LoggerFactory.getLogger(AutomaticTimerMacro.class);

    public AutomaticTimerMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AbstractTimerMacro#checkCondition(org.apromore.cpf.NodeType)
     */
    @Override
    protected boolean checkCondition(final NodeType node) {
        return node instanceof TimerType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AbstractTimerMacro#rewriteTimer(org.apromore.cpf.NodeType,
     * org.apromore.cpf.NetType)
     */
    @Override
    protected boolean rewriteTimer(final NodeType node, final NetType net) throws CanoniserException {
        final TaskType task = testFollowedByTask(node);
        if (task == null) {
            return false;
        }

        if (!isAutomaticTask(task)) {
            return false;
        }

        LOGGER.debug("Rewriting Timer (Automatic, onEnablement)");

        // We're a Timer before an automatic Task, this translates to a simple Task with Timer onEnablement in YAWL
        deleteNodeLater(node);

        // Set the correct YAWL Timer
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = createTimer((TimerType) node);
        yawlTimer.setTrigger(TimerTriggerType.ON_ENABLED);
        getContext().getControlFlowContext().getElementInfo(task.getId()).setTimer(yawlTimer);

        LOGGER.debug("Added YAWL Timer to Task {}", task.getId());

        // Connect the Task correctly
        addEdgeLater((createEdge(getContext().getFirstPredecessor(node.getId()), task)));

        // Do the changes and update our Maps
        cleanupNet(net);

        return true;
    }

    private boolean isAutomaticTask(final TaskType task) {
        if (task.getResourceTypeRef().isEmpty()) {
            // We can't decide so better assume NO
            return false;
        }

        // Assume we're automatic
        boolean isAutomatic = true;

        // Try to prove the converse
        for (final ResourceTypeRefType ref : task.getResourceTypeRef()) {
            final ResourceTypeType resource = getContext().getResourceTypeById(ref.getResourceTypeId());
            isAutomatic = isAutomatic && resource instanceof NonhumanType;
        }
        return isAutomatic;
    }

}
