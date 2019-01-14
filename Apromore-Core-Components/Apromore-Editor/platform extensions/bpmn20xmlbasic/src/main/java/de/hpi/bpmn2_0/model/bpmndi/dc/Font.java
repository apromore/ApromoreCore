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

package de.hpi.bpmn2_0.model.bpmndi.dc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Font complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Font">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="isBold" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isItalic" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isUnderline" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isStrikeThrough" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Font")
public class Font {

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "size")
    protected Double size;
    @XmlAttribute(name = "isBold")
    protected Boolean isBold;
    @XmlAttribute(name = "isItalic")
    protected Boolean isItalic;
    @XmlAttribute(name = "isUnderline")
    protected Boolean isUnderline;
    @XmlAttribute(name = "isStrikeThrough")
    protected Boolean isStrikeThrough;

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
     * Gets the value of the size property.
     *
     * @return possible object is
     *         {@link Double }
     */
    public Double getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     *
     * @param value allowed object is
     *              {@link Double }
     */
    public void setSize(Double value) {
        this.size = value;
    }

    /**
     * Gets the value of the isBold property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isIsBold() {
        return isBold;
    }

    /**
     * Sets the value of the isBold property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsBold(Boolean value) {
        this.isBold = value;
    }

    /**
     * Gets the value of the isItalic property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isIsItalic() {
        return isItalic;
    }

    /**
     * Sets the value of the isItalic property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsItalic(Boolean value) {
        this.isItalic = value;
    }

    /**
     * Gets the value of the isUnderline property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isIsUnderline() {
        return isUnderline;
    }

    /**
     * Sets the value of the isUnderline property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsUnderline(Boolean value) {
        this.isUnderline = value;
    }

    /**
     * Gets the value of the isStrikeThrough property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isIsStrikeThrough() {
        return isStrikeThrough;
    }

    /**
     * Sets the value of the isStrikeThrough property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsStrikeThrough(Boolean value) {
        this.isStrikeThrough = value;
    }

}
