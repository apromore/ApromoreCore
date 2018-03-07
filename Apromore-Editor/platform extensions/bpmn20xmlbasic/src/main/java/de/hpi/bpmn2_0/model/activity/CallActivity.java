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

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.CallableElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tCallActivity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCallActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tActivity">
 *       &lt;attribute name="calledElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCallActivity")
public class CallActivity
        extends Activity implements CallingElement {
    /* Constructors */
    public CallActivity() {
        super();
    }

    public CallActivity(Task t) {
        super(t);
    }

    @XmlTransient
    private List<FlowElement> processElements;

    /*
      * The diagram and process element of a linked subprocess
      */
    @XmlTransient
    public BPMNDiagram _diagramElement;

    @XmlAttribute
    @XmlIDREF
    protected CallableElement calledElement;

    /**
     * Gets the value of the calledElement property.
     *
     * @return possible object is
     *         {@link CallableElement }
     */
    public CallableElement getCalledElement() {
        return calledElement;
    }

    /**
     * Sets the value of the calledElement property.
     *
     * @param value allowed object is
     *              {@link CallableElement }
     */
    public void setCalledElement(CallableElement value) {
        this.calledElement = value;
    }

    public void acceptVisitor(Visitor v) {
        v.visitCallActivity(this);
    }

    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        if (getCalledElement() != null) {
            calledElements.add(getCalledElement());
        }

        return calledElements;
    }

    /**
     * Overrides the general addChild method to collect elements of a call
     * activity expanded sub process.
     */
    public void addChild(BaseElement el) {
        if (el instanceof FlowElement) {
            this._getFlowElementsOfTheGlobalProcess().add((FlowElement) el);
        }
    }

    /* Getter & Setter */

    /**
     * !!! Only for usages during the BPMN 2.0 Export process.
     * Returns the elements the expanded subprocess called be this call activity.
     */
    public List<FlowElement> _getFlowElementsOfTheGlobalProcess() {
        if (this.processElements == null) {
            this.processElements = new ArrayList<FlowElement>();
        }

        return this.processElements;
    }
}
