package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.Map;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
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
     * @param cpf  the parent CPF model
     * @param factory  BPMN element factory
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param edgeMap  map from CPF @cpfId edge identifiers to BPMN ids
     * @param flowWithoutSourceRefMap  deferred source nodes
     * @param flowWithoutTargetRefMap  deferred target nodes
     * @param collaboration  element accumulating pool participants
     * @throws CanoniserException  if the subprocess can't be constructed
     */
    public BpmnCallActivity(final CpfTaskType               task,
                            final IdFactory                 bpmnIdFactory,
                            final Map<String, TBaseElement> idMap) throws CanoniserException {

        // Ensure that the CPF task has a called Element
        if (task.getCalledElement() == null) {
            throw new CanoniserException("CPF task " + task.getId() + " has no called element");
        }

        setId(bpmnIdFactory.newId(task.getId()));
        idMap.put(task.getId(), this);
        setCalledElement(task.getCalledElement());
    }
}
