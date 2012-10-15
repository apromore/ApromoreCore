package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.Map;
import javax.xml.namespace.QName;

// Local packages
import static org.apromore.canoniser.bpmn.BpmnDefinitions.BPMN_NS;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TExpression;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * BPMN Process element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnProcess extends TProcess {

    /** No-arg constructor. */
    public BpmnProcess() {
        super();
    }

    /**
     * Construct a BPMN Process corresponding to a CPF Net.
     *
     * This constructor is only applicable to root processes.
     *
     * @param net  a CPF Net
     * @param cpf  the parent CPF model
     * @param factory  BPMN element factory
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param edgeMap  map from CPF @cpfId edge identifiers to BPMN ids
     * @param flowWithoutSourceRefMap  deferred source nodes
     * @param flowWithoutTargetRefMap  deferred target nodes
     * @param collaboration  element accumulating pool participants
     * @throws CanoniserException  if the process can't be constructed
     */
    public BpmnProcess(final NetType                    net,
                       final CanonicalProcessType       cpf,
                       final BpmnObjectFactory          factory,
                       final IdFactory                  bpmnIdFactory,
                       final Map<String, TBaseElement>  idMap,
                       final Map<String, TSequenceFlow> edgeMap,
                       final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                       final Map<String, TSequenceFlow> flowWithoutTargetRefMap,
                       final TCollaboration             collaboration) throws CanoniserException {

            // Add the BPMN Process element
            final TProcess process = this;
            process.setId(bpmnIdFactory.newId(net.getId()));
            //getRootElement().add(factory.createProcess(process));

            // Add the BPMN Participant element
            TParticipant participant = new TParticipant();
            participant.setId(bpmnIdFactory.newId("participant"));
            participant.setName(process.getName());  // TODO - use an extension element for pool name if it exists
            participant.setProcessRef(new QName(BPMN_NS, process.getId()));
            collaboration.getParticipant().add(participant);

            // Populate the BPMN Process element
            ProcessWrapper.populateProcess(new ProcessWrapper(process),
                                           net, cpf, factory, bpmnIdFactory, idMap, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);

            /*
            // If we haven't added the collaboration yet and this process is a pool, add the collaboration
            if (!getRootElement().contains(wrapperCollaboration)) {
                getRootElement().add(wrapperCollaboration);
            }
            */

    }
}
