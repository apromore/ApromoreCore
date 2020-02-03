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

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tConversationNode complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tConversationNode">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="participantRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tConversationNode", propOrder = {"participantRef",
        "messageFlowRef", "correlationKey"})
@XmlSeeAlso({CallConversation.class, SubConversation.class, Conversation.class})
public abstract class ConversationNode extends FlowNode implements
        ConversationElement {

    @XmlIDREF
    protected List<MessageFlow> messageFlowRef;
    protected List<CorrelationKey> correlationKey;

    @XmlIDREF
    protected List<Participant> participantRef;

    @XmlTransient
    public List<String> participantsIds;

    /*
      * Constructors
      */

    public ConversationNode() {
        super();
    }

    public ConversationNode(ConversationNode node) {
        super(node);

    }

    /**
     * Helper for the import, see {@link FlowElement#isElementWithFixedSize().
     */
    // @Override
    public boolean isElementWithFixedSize() {
        return true;
    }

    /**
     * For the fixed-size shape, return the fixed width.
     */
    public double getStandardWidth() {
        return 33.5;
    }

    /**
     * For the fixed-size shape, return the fixed height.
     */
    public double getStandardHeight() {
        return 29.0;
    }

    public void acceptVisitor(Visitor v) {
        v.visitConversationNode(this);
    }

    /**
     * Gets the value of the participantRef property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the participantRef property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getParticipantRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Participant }
     */
    public List<Participant> getParticipantRef() {
        if (participantRef == null) {
            participantRef = new ArrayList<Participant>();
        }
        return this.participantRef;
    }
}
