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

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.conversation.CorrelationKey;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tChoreographyActivity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tChoreographyActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *       &lt;sequence>
 *         &lt;element name="participant" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="initiatingParticipantRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tChoreographyActivity", propOrder = {
        "participantRef",
        "correlationKey",
        "initiatingParticipantRef",
        "loopType"
})
@XmlSeeAlso({
        ChoreographyTask.class,
        SubChoreography.class,
        CallChoreography.class
})
public abstract class ChoreographyActivity
        extends Activity {

    @XmlElement(required = true)
    @XmlIDREF
    protected List<Participant> participantRef;
    protected List<CorrelationKey> correlationKey;

    @XmlIDREF
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "IDREF")
    protected Participant initiatingParticipantRef;

    @XmlAttribute
    protected ChoreographyLoopType loopType;

    public ChoreographyActivity(ChoreographyActivity choreoAct) {
        super(choreoAct);

        if (!choreoAct.getParticipantRef().isEmpty()) {
            this.getParticipantRef().addAll(choreoAct.getParticipantRef());
        }
        if (!choreoAct.getCorrelationKey().isEmpty()) {
            this.getCorrelationKey().addAll(choreoAct.getCorrelationKey());
        }

        this.setInitiatingParticipantRef(choreoAct.getInitiatingParticipant());
        this.setLoopType(choreoAct.getLoopType());
    }


    public ChoreographyActivity() {
        super();
    }


    public void addChild(BaseElement child) {
        if (child instanceof Participant) {
            this.getParticipantRef().add((Participant) child);
            if (((Participant) child).isInitiating()) {
                this.setInitiatingParticipantRef((Participant) child);
            }
        }
    }


    public void acceptVisitor(Visitor v) {
        v.visitChoreographyActivity(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the participant property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participant property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipantRef().add(newItem);
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

    /**
     * Gets the value of the initiatingParticipantRef property.
     *
     * @return possible object is
     *         {@link Participant }
     */
    public Participant getInitiatingParticipant() {
        return initiatingParticipantRef;
    }

    /**
     * Sets the value of the initiatingParticipantRef property.
     *
     * @param value allowed object is
     *              {@link Participant }
     */
    public void setInitiatingParticipantRef(Participant value) {
        this.initiatingParticipantRef = value;
    }

    public ChoreographyLoopType getLoopType() {
        return loopType;
    }

    public void setLoopType(ChoreographyLoopType loopType) {
        this.loopType = loopType;
    }

    public List<CorrelationKey> getCorrelationKey() {
        if (correlationKey == null) {
            correlationKey = new ArrayList<CorrelationKey>();
        }
        return correlationKey;
    }

}
