package org.apromore.canoniser.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TCallActivity;

/**
 * BPMN Call Activity element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnCallActivity extends TCallActivity {

    /** No-arg constructor. */
    public BpmnCallActivity() {
        super();
    }

    /**
     * Construct a BPMN SubProcess corresponding to a CPF Task.
     *
     * @param task  a CPF Task with a defined subnet
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the subprocess can't be constructed
     */
    public BpmnCallActivity(final CpfTaskType task,
                            final Initializer initializer) throws CanoniserException {

        // Ensure that the CPF task has a called Element
        if (task.getCalledElement() == null) {
            throw new CanoniserException("CPF task " + task.getId() + " has no called element");
        }

        initializer.populateActivity(this, task);
        setCalledElement(task.getCalledElement());
    }
}
