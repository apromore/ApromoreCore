/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.15 at 11:16:00 AM EST 
//

package epml;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for typeFont complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="typeFont">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="family" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="weight" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="decoration">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="underline"/>
 *             &lt;enumeration value="overline"/>
 *             &lt;enumeration value="line-through"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="color" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="verticalAlign">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="top"/>
 *             &lt;enumeration value="middle"/>
 *             &lt;enumeration value="bottom"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="horizontalAlign">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="left"/>
 *             &lt;enumeration value="middle"/>
 *             &lt;enumeration value="right"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="rotation" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeFont")
public class TypeFont {

	@XmlAttribute
	protected String family;
	@XmlAttribute
	protected String style;
	@XmlAttribute
	protected String weight;
	@XmlAttribute
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger size;
	@XmlAttribute
	protected String decoration;
	@XmlAttribute
	protected String color;
	@XmlAttribute
	protected String verticalAlign;
	@XmlAttribute
	protected String horizontalAlign;
	@XmlAttribute
	protected BigDecimal rotation;

	/**
	 * Gets the value of the family property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * Sets the value of the family property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFamily(String value) {
		this.family = value;
	}

	/**
	 * Gets the value of the style property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the value of the style property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStyle(String value) {
		this.style = value;
	}

	/**
	 * Gets the value of the weight property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * Sets the value of the weight property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWeight(String value) {
		this.weight = value;
	}

	/**
	 * Gets the value of the size property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getSize() {
		return size;
	}

	/**
	 * Sets the value of the size property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setSize(BigInteger value) {
		this.size = value;
	}

	/**
	 * Gets the value of the decoration property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDecoration() {
		return decoration;
	}

	/**
	 * Sets the value of the decoration property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDecoration(String value) {
		this.decoration = value;
	}

	/**
	 * Gets the value of the color property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the value of the color property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setColor(String value) {
		this.color = value;
	}

	/**
	 * Gets the value of the verticalAlign property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVerticalAlign() {
		return verticalAlign;
	}

	/**
	 * Sets the value of the verticalAlign property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVerticalAlign(String value) {
		this.verticalAlign = value;
	}

	/**
	 * Gets the value of the horizontalAlign property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHorizontalAlign() {
		return horizontalAlign;
	}

	/**
	 * Sets the value of the horizontalAlign property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHorizontalAlign(String value) {
		this.horizontalAlign = value;
	}

	/**
	 * Gets the value of the rotation property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getRotation() {
		return rotation;
	}

	/**
	 * Sets the value of the rotation property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setRotation(BigDecimal value) {
		this.rotation = value;
	}

}
