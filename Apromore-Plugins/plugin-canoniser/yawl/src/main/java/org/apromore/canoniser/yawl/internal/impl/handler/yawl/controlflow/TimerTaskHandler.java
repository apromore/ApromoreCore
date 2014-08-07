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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerExpressionType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.yawlfoundation.yawlschema.TimerTriggerType;

/**
 * Converts a YAWL Timer to CPF.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class TimerTaskHandler extends BaseTaskHandler {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.BaseTaskHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (isAutomaticTask(getObject())) {
            // Add a Timer as delay before Automatic Task is executed

            // Convert CPF Timer Event -> CPF Task
            final TimerType timerNode = createTimer(getObject().getTimer());
            final NodeType taskNode = createTask(getObject());
            createSimpleEdge(timerNode, taskNode);

            // Link correctly to predecessor and successors separating the routing behavior from the task.
            linkToPredecessors(timerNode);
            linkToSucessors(taskNode);

        } else {
            // Is a Timeout either onEnablement or onStart of the Task
            // So the Task will automatically be completed as soon as the Timer expires.

            if (getObject().getTimer().getTrigger().equals(TimerTriggerType.ON_ENABLED)) {

                final ANDSplitType andSplit = createANDSplit();
                final TimerType timerNode = createTimer(getObject().getTimer());
                final TaskType taskNode = createTask(getObject());
                timerNode.getCancelNodeId().add(createCancellationRegion(taskNode));
                taskNode.getCancelNodeId().add(createCancellationRegion(timerNode));
                createSimpleEdge(andSplit, timerNode);
                createSimpleEdge(andSplit, taskNode);
                final XORJoinType xorJoin = createXORJoin();
                createSimpleEdge(timerNode, xorJoin);
                createSimpleEdge(taskNode, xorJoin);

                // Link correctly to predecessor and successors separating the routing behavior from the task.
                linkToPredecessors(andSplit);
                linkToSucessors(xorJoin);
            } else {

                EventType subProcessStartEvent = createEvent();
                final ANDSplitType andSplit = createANDSplit();
                createSimpleEdge(subProcessStartEvent, andSplit);
                final TimerType timerNode = createTimer(getObject().getTimer());
                final TaskType taskNode = createTask(getObject());
                timerNode.getCancelNodeId().add(createCancellationRegion(taskNode));
                taskNode.getCancelNodeId().add(createCancellationRegion(timerNode));
                createSimpleEdge(andSplit, timerNode);
                createSimpleEdge(andSplit, taskNode);
                final XORJoinType xorJoin = createXORJoin();
                createSimpleEdge(timerNode, xorJoin);
                createSimpleEdge(taskNode, xorJoin);
                EventType subProcessEndEvent = createEvent();
                createSimpleEdge(xorJoin, subProcessEndEvent);

                // Link correctly to predecessor and successors separating the routing behavior from the task.
                linkToPredecessors(subProcessStartEvent);
                linkToSucessors(subProcessEndEvent);
            }
        }

        super.convert();
    }

    private CancellationRefType createCancellationRegion(final NodeType node) {
        final CancellationRefType ref = CPF_FACTORY.createCancellationRefType();
        ref.setRefId(node.getId());
        return ref;
    }

    /**
     * Return a TimerType node that was not part of the original YAWL specification. The node is already added to its parent Net.
     *
     * @param timerType
     *
     * @param element
     * @return the converted TimerType
     * @throws CanoniserException
     */
    protected TimerType createTimer(final org.yawlfoundation.yawlschema.TimerType timerType) throws CanoniserException {
        final TimerType timer = CPF_FACTORY.createTimerType();
        timer.setId(generateUUID());
        timer.setOriginalID(null);

        if (timerType.getDuration() != null) {
            timer.setTimeDuration(timerType.getDuration());
        } else if (timerType.getExpiry() != null) {
            final GregorianCalendar calInstance = (GregorianCalendar) Calendar.getInstance();
            calInstance.setTimeInMillis(timerType.getExpiry());
            try {
                timer.setTimeDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calInstance));
            } catch (final DatatypeConfigurationException e) {
                throw new CanoniserException("Could not convert expiry date for YAWL timer", e);
            }
        } else if (timerType.getNetparam() != null) {
            // Create a Expression
            final TimerExpressionType timerExpression = CPF_FACTORY.createTimerExpressionType();
            timerExpression.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XPATH);
            timerExpression.setExpression(ExpressionUtils.createExpressionReferencingNetObject(timerType.getNetparam(), getConvertedParent()));
            timer.setTimeExpression(timerExpression);
        }

        getConvertedParent().getNode().add(timer);
        return timer;
    }

}
