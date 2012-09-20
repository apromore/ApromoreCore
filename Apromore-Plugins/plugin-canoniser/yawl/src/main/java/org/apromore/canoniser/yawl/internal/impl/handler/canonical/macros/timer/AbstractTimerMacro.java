package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer;

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
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.ContextAwareRewriteMacro;
import org.apromore.cpf.TimerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTimerMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTimerMacro.class.getName());

    public AbstractTimerMacro(final CanonicalConversionContext context) {
        super(context);
    }

    protected org.yawlfoundation.yawlschema.TimerType createTimer(final TimerType timer) {
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = getContext().getYawlObjectFactory().createTimerType();
        try {
            final DatatypeFactory factory = DatatypeFactory.newInstance();
            yawlTimer.setDuration(factory.newDuration(6000));
        } catch (final DatatypeConfigurationException e) {
            LOGGER.warn("Could not set Timer duration.", e);
        }
        return yawlTimer;
    }

}