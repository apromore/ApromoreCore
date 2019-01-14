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

package de.hpi.bpmn2_0.model.misc;

import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for tError complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tError">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *       &lt;attribute name="structureRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tError")
public class Error
        extends RootElement {

    @XmlElement
    protected ItemDefinition structureRef;
    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String errorCode;

    /* Constructor */
    public Error() {
        super();
        setId(SignavioUUID.generate());
    }

    /* Getter & Setter */

    /**
     * Gets the value of the structureRef property.
     *
     * @return possible object is
     *         {@link ItemDefinition }
     */
    public ItemDefinition getStructureRef() {
        return structureRef;
    }

    /**
     * Sets the value of the structureRef property.
     *
     * @param value allowed object is
     *              {@link ItemDefinition }
     */
    public void setStructureRef(ItemDefinition value) {
        this.structureRef = value;
    }

    public String getName() {
        return name;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
