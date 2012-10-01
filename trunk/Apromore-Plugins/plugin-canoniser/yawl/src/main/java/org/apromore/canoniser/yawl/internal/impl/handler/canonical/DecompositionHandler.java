package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.math.BigInteger;
import java.util.Set;

import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.SoftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.VariableBaseType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

/**
 * Base class for NetType and TaskType which are both converting to a Decomposition in YAWL
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T>
 *            type of the Element to be converted
 * @param <E>
 *            type of the already converted parent
 */
public abstract class DecompositionHandler<T, E> extends BaseTaskHandler<T, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecompositionHandler.class);

    protected WebServiceGatewayFactsType createDecomposition(final NodeType node) {
        final WebServiceGatewayFactsType decompositionType = YAWL_FACTORY.createWebServiceGatewayFactsType();
        // Decompositions must have unique IDs, so we can't use the NetElement ID here!
        decompositionType.setId(generateUUID());
        LOGGER.debug("Creating decomposition for {} with ID {}", node.getName(), decompositionType.getId());
        decompositionType.setName(node.getName());
        getContext().getYAWLRootSpecification().getDecomposition().add(decompositionType);
        getContext().addConvertedDecompositon(node.getId(), decompositionType);
        return decompositionType;
    }

    protected InputParameterFactsType convertInputParameterObject(final SoftType obj, final int index, final Set<String> nameSet) {
        final InputParameterFactsType param = YAWL_FACTORY.createInputParameterFactsType();
        convertBaseVariable(obj, index, nameSet, param);
        return param;
    }

    protected OutputParameterFactsType convertOutputParameterObject(final SoftType obj, final int index, final Set<String> nameSet) {
        final OutputParameterFactsType param = YAWL_FACTORY.createOutputParameterFactsType();
        convertBaseVariable(obj, index, nameSet, param);
        return param;
    }

    private void convertBaseVariable(final SoftType obj, final int index, final Set<String> nameSet, final VariableBaseType var) {
        if (nameSet.contains(obj.getName())) {
            var.setName(ConversionUtils.generateUniqueName(obj.getName(), nameSet));
        } else {
            var.setName(obj.getName());
        }
        nameSet.add(var.getName());
        var.setType(obj.getType());
        var.setIndex(BigInteger.valueOf(index));
        var.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
    }

}