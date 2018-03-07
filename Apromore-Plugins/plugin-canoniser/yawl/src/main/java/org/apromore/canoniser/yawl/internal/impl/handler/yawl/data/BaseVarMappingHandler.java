/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.data;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TaskType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;

/**
 * Base class for variable mapping conversions.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class BaseVarMappingHandler extends YAWLConversionHandler<VarMappingFactsType, TaskType> {

    protected ObjectRefType createObjectRef(final ObjectType param, final InputOutputType type, final boolean isOptional, final boolean isConsumed) {
        final ObjectRefType objectRef = CPF_FACTORY.createObjectRefType();
        objectRef.setId(generateUUID());
        objectRef.setObjectId(param.getId());
        objectRef.setOptional(isOptional);
        objectRef.setType(type);
        objectRef.setConsumed(isConsumed);
        return objectRef;
    }

    /**
     * Calculates a List of ObjectType names that are used in the specified xQuery.
     *
     * @param xQuery a YAWL XQuery
     * @param parentNet that contains the Objects
     * @return Set of ObjectType names
     */
    protected Set<String> calculateUsedNetVariables(final String xQuery, final NetType parentNet) {
        final Set<String> usedVariables = ExpressionUtils.determinedUsedVariables(xQuery, parentNet);
    
        for (String varName: usedVariables) {
            final ObjectType object = getContext().getObjectByName(varName, parentNet);
            if (object != null) {
                usedVariables.add(varName);
            }
        }
        return usedVariables;
    }

}