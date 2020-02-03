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

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tAssignment complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tAssignment">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="from" type="{http://www.omg.org/bpmn20}tBaseElementWithMixedContent"/>
 *         &lt;element name="to" type="{http://www.omg.org/bpmn20}tBaseElementWithMixedContent"/>
 *       &lt;/sequence>
 *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAssignment", propOrder = {
        "from",
        "to"
})
public class Assignment
        extends BaseElement {

    @XmlElement(required = true)
    protected String from;

    @XmlElement(required = true)
    protected String to;

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String language;

    /**
     * Gets the value of the from property.
     *
     * @return possible object is
     *         {@link TBaseElementWithMixedContent }
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     *
     * @param value allowed object is
     *              {@link TBaseElementWithMixedContent }
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     *
     * @return possible object is
     *         {@link TBaseElementWithMixedContent }
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     *
     * @param value allowed object is
     *              {@link TBaseElementWithMixedContent }
     */
    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Gets the value of the language property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
