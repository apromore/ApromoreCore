package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;
import javax.xml.bind.JAXBElement;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.XORSplitType;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TFlowNode;

/**
 * CPF 1.0 XOR split routing with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfXORSplitType extends XORSplitType implements CpfRoutingType {

    /** Secondary superclass. */
    private final CpfNodeTypeImpl super2;

    /** Constructor. */
    public CpfXORSplitType() {
        super2 = new CpfNodeTypeImpl();
    }

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getOutgoingEdges() {
        return super2.getOutgoingEdges();
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        TExclusiveGateway gateway = new TExclusiveGateway();
        initializer.populateGateway(gateway, this);
        return initializer.getFactory().createExclusiveGateway(gateway);
    }
}
