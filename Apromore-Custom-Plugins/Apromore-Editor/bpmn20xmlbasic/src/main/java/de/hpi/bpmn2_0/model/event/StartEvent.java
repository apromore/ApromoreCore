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

import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tStartEvent complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tStartEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tCatchEvent">
 *       &lt;attribute name="isInterrupting" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "startEvent")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tStartEvent")
public class StartEvent
        extends CatchEvent {

    @XmlAttribute
    protected Boolean isInterrupting;

    public void acceptVisitor(Visitor v) {
        v.visitStartEvent(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the isInterrupting property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isIsInterrupting() {
        if (isInterrupting == null) {
            return false;
        } else {
            return isInterrupting;
        }
    }

    /**
     * Sets the value of the isInterrupting property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsInterrupting(Boolean value) {
        this.isInterrupting = value;
    }

}
