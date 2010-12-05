
package org.apromore.cpf;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apromore.cpf package. 
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

    private final static QName _CanonicalProcess_QNAME = new QName("http://www.apromore.org/CPF", "CanonicalProcess");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apromore.cpf
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CanonicalProcessType }
     * 
     */
    public CanonicalProcessType createCanonicalProcessType() {
        return new CanonicalProcessType();
    }

    /**
     * Create an instance of {@link ResourceTypeType }
     * 
     */
    public ResourceTypeType createResourceTypeType() {
        return new ResourceTypeType();
    }

    /**
     * Create an instance of {@link XORSplitType }
     * 
     */
    public XORSplitType createXORSplitType() {
        return new XORSplitType();
    }

    /**
     * Create an instance of {@link XORJoinType }
     * 
     */
    public XORJoinType createXORJoinType() {
        return new XORJoinType();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link NetType }
     * 
     */
    public NetType createNetType() {
        return new NetType();
    }

    /**
     * Create an instance of {@link WorkType }
     * 
     */
    public WorkType createWorkType() {
        return new WorkType();
    }

    /**
     * Create an instance of {@link ORSplitType }
     * 
     */
    public ORSplitType createORSplitType() {
        return new ORSplitType();
    }

    /**
     * Create an instance of {@link EdgeType }
     * 
     */
    public EdgeType createEdgeType() {
        return new EdgeType();
    }

    /**
     * Create an instance of {@link JoinType }
     * 
     */
    public JoinType createJoinType() {
        return new JoinType();
    }

    /**
     * Create an instance of {@link RoutingType }
     * 
     */
    public RoutingType createRoutingType() {
        return new RoutingType();
    }

    /**
     * Create an instance of {@link HardType }
     * 
     */
    public HardType createHardType() {
        return new HardType();
    }

    /**
     * Create an instance of {@link EventType }
     * 
     */
    public EventType createEventType() {
        return new EventType();
    }

    /**
     * Create an instance of {@link ORJoinType }
     * 
     */
    public ORJoinType createORJoinType() {
        return new ORJoinType();
    }

    /**
     * Create an instance of {@link NodeType }
     * 
     */
    public NodeType createNodeType() {
        return new NodeType();
    }

    /**
     * Create an instance of {@link TaskType }
     * 
     */
    public TaskType createTaskType() {
        return new TaskType();
    }

    /**
     * Create an instance of {@link ANDJoinType }
     * 
     */
    public ANDJoinType createANDJoinType() {
        return new ANDJoinType();
    }

    /**
     * Create an instance of {@link TimerType }
     * 
     */
    public TimerType createTimerType() {
        return new TimerType();
    }

    /**
     * Create an instance of {@link SoftType }
     * 
     */
    public SoftType createSoftType() {
        return new SoftType();
    }

    /**
     * Create an instance of {@link ObjectRefType }
     * 
     */
    public ObjectRefType createObjectRefType() {
        return new ObjectRefType();
    }

    /**
     * Create an instance of {@link ResourceTypeRefType }
     * 
     */
    public ResourceTypeRefType createResourceTypeRefType() {
        return new ResourceTypeRefType();
    }

    /**
     * Create an instance of {@link StateType }
     * 
     */
    public StateType createStateType() {
        return new StateType();
    }

    /**
     * Create an instance of {@link HumanType }
     * 
     */
    public HumanType createHumanType() {
        return new HumanType();
    }

    /**
     * Create an instance of {@link ObjectType }
     * 
     */
    public ObjectType createObjectType() {
        return new ObjectType();
    }

    /**
     * Create an instance of {@link NonhumanType }
     * 
     */
    public NonhumanType createNonhumanType() {
        return new NonhumanType();
    }

    /**
     * Create an instance of {@link TypeAttribute }
     * 
     */
    public TypeAttribute createTypeAttribute() {
        return new TypeAttribute();
    }

    /**
     * Create an instance of {@link SplitType }
     * 
     */
    public SplitType createSplitType() {
        return new SplitType();
    }

    /**
     * Create an instance of {@link ANDSplitType }
     * 
     */
    public ANDSplitType createANDSplitType() {
        return new ANDSplitType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CanonicalProcessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.apromore.org/CPF", name = "CanonicalProcess")
    public JAXBElement<CanonicalProcessType> createCanonicalProcess(CanonicalProcessType value) {
        return new JAXBElement<CanonicalProcessType>(_CanonicalProcess_QNAME, CanonicalProcessType.class, null, value);
    }

}
