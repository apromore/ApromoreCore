package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.namespace.QName;

/**
 * CPF 1.0 object with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface CpfObjectType extends Attributed {

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    String DATA_STORE = "dataStore";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    String IS_COLLECTION = "isCollection";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    String ORIGINAL_NAME = "originalName";

    // CPF Object methods

    /**
     * @return unique element identifier
     * @see {@link ObjectType#getId}
     */
    String getId();

    /**
     * @return presentation name
     * @see {@link ObjectType#getName}
     */
    String getName();

    // Accessors for CPF extension attributes

    /** @return current value of the <code>dataStore</code> property */
    QName getDataStore();

    /** @param value  new value for the dataStore property */
    void setDataStore(final QName value);

    /** @return whether this task has any attribute named {@link #IS_COLLECTION}. */
    boolean isIsCollection();

    /** @param value  whether this CPF task corresponds to a BPMN collection data object */
    void setIsCollection(final Boolean value);

    /** @return the CPF Net this instance belongs to */
    CpfNetType getNet();

    /** @param newNet  the CPF Net this instance belongs to */
    void setNet(final CpfNetType newNet);

    /**
     * Usually this is the same as the CPF name, but it can sometimes differ if the canoniser had to rename this Object to avoid
     * having the same name as other Objects in the same Net.
     *
     * @return the name of the corresponding flow element from the original BPMN document
     */
    String getOriginalName();

    /**
     * Record the name of the corresponding element from the BPMN, if the CPF name needed to be renamed to guarantee uniqueness within the CPF Net.
     *
     * @param value  the name of the corresponding flow element from the original BPMN document
     */
    void setOriginalName(final String value);
}
