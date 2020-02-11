
package de.hpi.bpmn2_0.model.connector;

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
