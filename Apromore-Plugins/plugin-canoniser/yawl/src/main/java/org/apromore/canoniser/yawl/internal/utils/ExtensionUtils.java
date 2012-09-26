package org.apromore.canoniser.yawl.internal.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apromore.anf.AnnotationType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class ExtensionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionUtils.class);

    public static final String VIEWPORT = "viewport";

    public static final String LOCALE = "locale";

    public static final String FLOW = "flow";

    public static final String DOCUMENTATION = "documentation";

    public static final String LABEL = "label";

    public static final String DECORATOR = "decorator";

    public static final String FILTERS = "filters";

    public static final String CONSTRAINTS = "constraints";

    public static final String RESOURCING = "resourcing";

    public static final String FAMILIAR_PARTICIPANT = "familiarParticipant";

    public static final String CONFIGURATION = "configuration";

    public static final String CODELET = "codelet";

    public static final String YAWL_SERVICE = "yawlService";

    public static final String OUTPUT_PARAM = "outputParam";

    public static final String INPUT_PARAM = "inputParam";

    public static final String TIMER = "timer";

    public static final String METADATA = "metadata";

    public static final String YAWLSCHEMA_URL = "http://www.yawlfoundation.org/yawlschema";

    private static final JAXBContext YAWL_CONTEXT = initYAWLContext();

    private static JAXBContext initYAWLContext() {
        try {
            return JAXBContext.newInstance("org.yawlfoundation.yawlschema");
        } catch (final JAXBException e) {
            LOGGER.error("Could not create JAXBContext for YAWL Schema. This should never happen!", e);
            return null;
        }
    }

    private ExtensionUtils() {
    }

    public static boolean isValidFragment(final Object obj, final String namespace, final String localPart) {
        return ((Node) obj).getNamespaceURI().equals(namespace) && ((Node) obj).getLocalName().equals(localPart);
    }

    /**
     * 'Unmarshal' a YAWL object to its expected class. Will throw an JAXBException is class is not known. Note this method will also convert any
     * non-matching Object!
     *
     * @param object
     *            returned of 'getAny' or similiar
     * @param expectedClass
     *            object of this class will be returned
     * @return
     * @throws CanoniserException
     */
    public static <T> T unmarshalYAWLFragment(final Object object, final Class<T> expectedClass) throws CanoniserException {
        try {
            if (YAWL_CONTEXT != null) {
                final Unmarshaller u = YAWL_CONTEXT.createUnmarshaller();
                final JAXBElement<T> jaxbElement = u.unmarshal((Node) object, expectedClass);
                return jaxbElement.getValue();
            } else {
                throw new CanoniserException("Missing JAXBContext for YAWL!");
            }
        } catch (final JAXBException e) {
            throw new CanoniserException("Failed to parse YAWL extension with expected class " + expectedClass.getName(), e);
        }
    }

    /**
     * 'Marshal' a JAXB object of the YAWL schema to DOM Node that can be added to 'xs:any'
     *
     * @param elementName
     *            to use as local part
     * @param object
     *            to be marshaled
     * @param expectedClass
     *            class of the object
     * @return
     * @throws CanoniserException
     */
    public static <T> Element marshalYAWLFragment(final String elementName, final T object, final Class<T> expectedClass) throws CanoniserException {
        try {
            if (YAWL_CONTEXT != null) {
                final Marshaller m = YAWL_CONTEXT.createMarshaller();
                m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                final JAXBElement<T> element = new JAXBElement<T>(new QName(YAWLSCHEMA_URL, elementName), expectedClass, object);
                final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(false);
                Document doc;
                try {
                    doc = dbf.newDocumentBuilder().newDocument();
                } catch (final ParserConfigurationException e) {
                    throw new RuntimeException("Could not build document while marshalling YAWL fragment. This should never happen!", e);
                }
                m.marshal(element, doc);
                return doc.getDocumentElement();
            } else {
                throw new CanoniserException("Missing JAXBContext for YAWL!");
            }
        } catch (final JAXBException e) {
            throw new CanoniserException("Failed to add YAWL extension with name " + elementName, e);
        }
    }

    /**
     * Add the extension Element (XML) to the CPF Nodes attributes
     *
     * @param extensionElement
     * @param node
     */
    public static void addToExtensions(final Element extensionElement, final NodeType node) {
        node.getAttribute().add(createExtension(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF attributes
     *
     * @param extensionElement
     * @param cpt
     */
    public static void addToExtensions(final Element extensionElement, final CanonicalProcessType cpt) {
        cpt.getAttribute().add(createExtension(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF Net attributes
     *
     * @param extensionElement
     * @param net
     */
    public static void addToExtensions(final Element extensionElement, final NetType net) {
        net.getAttribute().add(createExtension(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF ResourceType attributes
     *
     * @param extensionElement
     * @param net
     */
    public static void addToExtensions(final Element extensionElement, final ResourceTypeType resourceType) {
        resourceType.getAttribute().add(createExtension(extensionElement));
    }

    private static TypeAttribute createExtension(final Element extensionElement) {
        final TypeAttribute attr = new ObjectFactory().createTypeAttribute();
        if (extensionElement.getNamespaceURI() != null) {
            attr.setName(extensionElement.getNamespaceURI() + "/" + extensionElement.getLocalName());
        } else {
            attr.setName(extensionElement.getLocalName());
        }
        attr.setAny(extensionElement);
        return attr;
    }

    /**
     * Get an extension attribute from a CPF Node
     *
     * @param node
     *            any CPF node
     * @param name
     *            name of the extension
     * @return
     */
    public static TypeAttribute getExtensionAttribute(final NodeType node, final String name) {
        for (TypeAttribute attr : node.getAttribute()) {
            if (name.equals(attr.getName())) {
                return attr;
            }
        }
        return null;
    }

    public static <T> T getFromNodeExtension(final NodeType node, final String elementName, final Class<T> excpectedClass, final T defaultValue) {
        TypeAttribute attr = getExtensionAttribute(node, elementName);
        if (attr != null && attr.getAny() != null) {
            try {
                return ExtensionUtils.unmarshalYAWLFragment(attr.getAny(), excpectedClass);
            } catch (CanoniserException e) {
                LOGGER.warn("Could unmarshal fragment from extension!", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Get an extension element from an AnnotationsType
     *
     * @param annotation
     * @param elementName
     * @param expectedClass
     * @param defaultValue
     * @return an object of type T in any case
     */
    public static <T> T getFromAnnotationsExtension(final AnnotationType annotation, final String elementName, final Class<T> expectedClass,
            final T defaultValue) {
        for (final Object extObj : annotation.getAny()) {
            try {
                if (ExtensionUtils.isValidFragment(extObj, ExtensionUtils.YAWLSCHEMA_URL, elementName)) {
                    return ExtensionUtils.unmarshalYAWLFragment(extObj, expectedClass);
                }
            } catch (final CanoniserException e) {
                LOGGER.warn("Could not convert YAWL extension {} with type {}", new String[] { elementName, expectedClass.getSimpleName() }, e);
            }
        }
        return defaultValue;
    }

}
