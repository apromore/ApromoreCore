package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;

// Local packages
import org.apromore.cpf.EdgeType;

/**
 * CPF 0.6 node with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public interface CpfNodeType {

    /**
     * @return every edge which has this node as its target
     */
    Set<EdgeType> getIncomingEdges();

    /**
     * @return every edge which has this node as its source
     */
    Set<EdgeType> getOutgoingEdges();
}
