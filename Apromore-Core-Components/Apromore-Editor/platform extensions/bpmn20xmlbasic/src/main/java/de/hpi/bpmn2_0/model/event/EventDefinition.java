/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2_0.model.event;

import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tEventDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tEventDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEventDefinition")
@XmlSeeAlso({
        TimerEventDefinition.class,
        CancelEventDefinition.class,
        MessageEventDefinition.class,
        ErrorEventDefinition.class,
        ConditionalEventDefinition.class,
        TerminateEventDefinition.class,
        LinkEventDefinition.class,
        EscalationEventDefinition.class,
        CompensateEventDefinition.class,
        SignalEventDefinition.class
})
public abstract class EventDefinition
        extends RootElement {

    /* Constructors */

    /**
     * Default constructor
     */
    public EventDefinition() {
    }

    /**
     * Copy constructor
     *
     * @param timerEventDefinition
     */
    public EventDefinition(EventDefinition eventDefinition) {
        super(eventDefinition);
    }

    public static EventDefinition createEventDefinition(String eventIdentifier) {
        if (eventIdentifier == null)
            return null;

        EventDefinition evDef = null;
        if (eventIdentifier.equalsIgnoreCase("Message"))
            evDef = new MessageEventDefinition();
        else if (eventIdentifier.equalsIgnoreCase("Escalation"))
            evDef = new EscalationEventDefinition();
        else if (eventIdentifier.equalsIgnoreCase("Error"))
            evDef = new ErrorEventDefinition();
        else if (eventIdentifier.equalsIgnoreCase("Cancel"))
            evDef = new CancelEventDefinition();
        else if (eventIdentifier.equalsIgnoreCase("Compensation"))
            evDef = new CompensateEventDefinition();
        else if (eventIdentifier.equalsIgnoreCase("Signal"))
            evDef = new SignalEventDefinition();
        else if (eventIdentifier.equalsIgnoreCase("Terminate"))
            evDef = new TerminateEventDefinition();

        if (evDef != null)
            evDef.setId(SignavioUUID.generate());

        return evDef;
    }
}
