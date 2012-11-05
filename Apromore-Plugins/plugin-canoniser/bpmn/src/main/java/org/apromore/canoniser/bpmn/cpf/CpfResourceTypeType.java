package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Collections;
import java.util.List;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ResourceTypeType;
import org.omg.spec.bpmn._20100524.model.TParticipant;

/**
 * CPF 1.0 resource type with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfResourceTypeType extends ResourceTypeType implements Attributed {

    /** No-arg constructor. */
    public CpfResourceTypeType() { }

    /**
     * Construct a CPF ResourceType corresponding to a BPMN Participant.
     *
     * @param participant  a BPMN Participant
     * @param initializer  global document construction state
     * @throws CanoniserException if the resource type can't be constructed
     */
    public CpfResourceTypeType(final TParticipant participant, final Initializer initializer) throws CanoniserException {
        //initializer.populateBaseElement(this, participant);
        setId(initializer.newId(participant.getId()));

        // Handle @name
        setName(requiredName(participant.getName()));
        initializer.addResourceType(this);
    }

    /**
     * @return every other resource type which has this one as a specialization
     */
    public List<CpfResourceTypeType> getGeneralizationRefs() {
        return Collections.emptyList();
    }
}
