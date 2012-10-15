package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.CPF_VERSION;
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * CPF 1.0 document root with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessType extends CanonicalProcessType {

    /** No-arg constructor. */
    public CpfCanonicalProcessType() {
        super();
    }

    /**
     * Construct a CPF document corresponding to a BPMN document.
     *
     * The resulting CPF requires its <code>uri</code> property to be set in order to be schema-legal.
     *
     * @param definitions  a BPMN document
     * @throws CanoniserException  if the CPF document can't be constructed
     */
    public CpfCanonicalProcessType(final BpmnDefinitions definitions) throws CanoniserException {
        super();

        // Generate identifiers scoped to this single CPF document
        final IdFactory cpfIdFactory = new IdFactory();

        // Map BPMN flow nodes to the CPF lanes containing them
        final Map<TFlowNode, TLane> laneMap = new HashMap<TFlowNode, TLane>();

        // Map BPMN flow nodes to CPF nodes
        final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap = new HashMap<TFlowNode, NodeType>();

        // Populate attributes
        setName(requiredName(definitions.getName()));
        setVersion(CPF_VERSION);

        // Each top-level BPMN Process becomes a CPF Net in the rootIDs list
        for (JAXBElement<? extends TRootElement> rootElement : definitions.getRootElement()) {
            if (rootElement.getValue() instanceof TProcess) {
                TProcess process = (TProcess) rootElement.getValue();
                new CpfNetType(this, cpfIdFactory, new ProcessWrapper(process), laneMap, bpmnFlowNodeToCpfNodeMap, null, definitions);
            }
        }
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param cpf  the CPF model to search
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    public NetType findNet(final String id) throws CanoniserException {

        for (final NetType net : getNet()) {
            if (id.equals(net.getId())) {
                return net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }
}
