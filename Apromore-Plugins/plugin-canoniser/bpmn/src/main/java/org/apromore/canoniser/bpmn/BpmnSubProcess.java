package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.Map;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.*;
import org.omg.spec.bpmn._20100524.model.*;

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
    public BpmnSubProcess(final CpfTaskType                task,
                          final CanonicalProcessType       cpf,
                          final BpmnObjectFactory          factory,
                          final IdFactory                  bpmnIdFactory,
                          final Map<String, TBaseElement>  idMap,
                          final Map<String, TSequenceFlow> edgeMap,
                          final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                          final Map<String, TSequenceFlow> flowWithoutTargetRefMap) throws CanoniserException {

        // Ensure that the CPF task has its subnet identifier set
        if (task.getSubnetId() == null) {
            throw new CanoniserException("Can't create a BPMN SubProcess from the CPF Task " + task.getId() + " which has no subnet");
        }

        setId(bpmnIdFactory.newId(task.getId()));
        setTriggeredByEvent(task.isTriggeredByEvent());
        idMap.put(task.getId(), this);
        ProcessWrapper.populateProcess(new ProcessWrapper(this, "subprocess"),
                                       ((CpfCanonicalProcessType) cpf).findNet(task.getSubnetId()),
                                       cpf, factory, bpmnIdFactory, idMap, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
    }
}
