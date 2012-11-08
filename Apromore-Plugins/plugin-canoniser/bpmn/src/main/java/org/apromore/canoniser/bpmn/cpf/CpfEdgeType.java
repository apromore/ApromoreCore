package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * CPF 0.6 edge with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfEdgeType extends EdgeType implements Attributed {

    /** This edge's source node. */
    protected NodeType source;

    /** This edge's target node. */
    protected NodeType target;

    // Constructors

    /** No-arg constructor. */
    public CpfEdgeType() { }

    /**
     * Construct a CPF Edge from a BPMN Sequence Flow.
     *
     * @param sequenceFlow  a BPMN Sequence Flow
     * @param initializer  global construction state
     * @throws CanoniserException if the <code>sequenceFlow</code> has more than one condition expression
     */
    public CpfEdgeType(final TSequenceFlow sequenceFlow, final Initializer initializer) throws CanoniserException {
        initializer.populateFlowElement(this, sequenceFlow);

        if (sequenceFlow.getConditionExpression() != null) {

            // We don't handle multiple conditions
            if (sequenceFlow.getConditionExpression().getContent().size() != 1) {
                throw new CanoniserException("BPMN sequence flow " + sequenceFlow.getId() + " has " +
                                             sequenceFlow.getConditionExpression().getContent().size() +
                                             " conditions, which the canoniser doesn't implement");
            }

            ConditionExpressionType conditionExpr = new ConditionExpressionType();
            conditionExpr.setExpression(sequenceFlow.getConditionExpression().getContent().get(0).toString());
            setConditionExpr(conditionExpr);
        }

        initializer.defer(new Initialization() {
            public void initialize() throws CanoniserException {

                // handle source
                CpfNodeType sourceRef = (CpfNodeType) initializer.findElement(sequenceFlow.getSourceRef());
                setSourceId(sourceRef.getId());
                ((CpfNodeType) sourceRef).getOutgoingEdges().add(CpfEdgeType.this);

                // handle target
                CpfNodeType targetRef = (CpfNodeType) initializer.findElement(sequenceFlow.getTargetRef());
                setTargetId(targetRef.getId());
                ((CpfNodeType) targetRef).getIncomingEdges().add(CpfEdgeType.this);
            }
        });
    }

    // Accessors

    /** @return this edge's source node */
    public NodeType getSourceRef() {
        return source;
    }

    /** @return this edge's target node */
    public NodeType getTargetRef() {
        return target;
    }

    /** @param node  the new source node */
    public void setSourceRef(final NodeType node) {
        source = node;
    }

    /** @param node  the new target node */
    public void setTargetRef(final NodeType node) {
        target = node;
    }
}
