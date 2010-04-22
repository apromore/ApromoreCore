
package org.apromore.canoniser.model_manager;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.canoniser.model_manager package. 
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

    private final static QName _CanoniseProcessInputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "CanoniseProcessInputMsg");
    private final static QName _CanoniseProcessOutputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "CanoniseProcessOutputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.canoniser.model_manager
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CanoniseProcessOutputMsgType }
     * 
     */
    public CanoniseProcessOutputMsgType createCanoniseProcessOutputMsgType() {
        return new CanoniseProcessOutputMsgType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link CanoniseProcessInputMsgType }
     * 
     */
    public CanoniseProcessInputMsgType createCanoniseProcessInputMsgType() {
        return new CanoniseProcessInputMsgType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CanoniseProcessInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "CanoniseProcessInputMsg")
    public JAXBElement<CanoniseProcessInputMsgType> createCanoniseProcessInputMsg(CanoniseProcessInputMsgType value) {
        return new JAXBElement<CanoniseProcessInputMsgType>(_CanoniseProcessInputMsg_QNAME, CanoniseProcessInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CanoniseProcessOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "CanoniseProcessOutputMsg")
    public JAXBElement<CanoniseProcessOutputMsgType> createCanoniseProcessOutputMsg(CanoniseProcessOutputMsgType value) {
        return new JAXBElement<CanoniseProcessOutputMsgType>(_CanoniseProcessOutputMsg_QNAME, CanoniseProcessOutputMsgType.class, null, value);
    }

}
