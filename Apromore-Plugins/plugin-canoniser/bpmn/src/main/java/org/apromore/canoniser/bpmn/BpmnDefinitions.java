package org.apromore.canoniser.bpmn;

// Java 2 Standard packges
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TDefinitions;

/**
 * BPMN 2.0 object model with canonisation methods.
 * <p>
 * To canonise a BPMN document, unmarshal the XML into an object of this class, and invoke the {@link #canonise} method.
 * The resulting {@link CanoniserResult} represents a list of CPF/ANF pairs.
 * Because a BPMN document may describe a collection of processes (for example, in a collaboration) the resulting
 * {@link CanoniserResult} may contain several {@link CanonicalProcessType} instances.
 * <p>
 * To decanonise a canonical model into BPMN, invoke the constructor {@link #BpmnDefinitions(CanonicalProcessType, AnnotationsType)}.
 * Only individual canonical models may be decanonised; there is no facility for generating a BPMN document containing
 * multiple top-level processes.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @version 0.4
 * @since 0.3
 */
@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL", name = "definitions")
public class BpmnDefinitions extends TDefinitions {

    /** Logger.  Named after the class. */
    @XmlTransient
    private final Logger logger = Logger.getLogger(BpmnDefinitions.class.getCanonicalName());

    /** Apromore URI. */
    public static final String APROMORE_URI = "http://apromore.org";

    /** Apromore version. */
    public static final String APROMORE_VERSION = "0.4";

    /**
     * Namespace of the document root element.
     *
     * Chosen arbitrarily to match Signavio.
     */
    public static final String TARGET_NS = "http://www.signavio.com/bpmn20";

    /** BPMN 2.0 namespace. */
    public static final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    /** XML schema for BPMN 2.0. */
    private static final Schema BPMN_SCHEMA;

    /** CPF schema version. */
    public static final String CPF_VERSION = "1.0";

    /** XPath expression language URI. */
    public static final String XPATH_URI = "http://www.w3.org/1999/XPath";

    /** XML Schema datatype language URI. */
    public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema";

    static {
        try {
            if (System.getProperty("bpmnvalidation") != null) {
                /* By default, marshallers and unmarshallers don't validate BPMN documents.
                 * Validation can be enabled by passing a -Dbpmnvalidation flag to Maven.
                 *
                 * This switches to loading the BPMN schema from the filesystem, as so:
                 */
                BPMN_SCHEMA = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
                    new File("../../../Apromore-Schema/bpmn-schema/src/main/resources/xsd/BPMN20.xsd")
                );
                /* The reason this isn't the default behavior is because it only works when
                 * executed from the complete Apromore checkout.  When Jenkins runs the
                 * tests, it tests each element in isolation and so the BPMN schema can't be loaded.
                 *
                 * Hence, we have the following code which loads the BPMN from the classpath:
                 */
            } else {
                ClassLoader loader = BpmnDefinitions.class.getClassLoader();
                BPMN_SCHEMA = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource[] {
                    new StreamSource(loader.getResourceAsStream("xsd/DC.xsd")),
                    new StreamSource(loader.getResourceAsStream("xsd/DI.xsd")),
                    new StreamSource(loader.getResourceAsStream("xsd/BPMNDI.xsd")),
                    new StreamSource(loader.getResourceAsStream("xsd/Semantic.xsd")),
                    new StreamSource(loader.getResourceAsStream("xsd/BPMN20.xsd"))
                });
                /* Unfortunately, the above code doesn't work; it fails to parse the root <definitions>.
                 * Until someone can figure out why it fails, BPMN validation is off unless explicitly
                 * requested by -Dbpmnvalidation.
                 */
            }
            assert BPMN_SCHEMA != null;

        } catch (SAXException e) {
            throw new RuntimeException("Couldn't parse BPMN XML schema", e);
        }
    }

    /**
     * No-arg constructor.
     *
     * Required for JUnit to work.
     */
    public BpmnDefinitions() { }

    /**
     * Construct a BPMN model from a canonical model.
     * In other words, de-canonise a CPF/ANF model into a BPMN one.
     *
     * @param cpf  a canonical process model
     * @param anf  annotations for the canonical process model
     * @throws CanoniserException if unable to generate BPMN from the given CPF and ANF arguments
     */
    public BpmnDefinitions(final CanonicalProcessType cpf, final AnnotationsType anf) throws CanoniserException {

        Initializer initializer = new Initializer(cpf);

        // We can get by without an ANF parameter, but we definitely need a CPF
        if (cpf == null) {
            throw new CanoniserException("Cannot create BPMN from null CPF");
        }

        // Set attributes of the document root
        setExporter(APROMORE_URI);
        setExporterVersion(APROMORE_VERSION);
        setExpressionLanguage(XPATH_URI);
        setId(null);
        setName(cpf.getName());
        setTargetNamespace(TARGET_NS);
        setTypeLanguage(XSD_URI);

        /* TODO - add as extension attributes
        String author = cpf.getAuthor();
        String creationDate = cpf.getCreationDate();
        String modificationDate = cpf.getModificationDate();
        */

        // Assume there will be pools, all of which belong to a single collaboration
        TCollaboration collaboration = initializer.factory.createTCollaboration();
        JAXBElement<TCollaboration> wrapperCollaboration = initializer.factory.createCollaboration(collaboration);

        // Translate CPF Nets as BPMN Processes
        for (final NetType net : initializer.cpf.getNet()) {

            // Only root elements are decanonised here; subnets are dealt with by recursion
            if (!cpf.getRootIds().contains(net.getId())) {
                continue;
            }

            getRootElement().add(initializer.factory.createProcess(new BpmnProcess(net, initializer, collaboration)));

            /*
            // If we haven't added the collaboration yet and this process is a pool, add the collaboration
            if (!getRootElement().contains(wrapperCollaboration)) {
                getRootElement().add(wrapperCollaboration);
            }
            */
        }

        // Make sure all the deferred fields did eventually get filled in
        initializer.close();

        // Translate any ANF annotations into a BPMNDI diagram element
        if (anf != null) {
            getBPMNDiagram().add(new BpmndiDiagram(anf, initializer));
        }
    }

    /**
     * Construct an instance from a stream.
     *
     * @param in  a BPMN-formatted stream
     * @param validate  whether to perform schema validation
     * @throws JAXBException if the stream can't be parsed as BPMN
     * @return the parsed instance
     */
    public static BpmnDefinitions newInstance(final InputStream in, final Boolean validate) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(BpmnObjectFactory.class,
                                                            org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                            org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                            org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                            org.omg.spec.dd._20100524.di.ObjectFactory.class)
                                               .createUnmarshaller();
        unmarshaller.setListener(new BpmnUnmarshallerListener());
        if (validate) {
            unmarshaller.setSchema(BPMN_SCHEMA);
        }
        return unmarshaller.unmarshal(new StreamSource(in), BpmnDefinitions.class)
                           .getValue();  // discard the JAXBElement wrapper
    }

    /**
     * Serialize this instance to a steam.
     *
     * Default deserialization is pretty-printed but not schema-validated.
     *
     * @param out  the stream for writing
     * @param validate  whether to perform schema validation
     * @throws JAXBException if the steam can't be written to
     */
    public void marshal(final OutputStream out, final Boolean validate) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(BpmnObjectFactory.class,
                                                        org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                        org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                        org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                        org.omg.spec.dd._20100524.di.ObjectFactory.class)
                                           .createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (validate) {
            marshaller.setSchema(BPMN_SCHEMA);
        }
        marshaller.marshal(this, out);
    }

    /**
     * Workaround for incorrect marshalling of {@link TLane#getFlowNodeRef} by JAXB.
     *
     * A flow node reference on a lane ought to be serialized as
     * <pre>
     * &lt;lane&gt;
     *   &lt;flowNodeRef&gt;id-123&lt;/flowNodeRef&gt;
     * &lt;/lane&gt;
     * </pre>
     * but instead they end up serialized as
     * <pre>
     * &lt;lane&gt;
     *   &lt;task id="id-123"/&gt;
     * &lt;/lane&gt;
     * </pre>
     * This method applies an XSLT transform to correct things.
     *
     * @param definitions  the buggy JAXB document
     * @param factory  source of elements for the result document
     * @throws JAXBException if <code>definitions</code> can't be marshalled to XML or unmarshalled back
     * @throws TransformerException  if the XSLT transformation fails
     * @return corrected JAXB document
     */
    // TODO - change the return type and the factory parameter to be Defintions and ObjectFactory, and move to bpmn-schema
    public static BpmnDefinitions correctFlowNodeRefs(final BpmnDefinitions definitions,
                                                      final BpmnObjectFactory factory) throws JAXBException, TransformerException {

        JAXBContext context = JAXBContext.newInstance(factory.getClass(),
                                                      org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                      org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.di.ObjectFactory.class);

        // Marshal the BPMN into a DOM tree
        DOMResult intermediateResult = new DOMResult();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(factory.createDefinitions(definitions), intermediateResult);

        // Apply the XSLT transformation, generating a new DOM tree
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(
            new StreamSource(ClassLoader.getSystemResourceAsStream("xsd/fix-flowNodeRef.xsl"))
        );
        DOMSource finalSource = new DOMSource(intermediateResult.getNode());
        DOMResult finalResult = new DOMResult();
        transformer.transform(finalSource, finalResult);

        // Unmarshal back to JAXB
        Object def2 = context.createUnmarshaller().unmarshal(finalResult.getNode());
        return ((JAXBElement<BpmnDefinitions>) def2).getValue();
    }
}
