/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Utilities for working with CPF {@link TypeAttribute}s.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public final class ExtensionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionUtils.class);

    public static final String YAWLSCHEMA_URL = "http://www.yawlfoundation.org/yawlschema";

    /** Static library cannot be instantiated. */
    private ExtensionUtils() {
    }

    /**
     * @param obj  an XML {@link Node}
     * @param namespace  the namespace of an extension name
     * @param localPart  the local part of an extension name
     * @return whether the <code>obj</code>'s QName matches the given extension name
     */
    public static boolean isValidFragment(final Object obj, final String namespace, final String localPart) {
        return ((Node) obj).getNamespaceURI().equals(namespace) && ((Node) obj).getLocalName().equals(localPart);
    }

    /**
     * 'Unmarshal' a native object to its expected class.
     * Will throw an JAXBException if class is not known.
     * Note this method will also convert any non-matching Object!
     *
     * @param object
     *            returned of 'getAny' or similiar
     * @param expectedClass
     *            object of this class will be returned
     * @param nativeContext
     *            context for marshalling the origin format
     * @return JAXB Object matching expected class
     * @throws CanoniserException
     */
    public static <T> T unmarshalFragment(final Object      object,
                                          final Class<T>    expectedClass,
                                          final JAXBContext nativeContext) throws CanoniserException {
        try {
            if (nativeContext != null) {
                final Unmarshaller u = nativeContext.createUnmarshaller();
                final JAXBElement<T> jaxbElement = u.unmarshal((Node) object, expectedClass);
                return jaxbElement.getValue();
            } else {
                throw new CanoniserException("Missing JAXBContext");
            }
        } catch (final JAXBException e) {
            throw new CanoniserException("Failed to parse extension with expected class " + expectedClass.getName(), e);
        }
    }

    /**
     * 'Marshal' a JAXB object of some native schema to a DOM Node that can be added to 'xs:any'.
     *
     * @param elementName
     *            to use as local part
     * @param object
     *            to be marshaled
     * @param expectedClass
     *            class of the object
     * @param nativeNS
     *            Namespace URL for the origin format
     * @param nativeContext
     *            context for marshalling the origin format
     * @return XML Element containing the markup fragment
     * @throws CanoniserException
     */
    public static <T> Element marshalFragment(final String      elementName,
                                              final T           object,
                                              final Class<T>    expectedClass,
                                              final String      nativeNS,
                                              final JAXBContext nativeContext) throws CanoniserException {
        try {
            if (nativeContext != null) {
                final Marshaller m = nativeContext.createMarshaller();
                m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                final JAXBElement<T> element = new JAXBElement<T>(new QName(nativeNS, elementName), expectedClass, object);
                final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(false);
                Document doc;
                try {
                    doc = dbf.newDocumentBuilder().newDocument();
                } catch (final ParserConfigurationException e) {
                    throw new CanoniserException("Could not build document while marshalling fragment. This should never happen!", e);
                }
                m.marshal(element, doc);
                return doc.getDocumentElement();
            } else {
                throw new CanoniserException("Missing JAXBContext");
            }
        } catch (final JAXBException e) {
            throw new CanoniserException("Failed to add extension with name " + elementName, e);
        }
    }

    /**
     * Add the extension Element (XML) to the CPF Nodes attributes.
     *
     * @param extensionElement  any XML Element
     * @param node  CPF Node
     * @param ownerName  identifier for the processor that can interpret this attribute
     */
    public static void addToExtensions(final Element extensionElement, final NodeType node, final String ownerName) {
        node.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    /**
     * Add the extension Element (XML) to the CPF attributes.
     *
     * @param extensionElement any XML Element
     * @param cpt CPF Process
     */
    public static void addToExtensions(final Element extensionElement, final CanonicalProcessType cpt, final String ownerName) {
        cpt.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    /**
     * Add the extension Element (XML) to the CPF Edge attributes.
     *
     * @param extensionElement any XML Element
     * @param edge CPF Edge
     */
    public static void addToExtensions(final Element extensionElement, final EdgeType edge, final String ownerName) {
        edge.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    /**
     * Add the extension Element (XML) to the CPF Net attributes.
     *
     * @param extensionElement any XML Element
     * @param net CPF Net
     */
    public static void addToExtensions(final Element extensionElement, final NetType net, final String ownerName) {
        net.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    /**
     * Add the extension Element (XML) to the CPF Object attributes.
     *
     * @param extensionElement any XML Element
     * @param object CPF Object
     */
    public static void addToExtensions(final Element extensionElement, final ObjectType object, final String ownerName) {
        object.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    /**
     * Add the extension Element (XML) to the CPF ObjectRef attributes.
     *
     * @param extensionElement any XML Element
     * @param objectRef CPF ObjectRef
     */
    public static void addToExtensions(final Element extensionElement, final ObjectRefType objectRef, final String ownerName) {
        objectRef.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    /**
     * Add the extension Element (XML) to the CPF ResourceType attributes.
     *
     * @param extensionElement any XML Element
     * @param resourceType CPF resource
     */
    public static void addToExtensions(final Element extensionElement, final ResourceTypeType resourceType, final String ownerName) {
        resourceType.getAttribute().add(createExtension(extensionElement, ownerName));
    }

    private static TypeAttribute createExtension(final Element extensionElement, final String ownerName) {
        final TypeAttribute attr = new ObjectFactory().createTypeAttribute();
        attr.setName(ownerName);
        attr.setAny(extensionElement);
        return attr;
    }

    /**
     * Get an extension attribute from a CPF Node.
     *
     * @param node
     *            any CPF node
     * @param name
     *            name of the extension
     * @return just the first TypeAttribute
     */
    public static TypeAttribute getExtensionAttribute(final NodeType node, final String name) {
        return getExtensionFromAttributes(node.getAttribute(), name);
    }

    /**
     * Sets a value on a CPF element which can be read back using {@link #hasExtension}.
     *
     * @param attributes  the attributes of the CPF element to annotate
     * @param name  {@link TypeAttribute#getName} of the attribute to flag
     * @param value  whether to flag the attribute as present or absent
     */
    public static void flagExtension(final List<TypeAttribute> attributes, final String name, boolean value) {
        if (value) {
            // Check whether there's already an existing flag
            for (TypeAttribute attribute : attributes) {
                if (name.equals(attribute.getName())) {
                    attribute.setValue(null);
                    return;  // already flagged, so nothing needs to be changed
                }
            }

            // Didn't find an existing flag, so create and add one
            TypeAttribute attribute = new TypeAttribute();
            attribute.setName(name);
            attributes.add(attribute);

        } else {
            // Remove any existing flags
            Iterator<TypeAttribute> i = attributes.iterator();
            while (i.hasNext()) {
                if (name.equals(i.next().getName())) {
                    i.remove();
                }
            }
        }
    }

    /**
     * Gets a value on a CPF element which was assigned using {@link #setString}.
     *
     * @param attributes  the attributes of the annotated CPF element
     * @param name  {@link TypeAttribute#getName} of the attribute
     * @return stored text
     */
    public static String getString(final List<TypeAttribute> attributes, final String name) {
        TypeAttribute attribute = getExtensionFromAttributes(attributes, name);
        return attribute == null ? null : attribute.getValue();
    }

    /**
     * Sets a value on a CPF element which can be read back using {@link #getString}.
     *
     * @param attributes  the attributes of the CPF element to annotate
     * @param name  {@link TypeAttribute#getName} of the attribute to flag
     * @param value  stored text
     */
    public static void setString(final List<TypeAttribute> attributes, final String name, final String value) {

        // Remove any existing attributes for this name
        Iterator<TypeAttribute> i = attributes.iterator();
        while (i.hasNext()) {
            if (name.equals(i.next().getName())) {
                i.remove();
            }
        }

        if (name != null) {
            // Add an attribute
            TypeAttribute attribute = new TypeAttribute();
            attribute.setName(name);
            attribute.setValue(value);
            attributes.add(attribute);
        }

    }

    /**
     * Get all matching extension attribute from a CPF Node.
     *
     * @param node
     *            any CPF node
     * @param name
     *            name of the extension
     * @return List of TypeAttribute
     */
    public static List<TypeAttribute> getExtensionAttributes(final NodeType node, final String name) {
        List<TypeAttribute> attrList = new ArrayList<TypeAttribute>();
        for (TypeAttribute attr :node.getAttribute()) {
            if (name.equals(attr.getName()) || (YAWLSCHEMA_URL + "/" + name).equals(attr.getName())) {
                attrList.add(attr);
            }
        }
        return attrList;
    }

    /**
     * Returns if the extension attribute is present in the list of attributes.
     *
     * @param attributes List of extension attributes
     * @param name of the extension attribute
     * @return true if found, false otherwise
     */
    public static boolean hasExtension(final List<TypeAttribute> attributes, final String name) {
        return getExtensionFromAttributes(attributes, name) != null;
    }

    private static TypeAttribute getExtensionFromAttributes(final List<TypeAttribute> attributeList, final String name) {
        for (TypeAttribute attr : attributeList) {
            if (name.equals(attr.getName()) || (YAWLSCHEMA_URL + "/" + name).equals(attr.getName())) {
                return attr;
            }
        }
        return null;
    }

    /**
     * Get an extension attribute from a CPF Node and unmarshals it to using the native namespace.
     *
     * @param node CPF Node
     * @param elementName of Extension
     * @param expectedClass from native schema
     * @param defaultValue if not found
     * @return Object of expectedClass
     */
    public static <T> T getFromNodeExtension(final NodeType node,
                                             final String elementName,
                                             final Class<T> expectedClass,
                                             final T defaultValue,
                                             final JAXBContext nativeContext) {

        return getFromExtension(node.getAttribute(), elementName, expectedClass, defaultValue, nativeContext);
    }

    /**
     * Get an extension attribute from a list of CPF attributes and unmarshals it to using the native namespace.
     *
     * @param attributes CPF list of attributes
     * @param elementName of Extension
     * @param expectedClass from native schema
     * @param defaultValue if not found
     * @return Object of expectedClass
     */
    public static <T> T getFromExtension(final List<TypeAttribute> attributes,
                                         final String elementName,
                                         final Class<T> expectedClass,
                                         final T defaultValue,
                                         final JAXBContext nativeContext) {

        TypeAttribute attr = getExtensionFromAttributes(attributes, elementName);
        if (attr != null && attr.getAny() != null) {
            try {
                return ExtensionUtils.unmarshalFragment(attr.getAny(), expectedClass, nativeContext);
            } catch (CanoniserException e) {
                LOGGER.warn("Could unmarshal fragment from extension!", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Get an extension element from an AnnotationsType.
     *
     * @param annotation
     * @param elementName
     * @param expectedClass
     * @param defaultValue
     * @param nativeContext
     * @return an object of type T in any case
     */
    public static <T> T getFromAnnotationsExtension(final AnnotationType annotation,
                                                    final String         elementName,
                                                    final Class<T>       expectedClass,
                                                    final T              defaultValue,
                                                    final JAXBContext    nativeContext) {

        for (final Object extObj : annotation.getAny()) {
            try {
                if (ExtensionUtils.isValidFragment(extObj, ExtensionUtils.YAWLSCHEMA_URL, elementName)) {
                    return ExtensionUtils.unmarshalFragment(extObj, expectedClass, nativeContext);
                }
            } catch (final CanoniserException e) {
                LOGGER.warn("Could not convert extension {} with type {}", new String[] { elementName, expectedClass.getSimpleName() }, e);
            }
        }
        return defaultValue;
    }


}
