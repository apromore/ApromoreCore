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

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tEventBasedGateway complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tEventBasedGateway">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tGateway">
 *       &lt;attribute name="instantiate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="eventGatewayType" type="{http://www.omg.org/bpmn20}tEventBasedGatewayType" default="Exclusive" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEventBasedGateway")
@StencilId("EventbasedGateway")
public class EventBasedGateway
        extends Gateway {

    @XmlAttribute
    protected Boolean instantiate;
    @XmlAttribute
    protected EventBasedGatewayType eventGatewayType;

    public void acceptVisitor(Visitor v) {
        v.visitEventBasedGateway(this);
    }


    /**
     * Gets the value of the instantiate property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isInstantiate() {
        if (instantiate == null) {
            return false;
        } else {
            return instantiate;
        }
    }

    /**
     * Sets the value of the instantiate property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setInstantiate(Boolean value) {
        this.instantiate = value;
    }

    /**
     * Gets the value of the eventGatewayType property.
     *
     * @return possible object is
     *         {@link EventBasedGatewayType }
     */
    public EventBasedGatewayType getEventGatewayType() {
        if (eventGatewayType == null) {
            return EventBasedGatewayType.EXCLUSIVE;
        } else {
            return eventGatewayType;
        }
    }

    /**
     * Sets the value of the eventGatewayType property.
     *
     * @param value allowed object is
     *              {@link EventBasedGatewayType }
     */
    public void setEventGatewayType(EventBasedGatewayType value) {
        this.eventGatewayType = value;
    }

}
