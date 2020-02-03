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

package de.hpi.bpmn2_0.model.activity.resource;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.model.FormalExpression;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tResourceAssignmentExpression complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tResourceAssignmentExpression">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}expression"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tResourceAssignmentExpression", propOrder = {
        "expression"
})
public class ResourceAssignmentExpression
        extends BaseElement {

    @XmlElementRefs({
            @XmlElementRef(type = FormalExpression.class),
            @XmlElementRef(type = Expression.class)
    })
    protected Expression expression;

    /**
     * Gets the value of the expression property.
     *
     * @return possible object is
     *         {@link FormalExpression }
     *         {@link Expression }
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     *
     * @param value allowed object is
     *              {@link FormalExpression }
     *              {@link Expression }
     */
    public void setExpression(Expression value) {
        this.expression = value;
    }

}
