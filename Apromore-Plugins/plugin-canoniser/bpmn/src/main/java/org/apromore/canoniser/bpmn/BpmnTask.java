package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.Map;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TTask;

/**
 * BPMN Task element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnTask extends TTask {

    /** No-arg constructor. */
    public BpmnTask() {
        super();
    }

    /**
     * Construct a BPMN Task corresponding to a CPF Task.
     *
     * @param task  a CPF Task with a defined subnet
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the subprocess can't be constructed
     */
    public BpmnTask(final CpfTaskType task,
                    final Initializer initializer) throws CanoniserException {

        initializer.populateBaseElement(this, task);
    }
}
