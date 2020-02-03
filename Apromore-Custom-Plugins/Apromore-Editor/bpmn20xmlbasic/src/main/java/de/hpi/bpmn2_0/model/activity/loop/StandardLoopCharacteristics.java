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

package de.hpi.bpmn2_0.model.activity.loop;

import de.hpi.bpmn2_0.model.FormalExpression;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;


/**
 * <p>Java class for tStandardLoopCharacteristics complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tStandardLoopCharacteristics">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tLoopCharacteristics">
 *       &lt;sequence>
 *         &lt;element name="loopCondition" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="testBefore" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="loopMaximum" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tStandardLoopCharacteristics", propOrder = {
        "loopCondition"
})
public class StandardLoopCharacteristics
        extends LoopCharacteristics {

    @XmlElement(name = "loopCondition", type = FormalExpression.class)
    protected FormalExpression loopCondition;
    @XmlAttribute
    protected Boolean testBefore;
    @XmlAttribute
    protected BigInteger loopMaximum;

    /**
     * Gets the value of the loopCondition property.
     *
     * @return possible object is
     *         {@link FormalExpression }
     */
    public FormalExpression getLoopCondition() {
        return loopCondition;
    }

    /**
     * Sets the value of the loopCondition property.
     *
     * @param value allowed object is
     *              {@link FormalExpression }
     */
    public void setLoopCondition(FormalExpression value) {
        this.loopCondition = value;
    }

    /**
     * Gets the value of the testBefore property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isTestBefore() {
        if (testBefore == null) {
            return false;
        } else {
            return testBefore;
        }
    }

    /**
     * Sets the value of the testBefore property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setTestBefore(Boolean value) {
        this.testBefore = value;
    }

    /**
     * Gets the value of the loopMaximum property.
     *
     * @return possible object is
     *         {@link BigInteger }
     */
    public BigInteger getLoopMaximum() {
        return loopMaximum;
    }

    /**
     * Sets the value of the loopMaximum property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setLoopMaximum(BigInteger value) {
        this.loopMaximum = value;
    }

}
