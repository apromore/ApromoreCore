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

package de.hpi.bpmn2_0.model.gateway;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tGateway complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tGateway">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *       &lt;attribute name="gatewayDirection" type="{http://www.omg.org/bpmn20}tGatewayDirection" default="unspecified" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tGateway")
@XmlSeeAlso({
        ComplexGateway.class,
        InclusiveGateway.class,
        EventBasedGateway.class,
        ParallelGateway.class,
        ExclusiveGateway.class
})
public class Gateway
        extends FlowNode {

    @XmlAttribute
    protected GatewayDirection gatewayDirection;

    /**
     * Helper for the import, see {@link FlowElement#isElementWithFixedSize().
     */
    // @Override
    public boolean isElementWithFixedSize() {
        return true;
    }

    /**
     * For the fixed-size shape, return the fixed width.
     */
    public double getStandardWidth() {
        return 40.0;
    }

    /**
     * For the fixed-size shape, return the fixed height.
     */
    public double getStandardHeight() {
        return 40.0;
    }

    public void acceptVisitor(Visitor v) {
        v.visitGateway(this);
    }


    /* Getter & Setter */

    /**
     * Gets the value of the gatewayDirection property.
     *
     * @return possible object is
     *         {@link GatewayDirection }
     */
    public GatewayDirection getGatewayDirection() {
        if (gatewayDirection == null) {
            return GatewayDirection.UNSPECIFIED;
        } else {
            return gatewayDirection;
        }
    }

    /**
     * Sets the value of the gatewayDirection property.
     *
     * @param value allowed object is
     *              {@link GatewayDirection }
     */
    public void setGatewayDirection(GatewayDirection value) {
        this.gatewayDirection = value;
    }

}
