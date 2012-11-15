package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;

/**
 * CPF 1.0 node with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfNodeTypeImpl {

    // Internal state

    /** Incoming edges. */
    private Set<CpfEdgeType> incomingEdges = new HashSet<CpfEdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<CpfEdgeType> outgoingEdges = new HashSet<CpfEdgeType>();  // TODO - diamond operator

    // Accessor methods

    /** @return every edge which has this node as its target */
    public Set<CpfEdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /** @return every edge which has this node as its source */
    public Set<CpfEdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }
}
