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

import de.hpi.bpmn2_0.model.AdHocOrdering;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tAdHocSubProcess complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tAdHocSubProcess">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tSubProcess">
 *       &lt;sequence>
 *         &lt;element name="completionCondition" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="cancelRemainingInstances" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="ordering" type="{http://www.omg.org/bpmn20}tAdHocOrdering" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAdHocSubProcess", propOrder = {
        "completionCondition"
})
public class AdHocSubProcess
        extends SubProcess {

    protected FormalExpression completionCondition;
    @XmlAttribute
    protected Boolean cancelRemainingInstances;
    @XmlAttribute
    protected AdHocOrdering ordering;

    public void acceptVisitor(Visitor v) {
        v.visitAdHocSubProcess(this);
    }

    /**
     * Gets the value of the completionCondition property.
     *
     * @return possible object is
     *         {@link FormalExpression }
     */
    public FormalExpression getCompletionCondition() {
        return completionCondition;
    }

    /**
     * Sets the value of the completionCondition property.
     *
     * @param value allowed object is
     *              {@link FormalExpression }
     */
    public void setCompletionCondition(FormalExpression value) {
        this.completionCondition = value;
    }

    /**
     * Gets the value of the cancelRemainingInstances property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isCancelRemainingInstances() {
        if (cancelRemainingInstances == null) {
            return true;
        } else {
            return cancelRemainingInstances;
        }
    }

    /**
     * Sets the value of the cancelRemainingInstances property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setCancelRemainingInstances(Boolean value) {
        this.cancelRemainingInstances = value;
    }

    /**
     * Gets the value of the ordering property.
     *
     * @return possible object is
     *         {@link AdHocOrdering }
     */
    public AdHocOrdering getOrdering() {
        return ordering;
    }

    /**
     * Sets the value of the ordering property.
     *
     * @param value allowed object is
     *              {@link AdHocOrdering }
     */
    public void setOrdering(AdHocOrdering value) {
        this.ordering = value;
    }

}
