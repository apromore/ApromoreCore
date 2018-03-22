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

package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.math.BigInteger;
import java.util.Set;

import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
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
        if (node.getName() != null && !node.getName().isEmpty()) {
            decompositionType.setName(node.getName());
        }
        getContext().getYAWLRootSpecification().getDecomposition().add(decompositionType);
        getContext().getControlFlowContext().addConvertedDecompositon(node.getId(), decompositionType);
        return decompositionType;
    }

    protected InputParameterFactsType convertInputParameterObject(final SoftType obj, final int index, final Set<String> nameSet) {
        final InputParameterFactsType defaultParam = YAWL_FACTORY.createInputParameterFactsType();
        InputParameterFactsType param = ExtensionUtils.getFromExtension(obj.getAttribute(), ExtensionUtils.INPUT_VARIABLE, InputParameterFactsType.class, defaultParam);
        convertBaseVariable(obj, index, nameSet, param);
        return param;
    }

    protected OutputParameterFactsType convertOutputParameterObject(final SoftType obj, final int index, final Set<String> nameSet) {
        final OutputParameterFactsType defaultParam = YAWL_FACTORY.createOutputParameterFactsType();
        OutputParameterFactsType param = ExtensionUtils.getFromExtension(obj.getAttribute(), ExtensionUtils.OUTPUT_VARIABLE, OutputParameterFactsType.class, defaultParam);
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