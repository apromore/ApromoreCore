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

package de.hpi.bpmn2_0.model.activity.misc;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.misc.Error;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tOperation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tOperation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="inMessageRef" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="outMessageRef" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
 *         &lt;element name="errorRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tOperation", propOrder = {
        "inMessageRef",
        "outMessageRef",
        "errorRef"
})
public class Operation
        extends BaseElement {

    @XmlElement(required = true)
    protected Message inMessageRef;
    @XmlElement
    protected Message outMessageRef;
    @XmlElement
    protected List<Error> errorRef;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;

    /**
     * Gets the value of the inMessageRef property.
     *
     * @return possible object is
     *         {@link Message }
     */
    public Message getInMessageRef() {
        return inMessageRef;
    }

    /**
     * Sets the value of the inMessageRef property.
     *
     * @param value allowed object is
     *              {@link Message }
     */
    public void setInMessageRef(Message value) {
        this.inMessageRef = value;
    }

    /**
     * Gets the value of the outMessageRef property.
     *
     * @return possible object is
     *         {@link Message }
     */
    public Message getOutMessageRef() {
        return outMessageRef;
    }

    /**
     * Sets the value of the outMessageRef property.
     *
     * @param value allowed object is
     *              {@link Message }
     */
    public void setOutMessageRef(Message value) {
        this.outMessageRef = value;
    }

    /**
     * Gets the value of the errorRef property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errorRef property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Error }
     */
    public List<Error> getErrorRef() {
        if (errorRef == null) {
            errorRef = new ArrayList<Error>();
        }
        return this.errorRef;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

}
