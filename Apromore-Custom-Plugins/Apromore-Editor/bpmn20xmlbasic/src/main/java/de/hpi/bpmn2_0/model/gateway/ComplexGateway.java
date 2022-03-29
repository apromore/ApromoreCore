/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package de.hpi.bpmn2_0.model.gateway;

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

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
