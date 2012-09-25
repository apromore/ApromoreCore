/**
 * Copyright 2012, Felix Mannhardt
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl;

import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        c.setName(getObject().getName() != null ? getObject().getName() : getObject().getUri());
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
            // convertDataTypeDefinitions(getObject().getAny());
        }

        for (final DecompositionType d : getObject().getDecomposition()) {
            // Will also convert all nested elements
            getContext().getHandlerFactory().createHandler(d, c, getObject()).convert();
        }

    }

    private void convertAnnotations(final CanonicalProcessType c, final YAWLSpecificationFactsType object) throws CanoniserException {
        // Link to Annotations
        getContext().getAnnotationResult().setUri(c.getUri());
        getContext().getAnnotationResult().setName(c.getName());
        addToAnnotations(ConversionUtils.marshalYAWLFragment("locale", getContext().getLayoutLocaleElement(), LayoutLocaleType.class));
    }

    private String convertCreatorList(final List<String> creatorList) {
        final StringBuilder sb = new StringBuilder();
        for (final String creator : creatorList) {
            sb.append(creator);
            sb.append(" ,");
        }
        return sb.substring(0, sb.length() - 2);
    }

}
