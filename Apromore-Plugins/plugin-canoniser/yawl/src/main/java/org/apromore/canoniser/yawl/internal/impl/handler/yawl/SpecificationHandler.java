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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.LayoutLocaleType;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Converts the YAWL specification
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class SpecificationHandler extends YAWLConversionHandler<YAWLSpecificationFactsType, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final CanonicalProcessType c = getContext().getCanonicalResult();
        // Canonical Process needs a name, so use URI if none specified
        c.setName(convertName(getObject()));
        c.setUri(getObject().getUri());

        final MetaDataType metaData = getObject().getMetaData();
        if (metaData != null) {
            c.setVersion(metaData.getVersion().toPlainString());
            if (metaData.getCreated() != null) {
                c.setCreationDate(metaData.getCreated().toXMLFormat());
            }
            c.setAuthor(convertCreatorList(metaData.getCreator()));
        }

        convertAnnotations(c, getObject());

        if (getObject().getAny() != null) {
            LOGGER.debug("Found DataTypeDefinitions: {}", getObject().getAny().toString());
            if (getObject().getAny() instanceof Element) {
                String dataTypes = convertDataTypesToString((Element)getObject().getAny());
                c.setDataTypes(dataTypes);
            }
        }

        for (final DecompositionType d : getObject().getDecomposition()) {
            // Will also convert all nested elements
            getContext().createHandler(d, c, getObject()).convert();
        }

    }

    private String convertDataTypesToString(final Element dataTypesElement) throws CanoniserException  {
        try {
            final DOMSource domSource = new DOMSource(dataTypesElement);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (IllegalArgumentException | TransformerFactoryConfigurationError | TransformerException e) {
            throw new CanoniserException(e);
        }
    }

    private void convertAnnotations(final CanonicalProcessType c, final YAWLSpecificationFactsType object) throws CanoniserException {
        // Link to Annotations
        getContext().getAnnotationResult().setUri(c.getUri());
        getContext().getAnnotationResult().setName(c.getName());
        getContext().addToAnnotations(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.LOCALE, getContext().getLayoutLocaleElement(), LayoutLocaleType.class));
        getContext().addToAnnotations(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.METADATA, object.getMetaData(), MetaDataType.class));
    }

    private String convertName(final YAWLSpecificationFactsType spec) {
        if (spec.getName() != null) {
            return spec.getName();
        } else if (spec.getMetaData().getTitle() != null) {
            return spec.getMetaData().getTitle();
        } else {
            return spec.getUri();
        }
    }

    private String convertCreatorList(final List<String> creatorList) {
        final StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = creatorList.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(" ,");
            }
        }
        return sb.toString();
    }

}
