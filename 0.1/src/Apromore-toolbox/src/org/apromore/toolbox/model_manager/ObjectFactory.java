
package org.apromore.toolbox.model_manager;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.toolbox.model_manager package. 
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

    private final static QName _MergeProcessesOutputMsg_QNAME = new QName("http://www.apromore.org/toolbox/model_manager", "MergeProcessesOutputMsg");
    private final static QName _MergeProcessesInputMsg_QNAME = new QName("http://www.apromore.org/toolbox/model_manager", "MergeProcessesInputMsg");
    private final static QName _SearchForSimilarProcessesOutputMsg_QNAME = new QName("http://www.apromore.org/toolbox/model_manager", "SearchForSimilarProcessesOutputMsg");
    private final static QName _SearchForSimilarProcessesInputMsg_QNAME = new QName("http://www.apromore.org/toolbox/model_manager", "SearchForSimilarProcessesInputMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.toolbox.model_manager
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MergeProcessesOutputMsgType }
     * 
     */
    public MergeProcessesOutputMsgType createMergeProcessesOutputMsgType() {
        return new MergeProcessesOutputMsgType();
    }

    /**
     * Create an instance of {@link VersionSummaryType }
     * 
     */
    public VersionSummaryType createVersionSummaryType() {
        return new VersionSummaryType();
    }

    /**
     * Create an instance of {@link ProcessVersionIdType }
     * 
     */
    public ProcessVersionIdType createProcessVersionIdType() {
        return new ProcessVersionIdType();
    }

    /**
     * Create an instance of {@link ProcessSummaryType }
     * 
     */
    public ProcessSummaryType createProcessSummaryType() {
        return new ProcessSummaryType();
    }

    /**
     * Create an instance of {@link AnnotationsType }
     * 
     */
    public AnnotationsType createAnnotationsType() {
        return new AnnotationsType();
    }

    /**
     * Create an instance of {@link SearchForSimilarProcessesOutputMsgType }
     * 
     */
    public SearchForSimilarProcessesOutputMsgType createSearchForSimilarProcessesOutputMsgType() {
        return new SearchForSimilarProcessesOutputMsgType();
    }

    /**
     * Create an instance of {@link SearchForSimilarProcessesInputMsgType }
     * 
     */
    public SearchForSimilarProcessesInputMsgType createSearchForSimilarProcessesInputMsgType() {
        return new SearchForSimilarProcessesInputMsgType();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link ProcessVersionIdsType }
     * 
     */
    public ProcessVersionIdsType createProcessVersionIdsType() {
        return new ProcessVersionIdsType();
    }

    /**
     * Create an instance of {@link MergeProcessesInputMsgType }
     * 
     */
    public MergeProcessesInputMsgType createMergeProcessesInputMsgType() {
        return new MergeProcessesInputMsgType();
    }

    /**
     * Create an instance of {@link ParametersType }
     * 
     */
    public ParametersType createParametersType() {
        return new ParametersType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link ProcessSummariesType }
     * 
     */
    public ProcessSummariesType createProcessSummariesType() {
        return new ProcessSummariesType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MergeProcessesOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/toolbox/model_manager", name = "MergeProcessesOutputMsg")
    public JAXBElement<MergeProcessesOutputMsgType> createMergeProcessesOutputMsg(MergeProcessesOutputMsgType value) {
        return new JAXBElement<MergeProcessesOutputMsgType>(_MergeProcessesOutputMsg_QNAME, MergeProcessesOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MergeProcessesInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/toolbox/model_manager", name = "MergeProcessesInputMsg")
    public JAXBElement<MergeProcessesInputMsgType> createMergeProcessesInputMsg(MergeProcessesInputMsgType value) {
        return new JAXBElement<MergeProcessesInputMsgType>(_MergeProcessesInputMsg_QNAME, MergeProcessesInputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchForSimilarProcessesOutputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/toolbox/model_manager", name = "SearchForSimilarProcessesOutputMsg")
    public JAXBElement<SearchForSimilarProcessesOutputMsgType> createSearchForSimilarProcessesOutputMsg(SearchForSimilarProcessesOutputMsgType value) {
        return new JAXBElement<SearchForSimilarProcessesOutputMsgType>(_SearchForSimilarProcessesOutputMsg_QNAME, SearchForSimilarProcessesOutputMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchForSimilarProcessesInputMsgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/toolbox/model_manager", name = "SearchForSimilarProcessesInputMsg")
    public JAXBElement<SearchForSimilarProcessesInputMsgType> createSearchForSimilarProcessesInputMsg(SearchForSimilarProcessesInputMsgType value) {
        return new JAXBElement<SearchForSimilarProcessesInputMsgType>(_SearchForSimilarProcessesInputMsg_QNAME, SearchForSimilarProcessesInputMsgType.class, null, value);
    }

}
