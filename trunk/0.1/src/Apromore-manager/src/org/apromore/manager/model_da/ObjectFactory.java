
package org.apromore.manager.model_da;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.manager.model_da package. 
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

    private final static QName _DeleteProcessVersionsInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "DeleteProcessVersionsInputMsg");
    private final static QName _DeleteProcessVersionsOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "DeleteProcessVersionsOutputMsg");
    private final static QName _DeleteEditSessionOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "DeleteEditSessionOutputMsg");
    private final static QName _ReadUserInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadUserInputMsg");
    private final static QName _ReadNativeTypesOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadNativeTypesOutputMsg");
    private final static QName _EditProcessDataOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "EditProcessDataOutputMsg");
    private final static QName _DeleteEditSessionInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "DeleteEditSessionInputMsg");
    private final static QName _WriteUserOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "WriteUserOutputMsg");
    private final static QName _ReadUserOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadUserOutputMsg");
    private final static QName _ReadAllUsersOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadAllUsersOutputMsg");
    private final static QName _WriteEditSessionOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "WriteEditSessionOutputMsg");
    private final static QName _ReadCanonicalAnfInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadCanonicalAnfInputMsg");
    private final static QName _ReadFormatOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadFormatOutputMsg");
    private final static QName _ReadProcessSummariesInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadProcessSummariesInputMsg");
    private final static QName _ReadEditSessionOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadEditSessionOutputMsg");
    private final static QName _ReadAllUsersInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadAllUsersInputMsg");
    private final static QName _ReadFormatInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadFormatInputMsg");
    private final static QName _ReadDomainsOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadDomainsOutputMsg");
    private final static QName _EditProcessDataInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "EditProcessDataInputMsg");
    private final static QName _WriteEditSessionInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "WriteEditSessionInputMsg");
    private final static QName _ReadCanonicalAnfOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadCanonicalAnfOutputMsg");
    private final static QName _WriteUserInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "WriteUserInputMsg");
    private final static QName _ReadNativeTypesInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadNativeTypesInputMsg");
    private final static QName _ReadEditSessionInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadEditSessionInputMsg");
    private final static QName _ReadProcessSummariesOutputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadProcessSummariesOutputMsg");
    private final static QName _ReadDomainsInputMsg_QNAME = new QName("http://www.apromore.org/data_access/model_manager", "ReadDomainsInputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.manager.model_da
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeleteProcessVersionsInputMsgType }
     * 
     */
    public DeleteProcessVersionsInputMsgType createDeleteProcessVersionsInputMsgType() {
        return new DeleteProcessVersionsInputMsgType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link ReadFormatInputMsgType }
     * 
     */
    public ReadFormatInputMsgType createReadFormatInputMsgType() {
        return new ReadFormatInputMsgType();
    }

    /**
     * Create an instance of {@link VersionSummaryType }
     * 
     */
    public VersionSummaryType createVersionSummaryType() {
        return new VersionSummaryType();
    }

    /**
     * Create an instance of {@link SearchHistoriesType }
     * 
     */
    public SearchHistoriesType createSearchHistoriesType() {
        return new SearchHistoriesType();
    }

    /**
     * Create an instance of {@link ReadFormatOutputMsgType }
     * 
     */
    public ReadFormatOutputMsgType createReadFormatOutputMsgType() {
        return new ReadFormatOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteEditSessionInputMsgType }
     * 
     */
    public WriteEditSessionInputMsgType createWriteEditSessionInputMsgType() {
        return new WriteEditSessionInputMsgType();
    }

    /**
     * Create an instance of {@link ReadProcessSummariesOutputMsgType }
     * 
     */
    public ReadProcessSummariesOutputMsgType createReadProcessSummariesOutputMsgType() {
        return new ReadProcessSummariesOutputMsgType();
    }

    /**
     * Create an instance of {@link WriteEditSessionOutputMsgType }
     * 
     */
    public WriteEditSessionOutputMsgType createWriteEditSessionOutputMsgType() {
        return new WriteEditSessionOutputMsgType();
    }

    /**
     * Create an instance of {@link ProcessVersionIdentifierType }
     * 
     */
    public ProcessVersionIdentifierType createProcessVersionIdentifierType() {
        return new ProcessVersionIdentifierType();
    }

    /**
     * Create an instance of {@link ReadEditSessionInputMsgType }
     * 
     */
    public ReadEditSessionInputMsgType createReadEditSessionInputMsgType() {
        return new ReadEditSessionInputMsgType();
    }

    /**
     * Create an instance of {@link ReadEditSessionOutputMsgType }
     * 
     */
    public ReadEditSessionOutputMsgType createReadEditSessionOutputMsgType() {
        return new ReadEditSessionOutputMsgType();
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
     * Create an instance of {@link ReadCanonicalAnfOutputMsgType }
     * 
     */
    public ReadCanonicalAnfOutputMsgType createReadCanonicalAnfOutputMsgType() {
        return new ReadCanonicalAnfOutputMsgType();
    }

    /**
     * Create an instance of {@link DeleteProcessVersionsOutputMsgType }
     * 
     */
    public DeleteProcessVersionsOutputMsgType createDeleteProcessVersionsOutputMsgType() {
        return new DeleteProcessVersionsOutputMsgType();
    }

    /**
     * Create an instance of {@link UsernamesType }
     * 
     */
    public UsernamesType createUsernamesType() {
        return new UsernamesType();
    }

    /**
     * Create an instance of {@link EditSessionType }
     * 
     */
    public EditSessionType createEditSessionType() {
        return new EditSessionType();
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
     * Create an instance of {@link DeleteEditSessionOutputMsgType }
     * 
     */
    public DeleteEditSessionOutputMsgType createDeleteEditSessionOutputMsgType() {
        return new DeleteEditSessionOutputMsgType();
    }

    /**
     * Create an instance of {@link EditProcessDataOutputMsgType }
     * 
     */
    public EditProcessDataOutputMsgType createEditProcessDataOutputMsgType() {
        return new EditProcessDataOutputMsgType();
    }

    /**
     * Create an instance of {@link ReadAllUsersInputMsgType }
     * 
     */
    public ReadAllUsersInputMsgType createReadAllUsersInputMsgType() {
        return new ReadAllUsersInputMsgType();
    }

    /**
     * Create an instance of {@link ReadNativeTypesInputMsgType }
     * 
     */
    public ReadNativeTypesInputMsgType createReadNativeTypesInputMsgType() {
        return new ReadNativeTypesInputMsgType();
    }

    /**
     * Create an instance of {@link UserType }
     * 
     */
    public UserType createUserType() {
        return new UserType();
    }

    /**
     * Create an instance of {@link ReadCanonicalAnfInputMsgType }
     * 
     */
    public ReadCanonicalAnfInputMsgType createReadCanonicalAnfInputMsgType() {
        return new ReadCanonicalAnfInputMsgType();
    }

    /**
     * Create an instance of {@link EditProcessDataInputMsgType }
     * 
     */
    public EditProcessDataInputMsgType createEditProcessDataInputMsgType() {
        return new EditProcessDataInputMsgType();
    }

    /**
     * Create an instance of {@link WriteUserOutputMsgType }
     * 
     */
    public WriteUserOutputMsgType createWriteUserOutputMsgType() {
        return new WriteUserOutputMsgType();
    }

    /**
     * Create an instance of {@link AnnotationsType }
     * 
     */
    public AnnotationsType createAnnotationsType() {
        return new AnnotationsType();
    }

    /**
     * Create an instance of {@link ReadNativeTypesOutputMsgType }
     * 
     */
    public ReadNativeTypesOutputMsgType createReadNativeTypesOutputMsgType() {
        return new ReadNativeTypesOutputMsgType();
    }

    /**
     * Create an instance of {@link ReadProcessSummariesInputMsgType }
     * 
     */
    public ReadProcessSummariesInputMsgType createReadProcessSummariesInputMsgType() {
        return new ReadProcessSummariesInputMsgType();
    }

    /**
     * Create an instance of {@link ReadDomainsOutputMsgType }
     * 
     */
    public ReadDomainsOutputMsgType createReadDomainsOutputMsgType() {
        return new ReadDomainsOutputMsgType();
    }

    /**
     * Create an instance of {@link FormatType }
     * 
     */
    public FormatType createFormatType() {
        return new FormatType();
    }

    /**
     * Create an instance of {@link WriteUserInputMsgType }
     * 
     */
    public WriteUserInputMsgType createWriteUserInputMsgType() {
        return new WriteUserInputMsgType();
    }

    /**
     * Create an instance of {@link DomainsType }
     * 
     */
    public DomainsType createDomainsType() {
        return new DomainsType();
    }

    /**
     * Create an instance of {@link NativeTypesType }
     * 
     */
    public NativeTypesType createNativeTypesType() {
        return new NativeTypesType();
    }

    /**
     * Create an instance of {@link ProcessSummariesType }
     * 
     */
    public ProcessSummariesType createProcessSummariesType() {
        return new ProcessSummariesType();
    }

    /**
     * Create an instance of {@link DeleteEditSessionInputMsgType }
     * 
     */
    public DeleteEditSessionInputMsgType createDeleteEditSessionInputMsgType() {
        return new DeleteEditSessionInputMsgType();
    }

    /**
     * Create an instance of {@link ReadAllUsersOutputMsgType }
     * 
     */
    public ReadAllUsersOutputMsgType createReadAllUsersOutputMsgType() {
        return new ReadAllUsersOutputMsgType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteProcessVersionsInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "DeleteProcessVersionsInputMsg")
    public JAXBElement<DeleteProcessVersionsInputMsgType> createDeleteProcessVersionsInputMsg(DeleteProcessVersionsInputMsgType value) {
        return new JAXBElement<DeleteProcessVersionsInputMsgType>(_DeleteProcessVersionsInputMsg_QNAME, DeleteProcessVersionsInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteProcessVersionsOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "DeleteProcessVersionsOutputMsg")
    public JAXBElement<DeleteProcessVersionsOutputMsgType> createDeleteProcessVersionsOutputMsg(DeleteProcessVersionsOutputMsgType value) {
        return new JAXBElement<DeleteProcessVersionsOutputMsgType>(_DeleteProcessVersionsOutputMsg_QNAME, DeleteProcessVersionsOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEditSessionOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "DeleteEditSessionOutputMsg")
    public JAXBElement<DeleteEditSessionOutputMsgType> createDeleteEditSessionOutputMsg(DeleteEditSessionOutputMsgType value) {
        return new JAXBElement<DeleteEditSessionOutputMsgType>(_DeleteEditSessionOutputMsg_QNAME, DeleteEditSessionOutputMsgType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadNativeTypesOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadNativeTypesOutputMsg")
    public JAXBElement<ReadNativeTypesOutputMsgType> createReadNativeTypesOutputMsg(ReadNativeTypesOutputMsgType value) {
        return new JAXBElement<ReadNativeTypesOutputMsgType>(_ReadNativeTypesOutputMsg_QNAME, ReadNativeTypesOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EditProcessDataOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "EditProcessDataOutputMsg")
    public JAXBElement<EditProcessDataOutputMsgType> createEditProcessDataOutputMsg(EditProcessDataOutputMsgType value) {
        return new JAXBElement<EditProcessDataOutputMsgType>(_EditProcessDataOutputMsg_QNAME, EditProcessDataOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEditSessionInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "DeleteEditSessionInputMsg")
    public JAXBElement<DeleteEditSessionInputMsgType> createDeleteEditSessionInputMsg(DeleteEditSessionInputMsgType value) {
        return new JAXBElement<DeleteEditSessionInputMsgType>(_DeleteEditSessionInputMsg_QNAME, DeleteEditSessionInputMsgType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadAllUsersOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadAllUsersOutputMsg")
    public JAXBElement<ReadAllUsersOutputMsgType> createReadAllUsersOutputMsg(ReadAllUsersOutputMsgType value) {
        return new JAXBElement<ReadAllUsersOutputMsgType>(_ReadAllUsersOutputMsg_QNAME, ReadAllUsersOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteEditSessionOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "WriteEditSessionOutputMsg")
    public JAXBElement<WriteEditSessionOutputMsgType> createWriteEditSessionOutputMsg(WriteEditSessionOutputMsgType value) {
        return new JAXBElement<WriteEditSessionOutputMsgType>(_WriteEditSessionOutputMsg_QNAME, WriteEditSessionOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadCanonicalAnfInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadCanonicalAnfInputMsg")
    public JAXBElement<ReadCanonicalAnfInputMsgType> createReadCanonicalAnfInputMsg(ReadCanonicalAnfInputMsgType value) {
        return new JAXBElement<ReadCanonicalAnfInputMsgType>(_ReadCanonicalAnfInputMsg_QNAME, ReadCanonicalAnfInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadFormatOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadFormatOutputMsg")
    public JAXBElement<ReadFormatOutputMsgType> createReadFormatOutputMsg(ReadFormatOutputMsgType value) {
        return new JAXBElement<ReadFormatOutputMsgType>(_ReadFormatOutputMsg_QNAME, ReadFormatOutputMsgType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadEditSessionOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadEditSessionOutputMsg")
    public JAXBElement<ReadEditSessionOutputMsgType> createReadEditSessionOutputMsg(ReadEditSessionOutputMsgType value) {
        return new JAXBElement<ReadEditSessionOutputMsgType>(_ReadEditSessionOutputMsg_QNAME, ReadEditSessionOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadAllUsersInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadAllUsersInputMsg")
    public JAXBElement<ReadAllUsersInputMsgType> createReadAllUsersInputMsg(ReadAllUsersInputMsgType value) {
        return new JAXBElement<ReadAllUsersInputMsgType>(_ReadAllUsersInputMsg_QNAME, ReadAllUsersInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadFormatInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadFormatInputMsg")
    public JAXBElement<ReadFormatInputMsgType> createReadFormatInputMsg(ReadFormatInputMsgType value) {
        return new JAXBElement<ReadFormatInputMsgType>(_ReadFormatInputMsg_QNAME, ReadFormatInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadDomainsOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadDomainsOutputMsg")
    public JAXBElement<ReadDomainsOutputMsgType> createReadDomainsOutputMsg(ReadDomainsOutputMsgType value) {
        return new JAXBElement<ReadDomainsOutputMsgType>(_ReadDomainsOutputMsg_QNAME, ReadDomainsOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EditProcessDataInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "EditProcessDataInputMsg")
    public JAXBElement<EditProcessDataInputMsgType> createEditProcessDataInputMsg(EditProcessDataInputMsgType value) {
        return new JAXBElement<EditProcessDataInputMsgType>(_EditProcessDataInputMsg_QNAME, EditProcessDataInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WriteEditSessionInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "WriteEditSessionInputMsg")
    public JAXBElement<WriteEditSessionInputMsgType> createWriteEditSessionInputMsg(WriteEditSessionInputMsgType value) {
        return new JAXBElement<WriteEditSessionInputMsgType>(_WriteEditSessionInputMsg_QNAME, WriteEditSessionInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadCanonicalAnfOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadCanonicalAnfOutputMsg")
    public JAXBElement<ReadCanonicalAnfOutputMsgType> createReadCanonicalAnfOutputMsg(ReadCanonicalAnfOutputMsgType value) {
        return new JAXBElement<ReadCanonicalAnfOutputMsgType>(_ReadCanonicalAnfOutputMsg_QNAME, ReadCanonicalAnfOutputMsgType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadNativeTypesInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadNativeTypesInputMsg")
    public JAXBElement<ReadNativeTypesInputMsgType> createReadNativeTypesInputMsg(ReadNativeTypesInputMsgType value) {
        return new JAXBElement<ReadNativeTypesInputMsgType>(_ReadNativeTypesInputMsg_QNAME, ReadNativeTypesInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReadEditSessionInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/data_access/model_manager", name = "ReadEditSessionInputMsg")
    public JAXBElement<ReadEditSessionInputMsgType> createReadEditSessionInputMsg(ReadEditSessionInputMsgType value) {
        return new JAXBElement<ReadEditSessionInputMsgType>(_ReadEditSessionInputMsg_QNAME, ReadEditSessionInputMsgType.class, null, value);
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

}
