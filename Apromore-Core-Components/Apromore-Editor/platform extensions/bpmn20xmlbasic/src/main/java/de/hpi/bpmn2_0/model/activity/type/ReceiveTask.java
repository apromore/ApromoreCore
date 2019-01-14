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
import de.hpi.bpmn2_0.model.activity.misc.ServiceImplementation;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tReceiveTask complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tReceiveTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tTask">
 *       &lt;attribute name="instantiate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="messageRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="operationRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tReceiveTask")
public class ReceiveTask
        extends Task {

    @XmlAttribute
    protected Boolean instantiate;
    @XmlAttribute
    protected QName messageRef;
    @XmlAttribute
    protected QName operationRef;
    @XmlAttribute
    protected ServiceImplementation implementation;

    public void acceptVisitor(Visitor v) {
        v.visitReceiveTask(this);
    }


    /**
     * Gets the value of the instantiate property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isInstantiate() {
        if (instantiate == null) {
            return false;
        } else {
            return instantiate;
        }
    }

    /**
     * Sets the value of the instantiate property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setInstantiate(Boolean value) {
        this.instantiate = value;
    }

    /**
     * Gets the value of the messageRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getMessageRef() {
        return messageRef;
    }

    /**
     * Sets the value of the messageRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setMessageRef(QName value) {
        this.messageRef = value;
    }

    /**
     * Gets the value of the operationRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getOperationRef() {
        return operationRef;
    }

    /**
     * Sets the value of the operationRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setOperationRef(QName value) {
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
