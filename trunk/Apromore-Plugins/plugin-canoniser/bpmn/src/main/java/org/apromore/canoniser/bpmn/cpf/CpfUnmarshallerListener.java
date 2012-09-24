package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

// Local classes
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;

/**
 * As CPF elements are unmarshalled, populate their convenience fields.
 *
 * The implemented convenience fields are:
 * <ul>
 * <li>{@link CpfNodeType#getIncomingEdges}</li>
 * <li>{@link CpfNodeType#getOutgoingEdges}</li>
 * </ul>
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfUnmarshallerListener extends Unmarshaller.Listener {

    /** Logger.  Named after the class. */
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    /** Map from node IDs to node objects */
    private final Map<String, NodeType> nodeMap = new HashMap<String, NodeType>();  // TODO - diamond operator

    /** {@inheritDoc} */
    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
        if (target instanceof EdgeType) {
            CpfEdgeType edge = (CpfEdgeType) target;
            logger.info("Unmarshal edge " + edge + " from " + edge.getSourceId() + " -> " + edge.getTargetId());

            edge.setSourceRef(nodeMap.get(edge.getSourceId()));
            ((CpfNodeType) edge.getSourceRef()).getOutgoingEdges().add(edge);

            edge.setTargetRef(nodeMap.get(edge.getTargetId()));
            ((CpfNodeType) edge.getTargetRef()).getIncomingEdges().add(edge);

        } else if (target instanceof NodeType) {
            NodeType node = (NodeType) target;

            nodeMap.put(node.getId(), node);

            if (target instanceof EventType) {
                CpfEventType event = (CpfEventType) target;
                logger.info("Unmarshal event " + event.getId());
            } else if (target instanceof CpfTaskType) {
                CpfTaskType task = (CpfTaskType) target;
                logger.info("Unmarshal task " + task.getId());
            } else if (target instanceof CpfXORSplitType) {
                CpfXORSplitType xorSplit = (CpfXORSplitType) target;
                logger.info("Unmarshal XOR split " + xorSplit.getId());
            } else {
                throw new Error(getClass() + " does not handle the NodeType " + target.getClass());
            }
        }
    }
}
