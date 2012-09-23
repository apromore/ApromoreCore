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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandlerImpl;
import org.apromore.cpf.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.TimerTriggerType;
import org.yawlfoundation.yawlschema.TimerType;

/**
 * Abstract base class for all handlers that convert from CPF to YAWL
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T>
 *            type of Element to be converted
 * @param <E>
 *            type of the already converted Parent
 */
public abstract class CanonicalElementHandler<T, E> extends ConversionHandlerImpl<T, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalElementHandler.class.getName());

    /**
     * @return the canonical conversion context
     */
    protected CanonicalConversionContext getContext() {
        return (CanonicalConversionContext) context;
    }

    protected boolean isOutputCondition(final NodeType object) {
        return getContext().getPostSet(object.getId()).size() == 0;
    }

    protected boolean isInputCondition(final NodeType object) {
        return getContext().getPreSet(object.getId()).size() == 0;
    }

    protected ExternalConditionFactsType createCondition(final NodeType node) {
        final ExternalConditionFactsType inputCondition = getContext().getYawlObjectFactory().createExternalConditionFactsType();
        inputCondition.setId(generateUUID(node.getId()));
        inputCondition.setName(node.getName());
        getContext().setElement(node.getId(), inputCondition);
        return inputCondition;
    }

    protected OutputConditionFactsType createOutputCondition(final NodeType node) {
        final OutputConditionFactsType outputCondition = getContext().getYawlObjectFactory().createOutputConditionFactsType();
        outputCondition.setId(generateUUID(node.getId()));
        outputCondition.setName(node.getName());
        getContext().setElement(node.getId(), outputCondition);
        return outputCondition;
    }

    protected ExternalTaskFactsType createTask(final NodeType node) {
        final ExternalTaskFactsType taskFacts = getContext().getYawlObjectFactory().createExternalTaskFactsType();
        taskFacts.setName(node.getName());
        taskFacts.setId(generateUUID(node.getId()));
        getContext().setElement(node.getId(), taskFacts);
        return taskFacts;
    }

    protected ControlTypeType getDefaultJoinType() {
        final ControlTypeType controlType = getContext().getYawlObjectFactory().createControlTypeType();
        controlType.setCode(ControlTypeCodeType.XOR);
        return controlType;
    }

    protected ControlTypeType getDefaultSplitType() {
        final ControlTypeType controlType = getContext().getYawlObjectFactory().createControlTypeType();
        controlType.setCode(ControlTypeCodeType.AND);
        return controlType;
    }

    protected TimerType createTimer(final NodeType node) {
        TimerType yawlTimer = getTimerExtension(node);
        if (yawlTimer == null) {
            // Set Default Values
            yawlTimer = getContext().getYawlObjectFactory().createTimerType();
            yawlTimer.setTrigger(TimerTriggerType.ON_ENABLED);
            try {
                final DatatypeFactory factory = DatatypeFactory.newInstance();
                yawlTimer.setDuration(factory.newDuration(6000));
            } catch (final DatatypeConfigurationException e) {
                LOGGER.warn("Could not set Timer duration.", e);
            }

        }
        return yawlTimer;
    }

    /**
     * Tries to find a YAWL Timer in the Extension
     *
     * @param object
     * @return
     */
    private TimerType getTimerExtension(final NodeType node) {
        return getContext().getYAWLExtensionFromAnnotations(node.getId(), "timer", TimerType.class);
    }

}
