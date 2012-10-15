package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.Map;

// Local packages
import org.apromore.cpf.EdgeType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
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
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param flowWithoutSourceRefMap  deferred source nodes
     * @param flowWithoutTargetRefMap  deferred target nodes
     */
    public BpmnSequenceFlow(final EdgeType edge,
                            final IdFactory bpmnIdFactory,
                            final Map<String, TBaseElement> idMap,
                            final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                            final Map<String, TSequenceFlow> flowWithoutTargetRefMap) {

        setId(bpmnIdFactory.newId(edge.getId()));

        // Deal with @conditionExpression
        if (edge.getConditionExpr() != null) {
            TExpression expression = new TExpression();
            expression.getContent().add(edge.getConditionExpr().getExpression());
            setConditionExpression(expression);
        }

        // Deal with @sourceId
        if (idMap.containsKey(edge.getSourceId())) {
            setSourceRef((TFlowNode) idMap.get(edge.getSourceId()));
        } else {
            assert !flowWithoutSourceRefMap.containsKey(this);
            flowWithoutSourceRefMap.put(edge.getSourceId(), this);
        }

        // Deal with @targetId
        if (idMap.containsKey(edge.getTargetId())) {
            setTargetRef((TFlowNode) idMap.get(edge.getTargetId()));
        } else {
            assert !flowWithoutTargetRefMap.containsKey(this);
            flowWithoutTargetRefMap.put(edge.getTargetId(), this);
        }
    }
}
