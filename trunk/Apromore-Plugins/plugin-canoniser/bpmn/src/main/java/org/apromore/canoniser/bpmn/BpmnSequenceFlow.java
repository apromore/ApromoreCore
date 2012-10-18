package org.apromore.canoniser.bpmn;

// Local packages
import org.apromore.cpf.EdgeType;
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
    public BpmnSequenceFlow(final EdgeType edge, final Initializer initializer) {

        setId(initializer.bpmnIdFactory.newId(edge.getId()));

        // Deal with @conditionExpression
        if (edge.getConditionExpr() != null) {
            TExpression expression = new TExpression();
            expression.getContent().add(edge.getConditionExpr().getExpression());
            setConditionExpression(expression);
        }

        // Deal with @sourceId
        if (initializer.idMap.containsKey(edge.getSourceId())) {
            setSourceRef((TFlowNode) initializer.idMap.get(edge.getSourceId()));
        } else {
            assert !initializer.flowWithoutSourceRefMap.containsKey(this);
            initializer.flowWithoutSourceRefMap.put(edge.getSourceId(), this);
        }

        // Deal with @targetId
        if (initializer.idMap.containsKey(edge.getTargetId())) {
            setTargetRef((TFlowNode) initializer.idMap.get(edge.getTargetId()));
        } else {
            assert !initializer.flowWithoutTargetRefMap.containsKey(this);
            initializer.flowWithoutTargetRefMap.put(edge.getTargetId(), this);
        }
    }
}
