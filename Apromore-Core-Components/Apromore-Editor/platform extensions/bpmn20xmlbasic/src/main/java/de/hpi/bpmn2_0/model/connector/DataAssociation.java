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

package de.hpi.bpmn2_0.model.connector;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.misc.Assignment;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tDataAssociation complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tDataAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="transformation" type="{http://www.omg.org/bpmn20}tFormalExpression" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}assignment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataAssociation", propOrder = {"transformation",
        "assignment", "sourceRefList", "targetRefList"})
@XmlSeeAlso({DataInputAssociation.class, DataOutputAssociation.class})
public class DataAssociation extends Edge {
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @XmlElement(type = FlowElement.class, name = "sourceRef")
    public List<FlowElement> sourceRefList;

    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @XmlElement(type = FlowElement.class, name = "targetRef")
    public List<FlowElement> targetRefList;

    @XmlTransient
    protected FlowElement parent;

    @XmlElement
    protected FormalExpression transformation;
    @XmlElement
    protected List<Assignment> assignment;

    public void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent != null && parent instanceof FlowElement) {
            this.parent = (FlowElement) parent;
        }
    }

    /**
     * The {@link Marshaller} invokes this method right before marshaling to
     * XML. It secures that sourceRefList and targetRefList only contains a
     * maximum of one element.
     *
     * @param marshaller The marshaling context
     */
    public void beforeMarshal(Marshaller marshaller) {
        /*
           * Check sourceRef
           */
        if (sourceRefList != null && sourceRefList.size() > 1) {
            FlowElement firstEle = sourceRefList.get(0);
            sourceRefList = new ArrayList<FlowElement>();
            sourceRefList.add(firstEle);
        }

        /*
           * Check targetRef
           */
        if (targetRefList != null && targetRefList.size() > 1) {
            FlowElement firstEle = targetRefList.get(0);
            targetRefList = new ArrayList<FlowElement>();
            targetRefList.add(firstEle);
        }
    }

    public void acceptVisitor(Visitor v) {
        v.visitDataAssociation(this);
    }


    /* Getter & Setter */

    /**
     * Gets the value of the transformation property.
     *
     * @return possible object is {@link TFormalExpression }
     */
    public FormalExpression getTransformation() {
        return transformation;
    }

    /**
     * Sets the value of the transformation property.
     *
     * @param value allowed object is {@link FormalExpression }
     */
    public void setTransformation(FormalExpression value) {
        this.transformation = value;
    }

    /**
     * Gets the value of the assignment property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the assignment property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getAssignment().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link TAssignment }
     */
    public List<Assignment> getAssignment() {
        if (assignment == null) {
            assignment = new ArrayList<Assignment>();
        }
        return this.assignment;
    }

    public FlowElement getSourceRef() {
        if (sourceRefList != null && sourceRefList.size() > 0) {
            return sourceRefList.get(0);
        }
        return null;
    }

    public void setSourceRef(FlowElement sourceRef) {
        if (sourceRefList == null) {
            sourceRefList = new ArrayList<FlowElement>();
        }
        sourceRefList.add(0, sourceRef);
    }

    public FlowElement getTargetRef() {
        if (targetRefList != null && targetRefList.size() > 0) {
            return targetRefList.get(0);
        }
        return null;
    }

    public void setTargetRef(FlowElement targetRef) {
        if (targetRefList == null) {
            targetRefList = new ArrayList<FlowElement>();
        }
        targetRefList.add(0, targetRef);
    }
}
