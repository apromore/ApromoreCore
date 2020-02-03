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

package de.hpi.bpmn2_0.model.gateway;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;

/**
 * <p/>
 * Java class for tComplexGateway complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name=&quot;tComplexGateway&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://www.omg.org/bpmn20}tGateway&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;activationCondition&quot; type=&quot;{http://www.omg.org/bpmn20}tExpression&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;default&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}IDREF&quot; /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tComplexGateway", propOrder = {"activationCondition"})
@StencilId("ComplexGateway")
public class ComplexGateway extends GatewayWithDefaultFlow {

    protected Expression activationCondition;

    /**
     * Refers at runtime to the number of tokens that are present on an incoming
     * Sequence Flow of the Complex Gateway.
     */
    @XmlTransient
    private int activationCount;

    /**
     * Refers at runtime to the number of tokens that are present on an incoming
     * Sequence Flow of the Complex Gateway.
     */
    @XmlTransient
    private boolean waitingForStart;


    public void acceptVisitor(Visitor v) {
        v.visitComplexGateway(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the activationCondition property.
     *
     * @return possible object is {@link Expression }
     */
    public Expression getActivationCondition() {
        return activationCondition;
    }

    /**
     * Sets the value of the activationCondition property.
     *
     * @param value allowed object is {@link Expression }
     */
    public void setActivationCondition(Expression value) {
        this.activationCondition = value;
    }

    /**
     * @return the activationCount
     */
    public int getActivationCount() {
        return activationCount;
    }

    /**
     * @param activationCount the activationCount to set
     */
    public void setActivationCount(int activationCount) {
        this.activationCount = activationCount;
    }

    /**
     * @param waitingForStart the waitingForStart to set
     */
    public void setWaitingForStart(boolean waitingForStart) {
        this.waitingForStart = waitingForStart;
    }

    /**
     * @return the waitingForStart
     */
    public boolean isWaitingForStart() {
        return waitingForStart;
    }


}
