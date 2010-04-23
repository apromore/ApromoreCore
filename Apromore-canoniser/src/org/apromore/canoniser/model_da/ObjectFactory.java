
package org.apromore.canoniser.model_da;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.canoniser.model_da package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _StoreNativeCpfInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreNativeCpfInputMsg");
    private final static QName _StoreNativeCpfOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreNativeCpfOutputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.canoniser.model_da
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link StoreNativeCpfOutputMsgType }
     * 
     */
    public StoreNativeCpfOutputMsgType createStoreNativeCpfOutputMsgType() {
        return new StoreNativeCpfOutputMsgType();
    }

    /**
     * Create an instance of {@link StoreNativeCpfInputMsgType }
     * 
     */
    public StoreNativeCpfInputMsgType createStoreNativeCpfInputMsgType() {
        return new StoreNativeCpfInputMsgType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreNativeCpfInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_canoniser", name = "StoreNativeCpfInputMsg")
    public JAXBElement<StoreNativeCpfInputMsgType> createStoreNativeCpfInputMsg(StoreNativeCpfInputMsgType value) {
        return new JAXBElement<StoreNativeCpfInputMsgType>(_StoreNativeCpfInputMsg_QNAME, StoreNativeCpfInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreNativeCpfOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_canoniser", name = "StoreNativeCpfOutputMsg")
    public JAXBElement<StoreNativeCpfOutputMsgType> createStoreNativeCpfOutputMsg(StoreNativeCpfOutputMsgType value) {
        return new JAXBElement<StoreNativeCpfOutputMsgType>(_StoreNativeCpfOutputMsg_QNAME, StoreNativeCpfOutputMsgType.class, null, value);
    }

}
