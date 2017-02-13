/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.internal.utils;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.yawlfoundation.yawlschema.ObjectFactory;

/**
 * Provides various helper methods to work with the TypeAttribute extension of CPF.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public final class ExtensionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionUtils.class);

    public static final String LOCALE = "locale";

    public static final String FLOW = "flow";

    public static final String DOCUMENTATION = "documentation";

    public static final String LABEL = "label";

    public static final String DECORATOR = "decorator";

    public static final String FILTERS = "filters";

    public static final String CONSTRAINTS = "constraints";

    public static final String OFFER = "offer";

    public static final String FAMILIAR_PARTICIPANT = "familiarParticipant";

    public static final String CONFIGURATION = "configuration";

    public static final String CODELET = "codelet";

    public static final String YAWL_SERVICE = "yawlService";

    public static final String TIMER = "timer";

    public static final String METADATA = "metadata";

    public static final String ALLOCATE = "allocate";

    public static final String START = "start";

    public static final String FILTER = "filter";

    public static final String DATA_TYPE_DEFINITIONS = "dataTypeDefinitions";

    public static final String INPUT_VARIABLE = "inputVariable";

    public static final String LOCAL_VARIABLE = "localVariable";

    public static final String OUTPUT_VARIABLE = "outputVariable";

    public static final String INPUT_PARAM = "inputParam";

    public static final String OUTPUT_PARAM = "outputParam";

    public static final String YAWLSCHEMA_URL = "http://www.yawlfoundation.org/yawlschema";

    private static final JAXBContext YAWL_CONTEXT = initYAWLContext();

    private static JAXBContext initYAWLContext() {
        try {
            return JAXBContext.newInstance("org.yawlfoundation.yawlschema", ObjectFactory.class.getClassLoader());
        } catch (final JAXBException e) {
            LOGGER.error("Could not create JAXBContext for YAWL Schema. This should never happen!", e);
            return null;
        }
    }

    private ExtensionUtils() {
    }

    public static boolean isValidFragment(final Object obj, final String namespace, final String localPart) {
        return org.apromore.canoniser.utils.ExtensionUtils.isValidFragment(obj, namespace, localPart);
    }

    /**
     * 'Unmarshal' a YAWL object to its expected class. Will throw an JAXBException is class is not known. Note this method will also convert any
     * non-matching Object!
     *
     * @param object
     *            returned of 'getAny' or similiar
     * @param expectedClass
     *            object of this class will be returned
     * @return YAWL Object matching excpected class
     * @throws CanoniserException
     */
    public static <T> T unmarshalYAWLFragment(final Object object, final Class<T> expectedClass) throws CanoniserException {
        return org.apromore.canoniser.utils.ExtensionUtils.unmarshalFragment(object, expectedClass, YAWL_CONTEXT);
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
     * @return XML Element containing the YAWL fragment
     * @throws CanoniserException
     */
    public static <T> Element marshalYAWLFragment(final String elementName, final T object, final Class<T> expectedClass) throws CanoniserException {
        return org.apromore.canoniser.utils.ExtensionUtils.marshalFragment(elementName, object, expectedClass, YAWLSCHEMA_URL, YAWL_CONTEXT);
    }

    /**
     * Add the extension Element (XML) to the CPF Nodes attributes
     *
     * @param extensionElement any XML Element
     * @param node CPF Node
     */
    public static void addToExtensions(final Element extensionElement, final NodeType node) {
        LOGGER.debug("Added YAWL extension {} to Node {}", extensionElement.getNodeName(), node.getId());
        org.apromore.canoniser.utils.ExtensionUtils.addToExtensions(extensionElement, node, name(extensionElement));
    }

    private static String name(final Element extensionElement) {
        String name = extensionElement.getLocalName();
        if (extensionElement.getNamespaceURI() != null) {
            name = extensionElement.getNamespaceURI() + "/" + name;
        }
        return name;
    }

    /**
     * Add the extension Element (XML) to the CPF attributes
     *
     * @param extensionElement any XML Element
     * @param cpt CPF Process
     */
    public static void addToExtensions(final Element extensionElement, final CanonicalProcessType cpt) {
        LOGGER.debug("Added YAWL extension {} to CPF", extensionElement.getNodeName());
        org.apromore.canoniser.utils.ExtensionUtils.addToExtensions(extensionElement, cpt, name(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF Edge attributes
     *
     * @param extensionElement any XML Element
     * @param edge CPF Edge
     */
    public static void addToExtensions(final Element extensionElement, final EdgeType edge) {
        LOGGER.debug("Added YAWL extension {} to Edge {}", extensionElement.getNodeName(), edge.getId());
        org.apromore.canoniser.utils.ExtensionUtils.addToExtensions(extensionElement, edge, name(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF Net attributes
     *
     * @param extensionElement any XML Element
     * @param net CPF Net
     */
    public static void addToExtensions(final Element extensionElement, final NetType net) {
        LOGGER.debug("Added YAWL extension {} to Net {}", extensionElement.getNodeName(), net.getId());
        org.apromore.canoniser.utils.ExtensionUtils.addToExtensions(extensionElement, net, name(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF Object attributes
     *
     * @param extensionElement any XML Element
     * @param object CPF Object
     */
    public static void addToExtensions(final Element extensionElement, final ObjectType object) {
        LOGGER.debug("Added YAWL extension {} to Object {}", extensionElement.getNodeName(), object.getId());
        org.apromore.canoniser.utils.ExtensionUtils.addToExtensions(extensionElement, object, name(extensionElement));
    }

    /**
     * Add the extension Element (XML) to the CPF ResourceType attributes
     *
     * @param extensionElement any XML Element
     * @param resourceType CPF resource
     */
    public static void addToExtensions(final Element extensionElement, final ResourceTypeType resourceType) {
        LOGGER.debug("Added YAWL extension {} to Resource {}", extensionElement.getNodeName(), resourceType.getId());
        org.apromore.canoniser.utils.ExtensionUtils.addToExtensions(extensionElement, resourceType, name(extensionElement));
    }

    /**
     * Get an extension attribute from a CPF Node
     *
     * @param node
     *            any CPF node
     * @param name
     *            name of the extension
     * @return just the first TypeAttribute
     */
    public static TypeAttribute getExtensionAttribute(final NodeType node, final String name) {
        return org.apromore.canoniser.utils.ExtensionUtils.getExtensionAttribute(node, name);
    }

    /**
     * Get all matching extension attribute from a CPF Node
     *
     * @param node
     *            any CPF node
     * @param name
     *            name of the extension
     * @return List of TypeAttribute
     */
    public static List<TypeAttribute> getExtensionAttributes(final TaskType node, final String name) {
        return org.apromore.canoniser.utils.ExtensionUtils.getExtensionAttributes(node, name);
    }


    /**
     * Returns if the extension attribute is present in the list of attributes.
     *
     * @param attributes List of extension attributes
     * @param name of the extension attribute
     * @return true if found, false otherwise
     */
    public static boolean hasExtension(final List<TypeAttribute> attributes, final String name) {
        return org.apromore.canoniser.utils.ExtensionUtils.hasExtension(attributes, name);
    }

    /**
     * Get an extension attribute from a CPF Node and unmarshals it to using the YAWL namespace.
     *
     * @param node CPF Node
     * @param elementName of Extension
     * @param expectedClass from YAWL schema
     * @param defaultValue if not found
     * @return Object of excpectedClass
     */
    public static <T> T getFromNodeExtension(final NodeType node, final String elementName, final Class<T> expectedClass,
            final T defaultValue) {
        return org.apromore.canoniser.utils.ExtensionUtils.getFromExtension(node.getAttribute(), elementName, expectedClass, defaultValue,
                                                                            YAWL_CONTEXT);
    }

    /**
     * Get an extension attribute from a list of CPF attributes and unmarshals it to using the YAWL namespace.
     *
     * @param attributes CPF list of attributes
     * @param elementName of Extension
     * @param expectedClass from YAWL schema
     * @param defaultValue if not found
     * @return Object of excpectedClass
     */
    public static <T> T getFromExtension(final List<TypeAttribute> attributes, final String elementName, final Class<T> expectedClass,
            final T defaultValue) {
        return org.apromore.canoniser.utils.ExtensionUtils.getFromExtension(attributes, elementName, expectedClass, defaultValue, YAWL_CONTEXT);
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
        return org.apromore.canoniser.utils.ExtensionUtils.getFromAnnotationsExtension(annotation, elementName, expectedClass, defaultValue,
                                                                                       YAWL_CONTEXT);
    }


}
