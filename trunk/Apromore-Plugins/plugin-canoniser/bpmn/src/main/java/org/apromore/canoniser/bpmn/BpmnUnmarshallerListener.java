package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

// Local classes
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * As BPMN elements are unmarshalled, populate their convenience fields.
 *
 * The implemented convenience fields are:
 * <ul>
 * <li>{@link TGateway#getGatewayDirection}</li>
 * </ul>
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnUnmarshallerListener extends Unmarshaller.Listener {

    /** Logger.  Named after the class. */
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    /** {@inheritDoc} */
    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
        if (target instanceof TSequenceFlow) {
            TSequenceFlow sequenceFlow = (TSequenceFlow) target;
            //logger.info("Unmarshal sequence flow " + sequenceFlow + " from " + sequenceFlow.getSourceRef() + " -> " + sequenceFlow.getTargetRef());

            /*
            edge.setSourceRef(nodeMap.get(edge.getSourceId()));
            ((CpfNodeType) edge.getSourceRef()).getOutgoingEdges().add(edge);

            edge.setTargetRef(nodeMap.get(edge.getTargetId()));
            ((CpfNodeType) edge.getTargetRef()).getIncomingEdges().add(edge);
            */
        }
    }
}
