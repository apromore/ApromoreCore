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

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tConditionalEventDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tConditionalEventDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tEventDefinition">
 *       &lt;sequence>
 *         &lt;element name="condition" type="{http://www.omg.org/bpmn20}tExpression"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tConditionalEventDefinition", propOrder = {
        "condition"
})
public class ConditionalEventDefinition
        extends EventDefinition {

    @XmlElement(required = true)
    protected Expression condition;

    /**
     * Gets the value of the condition property.
     *
     * @return possible object is
     *         {@link TExpression }
     */
    public Expression getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     *
     * @param value allowed object is
     *              {@link TExpression }
     */
    public void setCondition(Expression value) {
        this.condition = value;
    }

}
