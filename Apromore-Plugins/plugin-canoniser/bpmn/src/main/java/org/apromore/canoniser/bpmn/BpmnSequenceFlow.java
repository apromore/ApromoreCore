package org.apromore.canoniser.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEdgeType;
import org.omg.spec.bpmn._20100524.model.TExpression;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * BPMN Sequence Flow element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnSequenceFlow extends TSequenceFlow {

    /** No-arg constructor. */
    public BpmnSequenceFlow() {
        super();
    }

    /**
     * Construct a BPMN Sequence Flow corresponding to a CPF Edge.
     *
     * @param edge  a CPF edge
     * @param initializer  BPMN document construction state
     */
    public BpmnSequenceFlow(final CpfEdgeType edge, final Initializer initializer) {

        initializer.populateFlowElement(this, edge);

        // Deal with @conditionExpression
        if (edge.getConditionExpr() != null) {
            TExpression expression = new TExpression();
            expression.getContent().add(edge.getConditionExpr().getExpression());
            setConditionExpression(expression);
        }

        // Deal with @sourceId
        if (initializer.containsElement(edge.getSourceId())) {
            setSourceRef((TFlowNode) initializer.getElement(edge.getSourceId()));
        } else {
            initializer.recordFlowWithoutSourceRef(this, edge.getSourceId());
        }

        // Deal with @targetId
        if (initializer.containsElement(edge.getTargetId())) {
            setTargetRef((TFlowNode) initializer.getElement(edge.getTargetId()));
        } else {
            initializer.recordFlowWithoutTargetRef(this, edge.getTargetId());
        }
    }
}
