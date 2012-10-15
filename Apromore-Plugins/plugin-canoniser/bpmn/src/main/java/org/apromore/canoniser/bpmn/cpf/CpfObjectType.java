package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Iterator;

// Local packages
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TDataObject;

/**
 * CPF 1.0 object with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectType extends ObjectType {

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String IS_COLLECTION = "isCollection";

    // Constructors

    /** No-arg constructor. */
    public CpfObjectType() {
        super();
    }

    /**
     * Construct a CPF Task corresponding to a BPMN Call Activity.
     *
     * @param dataObject  a BPMN Data Object
     * @param initializer  global construction state
     */
    public CpfObjectType(final TDataObject dataObject, final Initializer initializer) {

        setConfigurable(false);  // BPMN doesn't have an obvious equivalent
        setIsCollection(dataObject.isIsCollection());

        initializer.populateFlowElement(this, dataObject);
    }

    // Accessors for CPF extension attributes

    /** @return whether this task has any attribute named {@link #IS_COLLECTION}. */
    public boolean isIsCollection() {
        for (TypeAttribute attribute : getAttribute()) {
            if (IS_COLLECTION.equals(attribute.getName())) {
                return true;
            }
        }
        return false;
    }

    /** @param value  whether this CPF task corresponds to a BPMN collection data object */
    public void setIsCollection(final Boolean value) {

        if (value) {
            // Check whether there's already an existing flag
            for (TypeAttribute attribute : getAttribute()) {
                if (IS_COLLECTION.equals(attribute.getName())) {
                    return;  // already flagged, so nothing needs to be changed
                }
            }

            // Didn't find an existing flag, so create and add one
            TypeAttribute attribute = new ObjectFactory().createTypeAttribute();
            attribute.setName(IS_COLLECTION);
            getAttribute().add(attribute);

            assert isIsCollection();

        } else {
            // Remove any existing flags
            Iterator<TypeAttribute> i = getAttribute().iterator();
            while (i.hasNext()) {
                if (IS_COLLECTION.equals(i.next().getName())) {
                    i.remove();
                }
            }

            assert !isIsCollection();
        }
    }
}
