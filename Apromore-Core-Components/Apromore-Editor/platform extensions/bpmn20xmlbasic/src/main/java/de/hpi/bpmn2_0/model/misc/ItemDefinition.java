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

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tItemDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tItemDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *       &lt;attribute name="structureRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="isCollection" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="itemKind" type="{http://www.omg.org/bpmn20}tItemKind" default="Information" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tItemDefinition")
public class ItemDefinition
        extends RootElement {

    @XmlElement
    protected String structure;
    @XmlAttribute
    protected Boolean isCollection;
    @XmlAttribute
    protected ItemKind itemKind;

    /**
     * Gets the value of the structureRef property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Sets the value of the structureRef property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStructure(String value) {
        this.structure = value;
    }

    /**
     * Gets the value of the isCollection property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isIsCollection() {
        if (isCollection == null) {
            return false;
        } else {
            return isCollection;
        }
    }

    /**
     * Sets the value of the isCollection property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsCollection(Boolean value) {
        this.isCollection = value;
    }

    /**
     * Gets the value of the itemKind property.
     *
     * @return possible object is
     *         {@link ItemKind }
     */
    public ItemKind getItemKind() {
        if (itemKind == null) {
            return ItemKind.INFORMATION;
        } else {
            return itemKind;
        }
    }

    /**
     * Sets the value of the itemKind property.
     *
     * @param value allowed object is
     *              {@link TItemKind }
     */
    public void setItemKind(ItemKind value) {
        this.itemKind = value;
    }

}
