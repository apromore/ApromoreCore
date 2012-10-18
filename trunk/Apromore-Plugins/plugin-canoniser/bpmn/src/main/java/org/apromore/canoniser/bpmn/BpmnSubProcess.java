package org.apromore.canoniser.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TSubProcess;

/**
 * BPMN SubProcess element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnSubProcess extends TSubProcess {

    /** No-arg constructor. */
    public BpmnSubProcess() {
        super();
    }

    /**
     * Construct a BPMN SubProcess corresponding to a CPF Task.
     *
     * @param task  a CPF Task with a defined subnet
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the subprocess can't be constructed
     */
    public BpmnSubProcess(final CpfTaskType task,
                          final Initializer initializer) throws CanoniserException {

        // Ensure that the CPF task has its subnet identifier set
        if (task.getSubnetId() == null) {
            throw new CanoniserException("Can't create a BPMN SubProcess from the CPF Task " + task.getId() + " which has no subnet");
        }

        initializer.populateBaseElement(this, task);
        setTriggeredByEvent(task.isTriggeredByEvent());
        initializer.populateProcess(new ProcessWrapper(this, "subprocess"),
                                    ((CpfCanonicalProcessType) initializer.cpf).findNet(task.getSubnetId()));
    }
}
