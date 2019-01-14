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

package de.hpi.bpmn2_0.model.activity.loop;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.event.ImplicitThrowEvent;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tComplexBehaviorDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tComplexBehaviorDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="condition" type="{http://www.omg.org/bpmn20}tFormalExpression"/>
 *         &lt;element name="event" type="{http://www.omg.org/bpmn20}tImplicitThrowEvent" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tComplexBehaviorDefinition", propOrder = {
        "condition",
        "event"
})
public class ComplexBehaviorDefinition
        extends BaseElement {

    @XmlElement(required = true)
    protected FormalExpression condition;
    protected ImplicitThrowEvent event;

    /**
     * Gets the value of the condition property.
     *
     * @return possible object is
     *         {@link FormalExpression }
     */
    public FormalExpression getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     *
     * @param value allowed object is
     *              {@link FormalExpression }
     */
    public void setCondition(FormalExpression value) {
        this.condition = value;
    }

    /**
     * Gets the value of the event property.
     *
     * @return possible object is
     *         {@link ImplicitThrowEvent }
     */
    public ImplicitThrowEvent getEvent() {
        return event;
    }

    /**
     * Sets the value of the event property.
     *
     * @param value allowed object is
     *              {@link ImplicitThrowEvent }
     */
    public void setEvent(ImplicitThrowEvent value) {
        this.event = value;
    }

}
