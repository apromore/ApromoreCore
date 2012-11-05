package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TProcess;

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
    public BpmnProcess(final CpfNetType     net,
                       final Initializer    initializer,
                       final TCollaboration collaboration) throws CanoniserException {

        // Add the BPMN Process element
        initializer.populateBaseElement(this, net);

        // Add the BPMN Participant element
        collaboration.getParticipant().add(new BpmnParticipant(this, initializer));

        // Populate the BPMN Process element
        initializer.populateProcess(new ProcessWrapper(this), net);
    }
}
