package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;

// Local packages
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.EdgeType;

/**
 * CPF 1.0 OR split routing with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfORSplitType extends ORSplitType implements CpfRoutingType {

    /** Secondary superclass. */
    private final CpfNodeTypeImpl super2;

    /** Constructor. */
    public CpfORSplitType() {
        super2 = new CpfNodeTypeImpl();
    }

    /** {@inheritDoc} */
    public Set<EdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<EdgeType> getOutgoingEdges() {
        return super2.getOutgoingEdges();
    }
}
