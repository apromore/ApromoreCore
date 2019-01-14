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

import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tEscalation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tEscalation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *       &lt;attribute name="structureRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEscalation")
public class Escalation
        extends RootElement {

    @XmlAttribute
    protected QName structureRef;
    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
    @XmlAttribute
    protected String escalationCode;

    /* Getter & Setter */

    /**
     * Gets the value of the structureRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getStructureRef() {
        return structureRef;
    }

    /**
     * Sets the value of the structureRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setStructureRef(QName value) {
        this.structureRef = value;
    }

    public String getName() {
        return name;
    }

    public String getEscalationCode() {
        return escalationCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEscalationCode(String escalationCode) {
        this.escalationCode = escalationCode;
    }

}
