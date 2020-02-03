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

package de.hpi.bpmn2_0.model.conversation;

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Collaboration;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.callable.GlobalConversation;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tCallConversation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCallConversation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tConversationNode">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}participantAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="calledElementRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCallConversation", propOrder = {
//    "participantAssociation"
})
public class CallConversation
        extends ConversationNode implements CallingElement {

    //    protected List<TParticipantAssociation> participantAssociation;
    @XmlIDREF
    @XmlAttribute
    protected Collaboration calledElementRef;

    @XmlTransient
    public List<DiagramElement> _diagramElements = new ArrayList<DiagramElement>();

    /*
     * Constructors
     */

    public CallConversation() {
        super();
    }

    public CallConversation(ConversationNode node) {
        super(node);

        if (node instanceof Conversation) {
            setCalledElementRef(new GlobalConversation());
        }
    }


    public void acceptVisitor(Visitor v) {
        v.visitCallConversation(this);
    }


    /**
     * Gets the value of the participantAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participantAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipantAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TParticipantAssociation }
     *
     *
     */
//    public List<TParticipantAssociation> getParticipantAssociation() {
//        if (participantAssociation == null) {
//            participantAssociation = new ArrayList<TParticipantAssociation>();
//        }
//        return this.participantAssociation;
//    }

    /**
     * Gets the value of the calledElementRef property.
     *
     * @return possible object is
     *         {@link Collaboration }
     */
    public Collaboration getCalledElementRef() {
        return calledElementRef;
    }

    /**
     * Sets the value of the calledElementRef property.
     *
     * @param value allowed object is
     *              {@link Collaboration }
     */
    public void setCalledElementRef(Collaboration value) {
        this.calledElementRef = value;
    }


    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        if (calledElementRef != null) {
            calledElements.add(getCalledElementRef());
        }

        return calledElements;
    }

}
