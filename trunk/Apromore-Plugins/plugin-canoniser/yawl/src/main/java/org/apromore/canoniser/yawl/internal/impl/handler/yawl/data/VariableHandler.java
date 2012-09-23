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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.NetType;
import org.apromore.cpf.SoftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.VariableBaseType;

/**
 * Converts the variables of a YAWL Net to CPF Objects.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class VariableHandler extends YAWLConversionHandler<VariableBaseType, NetType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariableHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        final SoftType alreadyConvertedObject = getContext().getObjectByName(getObject().getName(), getConvertedParent());
        if (alreadyConvertedObject == null) {
            final SoftType canonicalObject = getContext().getCanonicalOF().createSoftType();
            final String generatedOriginalId = ConversionUtils.buildObjectId(getConvertedParent().getId(), getObject().getName());
            canonicalObject.setId(generateUUID(DATA_ID_PREFIX, generatedOriginalId));
            canonicalObject.setOriginalID(generatedOriginalId);
            canonicalObject.setName(getObject().getName());
            canonicalObject.setType(getObject().getType());
            getConvertedParent().getObject().add(canonicalObject);
            LOGGER.debug("Adding Object for YAWL Net {} (Name: {}, Type: {})", new String[] { getConvertedParent().getId(),
                    canonicalObject.getName(), canonicalObject.getType() });
            getContext().addObjectForNet(canonicalObject, getConvertedParent());
        }

    }

}
