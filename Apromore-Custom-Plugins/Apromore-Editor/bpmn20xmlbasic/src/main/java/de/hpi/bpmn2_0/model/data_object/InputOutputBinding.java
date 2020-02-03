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

package de.hpi.bpmn2_0.model.data_object;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.activity.misc.Operation;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tInputOutputBinding complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tInputOutputBinding">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tBaseElement">
 *       &lt;attribute name="operationRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="inputDataRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="outputDataRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInputOutputBinding")
public class InputOutputBinding
        extends BaseElement {
    @XmlIDREF
    @XmlAttribute(name = "operationRef", required = true)
    protected Operation operationRef;
    @XmlAttribute(name = "inputDataRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object inputDataRef;
    @XmlAttribute(name = "outputDataRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object outputDataRef;

    /**
     * Gets the value of the operationRef property.
     *
     * @return possible object is
     *         {@link Operation }
     */
    public Operation getOperationRef() {
        return operationRef;
    }

    /**
     * Sets the value of the operationRef property.
     *
     * @param value allowed object is
     *              {@link Operation }
     */
    public void setOperationRef(Operation value) {
        this.operationRef = value;
    }

    /**
     * Gets the value of the inputDataRef property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getInputDataRef() {
        return inputDataRef;
    }

    /**
     * Sets the value of the inputDataRef property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setInputDataRef(Object value) {
        this.inputDataRef = value;
    }

    /**
     * Gets the value of the outputDataRef property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getOutputDataRef() {
        return outputDataRef;
    }

    /**
     * Sets the value of the outputDataRef property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setOutputDataRef(Object value) {
        this.outputDataRef = value;
    }

}
