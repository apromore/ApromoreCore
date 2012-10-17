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
     * @param initializer  BPMN document construction state
     * @param collaboration  element accumulating pool participants
     * @throws CanoniserException  if the process can't be constructed
     */
    public BpmnProcess(final NetType        net,
                       final Initializer    initializer,
                       final TCollaboration collaboration) throws CanoniserException {

            // Add the BPMN Process element
            final TProcess process = this;
            process.setId(initializer.bpmnIdFactory.newId(net.getId()));
            //getRootElement().add(factory.createProcess(process));

            // Add the BPMN Participant element
            TParticipant participant = new TParticipant();
            participant.setId(initializer.bpmnIdFactory.newId("participant"));
            participant.setName(process.getName());  // TODO - use an extension element for pool name if it exists
            participant.setProcessRef(new QName(BPMN_NS, process.getId()));
            collaboration.getParticipant().add(participant);

            // Populate the BPMN Process element
            ProcessWrapper.populateProcess(new ProcessWrapper(process), net, initializer);

            /*
            // If we haven't added the collaboration yet and this process is a pool, add the collaboration
            if (!getRootElement().contains(wrapperCollaboration)) {
                getRootElement().add(wrapperCollaboration);
            }
            */

    }
}
