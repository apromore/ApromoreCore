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

package de.hpi.bpmn2_0.model.callable;

import de.hpi.bpmn2_0.model.activity.misc.BusinessRuleTaskImplementation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p/>
 * Java class for tGlobalBusinessRuleTask complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tGlobalBusinessRuleTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tGlobalTask">
 *       &lt;attribute name="implementation" type="{http://www.omg.org/spec/BPMN/20100524/MODEL}tImplementation" default="##unspecified" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tGlobalBusinessRuleTask")
public class GlobalBusinessRuleTask extends GlobalTask {

    /* Constructors */
    public GlobalBusinessRuleTask() {
        super();
    }

    public GlobalBusinessRuleTask(GlobalTask gt) {
        super(gt);
    }

    @XmlAttribute(name = "implementation")
    protected BusinessRuleTaskImplementation implementation;

    /**
     * Gets the value of the implementation property.
     *
     * @return possible object is {@link String }
     */
    public BusinessRuleTaskImplementation getImplementation() {
        return implementation;
    }

    /**
     * Sets the value of the implementation property.
     *
     * @param value allowed object is {@link BusinessRuleTaskImplementation }
     */
    public void setImplementation(BusinessRuleTaskImplementation value) {
        this.implementation = value;
    }

}
