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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
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
     * Calculates a List of ObjectType that are used in the specified xQuery.
     * 
     * @param xQuery
     * @param parentNet
     * @return List of ObjectType
     */
    protected List<ObjectType> calculateUsedNetVariables(final String xQuery, final NetType parentNet) {
        // TODO improve using an XQuery Parser
        final ArrayList<ObjectType> usedVariables = new ArrayList<ObjectType>();
        // This will capture most of the variables that are used in a YAWL input mapping!
        final Pattern p = Pattern.compile("/" + parentNet.getOriginalID() + "/([^/]*)/text()");
        final Matcher m = p.matcher(xQuery);
        while (m.find()) {
            final String varName = m.group(1);
            final ObjectType object = getContext().getObjectByName(varName, parentNet);
            if (object != null) {
                usedVariables.add(object);
            }
        }
        if (usedVariables.isEmpty()) {
            // Try whole XML element without 'text()'
            final Pattern p2 = Pattern.compile("/" + parentNet.getOriginalID() + "/([^/]*)");
            final Matcher m2 = p2.matcher(xQuery);
            while (m2.find()) {
                final String varName = m2.group(1);
                final ObjectType object = getContext().getObjectByName(varName, parentNet);
                if (object != null) {
                    usedVariables.add(object);
                }
            }
        }
        return usedVariables;
    }

}