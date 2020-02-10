
package de.hpi.bpmn2_0.model.choreography;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
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
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.annotations.ContainerElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.callable.GlobalChoreographyTask;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tCallChoreography complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCallChoreography">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tChoreographyActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/MODEL}participantAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="calledChoreographyRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCallChoreography", propOrder = {
        "participantAssociation"
})
public class CallChoreography
        extends ChoreographyActivity implements ContainerElement, CallingElement {

    protected List<ParticipantAssociation> participantAssociation;
    @XmlAttribute(name = "calledChoreographyRef")
    @XmlIDREF
    protected Choreography calledChoreographyRef;

    @XmlTransient
    public List<DiagramElement> _diagramElements = new ArrayList<DiagramElement>();

    /*
    * Constructors
    */

    public CallChoreography() {
        super();
    }

    public CallChoreography(ChoreographyActivity choreoAct) {
        super(choreoAct);

        this.setStartQuantity(null);
        this.setCompletionQuantity(null);

        if (choreoAct instanceof ChoreographyTask) {
            this.setCalledChoreographyRef(new GlobalChoreographyTask());
        }
    }

    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        /* Global Task */
        if (calledChoreographyRef instanceof GlobalChoreographyTask) {
            calledElements.add(calledChoreographyRef);
        }

        /* Calling a sub choreography */
        else if (calledChoreographyRef instanceof Choreography) {
            for (FlowElement flowEl : calledChoreographyRef.getFlowElement()) {
                if (flowEl instanceof CallingElement) {
                    calledElements.addAll(((CallingElement) flowEl).getCalledElements());
                }
            }
        }

        return calledElements;
    }

    public List<Edge> getChildEdges() {
        List<Edge> edgeList = new ArrayList<Edge>();

        for (FlowElement fe : this.getFlowElement()) {
            if (fe instanceof Edge) {
                edgeList.add((Edge) fe);
            } else if (fe instanceof ContainerElement) {
                edgeList.addAll(((ContainerElement) fe).getChildEdges());
            }
        }

        return edgeList;
    }

    /* Getter & Setter */

    /**
     * Gets the value of the participantAssociation property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participantAssociation property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipantAssociation().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link ParticipantAssociation }
     */
    public List<ParticipantAssociation> getParticipantAssociation() {
        if (participantAssociation == null) {
            participantAssociation = new ArrayList<ParticipantAssociation>();
        }
        return this.participantAssociation;
    }

    /**
     * Gets the value of the calledChoreographyRef property.
     *
     * @return possible object is
     *         {@link Choreography }
     */
    public Choreography getCalledChoreographyRef() {
        return calledChoreographyRef;
    }

    /**
     * Sets the value of the calledChoreographyRef property.
     *
     * @param value allowed object is
     *              {@link Choreography }
     */
    public void setCalledChoreographyRef(Choreography value) {
        this.calledChoreographyRef = value;
    }

    public void acceptVisitor(Visitor v) {
        v.visitCallChoreography(this);
    }

    public List<DiagramElement> _getDiagramElements() {
        return _diagramElements;
    }

    public List<FlowElement> getFlowElement() {
        if (this.getCalledChoreographyRef() != null) {
            return this.getCalledChoreographyRef().getFlowElement();
        }

        return new ArrayList<FlowElement>();
    }


}
