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

package de.hpi.bpmn2_0.model.bpmndi;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.di.Plane;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for BPMNPlane complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="BPMNPlane">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/DD/20100524/DI}Plane">
 *       &lt;attribute name="bpmnElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "BPMNPlane")
@XmlAccessorType(XmlAccessType.FIELD)
public class BPMNPlane extends Plane {

    @XmlAttribute(name = "bpmnElement")
    @XmlIDREF
    protected BaseElement bpmnElement;

    /* Constructor */
    public BPMNPlane() {
        super();
        id = SignavioUUID.generate();
    }

    /* Getter & Setter */

    /**
     * Gets the value of the bpmnElement property.
     *
     * @return possible object is
     *         {@link BaseElement }
     */
    public BaseElement getBpmnElement() {
        return bpmnElement;
    }

    /**
     * Sets the value of the bpmnElement property.
     *
     * @param value allowed object is
     *              {@link BaseElement }
     */
    public void setBpmnElement(BaseElement value) {
        this.bpmnElement = value;
    }

}
