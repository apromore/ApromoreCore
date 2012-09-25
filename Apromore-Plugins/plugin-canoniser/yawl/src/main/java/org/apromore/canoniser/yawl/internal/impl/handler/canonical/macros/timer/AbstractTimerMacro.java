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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.ContextAwareRewriteMacro;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TimerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for all kind of Timer related rewriting. Basically if does the iterating through the Nodes and provides some common utility
 * methods.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public abstract class AbstractTimerMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTimerMacro.class);

    public AbstractTimerMacro(final CanonicalConversionContext context) {
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
            for (int i = 0; i < net.getNode().size(); i++) {
                final NodeType node = net.getNode().get(i);
                if (checkCondition(node)) {
                    if (rewriteTimer(node, net)) {
                        hasRewritten = true;
                        i = -1;
                    }
                }
            }
        }

        return hasRewritten;
    }

    /**
     * @param node
     * @return
     */
    protected abstract boolean checkCondition(final NodeType node);

    /**
     * @param node
     * @param net
     * @return
     */
    protected abstract boolean rewriteTimer(final NodeType node, final NetType net);

    /**
     * @param timer
     * @return
     */
    protected org.yawlfoundation.yawlschema.TimerType createTimer(final TimerType timer) {
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = ConversionUtils.YAWL_FACTORY.createTimerType();
        try {
            final DatatypeFactory factory = DatatypeFactory.newInstance();
            yawlTimer.setDuration(factory.newDuration(6000));
        } catch (final DatatypeConfigurationException e) {
            LOGGER.warn("Could not set Timer duration.", e);
        }
        return yawlTimer;
    }

}