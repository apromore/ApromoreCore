/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.data;

import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.NetType;
import org.apromore.cpf.SoftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the variables of a YAWL Net to CPF Objects.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class VariableHandler<T> extends YAWLConversionHandler<T, NetType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariableHandler.class);

    protected SoftType createSoftType(final String objectName, final String objectType) {
        final SoftType alreadyConvertedObject = getContext().getObjectByName(objectName, getConvertedParent());
        if (alreadyConvertedObject == null) {
            final SoftType canonicalObject = CPF_FACTORY.createSoftType();
            final String generatedOriginalId = ExpressionUtils.buildObjectId(getConvertedParent().getId(), objectName);
            canonicalObject.setId(generateUUID(DATA_ID_PREFIX, generatedOriginalId));
            canonicalObject.setOriginalID(generatedOriginalId);
            canonicalObject.setName(objectName);
            canonicalObject.setType(objectType);
            getConvertedParent().getObject().add(canonicalObject);
            LOGGER.debug("Adding Object for YAWL Net {} (Name: {}, Type: {})", new String[] { getConvertedParent().getId(),
                    canonicalObject.getName(), canonicalObject.getType() });
            getContext().addObjectForNet(canonicalObject, getConvertedParent());
            return canonicalObject;
        } else {
            return alreadyConvertedObject;
        }
    }

}
