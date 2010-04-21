
package org.apromore.manager.model_manager;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.manager.model_manager package. 
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

    private final static QName _ReadFormatsInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadFormatsInputMsg");
    private final static QName _ReadFormatsOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadFormatsOutputMsg");
    private final static QName _ReadProcessSummariesInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadProcessSummariesInputMsg");
    private final static QName _WriteUserInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "WriteUserInputMsg");
    private final static QName _ReadUserInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadUserInputMsg");
    private final static QName _WriteUserOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "WriteUserOutputMsg");
    private final static QName _ReadUserOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadUserOutputMsg");
    private final static QName _ReadProcessSummariesOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadProcessSummariesOutputMsg");
    private final static QName _ReadDomainsInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadDomainsInputMsg");
    private final static QName _ReadDomainsOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadDomainsOutputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.manager.model_manager
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WriteUserOutputMsgType }
     * 
     */
    public WriteUserOutputMsgType createWriteUserOutputMsgType() {
        return new WriteUserOutputMsgType();
    }

    /**
     * Create an instance of {@link ReadFormatsOutputMsgType }
     * 
     */
    public ReadFormatsOutputMsgType createReadFormatsOutputMsgType() {
        return new ReadFormatsOutputMsgType();
    }

    /**
     * Create an instance of {@link ProcessSummariesType }
     * 
     */
    public ProcessSummariesType createProcessSummariesType() {
        return new ProcessSummariesType();
    }

    /**
     * Create an instance of {@link FormatsType }
     * 
     */
    public FormatsType createFormatsType() {
        return new FormatsType();
    }

    /**
     * Create an instance of {@link ReadDomainsOutputMsgType }
     * 
     */
    public ReadDomainsOutputMsgType createReadDomainsOutputMsgType() {
        return new ReadDomainsOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteUserInputMsgType }
     * 
     */
    public WriteUserInputMsgType createWriteUserInputMsgType() {
        return new WriteUserInputMsgType();
    }

    /**
     * Create an instance of {@link ReadProcessSummariesOutputMsgType }
     * 
     */
    public ReadProcessSummariesOutputMsgType createReadProcessSummariesOutputMsgType() {
        return new ReadProcessSummariesOutputMsgType();
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
     * Create an instance of {@link DomainsType }
     * 
     */
    public DomainsType createDomainsType() {
        return new DomainsType();
    }

    /**
     * Create an instance of {@link ReadDomainsInputMsgType }
     * 
     */
    public ReadDomainsInputMsgType createReadDomainsInputMsgType() {
        return new ReadDomainsInputMsgType();
    }

    /**
     * Create an instance of {@link ReadUserOutputMsgType }
     * 
     */
    public ReadUserOutputMsgType createReadUserOutputMsgType() {
        return new ReadUserOutputMsgType();
    }

    /**
     * Create an instance of {@link UserType }
     * 
     */
    public UserType createUserType() {
        return new UserType();
    }

    /**
     * Create an instance of {@link SearchHistoriesType }
     * 
     */
    public SearchHistoriesType createSearchHistoriesType() {
        return new SearchHistoriesType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link ReadFormatsInputMsgType }
     * 
     */
    public ReadFormatsInputMsgType createReadFormatsInputMsgType() {
        return new ReadFormatsInputMsgType();
    }

    /**
     * Create an instance of {@link ProcessSummaryType }
     * 
     */
    public ProcessSummaryType createProcessSummaryType() {
        return new ProcessSummaryType();
    }

    /**
     * Create an instance of {@link ReadUserInputMsgType }
     * 
     */
    public ReadUserInputMsgType createReadUserInputMsgType() {
        return new ReadUserInputMsgType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadFormatsInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadFormatsInputMsg")
    public JAXBElement<ReadFormatsInputMsgType> createReadFormatsInputMsg(ReadFormatsInputMsgType value) {
        return new JAXBElement<ReadFormatsInputMsgType>(_ReadFormatsInputMsg_QNAME, ReadFormatsInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadFormatsOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadFormatsOutputMsg")
    public JAXBElement<ReadFormatsOutputMsgType> createReadFormatsOutputMsg(ReadFormatsOutputMsgType value) {
        return new JAXBElement<ReadFormatsOutputMsgType>(_ReadFormatsOutputMsg_QNAME, ReadFormatsOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadProcessSummariesInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadProcessSummariesInputMsg")
    public JAXBElement<ReadProcessSummariesInputMsgType> createReadProcessSummariesInputMsg(ReadProcessSummariesInputMsgType value) {
        return new JAXBElement<ReadProcessSummariesInputMsgType>(_ReadProcessSummariesInputMsg_QNAME, ReadProcessSummariesInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteUserInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "WriteUserInputMsg")
    public JAXBElement<WriteUserInputMsgType> createWriteUserInputMsg(WriteUserInputMsgType value) {
        return new JAXBElement<WriteUserInputMsgType>(_WriteUserInputMsg_QNAME, WriteUserInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadUserInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadUserInputMsg")
    public JAXBElement<ReadUserInputMsgType> createReadUserInputMsg(ReadUserInputMsgType value) {
        return new JAXBElement<ReadUserInputMsgType>(_ReadUserInputMsg_QNAME, ReadUserInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteUserOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "WriteUserOutputMsg")
    public JAXBElement<WriteUserOutputMsgType> createWriteUserOutputMsg(WriteUserOutputMsgType value) {
        return new JAXBElement<WriteUserOutputMsgType>(_WriteUserOutputMsg_QNAME, WriteUserOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadUserOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadUserOutputMsg")
    public JAXBElement<ReadUserOutputMsgType> createReadUserOutputMsg(ReadUserOutputMsgType value) {
        return new JAXBElement<ReadUserOutputMsgType>(_ReadUserOutputMsg_QNAME, ReadUserOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadProcessSummariesOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadProcessSummariesOutputMsg")
    public JAXBElement<ReadProcessSummariesOutputMsgType> createReadProcessSummariesOutputMsg(ReadProcessSummariesOutputMsgType value) {
        return new JAXBElement<ReadProcessSummariesOutputMsgType>(_ReadProcessSummariesOutputMsg_QNAME, ReadProcessSummariesOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadDomainsInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadDomainsInputMsg")
    public JAXBElement<ReadDomainsInputMsgType> createReadDomainsInputMsg(ReadDomainsInputMsgType value) {
        return new JAXBElement<ReadDomainsInputMsgType>(_ReadDomainsInputMsg_QNAME, ReadDomainsInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadDomainsOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadDomainsOutputMsg")
    public JAXBElement<ReadDomainsOutputMsgType> createReadDomainsOutputMsg(ReadDomainsOutputMsgType value) {
        return new JAXBElement<ReadDomainsOutputMsgType>(_ReadDomainsOutputMsg_QNAME, ReadDomainsOutputMsgType.class, null, value);
    }

}
