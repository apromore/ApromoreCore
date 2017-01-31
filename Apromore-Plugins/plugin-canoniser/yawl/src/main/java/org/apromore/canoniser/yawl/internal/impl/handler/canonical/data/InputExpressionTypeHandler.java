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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExpressionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.VarMappingSetType;

/**
 * Convert Input Expressions to YAWL parameter mappings
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class InputExpressionTypeHandler extends ExpressionTypeHandler<InputExpressionType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputExpressionTypeHandler.class);

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (getObject().getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XQUERY) ) {
            TaskType originalParent = (TaskType) getOriginalParent();
            NetFactsType parentNet = getContext().getControlFlowContext().getElementInfo(originalParent.getId()).getParent();
            if (ConversionUtils.isCompositeTask(originalParent)) {
                convertCompositeTaskInputParameter(getObject(), getConvertedParent(), parentNet);
            } else {
                convertAtomicTaskInputParameter(getObject(), getConvertedParent(), parentNet);
            }
        } else {
            LOGGER.warn("Unsupported language {}", getObject().getLanguage());
        }

    }

    private void convertAtomicTaskInputParameter(final InputExpressionType expr, final ExternalTaskFactsType task, final NetFactsType parentNet) {
        try {
            convertToInputMapping(expr, task, parentNet);
        } catch (CanoniserException e) {
            LOGGER.warn("Could not convert input expression for task", e);
            getContext().getMessageInterface().addMessage("Could not convert input expression {0} for task {1}", expr.getExpression(), task.getId());
        }
    }

    private void convertCompositeTaskInputParameter(final InputExpressionType expr, final ExternalTaskFactsType task, final NetFactsType parentNet) {
        try {
            convertToInputMapping(expr, task, parentNet);
        } catch (CanoniserException e) {
            LOGGER.warn("Could not convert input expression for task", e);
            getContext().getMessageInterface().addMessage("Could not convert input expression {0} for task {1}", expr.getExpression(), task.getId());
        }

    }

    private String convertToInputMapping(final InputExpressionType expr, final ExternalTaskFactsType task, final NetFactsType parentNet) throws CanoniserException {
        VarMappingFactsType inputMapping = YAWL_FACTORY.createVarMappingFactsType();

        ExpressionType yawlExpression = YAWL_FACTORY.createExpressionType();
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLNetQuery(expr, parentNet);
        yawlExpression.setQuery(yawlXQuery);

        String taskObjectName = CPFSchema.getTaskObjectName(expr.getExpression());

        inputMapping.setExpression(yawlExpression);
        inputMapping.setMapsTo(taskObjectName);

        getOrCreateStartingMappings(task).getMapping().add(inputMapping);
        return taskObjectName;
    }

    private VarMappingSetType getOrCreateStartingMappings(final ExternalTaskFactsType task) {
        if (task.getStartingMappings() == null) {
            task.setStartingMappings(YAWL_FACTORY.createVarMappingSetType());
        }
        return task.getStartingMappings();
    }


}