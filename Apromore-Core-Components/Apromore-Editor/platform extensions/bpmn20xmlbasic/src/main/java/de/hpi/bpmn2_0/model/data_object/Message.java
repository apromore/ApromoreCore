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

package de.hpi.bpmn2_0.model.data_object;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.misc.ItemDefinition;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tMessage complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tMessage">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="structureRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMessage")
public class Message
        extends FlowNode {

    @XmlAttribute
    @XmlIDREF
    protected ItemDefinition structureRef;

    @XmlTransient
    private boolean isInitiating;

    public void acceptVisitor(Visitor v) {
        v.visitMessage(this);
    }

    /**
     * Retrieves the association edge connecting the message object with an
     * choreography activity or participant.
     *
     * @return
     */
    public Association getDataConnectingAssociation() {
        List<Association> associationList = new ArrayList<Association>();

        for (FlowElement element : this.getIncoming()) {
            if (element instanceof Association)
                associationList.add((Association) element);
        }

        for (FlowElement element : this.getOutgoing()) {
            if (element instanceof Association)
                associationList.add((Association) element);
        }

        for (Association msgAssociation : associationList) {
            if (msgAssociation.getSourceRef() instanceof ChoreographyActivity
                    || msgAssociation.getSourceRef() instanceof Participant
                    || msgAssociation.getTargetRef() instanceof ChoreographyActivity
                    || msgAssociation.getTargetRef() instanceof Participant) {

                return msgAssociation;
            }
        }

        return null;
    }

    /* Getter & Setter */

    /**
     * Gets the value of the structureRef property.
     *
     * @return possible object is
     *         {@link ItemDefinition }
     */
    public ItemDefinition getStructureRef() {
        return structureRef;
    }

    /**
     * Sets the value of the structureRef property.
     *
     * @param value allowed object is
     *              {@link ItemDefinition }
     */
    public void setStructureRef(ItemDefinition value) {
        this.structureRef = value;
    }

    /**
     * @return the isInitiating
     */
    public boolean isInitiating() {
        return isInitiating;
    }

    /**
     * @param isInitiating the isInitiating to set
     */
    public void setInitiating(boolean isInitiating) {
        this.isInitiating = isInitiating;
    }

}
