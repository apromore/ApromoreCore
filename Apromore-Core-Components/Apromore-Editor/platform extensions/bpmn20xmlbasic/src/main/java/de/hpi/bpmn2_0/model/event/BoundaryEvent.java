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

import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

/**
 * <p/>
 * Java class for tBoundaryEvent complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tBoundaryEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tCatchEvent">
 *       &lt;attribute name="cancelActivity" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="attachedToRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "boundaryEvent")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tBoundaryEvent")
public class BoundaryEvent extends IntermediateCatchEvent {

    @XmlAttribute
    protected Boolean cancelActivity;
    @XmlAttribute(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Activity attachedToRef;

    public void afterUnmarshal(Unmarshaller u, Object parent) {
        if (this.getAttachedToRef() != null) {
            this.getAttachedToRef().getBoundaryEventRefs().add(this);
        }
    }

    public void acceptVisitor(Visitor v) {
        v.visitBoundaryEvent(this);
    }


    /* Getter & Setter */

    /**
     * Gets the value of the cancelActivity property.
     *
     * @return possible object is {@link Boolean }
     */
    public boolean isCancelActivity() {
        if (cancelActivity == null) {
            return true;
        } else {
            return cancelActivity;
        }
    }

    /**
     * Sets the value of the cancelActivity property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setCancelActivity(Boolean value) {
        this.cancelActivity = value;
    }

    /**
     * Gets the value of the attachedToRef property.
     *
     * @return possible object is {@link Activity }
     */
    public Activity getAttachedToRef() {
        return attachedToRef;
    }

    /**
     * Sets the value of the attachedToRef property.
     *
     * @param value allowed object is {@link Activity }
     */
    public void setAttachedToRef(Activity value) {
        this.attachedToRef = value;
    }

}
