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

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;

/**
 * Converts the Output Mappings of a YAWL Task to Object references.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class OutputVarMappingHandler extends BaseVarMappingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputVarMappingHandler.class);

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
        final ObjectType netVariable = getContext().getObjectByName(getObject().getMapsTo(), parentNet);

        if (netVariable != null) {
            final ObjectRefType objectRef = createObjectRef(netVariable, InputOutputType.OUTPUT, false, false);
            objectRef.setConsumed(true);
            LOGGER.debug("Adding Object Reference for YAWL Task {} (Type: {}, Source: {}, Target: {})", new String[] {
                    getConvertedParent().getName(), objectRef.getType().toString(), objectRef.getObjectId(), null });
            task.getObjectRef().add(objectRef);

            // Store the xQuery expression in a canonical way
            task.getOutputExpr().add(convertXQuery(xQuery, netVariable));

        } else {
            // Referred Net Object is not converted, either the YAWL model is incomplete or there is a conversion issue -> Issue warning message
            LOGGER.warn("Could not find Net variable {} for output mapping in Task {}", getObject().getMapsTo(), task.getName());
            getContext().getMessageInterface().addMessage("Could not find Net variable {0} for output mapping in Task {1}", getObject().getMapsTo(), task.getName());
        }
    }

    private OutputExpressionType convertXQuery(final String xQuery, final ObjectType mapsTo) throws CanoniserException {
        final OutputExpressionType outputExprType = CPF_FACTORY.createOutputExpressionType();
        outputExprType.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        ExternalTaskFactsType task = (ExternalTaskFactsType) getOriginalParent();
        String outputExpr = CPFSchema.createOuputExpression(mapsTo.getName(), ExpressionUtils.createQueryReferencingTaskVariables(xQuery, task));
        outputExprType.setExpression(outputExpr);
        return outputExprType;
    }

}
