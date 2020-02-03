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

package de.hpi.bpmn2_0.model.connector;

import de.hpi.bpmn2_0.model.BaseElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tMessageFlowAssociation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tMessageFlowAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;attribute name="innerMessageFlowRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="outerMessageFlowRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMessageFlowAssociation")
public class MessageFlowAssociation
        extends BaseElement {

    @XmlAttribute(required = true)
    protected QName innerMessageFlowRef;
    @XmlAttribute(required = true)
    protected QName outerMessageFlowRef;

    /**
     * Gets the value of the innerMessageFlowRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getInnerMessageFlowRef() {
        return innerMessageFlowRef;
    }

    /**
     * Sets the value of the innerMessageFlowRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setInnerMessageFlowRef(QName value) {
        this.innerMessageFlowRef = value;
    }

    /**
     * Gets the value of the outerMessageFlowRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getOuterMessageFlowRef() {
        return outerMessageFlowRef;
    }

    /**
     * Sets the value of the outerMessageFlowRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setOuterMessageFlowRef(QName value) {
        this.outerMessageFlowRef = value;
    }

}
