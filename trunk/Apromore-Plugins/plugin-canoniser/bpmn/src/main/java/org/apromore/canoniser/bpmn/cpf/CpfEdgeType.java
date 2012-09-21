package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;

/**
 * CPF 0.6 edge with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfEdgeType extends EdgeType {

    /** This edge's source node. */
    protected NodeType source;

    /** This edge's target node. */
    protected NodeType target;

    /** @return this edge's source node */
    public NodeType getSourceRef() {
        return source;
    }

    /** @return this edge's target node */
    public NodeType getTargetRef() {
        return target;
    }

    /** @param node  the new source node */
    public void setSourceRef(NodeType node) {
        source = node;
    }

    /** @param node  the new target node */
    public void setTargetRef(NodeType node) {
        target = node;
    }
}
