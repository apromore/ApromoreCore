
package org.apromore.toolbox.model_da;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.toolbox.model_da package. 
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

    private final static QName _StoreCpfOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_toolbox", "StoreCpfOutputMsg");
    private final static QName _ReadProcessSummariesInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_toolbox", "ReadProcessSummariesInputMsg");
    private final static QName _ReadProcessSummariesOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_toolbox", "ReadProcessSummariesOutputMsg");
    private final static QName _ReadCanonicalsInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_toolbox", "ReadCanonicalsInputMsg");
    private final static QName _ReadCanonicalsOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_toolbox", "ReadCanonicalsOutputMsg");
    private final static QName _StoreCpfInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_toolbox", "StoreCpfInputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.toolbox.model_da
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
     * Create an instance of {@link ProcessVersionsType }
     * 
     */
    public ProcessVersionsType createProcessVersionsType() {
        return new ProcessVersionsType();
    }

    /**
     * Create an instance of {@link ReadCanonicalsInputMsgType }
     * 
     */
    public ReadCanonicalsInputMsgType createReadCanonicalsInputMsgType() {
        return new ReadCanonicalsInputMsgType();
    }

    /**
     * Create an instance of {@link ReadProcessSummariesOutputMsgType }
     * 
     */
    public ReadProcessSummariesOutputMsgType createReadProcessSummariesOutputMsgType() {
        return new ReadProcessSummariesOutputMsgType();
    }

    /**
     * Create an instance of {@link AnnotationsType }
     * 
     */
    public AnnotationsType createAnnotationsType() {
        return new AnnotationsType();
    }

    /**
     * Create an instance of {@link ProcessSummariesType }
     * 
     */
    public ProcessSummariesType createProcessSummariesType() {
        return new ProcessSummariesType();
    }

    /**
     * Create an instance of {@link ProcessSummaryType }
     * 
     */
    public ProcessSummaryType createProcessSummaryType() {
        return new ProcessSummaryType();
    }

    /**
     * Create an instance of {@link CanonicalsType }
     * 
     */
    public CanonicalsType createCanonicalsType() {
        return new CanonicalsType();
    }

    /**
     * Create an instance of {@link ReadCanonicalsOutputMsgType }
     * 
     */
    public ReadCanonicalsOutputMsgType createReadCanonicalsOutputMsgType() {
        return new ReadCanonicalsOutputMsgType();
    }

    /**
     * Create an instance of {@link ProcessVersionType }
     * 
     */
    public ProcessVersionType createProcessVersionType() {
        return new ProcessVersionType();
    }

    /**
     * Create an instance of {@link CanonicalType }
     * 
     */
    public CanonicalType createCanonicalType() {
        return new CanonicalType();
    }

    /**
     * Create an instance of {@link ReadProcessSummariesInputMsgType }
     * 
     */
    public ReadProcessSummariesInputMsgType createReadProcessSummariesInputMsgType() {
        return new ReadProcessSummariesInputMsgType();
    }

    /**
     * Create an instance of {@link VersionSummaryType }
     * 
     */
    public VersionSummaryType createVersionSummaryType() {
        return new VersionSummaryType();
    }

    /**
     * Create an instance of {@link StoreCpfInputMsgType }
     * 
     */
    public StoreCpfInputMsgType createStoreCpfInputMsgType() {
        return new StoreCpfInputMsgType();
    }

    /**
     * Create an instance of {@link StoreCpfOutputMsgType }
     * 
     */
    public StoreCpfOutputMsgType createStoreCpfOutputMsgType() {
        return new StoreCpfOutputMsgType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreCpfOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_toolbox", name = "StoreCpfOutputMsg")
    public JAXBElement<StoreCpfOutputMsgType> createStoreCpfOutputMsg(StoreCpfOutputMsgType value) {
        return new JAXBElement<StoreCpfOutputMsgType>(_StoreCpfOutputMsg_QNAME, StoreCpfOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadProcessSummariesInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_toolbox", name = "ReadProcessSummariesInputMsg")
    public JAXBElement<ReadProcessSummariesInputMsgType> createReadProcessSummariesInputMsg(ReadProcessSummariesInputMsgType value) {
        return new JAXBElement<ReadProcessSummariesInputMsgType>(_ReadProcessSummariesInputMsg_QNAME, ReadProcessSummariesInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadProcessSummariesOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_toolbox", name = "ReadProcessSummariesOutputMsg")
    public JAXBElement<ReadProcessSummariesOutputMsgType> createReadProcessSummariesOutputMsg(ReadProcessSummariesOutputMsgType value) {
        return new JAXBElement<ReadProcessSummariesOutputMsgType>(_ReadProcessSummariesOutputMsg_QNAME, ReadProcessSummariesOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadCanonicalsInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_toolbox", name = "ReadCanonicalsInputMsg")
    public JAXBElement<ReadCanonicalsInputMsgType> createReadCanonicalsInputMsg(ReadCanonicalsInputMsgType value) {
        return new JAXBElement<ReadCanonicalsInputMsgType>(_ReadCanonicalsInputMsg_QNAME, ReadCanonicalsInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadCanonicalsOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_toolbox", name = "ReadCanonicalsOutputMsg")
    public JAXBElement<ReadCanonicalsOutputMsgType> createReadCanonicalsOutputMsg(ReadCanonicalsOutputMsgType value) {
        return new JAXBElement<ReadCanonicalsOutputMsgType>(_ReadCanonicalsOutputMsg_QNAME, ReadCanonicalsOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreCpfInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_toolbox", name = "StoreCpfInputMsg")
    public JAXBElement<StoreCpfInputMsgType> createStoreCpfInputMsg(StoreCpfInputMsgType value) {
        return new JAXBElement<StoreCpfInputMsgType>(_StoreCpfInputMsg_QNAME, StoreCpfInputMsgType.class, null, value);
    }

}
