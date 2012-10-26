package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;

// Local packages
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.ANDSplitType;

/**
 * CPF 0.6 task with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfANDSplitType extends ANDSplitType implements CpfNodeType {

    /** Incoming edges. */
    private Set<EdgeType> incomingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<EdgeType> outgoingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /**
     * @return every edge which has this node as its target
     */
    public Set<EdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * @return every edge which has this node as its source
     */
    public Set<EdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }
}
