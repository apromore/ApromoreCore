package org.apromore.canoniser.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.exception.CanoniserException;
import static  org.apromore.cpf.InputOutputType.OUTPUT;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;

/**
 * BPMN Data Output Association with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataOutputAssociation extends TDataOutputAssociation {

    /** No-arg constructor. */
    public BpmnDataOutputAssociation() {
        super();
    }

    /**
     * Construct a BPMN Data Output Association corresponding to a CPF ObjectRef.
     *
     * @param objectRef  a CPF Object Reference of type {@link #OUTPUT}
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the association can't be constructed
     */
    public BpmnDataOutputAssociation(final CpfObjectRefType objectRef, final Initializer initializer) throws CanoniserException {
        assert OUTPUT.equals(objectRef.getType()) : objectRef.getId() + " is not typed as an output";
        initializer.populateBaseElement(this, objectRef);
    }
}
