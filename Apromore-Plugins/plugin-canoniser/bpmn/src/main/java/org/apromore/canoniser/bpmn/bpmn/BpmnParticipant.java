package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;

/**
 * BPMN Participant element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnParticipant extends TParticipant {

    /** No-arg constructor. */
    public BpmnParticipant() {
        super();
    }

    /**
     * Construct a BPMN Participant corresponding to (and referencing) a BPMN Process.
     *
     * @param process  the BPMN Process this participant references
     * @param initializer  BPMN document construction state
     */
    public BpmnParticipant(final TProcess process,
                           final Initializer initializer) {

        setId(initializer.newId(process.getId() + "_pool"));
        setName(process.getName());  // TODO - use an extension element for pool name if it exists
        setProcessRef(new QName(initializer.getTargetNamespace(), process.getId()));
    }
}
