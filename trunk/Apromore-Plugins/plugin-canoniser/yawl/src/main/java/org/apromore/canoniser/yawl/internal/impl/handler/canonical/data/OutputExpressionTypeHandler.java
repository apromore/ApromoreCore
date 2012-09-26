package org.apromore.canoniser.yawl.internal.impl.handler.canonical.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.ExpressionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.VarMappingSetType;

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

    private void convertAtomicTaskOutputParameter(final OutputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {
        DecompositionFactsType d = getDecomposition(task);
        String netObjectName = convertToOutputMapping(expr, task);

        // Look if already added
      //TODO create output parameter of used task variables and assure NET variable is present
//        OutputParameterFactsType outputParam = YAWL_FACTORY.createOutputParameterFactsType();
//        outputParam.setName(netObjectName);
//        outputParam.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
//        outputParam.setType(ExpressionUtils.determineResultType(expr));
//        d.getOutputParam().add(outputParam);
    }

    private void convertCompositeTaskOutputParameter(final OutputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {

        String netObjectName = convertToOutputMapping(expr, task);
        String subnetId = ((TaskType)getOriginalParent()).getSubnetId();

        // Look if already converted
        //TODO create output parameter of used task variables and assure NET variable is present
//        if (getContext().getConvertedParameter(netObjectName, subnetId) == null) {
//            OutputParameterFactsType outputParam = YAWL_FACTORY.createOutputParameterFactsType();
//            outputParam.setName(netObjectName);
//            outputParam.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
//            outputParam.setType(ExpressionUtils.determineResultType(expr));
//
//            DecompositionFactsType d = getDecomposition(task);
//            if (d != null) {
//                d.getOutputParam().add(outputParam);
//            } else {
//                // Remember we need to add this variables later
//                getContext().addIntroducedVariable(subnetId, outputParam);
//            }
//        }
    }

    private String convertToOutputMapping(final OutputExpressionType expr, final ExternalTaskFactsType task) throws CanoniserException {
        VarMappingFactsType outputMapping = YAWL_FACTORY.createVarMappingFactsType();
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLTaskQuery(expr);
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
