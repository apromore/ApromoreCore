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


package de.hpi.bpmn2_0.model.activity.resource;

import de.hpi.bpmn2_0.model.BaseElement;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tResourceRole complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tResourceRole">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tBaseElement">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="resourceRef" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *           &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/MODEL}resourceParameterBinding" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/MODEL}resourceAssignmentExpression" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tResourceRole", propOrder = {
        "resourceRef",
        "resourceParameterBinding",
        "resourceAssignmentExpression"
})
@XmlSeeAlso({
        Performer.class
})
public class ResourceRole
        extends BaseElement {

    protected QName resourceRef;
    protected List<ResourceParameterBinding> resourceParameterBinding;
    protected ResourceAssignmentExpression resourceAssignmentExpression;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the resourceRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getResourceRef() {
        return resourceRef;
    }

    /**
     * Sets the value of the resourceRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setResourceRef(QName value) {
        this.resourceRef = value;
    }

    /**
     * Gets the value of the resourceParameterBinding property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceParameterBinding property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceParameterBinding().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceParameterBinding }
     */
    public List<ResourceParameterBinding> getResourceParameterBinding() {
        if (resourceParameterBinding == null) {
            resourceParameterBinding = new ArrayList<ResourceParameterBinding>();
        }
        return this.resourceParameterBinding;
    }

    /**
     * Gets the value of the resourceAssignmentExpression property.
     *
     * @return possible object is
     *         {@link ResourceAssignmentExpression }
     */
    public ResourceAssignmentExpression getResourceAssignmentExpression() {
        return resourceAssignmentExpression;
    }

    /**
     * Sets the value of the resourceAssignmentExpression property.
     *
     * @param value allowed object is
     *              {@link ResourceAssignmentExpression }
     */
    public void setResourceAssignmentExpression(ResourceAssignmentExpression value) {
        this.resourceAssignmentExpression = value;
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
