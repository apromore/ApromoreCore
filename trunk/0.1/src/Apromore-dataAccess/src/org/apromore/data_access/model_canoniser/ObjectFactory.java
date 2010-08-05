
package org.apromore.data_access.model_canoniser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.data_access.model_canoniser package. 
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

    private final static QName _StoreNativeInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreNativeInputMsg");
    private final static QName _StoreVersionOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreVersionOutputMsg");
    private final static QName _StoreNativeOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreNativeOutputMsg");
    private final static QName _StoreNativeCpfInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreNativeCpfInputMsg");
    private final static QName _StoreNativeCpfOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreNativeCpfOutputMsg");
    private final static QName _StoreVersionInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_canoniser", "StoreVersionInputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.data_access.model_canoniser
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StoreNativeCpfInputMsgType }
     * 
     */
    public StoreNativeCpfInputMsgType createStoreNativeCpfInputMsgType() {
        return new StoreNativeCpfInputMsgType();
    }

    /**
     * Create an instance of {@link StoreNativeInputMsgType }
     * 
     */
    public StoreNativeInputMsgType createStoreNativeInputMsgType() {
        return new StoreNativeInputMsgType();
    }

    /**
     * Create an instance of {@link ProcessSummaryType }
     * 
     */
    public ProcessSummaryType createProcessSummaryType() {
        return new ProcessSummaryType();
    }

    /**
     * Create an instance of {@link StoreNativeCpfOutputMsgType }
     * 
     */
    public StoreNativeCpfOutputMsgType createStoreNativeCpfOutputMsgType() {
        return new StoreNativeCpfOutputMsgType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link StoreNativeOutputMsgType }
     * 
     */
    public StoreNativeOutputMsgType createStoreNativeOutputMsgType() {
        return new StoreNativeOutputMsgType();
    }

    /**
     * Create an instance of {@link VersionSummaryType }
     * 
     */
    public VersionSummaryType createVersionSummaryType() {
        return new VersionSummaryType();
    }

    /**
     * Create an instance of {@link StoreVersionOutputMsgType }
     * 
     */
    public StoreVersionOutputMsgType createStoreVersionOutputMsgType() {
        return new StoreVersionOutputMsgType();
    }

    /**
     * Create an instance of {@link StoreVersionInputMsgType }
     * 
     */
    public StoreVersionInputMsgType createStoreVersionInputMsgType() {
        return new StoreVersionInputMsgType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreNativeInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_canoniser", name = "StoreNativeInputMsg")
    public JAXBElement<StoreNativeInputMsgType> createStoreNativeInputMsg(StoreNativeInputMsgType value) {
        return new JAXBElement<StoreNativeInputMsgType>(_StoreNativeInputMsg_QNAME, StoreNativeInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreVersionOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_canoniser", name = "StoreVersionOutputMsg")
    public JAXBElement<StoreVersionOutputMsgType> createStoreVersionOutputMsg(StoreVersionOutputMsgType value) {
        return new JAXBElement<StoreVersionOutputMsgType>(_StoreVersionOutputMsg_QNAME, StoreVersionOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreNativeOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_canoniser", name = "StoreNativeOutputMsg")
    public JAXBElement<StoreNativeOutputMsgType> createStoreNativeOutputMsg(StoreNativeOutputMsgType value) {
        return new JAXBElement<StoreNativeOutputMsgType>(_StoreNativeOutputMsg_QNAME, StoreNativeOutputMsgType.class, null, value);
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

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreVersionInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_canoniser", name = "StoreVersionInputMsg")
    public JAXBElement<StoreVersionInputMsgType> createStoreVersionInputMsg(StoreVersionInputMsgType value) {
        return new JAXBElement<StoreVersionInputMsgType>(_StoreVersionInputMsg_QNAME, StoreVersionInputMsgType.class, null, value);
    }

}
