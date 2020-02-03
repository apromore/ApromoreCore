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

package de.hpi.bpmn2_0.model.activity.type;

import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.misc.Operation;
import de.hpi.bpmn2_0.model.activity.misc.ServiceImplementation;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tSendTask complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tSendTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tTask">
 *       &lt;attribute name="messageRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="operationRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSendTask")
public class SendTask
        extends Task {
    @XmlIDREF
    @XmlAttribute
    protected Message messageRef;
    @XmlIDREF
    @XmlAttribute
    protected Operation operationRef;

    @XmlAttribute
    protected ServiceImplementation implementation;

    public void acceptVisitor(Visitor v) {
        v.visitSendTask(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the messageRef property.
     *
     * @return possible object is
     *         {@link Message }
     */
    public Message getMessageRef() {
        return messageRef;
    }

    /**
     * Sets the value of the messageRef property.
     *
     * @param value allowed object is
     *              {@link Message }
     */
    public void setMessageRef(Message value) {
        this.messageRef = value;
    }

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
     * @return the implementation
     */
    public ServiceImplementation getImplementation() {
        return implementation;
    }

    /**
     * @param implementation the implementation to set
     */
    public void setImplementation(ServiceImplementation implementation) {
        this.implementation = implementation;
    }

}
