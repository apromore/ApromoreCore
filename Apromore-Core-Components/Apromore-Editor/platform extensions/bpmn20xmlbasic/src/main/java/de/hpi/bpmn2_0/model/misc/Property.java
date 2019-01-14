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

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.data_object.DataState;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tProperty complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tProperty">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataState" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="itemSubjectRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tProperty", propOrder = {
        "dataState",
        "structure"
})
public class Property
        extends BaseElement {

    @XmlElement
    protected DataState dataState;

    @XmlAttribute
    protected String name;

    @XmlAttribute
    protected ItemKind itemKind;

    @XmlAttribute
    protected boolean isCollection;

    @XmlElement
    protected String structure;


    /* Getter & Setter */

    /**
     * Gets the value of the dataState property.
     *
     * @return possible object is
     *         {@link DataState }
     */
    public DataState getDataState() {
        return dataState;
    }

    /**
     * Sets the value of the dataState property.
     *
     * @param value allowed object is
     *              {@link DataState }
     */
    public void setDataState(DataState value) {
        this.dataState = value;
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

    /**
     * Gets the value of the itemSubjectRef property.
     *
     * @return possible object is
     *         {@link ItemKind }
     */
    public ItemKind getItemKind() {
        return itemKind;
    }

    /**
     * @return the isCollection
     */
    public boolean isCollection() {
        return isCollection;
    }

    /**
     * @param isCollection the isCollection to set
     */
    public void setCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    /**
     * @param itemKind the itemKind to set
     */
    public void setItemKind(ItemKind itemKind) {
        this.itemKind = itemKind;
    }

    /**
     * @return the structure
     */
    public String getStructure() {
        return structure;
    }

    /**
     * @param structure the structure to set
     */
    public void setStructure(String structure) {
        this.structure = structure;
    }

}
