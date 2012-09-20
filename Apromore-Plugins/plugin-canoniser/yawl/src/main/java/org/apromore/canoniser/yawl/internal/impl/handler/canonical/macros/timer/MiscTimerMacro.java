/**
 * Copyright 2012, Felix Mannhardt
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer;

import java.util.ListIterator;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.CanonicalProcessType;
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
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public class MiscTimerMacro extends AbstractTimerMacro {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscTimerMacro.class);

    public MiscTimerMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro#rewrite(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {

            final ListIterator<NodeType> nodeIterator = net.getNode().listIterator();
            while (nodeIterator.hasNext()) {
                final NodeType node = nodeIterator.next();
                if (node instanceof TimerType) {
                    hasRewritten = hasRewritten || rewriteMisc((TimerType) node, net, cpf, nodeIterator);
                }
            }

            if (hasRewritten) {
                cleanupNet(net);
            }
        }

        if (hasRewritten) {
            getContext().invalidateCPFCaches();
        }

        return hasRewritten;
    }

    private boolean rewriteMisc(TimerType timer, NetType net, CanonicalProcessType cpf, ListIterator<NodeType> nodeIterator) {
        LOGGER.debug("Rewriting Timer (Misc)");
        
        ObjectFactory oF = new ObjectFactory();
        TaskType task = oF.createTaskType();
        task.setId(generateUUID());
        task.setName(timer.getName());

        addNodeLater(task);
        deleteNodeLater(timer);
        
        // Set the correct YAWL Timer
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = createTimer(timer);
        yawlTimer.setTrigger(TimerTriggerType.ON_ENABLED);
        getContext().getElementInfo(task.getId()).timer = yawlTimer;
        // Remember that this Task should be automatic
        getContext().getElementInfo(task.getId()).isAutomatic = true;

        LOGGER.debug("Added YAWL Timer to introduced Task {}", task.getId());

        // Connect the Task correctly
        addEdgeLater(createEdge(getContext().getFirstPredecessor(timer.getId()), task));
        addEdgeLater(createEdge(task, getContext().getFirstSuccessor(timer.getId())));
                
        return true;
    }

}
