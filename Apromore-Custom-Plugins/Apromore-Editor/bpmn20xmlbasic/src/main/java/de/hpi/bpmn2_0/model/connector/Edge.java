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
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * Represents all types of edges in a BPMN 2.0 process.
 *
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 */
@XmlSeeAlso({
        SequenceFlow.class,
        Association.class,
        MessageFlow.class,
        ConversationLink.class,
        DataAssociation.class,
        DataInputAssociation.class,
        DataOutputAssociation.class
})
public abstract class Edge extends FlowElement {

    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    public FlowElement sourceRef;

    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    public FlowElement targetRef;

    public Edge() {
    }

    public Edge(Edge edge) {
        super(edge);

        this.setSourceRef(edge.getSourceRef());
        this.setTargetRef(edge.getTargetRef());
    }


    /**
     * Returns true if source and target node are of same pool
     */
    public boolean sourceAndTargetContainedInSamePool() {
        /* Ensure that both source and target are connected */
        if (this.getSourceRef() == null || this.getTargetRef() == null)
            return false;

        return !(this.getSourceRef() instanceof FlowNode &&
                this.getTargetRef() instanceof FlowNode &&
                ((FlowNode) this.getTargetRef()).getPool() != ((FlowNode) this.getSourceRef()).getPool());
    }

    public void acceptVisitor(Visitor v) {
        v.visitEdge(this);
    }

    /* Getters */

    /**
     * @return the sourceRef
     */
    public FlowElement getSourceRef() {
        return sourceRef;
    }

    /**
     * @return the targetRef
     */
    public FlowElement getTargetRef() {
        return targetRef;
    }

    /* Setters */

    /**
     * @param sourceRef the sourceRef to set
     */
    public void setSourceRef(FlowElement sourceRef) {
        this.sourceRef = sourceRef;
    }

    /**
     * @param targetRef the targetRef to set
     */
    public void setTargetRef(FlowElement targetRef) {
        this.targetRef = targetRef;
    }
}
