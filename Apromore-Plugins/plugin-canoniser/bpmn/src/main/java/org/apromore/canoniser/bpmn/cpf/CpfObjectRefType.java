package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ObjectRefType;
import org.omg.spec.bpmn._20100524.model.TAssociation;

/**
 * CPF 1.0 object reference with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectRefType extends ObjectRefType implements Attributed {

    // Constructors

    /** No-arg constructor. */
    public CpfObjectRefType() {
        super();
    }

    /**
     * Construct a CPF ObjectRef corresponding to a BPMN Association.
     *
     * @param association  a BPMN Association
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectRefType(final TAssociation association,
                            final Initializer  initializer) throws CanoniserException {

        initializer.populateBaseElement(this, association);
    }
}
