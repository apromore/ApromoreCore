package org.apromore.canoniser.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.exception.CanoniserException;
import static  org.apromore.cpf.InputOutputType.INPUT;
import org.omg.spec.bpmn._20100524.model.TDataInputAssociation;

/**
 * BPMN Data Input Association with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataInputAssociation extends TDataInputAssociation {

    /** No-arg constructor. */
    public BpmnDataInputAssociation() {
        super();
    }

    /**
     * Construct a BPMN Data Input Association corresponding to a CPF ObjectRef.
     *
     * @param objectRef  a CPF Object Reference of type {@link #INPUT}
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the association can't be constructed
     */
    public BpmnDataInputAssociation(final CpfObjectRefType objectRef, final Initializer initializer) throws CanoniserException {
        assert INPUT.equals(objectRef.getType()) : objectRef.getId() + " is not typed as an input";
        initializer.populateBaseElement(this, objectRef);
    }
}
