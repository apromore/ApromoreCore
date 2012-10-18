package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

// Local classes
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;

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

    private final BpmnIDResolver bpmnIdResolver;

    /**
     * Constructor.
     *
     * @param idResolver  linked IDREF handler
     */
    BpmnUnmarshallerListener(final BpmnIDResolver idResolver) {
        bpmnIdResolver = idResolver;
    }

    /** {@inheritDoc} */
    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
        if (target instanceof TDefinitions) {
            TDefinitions definitions = (TDefinitions) target;
            bpmnIdResolver.setTargetNamespace(definitions.getTargetNamespace());
        } else if (target instanceof TFlowNode) {
            TFlowNode flowNode = (TFlowNode) target;

            // remove any existing incoming or outgoing lists, since they tend to be wrong anyway and the IDResolver will rebuild them for us
            flowNode.getIncoming().clear();
            flowNode.getOutgoing().clear();

            if (flowNode instanceof TGateway) {
                 // collate the set of gateways, since their directions need to be derived
                 bpmnIdResolver.addGateway((TGateway) target);
            }
        }
    }
}
