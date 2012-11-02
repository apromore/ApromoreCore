package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfObjectType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;

/**
 * BPMN Data Store Reference with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataStoreReference extends TDataStoreReference {

    /** No-arg constructor. */
    public BpmnDataStoreReference() {
        super();
    }

    /**
     * Construct a BPMN Data StoreReference corresponding to a CPF Object.
     *
     * @param cpfObject  a CPF object
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the data object can't be constructed
     */
    public BpmnDataStoreReference(final CpfObjectType cpfObject, final Initializer initializer) throws CanoniserException {

        initializer.populateFlowElement(this, cpfObject);

        QName dataStore = cpfObject.getDataStore();
        assert dataStore != null : "CPF Object " + cpfObject.getId() + " doesn't have a data store set";
        setDataStoreRef(dataStore);
    }
}
