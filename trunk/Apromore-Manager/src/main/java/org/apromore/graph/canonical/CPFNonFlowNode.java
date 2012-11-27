package org.apromore.graph.canonical;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * Base class for nodes that does not take part of the control flow.
 *
 * @author Tobias Hoppe
 */
public class CPFNonFlowNode extends Vertex implements INonFlowNode {

    /**
     * Create a new node that does not take part of the control flow.
     */
    public CPFNonFlowNode() {
        super();
    }

}
