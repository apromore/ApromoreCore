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

package de.hpi.bpmn2_0.model.bpmndi;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.bpmndi.di.LabeledEdge;
import de.hpi.bpmn2_0.model.bpmndi.di.MessageVisibleKind;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;

/**
 * <p/>
 * Java class for BPMNEdge complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="BPMNEdge">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/DD/20100524/DI}LabeledEdge">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/DI}BPMNLabel" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="bpmnElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="sourceElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="targetElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="messageVisibleKind" type="{http://www.omg.org/spec/BPMN/20100524/DI}MessageVisibleKind" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "BPMNEdge")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BPMNEdge", propOrder = {"bpmnLabel"})
public class BPMNEdge extends LabeledEdge {

    @XmlElement(name = "BPMNLabel")
    protected BPMNLabel bpmnLabel;

    @XmlIDREF
    @XmlAttribute(name = "bpmnElement")
    protected BaseElement bpmnElement;

    @XmlIDREF
    @XmlAttribute(name = "sourceElement")
    protected DiagramElement sourceElement;

    @XmlIDREF
    @XmlAttribute(name = "targetElement")
    protected DiagramElement targetElement;

    @XmlAttribute(name = "messageVisibleKind")
    protected MessageVisibleKind messageVisibleKind;

    public void acceptVisitor(Visitor v) {
        v.visitBpmnEdge(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the bpmnLabel property.
     *
     * @return possible object is {@link BPMNLabel }
     */
    public BPMNLabel getBPMNLabel() {
        return bpmnLabel;
    }

    /**
     * Sets the value of the bpmnLabel property.
     *
     * @param value allowed object is {@link BPMNLabel }
     */
    public void setBPMNLabel(BPMNLabel value) {
        this.bpmnLabel = value;
    }

    /**
     * Gets the value of the bpmnElement property.
     *
     * @return possible object is {@link BaseElement }
     */
    public BaseElement getBpmnElement() {
        return bpmnElement;
    }

    /**
     * Sets the value of the bpmnElement property.
     *
     * @param value allowed object is {@link BaseElement }
     */
    public void setBpmnElement(BaseElement value) {
        this.bpmnElement = value;
    }

    /**
     * Gets the value of the sourceElement property.
     *
     * @return possible object is {@link DiagramElement }
     */
    public DiagramElement getSourceElement() {
        return sourceElement;
    }

    /**
     * Sets the value of the sourceElement property.
     *
     * @param value allowed object is {@link DiagramElement }
     */
    public void setSourceElement(DiagramElement value) {
        this.sourceElement = value;
    }

    /**
     * Gets the value of the targetElement property.
     *
     * @return possible object is {@link DiagramElement }
     */
    public DiagramElement getTargetElement() {
        return targetElement;
    }

    /**
     * Sets the value of the targetElement property.
     *
     * @param value allowed object is {@link DiagramElement }
     */
    public void setTargetElement(DiagramElement value) {
        this.targetElement = value;
    }

    /**
     * Gets the value of the messageVisibleKind property.
     *
     * @return possible object is {@link MessageVisibleKind }
     */
    public MessageVisibleKind getMessageVisibleKind() {
        return messageVisibleKind;
    }

    /**
     * Sets the value of the messageVisibleKind property.
     *
     * @param value allowed object is {@link MessageVisibleKind }
     */
    public void setMessageVisibleKind(MessageVisibleKind value) {
        this.messageVisibleKind = value;
    }
}
