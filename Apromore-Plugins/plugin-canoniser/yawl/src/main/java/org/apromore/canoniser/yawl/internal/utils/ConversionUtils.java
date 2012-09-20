package org.apromore.canoniser.yawl.internal.utils;

import java.math.BigInteger;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apromore.cpf.NodeType;
import org.w3c.dom.Node;

/**
 * Helper class for all kind of various static methods and constants
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public class ConversionUtils {

    public static final String EXTENSION_DATA_TYPE_DEFINITIONS = "org.apromore.canoniser.yawl.dataTypeDefinitions";

    public static final String YAWL_COMPLEX_MAPPING_EXTENSION = "yawlComplexMapping";

    public static final String YAWL_EXPRESSION_EXTENSION = "yawlOriginalExpression";

    public static final String YAWLSCHEMA_URL = "http://www.yawlfoundation.org/yawlschema";

    public static final String CONFIGURATION_EXTENSION = "configuration";

    private static final Pattern COLOR_REGEX = Pattern.compile("R([0-9][0-9][0-9])G([0-9][0-9][0-9])B([0-9][0-9][0-9])");

    private static final JAXBContext YAWL_CONTEXT = initYAWLContext();

    private static JAXBContext initYAWLContext() {
        try {
            return JAXBContext.newInstance("org.yawlfoundation.yawlschema");
        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertColorToString(final int colorAsInt) {
        final int rgb = colorAsInt;
        final int red = (rgb >> 16) & 0x0ff;
        final int green = (rgb >> 8) & 0x0ff;
        final int blue = (rgb) & 0x0ff;
        final String color = String.format("R%dG%dB%d", red, green, blue);
        return color;
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
        while (nameSet.contains(newName) && (i < 1000)) { // Prevent infinite loops on strange input data
            newName = originalName + (++i);
        }
        return newName;
    }

    public static boolean isValidFragment(final Object obj, final String namespace, final String localPart) throws JAXBException {
        return ((Node) obj).getNamespaceURI().equals(namespace) && ((Node) obj).getLocalName().equals(localPart);
    }

    /**
     * 'Unmarshal' a YAWL object to its JAXB class
     * 
     * @param obj
     * @param expectedClass
     * @return
     * @throws JAXBException
     */
    public static <T> T unmarshalYAWLFragment(final Object obj, final Class<T> expectedClass) throws JAXBException {
        final Unmarshaller u = YAWL_CONTEXT.createUnmarshaller();
        final JAXBElement<T> jaxbElement = u.unmarshal((Node) obj, expectedClass);
        return jaxbElement.getValue();
    }

    /**
     * 'Marshal' a JAXB object of the YAWL schema to JAXB fragment
     * 
     * @param elementName
     * @param obj
     * @param expectedClass
     * @return
     */
    public static <T> JAXBElement<T> marshalYAWLFragment(final String elementName, final T obj, final Class<T> expectedClass) {
        return new JAXBElement<T>(new QName(YAWLSCHEMA_URL, elementName), expectedClass, obj);
    }
}
