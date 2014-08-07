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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.ContextAwareRewriteMacro;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ObjectFactory;

/**
 * Abstract base class for all kind of Timer related rewriting. Basically if does the iterating through the Nodes and provides some common utility
 * methods.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class AbstractTimerMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTimerMacro.class);

    private static final int DEFAULT_TIMER_DURATION = 3600;

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
                if (checkCondition(node) && rewriteTimer(node, net)) {
                    hasRewritten = true;
                    i = -1;
                }
            }
        }

        return hasRewritten;
    }

    /**
     * Check if start of Pattern is detected
     *
     * @param node
     *            current Node
     * @return true if start Node of Pattern detected
     */
    protected abstract boolean checkCondition(final NodeType node);

    /**
     * Rewrite the Timer pattern if it matches in complete. Please note as CONTRACT this method MUST call {@link #cleanNet(NetType)} if any Nodes or
     * Edges are modified.
     *
     * @param node
     *            current CPF Node
     * @param net
     *            current CPF Net
     * @return true if Net was changed, false otherwise
     * @throws CanoniserException
     */
    protected abstract boolean rewriteTimer(final NodeType node, final NetType net) throws CanoniserException;

    /**
     * Try to get the Timer from YAWL Extension, if not successful create default Timer
     *
     * @param timerNode
     *            of CPF
     * @return YAWL TimerType
     * @throws CanoniserException
     */
    protected org.yawlfoundation.yawlschema.TimerType createTimer(final TimerType timerNode) throws CanoniserException {
        TypeAttribute timerExt = ExtensionUtils.getExtensionAttribute(timerNode, ExtensionUtils.TIMER);
        if (timerExt != null) {
            return ExtensionUtils.unmarshalYAWLFragment(timerExt, org.yawlfoundation.yawlschema.TimerType.class);
        } else {
            // Default Timer
            final org.yawlfoundation.yawlschema.TimerType yawlTimer = new ObjectFactory().createTimerType();
            try {
                final DatatypeFactory factory = DatatypeFactory.newInstance();
                yawlTimer.setDuration(factory.newDuration(DEFAULT_TIMER_DURATION));
            } catch (final DatatypeConfigurationException e) {
                LOGGER.warn("Could not set Timer duration.", e);
            }
            return yawlTimer;
        }
    }

}