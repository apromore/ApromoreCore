package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.exception.CanoniserException;
import static  org.apromore.cpf.InputOutputType.INPUT;
import org.omg.spec.bpmn._20100524.model.TActivity;
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
     * @param parent  the BPMN activity this instance belongs to
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the association can't be constructed
     */
    public BpmnDataInputAssociation(final CpfObjectRefType objectRef,
                                    final TActivity        parent,
                                    final Initializer      initializer) throws CanoniserException {
        assert INPUT.equals(objectRef.getType()) : objectRef.getId() + " is not typed as an input";
        initializer.populateBaseElement(this, objectRef);

        // There's a bug in JAXB that makes it impossible to directly add elements to collections of IDREFs, like sourceRef
        // As a workaround, I put the id of the sourceRef into an attribute and fix it later using XSLT
        getOtherAttributes().put(new QName("workaround"), initializer.getElement(objectRef.getObjectId()).getId());

        setTargetRef(parent);
    }
}
