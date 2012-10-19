package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.CPF_VERSION;
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * CPF 1.0 document root with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessType extends CanonicalProcessType {

    /** Qualified name of the root element <code>cpf:CanonicalProcess</code>. */
    private static final QName CPF_ROOT = new QName("http://www.apromore.org/CPF", "CanonicalProcess");

    /** XML schema for CPF 1.0. */
    private static final Schema CPF_SCHEMA;

    /** Property name for use with {@link Unmarshaller#setProperty} to configure a {@link com.sun.xml.bind.IDResolver}. */
    private static final String ID_RESOLVER = "com.sun.xml.bind.IDResolver";

    /** Property name for use with {@link Unmarshaller#setProperty} to configure an alternate JAXB ObjectFactory. */
    private static final String OBJECT_FACTORY = "com.sun.xml.bind.ObjectFactory";

    static {
        ClassLoader loader = CpfCanonicalProcessType.class.getClassLoader();
        try {
            CPF_SCHEMA  = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
                new StreamSource(loader.getResourceAsStream("xsd/cpf_1.0.xsd"))
            );
        } catch (SAXException e) {
            throw new RuntimeException("Unable to parse CPF schema", e);
        }
    }

    /** No-arg constructor. */
    public CpfCanonicalProcessType() {
        super();
    }

    /**
     * Construct a CPF document corresponding to a BPMN document.
     *
     * The resulting CPF requires its <code>uri</code> property to be set in order to be schema-legal.
     *
     * @param definitions  a BPMN document
     * @throws CanoniserException  if the CPF document can't be constructed
     */
    public CpfCanonicalProcessType(final BpmnDefinitions definitions) throws CanoniserException {
        super();

        // Generate identifiers scoped to this single CPF document
        final IdFactory cpfIdFactory = new IdFactory();

        // Map BPMN flow nodes to the CPF lanes containing them
        final Map<TFlowNode, TLane> laneMap = new HashMap<TFlowNode, TLane>();

        // Map BPMN flow nodes to CPF nodes
        final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap = new HashMap<TFlowNode, NodeType>();

        final Initializer initializer = new Initializer(this, cpfIdFactory, definitions, laneMap, bpmnFlowNodeToCpfNodeMap);

        // Populate attributes
        setName(requiredName(definitions.getName()));
        setVersion(CPF_VERSION);

        // Each top-level BPMN Process becomes a CPF Net in the rootIDs list
        for (JAXBElement<? extends TRootElement> rootElement : definitions.getRootElement()) {
            if (rootElement.getValue() instanceof TProcess) {
                TProcess process = (TProcess) rootElement.getValue();
                new CpfNetType(new ProcessWrapper(process), null, initializer);
            }
        }
    }

    /**
     * Construct an instance from a CPF-formatted stream.
     *
     * @param in  a CPF-formatted stream
     * @param validate  whether to perform schema validation while parsing
     * @return JAXB object model of the parsed stream
     * @throws JAXBException if the stream can't be unmarshalled as CPF
     */
    public static CanonicalProcessType newInstance(final InputStream in, final Boolean validate) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT)
                                               .createUnmarshaller();
        unmarshaller.setListener(new CpfUnmarshallerListener());
        unmarshaller.setProperty(ID_RESOLVER, new CpfIDResolver());
        unmarshaller.setProperty(OBJECT_FACTORY, new ObjectFactory());
        if (validate) {
            unmarshaller.setSchema(CPF_SCHEMA);
        }
        return unmarshaller.unmarshal(new StreamSource(in), CanonicalProcessType.class)
                           .getValue();
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    public NetType findNet(final String id) throws CanoniserException {

        for (final NetType net : getNet()) {
            if (id.equals(net.getId())) {
                return net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }

    /**
     * Write this instance to a stream.
     *
     * @param out  the destination stream
     * @param validate  whether to perform schema validation during serialization
     * @throws JAXBException if serialization fails
     */
    public void marshal(final OutputStream out, final Boolean validate) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (validate) {
            marshaller.setSchema(CPF_SCHEMA);
        }
        marshaller.marshal(new JAXBElement<CanonicalProcessType>(CPF_ROOT, CanonicalProcessType.class, this), out);
    }
}
