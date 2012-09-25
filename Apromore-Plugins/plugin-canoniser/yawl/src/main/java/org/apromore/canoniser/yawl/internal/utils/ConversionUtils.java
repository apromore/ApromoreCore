package org.apromore.canoniser.yawl.internal.utils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Helper class for all kind of various static methods and constants
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class ConversionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionUtils.class);

    public static final org.apromore.cpf.ObjectFactory CPF_FACTORY = new org.apromore.cpf.ObjectFactory();

    public static final org.apromore.anf.ObjectFactory ANF_FACTORY = new org.apromore.anf.ObjectFactory();

    public static final org.yawlfoundation.yawlschema.ObjectFactory YAWL_FACTORY = new org.yawlfoundation.yawlschema.ObjectFactory();

    public static final org.yawlfoundation.yawlschema.orgdata.ObjectFactory YAWL_ORG_FACTORY = new org.yawlfoundation.yawlschema.orgdata.ObjectFactory();

    public static final String YAWLSCHEMA_URL = "http://www.yawlfoundation.org/yawlschema";

    private static final int MAX_ITERATION_COUNT = 10000;

    private static final Pattern COLOR_REGEX = Pattern.compile("R:([0-9][0-9][0-9])G:([0-9][0-9][0-9])B:([0-9][0-9][0-9])");

    private static final JAXBContext YAWL_CONTEXT = initYAWLContext();

    /**
     * Hidden constructor as this class is not meant to be instantiated
     */
    private ConversionUtils() {
        super();
    }

    private static JAXBContext initYAWLContext() {
        try {
            return JAXBContext.newInstance("org.yawlfoundation.yawlschema");
        } catch (final JAXBException e) {
            LOGGER.error("Could not create JAXBContext for YAWL Schema. This should never happen!", e);
            return null;
        }
    }

    public static String convertColorToString(final int colorAsInt) {
        final int rgb = colorAsInt;
        final int red = (rgb >> 16) & 0x0ff;
        final int green = (rgb >> 8) & 0x0ff;
        final int blue = (rgb) & 0x0ff;
        return String.format("R:%dG:%dB:%d", red, green, blue);
    }

    public static BigInteger convertColorToBigInteger(final String colorAsString) {
        final Matcher matcher = COLOR_REGEX.matcher(colorAsString);
        if (matcher.matches()) {
            final int r = Integer.valueOf(matcher.group(1));
            final int g = Integer.valueOf(matcher.group(2));
            final int b = Integer.valueOf(matcher.group(3));
            final int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
            return BigInteger.valueOf(rgb);
        }
        return BigInteger.valueOf(0);
    }

    public static String buildObjectId(final String netId, final String varName) {
        return netId + "_" + varName;
    }

    public static String buildObjectId(final NodeType parentTask, final String varName) {
        return parentTask.getId() + "_" + varName;
    }

    public static String generateUniqueName(final String originalName, final Set<String> nameSet) {
        int i = 1;
        String newName = originalName + i;
        while (nameSet.contains(newName) && (i < MAX_ITERATION_COUNT)) { // Prevent infinite loops on strange input data
            newName = originalName + (++i);
        }
        return newName;
    }

    public static boolean isValidFragment(final Object obj, final String namespace, final String localPart) {
        return ((Node) obj).getNamespaceURI().equals(namespace) && ((Node) obj).getLocalName().equals(localPart);
    }

    /**
     * 'Unmarshal' a YAWL object to its expected class. Will throw an JAXBException is class is not known or wrong.
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
        final TypeAttribute attr = CPF_FACTORY.createTypeAttribute();
        if (extensionElement.getNamespaceURI() != null) {
            attr.setName(extensionElement.getNamespaceURI() + "/" + extensionElement.getLocalName());
        } else {
            attr.setName(extensionElement.getLocalName());
        }
        attr.setAny(extensionElement);
        return attr;
    }

    /**
     * Get an extension element from the Node
     *
     * @param node
     * @param name
     * @return
     */
    public static TypeAttribute getFromExtensions(final NodeType node, final String name) {
        for (TypeAttribute attr: node.getAttribute()) {
            if (name.equals(attr.getName())) {
                return attr;
            }
        }
        return null;
    }

    /**
     * Returns a nicely formatted String with information about a Collection of Nodes
     *
     * @param nodes
     *            Collection of Nodes
     * @return
     */
    public static String nodesToString(final Collection<NodeType> nodes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final Iterator<NodeType> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            final NodeType node = nodeIterator.next();
            sb.append(toString(node));
            if (nodeIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a nicely formatted String with information about a Collection of Edges
     *
     * @param edges
     *            Collection of Edges
     * @return
     */
    public static String edgesToString(final Collection<EdgeType> edges) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final Iterator<EdgeType> edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            final EdgeType edge = edgeIterator.next();
            sb.append(toString(edge));
            if (edgeIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a nicely formatted String with information about any Node
     *
     * @param node
     *            NodeType
     * @return
     */
    public static String toString(final NodeType node) {
        final ToStringBuilder sb = new ToStringBuilder(node, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", node.getId()).append("name", node.getName()).toString();
    }

    /**
     * Returns a nicely formatted String with information about a Work Node
     *
     * @param node
     *            NodeType
     * @return
     */
    public static String toString(final WorkType node) {
        final ToStringBuilder sb = new ToStringBuilder(node, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", node.getId()).append("name", node.getName()).append("cancelNode", node.getCancelNodeId())
                .append("cancelEdge", node.getCancelEdgeId()).append("inputExpr", node.getInputExpr()).append("outputExpr", node.getInputExpr())
                .toString();
    }

    /**
     * Returns a nicely formatted String with information about an Edge
     *
     * @param edge
     *            EdgeType
     * @return
     */
    public static String toString(final EdgeType edge) {
        final ToStringBuilder sb = new ToStringBuilder(edge, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("source", edge.getSourceId()).append("target", edge.getTargetId()).append("id", edge.getId()).toString();
    }

    /**
     * Returns a nicely formatted String with information about an Net
     *
     * @param net
     *            NetType
     * @return
     */
    public static String toString(final NetType net) {
        final ToStringBuilder sb = new ToStringBuilder(net, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", net.getId()).append("name", net.getName()).append("nodes", nodesToString(net.getNode()))
                .append("edge", edgesToString(net.getEdge())).toString();
    }

}