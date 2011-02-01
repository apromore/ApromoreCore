
package org.apromore.portal.model_oryx;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.portal.model_oryx package. 
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

    private final static QName _WriteAnnotationOutputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteAnnotationOutputMsg");
    private final static QName _CloseSessionInputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "CloseSessionInputMsg");
    private final static QName _WriteProcessOutputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteProcessOutputMsg");
    private final static QName _ReadNativeOutputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "ReadNativeOutputMsg");
    private final static QName _ReadNativeInputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "ReadNativeInputMsg");
    private final static QName _WriteNewAnnotationOutputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteNewAnnotationOutputMsg");
    private final static QName _WriteNewProcessInputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteNewProcessInputMsg");
    private final static QName _WriteNewProcessOutputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteNewProcessOutputMsg");
    private final static QName _WriteProcessInputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteProcessInputMsg");
    private final static QName _CloseSessionOutputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "CloseSessionOutputMsg");
    private final static QName _WriteNewAnnotationInputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteNewAnnotationInputMsg");
    private final static QName _WriteAnnotationInputMsg_QNAME = new QName("http://www.apromore.org/portal/model_oryx", "WriteAnnotationInputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.portal.model_oryx
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WriteProcessOutputMsgType }
     * 
     */
    public WriteProcessOutputMsgType createWriteProcessOutputMsgType() {
        return new WriteProcessOutputMsgType();
    }

    /**
     * Create an instance of {@link ReadNativeInputMsgType }
     * 
     */
    public ReadNativeInputMsgType createReadNativeInputMsgType() {
        return new ReadNativeInputMsgType();
    }

    /**
     * Create an instance of {@link WriteNewProcessOutputMsgType }
     * 
     */
    public WriteNewProcessOutputMsgType createWriteNewProcessOutputMsgType() {
        return new WriteNewProcessOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteProcessInputMsgType }
     * 
     */
    public WriteProcessInputMsgType createWriteProcessInputMsgType() {
        return new WriteProcessInputMsgType();
    }

    /**
     * Create an instance of {@link CloseSessionInputMsgType }
     * 
     */
    public CloseSessionInputMsgType createCloseSessionInputMsgType() {
        return new CloseSessionInputMsgType();
    }

    /**
     * Create an instance of {@link WriteAnnotationOutputMsgType }
     * 
     */
    public WriteAnnotationOutputMsgType createWriteAnnotationOutputMsgType() {
        return new WriteAnnotationOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteNewAnnotationInputMsgType }
     * 
     */
    public WriteNewAnnotationInputMsgType createWriteNewAnnotationInputMsgType() {
        return new WriteNewAnnotationInputMsgType();
    }

    /**
     * Create an instance of {@link CloseSessionOutputMsgType }
     * 
     */
    public CloseSessionOutputMsgType createCloseSessionOutputMsgType() {
        return new CloseSessionOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteNewAnnotationOutputMsgType }
     * 
     */
    public WriteNewAnnotationOutputMsgType createWriteNewAnnotationOutputMsgType() {
        return new WriteNewAnnotationOutputMsgType();
    }

    /**
     * Create an instance of {@link ReadNativeOutputMsgType }
     * 
     */
    public ReadNativeOutputMsgType createReadNativeOutputMsgType() {
        return new ReadNativeOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteNewProcessInputMsgType }
     * 
     */
    public WriteNewProcessInputMsgType createWriteNewProcessInputMsgType() {
        return new WriteNewProcessInputMsgType();
    }

    /**
     * Create an instance of {@link WriteAnnotationInputMsgType }
     * 
     */
    public WriteAnnotationInputMsgType createWriteAnnotationInputMsgType() {
        return new WriteAnnotationInputMsgType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteAnnotationOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteAnnotationOutputMsg")
    public JAXBElement<WriteAnnotationOutputMsgType> createWriteAnnotationOutputMsg(WriteAnnotationOutputMsgType value) {
        return new JAXBElement<WriteAnnotationOutputMsgType>(_WriteAnnotationOutputMsg_QNAME, WriteAnnotationOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CloseSessionInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "CloseSessionInputMsg")
    public JAXBElement<CloseSessionInputMsgType> createCloseSessionInputMsg(CloseSessionInputMsgType value) {
        return new JAXBElement<CloseSessionInputMsgType>(_CloseSessionInputMsg_QNAME, CloseSessionInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteProcessOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteProcessOutputMsg")
    public JAXBElement<WriteProcessOutputMsgType> createWriteProcessOutputMsg(WriteProcessOutputMsgType value) {
        return new JAXBElement<WriteProcessOutputMsgType>(_WriteProcessOutputMsg_QNAME, WriteProcessOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadNativeOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "ReadNativeOutputMsg")
    public JAXBElement<ReadNativeOutputMsgType> createReadNativeOutputMsg(ReadNativeOutputMsgType value) {
        return new JAXBElement<ReadNativeOutputMsgType>(_ReadNativeOutputMsg_QNAME, ReadNativeOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadNativeInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "ReadNativeInputMsg")
    public JAXBElement<ReadNativeInputMsgType> createReadNativeInputMsg(ReadNativeInputMsgType value) {
        return new JAXBElement<ReadNativeInputMsgType>(_ReadNativeInputMsg_QNAME, ReadNativeInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteNewAnnotationOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteNewAnnotationOutputMsg")
    public JAXBElement<WriteNewAnnotationOutputMsgType> createWriteNewAnnotationOutputMsg(WriteNewAnnotationOutputMsgType value) {
        return new JAXBElement<WriteNewAnnotationOutputMsgType>(_WriteNewAnnotationOutputMsg_QNAME, WriteNewAnnotationOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteNewProcessInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteNewProcessInputMsg")
    public JAXBElement<WriteNewProcessInputMsgType> createWriteNewProcessInputMsg(WriteNewProcessInputMsgType value) {
        return new JAXBElement<WriteNewProcessInputMsgType>(_WriteNewProcessInputMsg_QNAME, WriteNewProcessInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteNewProcessOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteNewProcessOutputMsg")
    public JAXBElement<WriteNewProcessOutputMsgType> createWriteNewProcessOutputMsg(WriteNewProcessOutputMsgType value) {
        return new JAXBElement<WriteNewProcessOutputMsgType>(_WriteNewProcessOutputMsg_QNAME, WriteNewProcessOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteProcessInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteProcessInputMsg")
    public JAXBElement<WriteProcessInputMsgType> createWriteProcessInputMsg(WriteProcessInputMsgType value) {
        return new JAXBElement<WriteProcessInputMsgType>(_WriteProcessInputMsg_QNAME, WriteProcessInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CloseSessionOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "CloseSessionOutputMsg")
    public JAXBElement<CloseSessionOutputMsgType> createCloseSessionOutputMsg(CloseSessionOutputMsgType value) {
        return new JAXBElement<CloseSessionOutputMsgType>(_CloseSessionOutputMsg_QNAME, CloseSessionOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteNewAnnotationInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteNewAnnotationInputMsg")
    public JAXBElement<WriteNewAnnotationInputMsgType> createWriteNewAnnotationInputMsg(WriteNewAnnotationInputMsgType value) {
        return new JAXBElement<WriteNewAnnotationInputMsgType>(_WriteNewAnnotationInputMsg_QNAME, WriteNewAnnotationInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteAnnotationInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/portal/model_oryx", name = "WriteAnnotationInputMsg")
    public JAXBElement<WriteAnnotationInputMsgType> createWriteAnnotationInputMsg(WriteAnnotationInputMsgType value) {
        return new JAXBElement<WriteAnnotationInputMsgType>(_WriteAnnotationInputMsg_QNAME, WriteAnnotationInputMsgType.class, null, value);
    }

}
