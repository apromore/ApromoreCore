package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Iterator;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataState;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;

/**
 * CPF 1.0 object with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectType extends ObjectType implements Attributed {

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String DATA_STORE = "dataStore";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String IS_COLLECTION = "isCollection";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String ORIGINAL_NAME = "originalName";

    // Convenience properties

    /** The parent Net this instance belongs to. */
    private CpfNetType net;

    // Constructors

    /** No-arg constructor. */
    public CpfObjectType() {
        super();
    }

    /**
     * Construct a CPF Object corresponding to a BPMN Data Object.
     *
     * @param dataObject  a BPMN Data Object
     * @param parent  the CPF Net this Object will belong to
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectType(final TDataObject dataObject,
                         final CpfNetType  parent,
                         final Initializer initializer) throws CanoniserException {

        //setConfigurable(false);  // Ignore configurability until we implement Configurable BPMN
        setIsCollection(dataObject.isIsCollection());

        setNet(parent);
        initializer.populateFlowElement(this, dataObject);
    }

    /**
     * Construct a CPF Object corresponding to a BPMN Data Store Reference.
     *
     * @param dataStoreReference a BPMN Data Store Reference
     * @param parent  the CPF Net this Object will belong to
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectType(final TDataStoreReference dataStoreReference,
                         final CpfNetType          parent,
                         final Initializer         initializer) throws CanoniserException {

        TDataState dataState = dataStoreReference.getDataState();
        QName dataStoreRef = dataStoreReference.getDataStoreRef();
        QName itemSubjectRef = dataStoreReference.getItemSubjectRef();

        //setConfigurable(false);  // Ignore until Configurable BPMN gets implemented
        setDataStore(dataStoreReference.getDataStoreRef());
        setIsCollection(false);

        setNet(parent);
        initializer.populateFlowElement(this, dataStoreReference);
    }

    // Accessors for CPF extension attributes

    public QName getDataStore() {
        for (TypeAttribute attribute : getAttribute()) {
            if (DATA_STORE.equals(attribute.getName())) {
                return QName.valueOf(attribute.getValue());
            }
        }
        return null;
    }

    public void setDataStore(final QName value) {

        // Remove any existing attribute
        Iterator<TypeAttribute> i = getAttribute().iterator();
        while (i.hasNext()) {
            if (DATA_STORE.equals(i.next().getName())) {
                i.remove();
            }
        }

        if (value != null) {
            // Create a new attribute for the original name
            TypeAttribute attribute = new ObjectFactory().createTypeAttribute();
            attribute.setName(DATA_STORE);
            attribute.setValue(value.toString());
            getAttribute().add(attribute);

            assert value.equals(getDataStore());
        } else {
            assert getDataStore() == null;
        }
    }

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

    /** @return the CPF Net this instance belongs to */
    public CpfNetType getNet() {
        return net;
    }

    /** @param newNet  the CPF Net this instance belongs to */
    public void setNet(final CpfNetType newNet) {
        net = newNet;
    }

    /**
     * Usually this is the same as the CPF name, but it can sometimes differ if the canoniser had to rename this Object to avoid
     * having the same name as other Objects in the same Net.
     *
     * @return the name of the corresponding flow element from the original BPMN document
     */
    public String getOriginalName() {

        // Check for an existing attribute with the right name
        for (TypeAttribute attribute : getAttribute()) {
            if (ORIGINAL_NAME.equals(attribute.getName())) {
                return attribute.getValue();
            }
        }

        // Didn't find an original name
        return null;
    }

    /**
     * Record the name of the corresponding element from the BPMN, if the CPF name needed to be renamed to guarantee uniqueness within the CPF Net.
     *
     * @param value  the name of the corresponding flow element from the original BPMN document
     */
    public void setOriginalName(final String value) {

        // Remove any existing attribute
        Iterator<TypeAttribute> i = getAttribute().iterator();
        while (i.hasNext()) {
            if (ORIGINAL_NAME.equals(i.next().getName())) {
                i.remove();
            }
        }

        if (value != null) {
            // Create a new attribute for the original name
            TypeAttribute attribute = new ObjectFactory().createTypeAttribute();
            attribute.setName(ORIGINAL_NAME);
            attribute.setValue(value);
            getAttribute().add(attribute);

            assert value.equals(getOriginalName());
        } else {
            assert getOriginalName() == null;
        }
    }
}
