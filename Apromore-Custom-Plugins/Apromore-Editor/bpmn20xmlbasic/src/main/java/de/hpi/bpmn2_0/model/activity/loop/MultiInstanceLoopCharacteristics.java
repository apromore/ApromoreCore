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

import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.event.EventDefinition;
import de.hpi.bpmn2_0.model.misc.Property;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tMultiInstanceLoopCharacteristics complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tMultiInstanceLoopCharacteristics">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tLoopCharacteristics">
 *       &lt;sequence>
 *         &lt;element name="loopCardinality" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *         &lt;element name="loopDataInput" type="{http://www.omg.org/bpmn20}tDataInput" minOccurs="0"/>
 *         &lt;element name="loopDataOutput" type="{http://www.omg.org/bpmn20}tDataOutput" minOccurs="0"/>
 *         &lt;element name="inputDataItem" type="{http://www.omg.org/bpmn20}tProperty" minOccurs="0"/>
 *         &lt;element name="outputDataItem" type="{http://www.omg.org/bpmn20}tProperty" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}complexBehaviorDefinition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="completionCondition" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isSequential" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="behavior" type="{http://www.omg.org/bpmn20}tMultiInstanceFlowCondition" default="all" />
 *       &lt;attribute name="oneBehaviorEventRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="noneBehaviorEventRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMultiInstanceLoopCharacteristics", propOrder = {
        "loopCardinality",
        "loopDataInput",
        "loopDataOutput",
        "inputDataItem",
        "outputDataItem",
        "complexBehaviorDefinition",
        "completionCondition"
})
public class MultiInstanceLoopCharacteristics
        extends LoopCharacteristics {

    @XmlElements({
            @XmlElement(type = FormalExpression.class),
            @XmlElement(type = Expression.class)
    })
    protected Expression loopCardinality;

    protected DataInput loopDataInput;
    protected DataOutput loopDataOutput;
    protected Property inputDataItem;
    protected Property outputDataItem;
    protected List<ComplexBehaviorDefinition> complexBehaviorDefinition;

    @XmlElements({
            @XmlElement(type = FormalExpression.class),
            @XmlElement(type = Expression.class)
    })
    protected Expression completionCondition;
    @XmlAttribute
    protected Boolean isSequential;
    @XmlAttribute
    protected MultiInstanceFlowCondition behavior;

    @XmlAttribute
    @XmlIDREF
    protected EventDefinition oneBehaviorEventRef;
    @XmlIDREF
    @XmlAttribute
    protected EventDefinition noneBehaviorEventRef;

    /**
     * Gets the value of the loopCardinality property.
     *
     * @return possible object is
     *         {@link TExpression }
     */
    public Expression getLoopCardinality() {
        return loopCardinality;
    }

    /**
     * Sets the value of the loopCardinality property.
     *
     * @param value allowed object is
     *              {@link Expression }
     */
    public void setLoopCardinality(Expression value) {
        this.loopCardinality = value;
    }

    /**
     * Gets the value of the loopDataInput property.
     *
     * @return possible object is
     *         {@link DataInput }
     */
    public DataInput getLoopDataInput() {
        return loopDataInput;
    }

    /**
     * Sets the value of the loopDataInput property.
     *
     * @param value allowed object is
     *              {@link DataInput }
     */
    public void setLoopDataInput(DataInput value) {
        this.loopDataInput = value;
    }

    /**
     * Gets the value of the loopDataOutput property.
     *
     * @return possible object is
     *         {@link DataOutput }
     */
    public DataOutput getLoopDataOutput() {
        return loopDataOutput;
    }

    /**
     * Sets the value of the loopDataOutput property.
     *
     * @param value allowed object is
     *              {@link DataOutput }
     */
    public void setLoopDataOutput(DataOutput value) {
        this.loopDataOutput = value;
    }

    /**
     * Gets the value of the inputDataItem property.
     *
     * @return possible object is
     *         {@link TProperty }
     */
    public Property getInputDataItem() {
        return inputDataItem;
    }

    /**
     * Sets the value of the inputDataItem property.
     *
     * @param value allowed object is
     *              {@link Property }
     */
    public void setInputDataItem(Property value) {
        this.inputDataItem = value;
    }

    /**
     * Gets the value of the outputDataItem property.
     *
     * @return possible object is
     *         {@link Property }
     */
    public Property getOutputDataItem() {
        return outputDataItem;
    }

    /**
     * Sets the value of the outputDataItem property.
     *
     * @param value allowed object is
     *              {@link Property }
     */
    public void setOutputDataItem(Property value) {
        this.outputDataItem = value;
    }

    /**
     * Gets the value of the complexBehaviorDefinition property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the complexBehaviorDefinition property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComplexBehaviorDefinition().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link ComplexBehaviorDefinition }
     */
    public List<ComplexBehaviorDefinition> getComplexBehaviorDefinition() {
        if (complexBehaviorDefinition == null) {
            complexBehaviorDefinition = new ArrayList<ComplexBehaviorDefinition>();
        }
        return this.complexBehaviorDefinition;
    }

    /**
     * Gets the value of the completionCondition property.
     *
     * @return possible object is
     *         {@link Expression }
     */
    public Expression getCompletionCondition() {
        return completionCondition;
    }

    /**
     * Sets the value of the completionCondition property.
     *
     * @param value allowed object is
     *              {@link Expression }
     */
    public void setCompletionCondition(Expression value) {
        this.completionCondition = value;
    }

    /**
     * Gets the value of the isSequential property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isIsSequential() {
        if (isSequential == null) {
            return false;
        } else {
            return isSequential;
        }
    }

    /**
     * Sets the value of the isSequential property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsSequential(Boolean value) {
        this.isSequential = value;
    }

    /**
     * Gets the value of the behavior property.
     *
     * @return possible object is
     *         {@link MultiInstanceFlowCondition }
     */
    public MultiInstanceFlowCondition getBehavior() {
        if (behavior == null) {
            return MultiInstanceFlowCondition.ALL;
        } else {
            return behavior;
        }
    }

    /**
     * Sets the value of the behavior property.
     *
     * @param value allowed object is
     *              {@link MultiInstanceFlowCondition }
     */
    public void setBehavior(MultiInstanceFlowCondition value) {
        this.behavior = value;
    }

    /**
     * Gets the value of the oneBehaviorEventRef property.
     *
     * @return possible object is
     *         {@link EventDefinition }
     */
    public EventDefinition getOneBehaviorEventRef() {
        return oneBehaviorEventRef;
    }

    /**
     * Sets the value of the oneBehaviorEventRef property.
     *
     * @param value allowed object is
     *              {@link EventDefinition }
     */
    public void setOneBehaviorEventRef(EventDefinition value) {
        this.oneBehaviorEventRef = value;
    }

    /**
     * Gets the value of the noneBehaviorEventRef property.
     *
     * @return possible object is
     *         {@link EventDefinition }
     */
    public EventDefinition getNoneBehaviorEventRef() {
        return noneBehaviorEventRef;
    }

    /**
     * Sets the value of the noneBehaviorEventRef property.
     *
     * @param value allowed object is
     *              {@link EventDefinition }
     */
    public void setNoneBehaviorEventRef(EventDefinition value) {
        this.noneBehaviorEventRef = value;
    }

}
