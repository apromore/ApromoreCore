/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.03 at 05:04:23 PM CET 
//

package org.yawlfoundation.yawlschema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for LayoutAttributesFactsType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="LayoutAttributesFactsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="autosize" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="backgroundColor" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="bendable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="bounds" type="{http://www.yawlfoundation.org/yawlschema}LayoutRectangleType"/>
 *         &lt;element name="connectable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="disconnectable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="editable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="endFill" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="font" type="{http://www.yawlfoundation.org/yawlschema}LayoutFontType"/>
 *         &lt;element name="foregroundColor" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="labelposition" type="{http://www.yawlfoundation.org/yawlschema}LayoutPointType"/>
 *         &lt;element name="linecolor" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="lineEnd" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="lineStyle" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="offset" type="{http://www.yawlfoundation.org/yawlschema}LayoutPointType"/>
 *         &lt;element name="opaque" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="points" type="{http://www.yawlfoundation.org/yawlschema}LayoutPointsType"/>
 *         &lt;element name="resize" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="size" type="{http://www.yawlfoundation.org/yawlschema}LayoutDimensionType"/>
 *         &lt;element name="sizeable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayoutAttributesFactsType", propOrder = { "autosizeOrBackgroundColorOrBendable" })
public class LayoutAttributesFactsType {

	@XmlElementRefs({
			@XmlElementRef(name = "editable", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "connectable", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "offset", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "bounds", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "disconnectable", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "labelposition", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "opaque", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "font", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "linecolor", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "resize", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "foregroundColor", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "autosize", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "endFill", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "lineEnd", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "points", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "lineStyle", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "sizeable", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "backgroundColor", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "bendable", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class),
			@XmlElementRef(name = "size", namespace = "http://www.yawlfoundation.org/yawlschema", type = JAXBElement.class) })
	protected List<JAXBElement<?>> autosizeOrBackgroundColorOrBendable;

	/**
	 * Gets the value of the autosizeOrBackgroundColorOrBendable property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the autosizeOrBackgroundColorOrBendable
	 * property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAutosizeOrBackgroundColorOrBendable().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link Boolean }{@code >} {@link JAXBElement }
	 * {@code <}{@link Boolean }{@code >} {@link JAXBElement }{@code <}
	 * {@link LayoutPointType }{@code >} {@link JAXBElement }{@code <}
	 * {@link LayoutRectangleType }{@code >} {@link JAXBElement }{@code <}
	 * {@link Boolean }{@code >} {@link JAXBElement }{@code <}{@link Boolean }
	 * {@code >} {@link JAXBElement }{@code <}{@link LayoutPointType }{@code >}
	 * {@link JAXBElement }{@code <}{@link LayoutFontType }{@code >}
	 * {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
	 * {@link JAXBElement }{@code <}{@link Boolean }{@code >} {@link JAXBElement }
	 * {@code <}{@link BigInteger }{@code >} {@link JAXBElement }{@code <}
	 * {@link Boolean }{@code >} {@link JAXBElement }{@code <}{@link BigInteger }
	 * {@code >} {@link JAXBElement }{@code <}{@link Boolean }{@code >}
	 * {@link JAXBElement }{@code <}{@link LayoutPointsType }{@code >}
	 * {@link JAXBElement }{@code <}{@link Boolean }{@code >} {@link JAXBElement }
	 * {@code <}{@link BigInteger }{@code >} {@link JAXBElement }{@code <}
	 * {@link BigInteger }{@code >} {@link JAXBElement }{@code <}
	 * {@link LayoutDimensionType }{@code >} {@link JAXBElement }{@code <}
	 * {@link Boolean }{@code >}
	 * 
	 * 
	 */
	public List<JAXBElement<?>> getAutosizeOrBackgroundColorOrBendable() {
		if (autosizeOrBackgroundColorOrBendable == null) {
			autosizeOrBackgroundColorOrBendable = new ArrayList<JAXBElement<?>>();
		}
		return this.autosizeOrBackgroundColorOrBendable;
	}

}
