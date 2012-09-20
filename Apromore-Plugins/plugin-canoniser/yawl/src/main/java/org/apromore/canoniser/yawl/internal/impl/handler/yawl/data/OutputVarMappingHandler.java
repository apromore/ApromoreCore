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

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExpressionType;

/**
 * Converts the Output Mappings of a YAWL Task to Object references.
 * 
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class OutputVarMappingHandler extends BaseVarMappingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputVarMappingHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        final TaskType task = getConvertedParent();

        final NetType parentNet = getContext().getNetForTaskId(task.getId());

        final String xQuery = getObject().getExpression().getQuery();

        final List<ObjectType> taskVariableList = calculateUsedTaskVariables(xQuery, getConvertedParent());
        final ObjectType netVariable = getContext().getObjectByName(getObject().getMapsTo(), parentNet);

        if (netVariable != null) {
            for (final ObjectType taskVariable : taskVariableList) {
                if (netVariable != null) {
                    final ObjectRefType objectRef = createObjectRef(netVariable, InputOutputType.OUTPUT, false, false);
                    // objectRef.setMapsToObjectId(taskVariable.getId());
                    objectRef.setConsumed(true);
                    objectRef.getAttribute().add(createExpressionAttribute(getObject().getExpression()));
                    if (taskVariableList.size() > 1) {
                        // Remember this mapping is part of a complex expression
                        // objectRef.getAttribute().add(createComplexMappingAttribute(taskVariableList.size()));
                    }
                    LOGGER.debug("Adding Object Reference for YAWL Task {} (Type: {}, Source: {}, Target: {})", new String[] {
                            getConvertedParent().getName(), objectRef.getType().toString(), objectRef.getObjectId(), null });
                    task.getObjectRef().add(objectRef);
                }
            }
        } else {
            throw new CanoniserException("Could not find Net variable " + getObject().getMapsTo() + " for output mapping in Task " + task.getName());
        }

    }

    private TypeAttribute createExpressionAttribute(final ExpressionType expressionType) throws CanoniserException {
        final TypeAttribute expressionAttr = getContext().getCanonicalOF().createTypeAttribute();
        expressionAttr.setName(ConversionUtils.YAWL_EXPRESSION_EXTENSION);
        try {
            expressionAttr.setAny(ConversionUtils
                    .marshalYAWLFragment(ConversionUtils.YAWL_EXPRESSION_EXTENSION, expressionType, ExpressionType.class));
        } catch (JAXBException e) {
            throw new CanoniserException("Failed to add the expression extension element", e);
        }
        return expressionAttr;
    }

}
