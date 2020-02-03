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

import de.hpi.bpmn2_0.model.Expression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tTimerEventDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tTimerEventDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tEventDefinition">
 *       &lt;choice>
 *         &lt;element name="timeDate" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *         &lt;element name="timeCycle" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTimerEventDefinition", propOrder = {
        "timeDate",
        "timeCycle",
        "timeDuration"
})
public class TimerEventDefinition
        extends EventDefinition {

    /* Attributes */

    protected Expression timeDate;
    protected Expression timeCycle;
    protected Expression timeDuration;

    /* Constructors */

    /**
     * Default constructor
     */
    public TimerEventDefinition() {
    }

    /**
     * Copy constructor based on {@link TimerEventDefinition}
     *
     * @param timerEventDefinition
     */
    public TimerEventDefinition(TimerEventDefinition timerEventDefinition) {
        super(timerEventDefinition);

        this.setTimeDate(timerEventDefinition.getTimeDate());
        this.setTimeCycle(timerEventDefinition.getTimeCycle());
    }

    /* Getter & Setter */

    /**
     * Gets the value of the timeDate property.
     *
     * @return possible object is
     *         {@link Expression }
     */
    public Expression getTimeDate() {
        return timeDate;
    }

    /**
     * Sets the value of the timeDate property.
     *
     * @param value allowed object is
     *              {@link Expression }
     */
    public void setTimeDate(Expression value) {
        this.timeDate = value;
    }

    /**
     * Gets the value of the timeCycle property.
     *
     * @return possible object is
     *         {@link Expression }
     */
    public Expression getTimeCycle() {
        return timeCycle;
    }

    /**
     * Sets the value of the timeCycle property.
     *
     * @param value allowed object is
     *              {@link Expression }
     */
    public void setTimeCycle(Expression value) {
        this.timeCycle = value;
    }

    public Expression getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(Expression timeDuration) {
        this.timeDuration = timeDuration;
    }

}
