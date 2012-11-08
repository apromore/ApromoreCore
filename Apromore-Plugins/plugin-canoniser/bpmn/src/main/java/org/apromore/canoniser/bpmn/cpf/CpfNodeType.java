package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;

// Local packages
import org.apromore.cpf.EdgeType;

/**
 * CPF 1.0 node with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public interface CpfNodeType extends Attributed {

    // Methods already present in CPF NodeType

    /**
     * @return whether this element is configurable
     * @see {@link NodeType#getConfigurable}
     */
    Boolean isConfigurable();

    /** @return the identifier for this element, unique within the CPF document */
    String getId();

    /** @return the presentation name of this element */
    String getName();

    // Added convenience methods

    /** @return every edge which has this node as its target */
    Set<EdgeType> getIncomingEdges();

    /** @return every edge which has this node as its source */
    Set<EdgeType> getOutgoingEdges();
}
