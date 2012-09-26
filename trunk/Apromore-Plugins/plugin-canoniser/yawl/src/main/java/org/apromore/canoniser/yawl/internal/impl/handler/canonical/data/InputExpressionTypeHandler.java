package org.apromore.canoniser.yawl.internal.impl.handler.canonical.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.ExpressionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.VarMappingSetType;

public class InputExpressionTypeHandler extends ExpressionTypeHandler<InputExpressionType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputExpressionTypeHandler.class);

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (getObject().getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XQUERY) ) {
            if (ConversionUtils.isCompositeTask((TaskType) getOriginalParent())) {
                convertCompositeTaskInputParameter(getObject(), getConvertedParent());
            } else {
                convertAtomicTaskInputParameter(getObject(), getConvertedParent());
            }
        } else {
            LOGGER.warn("Unsupported language {}", getObject().getLanguage());
        }

    }

    private void convertAtomicTaskInputParameter(final InputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {
        DecompositionFactsType d = getDecomposition(task);
        String taskObjectName = convertToInputMapping(expr, task);

        // Look if already added
        InputParameterFactsType inputParam = YAWL_FACTORY.createInputParameterFactsType();
        inputParam.setName(taskObjectName);
        //TODO determine type
        inputParam.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
        inputParam.setType(ExpressionUtils.determineResultType(expr));
        d.getInputParam().add(inputParam);
    }

    private void convertCompositeTaskInputParameter(final InputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {

        String taskObjectName = convertToInputMapping(expr, task);
        // Look if already converted
        InputParameterFactsType inputParam = YAWL_FACTORY.createInputParameterFactsType();
        inputParam.setName(taskObjectName);
        inputParam.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
        inputParam.setType(ExpressionUtils.determineResultType(expr));

        DecompositionFactsType d = getDecomposition(task);
        if (d != null) {
            d.getInputParam().add(inputParam);
        } else {
            getContext().addIntroducedVariable(((TaskType)getOriginalParent()).getSubnetId(), inputParam);
        }

    }

    private String convertToInputMapping(final InputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {
        VarMappingFactsType inputMapping = YAWL_FACTORY.createVarMappingFactsType();

        ExpressionType yawlExpression = YAWL_FACTORY.createExpressionType();
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLNetQuery(expr);
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