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

import java.text.DateFormat;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.TypeAttribute;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Converts the YAWL specification
 * 
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class SpecificationHandler extends YAWLConversionHandler<YAWLSpecificationFactsType, Object> {

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
                // TODO set date
                // c.setCreationDate(metaData.getCreated());
            }
            c.setAuthor(convertCreatorList(metaData.getCreator()));
        }

        if (getObject().getAny() != null) {
            // convertDataTypeDefinitions(getObject().getAny());
        }

        for (final DecompositionType d : getObject().getDecomposition()) {
            // Will also convert all nested elements
            getContext().getHandlerFactory().createHandler(d, c, getObject()).convert();
        }

    }

    private TypeAttribute convertDataTypeDefinitions(final Object dataTypeDefinitions) {
        final TypeAttribute attr = getContext().getCanonicalOF().createTypeAttribute();
        attr.setName(ConversionUtils.EXTENSION_DATA_TYPE_DEFINITIONS);
        attr.setAny(dataTypeDefinitions);
        return attr;
    }

    private String convertCreatorList(final List<String> creatorList) {
        final StringBuilder sb = new StringBuilder();
        for (final String creator : creatorList) {
            sb.append(creator);
            sb.append(" ,");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private String convertDate(final XMLGregorianCalendar xmlDate) {
        return DateFormat.getInstance().format(xmlDate.toGregorianCalendar().getTime());
    }

}
