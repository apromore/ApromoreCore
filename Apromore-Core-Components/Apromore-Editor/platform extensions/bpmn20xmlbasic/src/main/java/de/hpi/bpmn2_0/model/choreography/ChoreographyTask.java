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

package de.hpi.bpmn2_0.model.choreography;

import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Java class for tChoreographyTask complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tChoreographyTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tChoreographyActivity">
 *       &lt;sequence>
 *         &lt;element name="messageFlowRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tChoreographyTask", propOrder = {
        "messageFlowRef"
})
public class ChoreographyTask
        extends ChoreographyActivity {

    @XmlIDREF
    @XmlElement(type = MessageFlow.class/*required = true*/)
    protected List<MessageFlow> messageFlowRef;

    /**
     * Creates a MessageFlow to create schema valid XML
     *
     * @param choreography
     */
    public void createMessageFlows(Choreography choreography) {
        /* Insert a message flow from first to last participant */
        if (getParticipantRef().size() >= 2) {

            // The initiating participant has to be the source of the
            // message flow
            Participant p1 = getParticipantRef().get(0);
            Participant p2 = getParticipantRef().get(1);
            Participant source = p1;
            Participant target = p2;

            if (p2.isInitiating()) {
                source = p2;
                target = p1;
            }

            /* Create message flow */
            MessageFlow msgFlow = new MessageFlow();
            msgFlow.setId(SignavioUUID.generate());

            /* Append message object */
            if (source._msgRef != null) {
                msgFlow.setMessageRef(source._msgRef);
            }

            msgFlow.setSourceRef(source);
            msgFlow.setTargetRef(target);

            /* Add references */
            getMessageFlows().add(msgFlow);
            choreography.getMessageFlow().add(msgFlow);

            /* Insert the optional second message flow */
            if (target._msgRef != null) {

                /* Create message flow */
                MessageFlow msgFlow2 = new MessageFlow();
                msgFlow2.setId(SignavioUUID.generate());

                /* Append message object */
                msgFlow2.setMessageRef(target._msgRef);

                msgFlow2.setSourceRef(target);
                msgFlow2.setTargetRef(source);

                /* Add references */
                getMessageFlows().add(msgFlow2);
                choreography.getMessageFlow().add(msgFlow2);
            }


        }
    }

    public void acceptVisitor(Visitor v) {
        v.visitChoreographyTask(this);
    }

    /**
     * Gets the value of the messageFlowRef property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageFlowRef property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageFlowRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link MessageFlow }
     */
    public List<MessageFlow> getMessageFlows() {
        if (messageFlowRef == null) {
            messageFlowRef = new ArrayList<MessageFlow>();
        }
        return this.messageFlowRef;
    }

}
