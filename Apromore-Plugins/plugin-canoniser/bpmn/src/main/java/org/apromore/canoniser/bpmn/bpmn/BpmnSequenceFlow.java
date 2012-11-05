package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
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

        // Defer dealing with @sourceRef and @targetRef until all elements have been created
        initializer.defer(new Initialization() {
            public void initialize() {
                setSourceRef((TFlowNode) initializer.findElement(edge.getSourceId()));
                setTargetRef((TFlowNode) initializer.findElement(edge.getTargetId()));
            }
        });
    }
}
