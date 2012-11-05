package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.cpf.ResourceTypeRefType;
import org.omg.spec.bpmn._20100524.model.TLane;

/**
 * CPF 1.0 resource type reference with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfResourceTypeRefType extends ResourceTypeRefType implements Attributed {

    /** No-arg constructor. */
    public CpfResourceTypeRefType() { }

    /**
     * Construct a CPF Resource reference corresponding to a BPMN Lane.
     *
     * @param lane  the BPMN Lane
     * @param initializer  CPF document construction state
     */
    public CpfResourceTypeRefType(final TLane lane, final Initializer initializer) {
        setId(initializer.newId(null));
        //setOptional(false);  // redundant, since false is the default
        setQualifier(null);
        setResourceTypeId(lane.getId());
    }
}
