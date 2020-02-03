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


package de.hpi.bpmn2_0.model.choreography;

import de.hpi.bpmn2_0.model.BaseElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tParticipantAssociation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tParticipantAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="innerParticipantRef" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="outerParticipantRef" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParticipantAssociation", propOrder = {
        "innerParticipantRef",
        "outerParticipantRef"
})
public class ParticipantAssociation
        extends BaseElement {

    @XmlElement(required = true)
    protected QName innerParticipantRef;
    @XmlElement(required = true)
    protected QName outerParticipantRef;

    /**
     * Gets the value of the innerParticipantRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getInnerParticipantRef() {
        return innerParticipantRef;
    }

    /**
     * Sets the value of the innerParticipantRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setInnerParticipantRef(QName value) {
        this.innerParticipantRef = value;
    }

    /**
     * Gets the value of the outerParticipantRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getOuterParticipantRef() {
        return outerParticipantRef;
    }

    /**
     * Sets the value of the outerParticipantRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setOuterParticipantRef(QName value) {
        this.outerParticipantRef = value;
    }

}
