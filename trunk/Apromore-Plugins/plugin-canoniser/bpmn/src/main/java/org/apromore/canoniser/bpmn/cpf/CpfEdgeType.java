package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CPF 0.6 edge with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfEdgeType extends EdgeType implements Attributed {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpfEdgeType.class);

    /** {@link TypeAttribute} name for the name of the original BPMN sequence flow. */
    private static final String  NAME = "name";

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

        // Handle conditionExpression
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

        // Handle @name
        setName(sequenceFlow.getName());

        initializer.defer(new Initialization() {
            public void initialize() throws CanoniserException {
                // handle source
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Flow Sequence source: " + sequenceFlow.getSourceRef());
                }
                CpfNodeType sourceRef = (CpfNodeType) initializer.findElement(sequenceFlow.getSourceRef());
                setSourceId(sourceRef.getId());
                sourceRef.getOutgoingEdges().add(CpfEdgeType.this);

                // handle target
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Flow Sequence target: " + sequenceFlow.getTargetRef());
                }
                CpfNodeType targetRef = (CpfNodeType) initializer.findElement(sequenceFlow.getTargetRef());
                setTargetId(targetRef.getId());
                targetRef.getIncomingEdges().add(CpfEdgeType.this);
            }
        });
    }

    // Accessors

    /** @return this edge's original BPMN name */
    public String getName() {
        return ExtensionUtils.getString(getAttribute(), NAME);
    }

    /** @param name the name of the original BPMN sequence flow */
    public void setName(final String name) {
        ExtensionUtils.setString(getAttribute(), NAME, name);
    }

    /** @return this edge's source node */
    public NodeType getSourceRef() {
        return source;
    }

    /** @param node  the new source node */
    public void setSourceRef(final NodeType node) {
        source = node;
    }

    /** @param node  the new target node */
    public void setTargetRef(final NodeType node) {
        target = node;
    }

    /** @return this edge's target node */
    public NodeType getTargetRef() {
        return target;
    }
}
