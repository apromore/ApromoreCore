/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExpressionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.VarMappingSetType;

/**
 * Convert Output Expressions to YAWL parameter mappings
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class OutputExpressionTypeHandler extends ExpressionTypeHandler<OutputExpressionType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputExpressionTypeHandler.class);

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (getObject().getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XQUERY)) {
            if (ConversionUtils.isCompositeTask((TaskType) getOriginalParent())) {
                convertCompositeTaskOutputParameter(getObject(), getConvertedParent());
            } else {
                convertAtomicTaskOutputParameter(getObject(), getConvertedParent());
            }
        } else {
            LOGGER.warn("Unsupported language {}", getObject().getLanguage());
        }

    }

    private void convertAtomicTaskOutputParameter(final OutputExpressionType expr, final ExternalTaskFactsType task) {
        try {
            convertToOutputMapping(expr, task);
        } catch (CanoniserException e) {
            LOGGER.warn("Could not convert output expression for task", e);
            getContext().getMessageInterface().addMessage("Could not convert output expression {0} for task {1}", expr.getExpression(), task.getId());
        }
    }

    private void convertCompositeTaskOutputParameter(final OutputExpressionType expr, final ExternalTaskFactsType task) {
        try {
            convertToOutputMapping(expr, task);
        } catch (CanoniserException e) {
            LOGGER.warn("Could not convert output expression for task", e);
            getContext().getMessageInterface().addMessage("Could not convert output expression {0} for task {1}", expr.getExpression(), task.getId());
        }
    }

    private String convertToOutputMapping(final OutputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {
        VarMappingFactsType outputMapping = YAWL_FACTORY.createVarMappingFactsType();
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLTaskQuery(expr, task);
        ExpressionType yawlExpression = YAWL_FACTORY.createExpressionType();
        yawlExpression.setQuery(yawlXQuery);
        outputMapping.setExpression(yawlExpression);
        String netObjectName = CPFSchema.getNetObjectName(expr.getExpression());
        outputMapping.setMapsTo(netObjectName);
        getOrCreateCompletedMappings(task).getMapping().add(outputMapping);
        return netObjectName;
    }

    private VarMappingSetType getOrCreateCompletedMappings(final ExternalTaskFactsType task) {
        if (task.getCompletedMappings() == null) {
            task.setCompletedMappings(YAWL_FACTORY.createVarMappingSetType());
        }
        return task.getCompletedMappings();
    }

}