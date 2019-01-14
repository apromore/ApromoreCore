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

package de.hpi.bpmn2_0.model.activity;

import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tTransaction complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tTransaction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tActivity">
 *       &lt;attribute name="method" type="{http://www.omg.org/bpmn20}tTransactionMethod" default="compensate" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTransaction")
public class Transaction
        extends SubProcess {

    @XmlAttribute
    protected TransactionMethod method;

    public void acceptVisitor(Visitor v) {
        v.visitTransaction(this);
    }

    /**
     * Gets the value of the method property.
     *
     * @return possible object is
     *         {@link TransactionMethod }
     */
    public TransactionMethod getMethod() {
        if (method == null) {
            return TransactionMethod.COMPENSATE;
        } else {
            return method;
        }
    }

    /**
     * Sets the value of the method property.
     *
     * @param value allowed object is
     *              {@link TransactionMethod }
     */
    public void setMethod(TransactionMethod value) {
        this.method = value;
    }

}
