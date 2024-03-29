/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package de.hpi.bpmn2_0.model.connector;

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
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
 **/

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
