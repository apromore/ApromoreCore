
package org.apromore.manager.model_canoniser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.manager.model_canoniser package. 
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
    private final static QName _CanoniseVersionOutputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "CanoniseVersionOutputMsg");
    private final static QName _CanoniseProcessOutputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "CanoniseProcessOutputMsg");
    private final static QName _CanoniseVersionInputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "CanoniseVersionInputMsg");
    private final static QName _DeCanoniseProcessInputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "DeCanoniseProcessInputMsg");
    private final static QName _DeCanoniseProcessOutputMsg_QNAME = new QName("http://www.apromore.org/canoniser/model_manager", "DeCanoniseProcessOutputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.manager.model_canoniser
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeCanoniseProcessOutputMsgType }
     * 
     */
    public DeCanoniseProcessOutputMsgType createDeCanoniseProcessOutputMsgType() {
        return new DeCanoniseProcessOutputMsgType();
    }

    /**
     * Create an instance of {@link CanoniseVersionOutputMsgType }
     * 
     */
    public CanoniseVersionOutputMsgType createCanoniseVersionOutputMsgType() {
        return new CanoniseVersionOutputMsgType();
    }

    /**
     * Create an instance of {@link CanoniseProcessOutputMsgType }
     * 
     */
    public CanoniseProcessOutputMsgType createCanoniseProcessOutputMsgType() {
        return new CanoniseProcessOutputMsgType();
    }

    /**
     * Create an instance of {@link VersionSummaryType }
     * 
     */
    public VersionSummaryType createVersionSummaryType() {
        return new VersionSummaryType();
    }

    /**
     * Create an instance of {@link CanoniseProcessInputMsgType }
     * 
     */
    public CanoniseProcessInputMsgType createCanoniseProcessInputMsgType() {
        return new CanoniseProcessInputMsgType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link DeCanoniseProcessInputMsgType }
     * 
     */
    public DeCanoniseProcessInputMsgType createDeCanoniseProcessInputMsgType() {
        return new DeCanoniseProcessInputMsgType();
    }

    /**
     * Create an instance of {@link CanoniseVersionInputMsgType }
     * 
     */
    public CanoniseVersionInputMsgType createCanoniseVersionInputMsgType() {
        return new CanoniseVersionInputMsgType();
    }

    /**
     * Create an instance of {@link ProcessSummaryType }
     * 
     */
    public ProcessSummaryType createProcessSummaryType() {
        return new ProcessSummaryType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CanoniseVersionOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "CanoniseVersionOutputMsg")
    public JAXBElement<CanoniseVersionOutputMsgType> createCanoniseVersionOutputMsg(CanoniseVersionOutputMsgType value) {
        return new JAXBElement<CanoniseVersionOutputMsgType>(_CanoniseVersionOutputMsg_QNAME, CanoniseVersionOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CanoniseProcessOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "CanoniseProcessOutputMsg")
    public JAXBElement<CanoniseProcessOutputMsgType> createCanoniseProcessOutputMsg(CanoniseProcessOutputMsgType value) {
        return new JAXBElement<CanoniseProcessOutputMsgType>(_CanoniseProcessOutputMsg_QNAME, CanoniseProcessOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CanoniseVersionInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "CanoniseVersionInputMsg")
    public JAXBElement<CanoniseVersionInputMsgType> createCanoniseVersionInputMsg(CanoniseVersionInputMsgType value) {
        return new JAXBElement<CanoniseVersionInputMsgType>(_CanoniseVersionInputMsg_QNAME, CanoniseVersionInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeCanoniseProcessInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "DeCanoniseProcessInputMsg")
    public JAXBElement<DeCanoniseProcessInputMsgType> createDeCanoniseProcessInputMsg(DeCanoniseProcessInputMsgType value) {
        return new JAXBElement<DeCanoniseProcessInputMsgType>(_DeCanoniseProcessInputMsg_QNAME, DeCanoniseProcessInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeCanoniseProcessOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/canoniser/model_manager", name = "DeCanoniseProcessOutputMsg")
    public JAXBElement<DeCanoniseProcessOutputMsgType> createDeCanoniseProcessOutputMsg(DeCanoniseProcessOutputMsgType value) {
        return new JAXBElement<DeCanoniseProcessOutputMsgType>(_DeCanoniseProcessOutputMsg_QNAME, DeCanoniseProcessOutputMsgType.class, null, value);
    }

}
