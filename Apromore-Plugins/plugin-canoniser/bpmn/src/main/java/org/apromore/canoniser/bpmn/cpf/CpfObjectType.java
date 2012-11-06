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
public interface CpfObjectType extends Attributed {

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String DATA_STORE = "dataStore";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String IS_COLLECTION = "isCollection";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String ORIGINAL_NAME = "originalName";

    // CPF Object methods

    public String getId();
    public String getName();

    // Accessors for CPF extension attributes

    /** @return current value of the <code>dataStore</code> property */
    public QName getDataStore();

    /** @return the CPF Net this instance belongs to */
    public CpfNetType getNet();

    /**
     * Usually this is the same as the CPF name, but it can sometimes differ if the canoniser had to rename this Object to avoid
     * having the same name as other Objects in the same Net.
     *
     * @return the name of the corresponding flow element from the original BPMN document
     */
    public String getOriginalName();

    /**
     * Record the name of the corresponding element from the BPMN, if the CPF name needed to be renamed to guarantee uniqueness within the CPF Net.
     *
     * @param value  the name of the corresponding flow element from the original BPMN document
     */
    public void setOriginalName(final String value);
}
