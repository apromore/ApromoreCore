package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfObjectType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TDataObject;

/**
 * BPMN Data Object with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataObject extends TDataObject {

    /** No-arg constructor. */
    public BpmnDataObject() { }

    /**
     * Construct a BPMN Data Object corresponding to a CPF Object.
     *
     * @param cpfObject  a CPF object
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the data object can't be constructed
     */
    public BpmnDataObject(final CpfObjectType cpfObject, final Initializer initializer) throws CanoniserException {

        initializer.populateFlowElement(this, cpfObject);
    }
}
