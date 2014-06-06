package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.InputOutputType.INPUT;
import static org.apromore.cpf.InputOutputType.OUTPUT;
import org.apromore.cpf.ObjectRefType;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TDataInputAssociation;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TFlowElement;

/**
 * CPF 1.0 object reference with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectRefType extends ObjectRefType implements Attributed {

    // Constructors

    /** No-arg constructor. */
    public CpfObjectRefType() { }

    /**
     * Construct a CPF ObjectRef corresponding to a BPMN DataInputAssociation.
     *
     * @param association  a BPMN DataInputAssociation
     * @param parent  the BPMN Activity containing the <code>association</code>
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectRefType(final TDataInputAssociation association,
                            final TActivity             parent,
                            final Initializer           initializer) throws CanoniserException {

        initializer.populateBaseElement(this, association);

        setType(INPUT);

        initializer.defer(new Initialization() {
            @Override
            public void initialize() throws CanoniserException {

                // A single source is the only thing that makes sense, surely?
                if (association.getSourceRef().size() != 1) {
                    throw new CanoniserException("BPMN data input association " + association.getId() + " has " +
                                                 association.getSourceRef().size() + " sources");
                }

                // Handle objectId
                CpfObjectType object = (CpfObjectType) initializer.findElement((TFlowElement) association.getSourceRef().get(0).getValue());
                if (object == null) {
                    throw new CanoniserException("DataInputAssociation " + association.getId() + " didn't have an identifiable source object");
                }
                CpfObjectRefType.this.setObjectId(object.getId());
            }
        });
    }

    /**
     * Construct a CPF ObjectRef corresponding to a BPMN DataOutputAssociation.
     *
     * @param association  a BPMN DataOutputAssociation
     * @param parent  the BPMN Activity containing the <code>association</code>
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectRefType(final TDataOutputAssociation association,
                            final TActivity              parent,
                            final Initializer            initializer) throws CanoniserException {

        initializer.populateBaseElement(this, association);

        setType(OUTPUT);

        initializer.defer(new Initialization() {
            @Override
            public void initialize() {

                // Handle objectId
                CpfObjectType object = (CpfObjectType) initializer.findElement(association.getTargetRef());
                CpfObjectRefType.this.setObjectId(object.getId());
            }
        });
    }
}

