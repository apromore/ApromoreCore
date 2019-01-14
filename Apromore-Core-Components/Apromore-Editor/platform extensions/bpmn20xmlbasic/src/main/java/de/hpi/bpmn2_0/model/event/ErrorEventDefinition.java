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

import de.hpi.bpmn2_0.model.misc.Error;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tErrorEventDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tErrorEventDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tEventDefinition">
 *       &lt;attribute name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="errorRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tErrorEventDefinition")
public class ErrorEventDefinition
        extends EventDefinition {

    @XmlAttribute
    @XmlIDREF
    protected Error errorRef;


    /* Getter & Setter */

    /**
     * Gets the value of the errorRef property.
     *
     * @return possible object is
     *         {@link Error }
     */
    public Error getErrorRef() {
        return errorRef;
    }

    /**
     * Sets the value of the errorRef property.
     *
     * @param value allowed object is
     *              {@link Error }
     */
    public void setErrorRef(Error value) {
        this.errorRef = value;
    }

}
