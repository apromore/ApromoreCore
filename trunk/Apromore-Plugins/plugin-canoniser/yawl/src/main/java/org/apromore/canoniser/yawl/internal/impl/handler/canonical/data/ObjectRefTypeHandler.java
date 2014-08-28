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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.ExpressionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.VarMappingSetType;
import org.yawlfoundation.yawlschema.VariableBaseType;

public class ObjectRefTypeHandler extends CanonicalElementHandler<ObjectRefType, ExternalTaskFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRefTypeHandler.class);

    @Override
    public void convert() throws CanoniserException {
        ObjectRefType ref = getObject();
        TaskType originalTask = (TaskType) getOriginalParent();
        NetFactsType net = getContext().getControlFlowContext().getElementInfo(originalTask.getId()).getParent();

        ObjectType netObject = getContext().getObjectTypeById(ref.getObjectId());

        if (netObject != null) {
            if (ref.getType().equals(InputOutputType.INPUT)) {
                // Add Input Mapping + Task Variable
                if (!ConversionUtils.isCompositeTask(originalTask)) {
                    DecompositionFactsType d = getContext().getControlFlowContext().getConvertedDecomposition(originalTask.getId());
                    String varName = addInputMapping(netObject, net, getConvertedParent(), d);
                    addTaskInputParameter(netObject, varName, d);
                } else {
                    getContext().getMessageInterface().addMessage(
                            "Ignoring the reference to Object {0} as Object conversion for composite task {1} is ambiguous!", netObject.getName(),
                            getConvertedParent().getId());
                }
            } else {
                // Add Output Mapping + Task Variable
                if (!ConversionUtils.isCompositeTask(originalTask)) {
                    DecompositionFactsType d = getContext().getControlFlowContext().getConvertedDecomposition(originalTask.getId());
                    String varName = addOutputMapping(netObject, net, getConvertedParent(), d);
                    addTaskOutputParameter(netObject, varName, d);
                } else {
                    getContext().getMessageInterface().addMessage(
                            "Ignoring the reference to Object {0} as Object conversion for composite task {1} is ambiguous!", netObject.getName(),
                            getConvertedParent().getId());
                }
            }
        } else {
            LOGGER.warn("Missing referenced Object. Probably an invalid CPF!");
        }

    }

    private void addTaskOutputParameter(final ObjectType netObject, final String varName, final DecompositionFactsType taskDecomposition) {
        OutputParameterFactsType outputParam = YAWL_FACTORY.createOutputParameterFactsType();
        outputParam.setName(varName);
        if (netObject instanceof SoftType) {
            outputParam.setType(((SoftType) netObject).getType());
        } else {
            // Object is a Document
            outputParam.setType("YDocumentType");
        }
        outputParam.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
        taskDecomposition.getOutputParam().add(outputParam);
    }

    private String addOutputMapping(final ObjectType netObject, final NetFactsType net, final ExternalTaskFactsType task, final DecompositionFactsType d) {
        VarMappingFactsType outputMapping = YAWL_FACTORY.createVarMappingFactsType();
        ExpressionType yawlExpression = YAWL_FACTORY.createExpressionType();
        String generateUniqueName = ConversionUtils.generateUniqueName(netObject.getName(), getNameSet(d.getOutputParam()));
        String yawlXQuery = ExpressionUtils.createYAWLOutputExpression(generateUniqueName, task);
        yawlExpression.setQuery(yawlXQuery);
        outputMapping.setExpression(yawlExpression);
        outputMapping.setMapsTo(netObject.getName());
        getOrCreateCompletedMappings(task).getMapping().add(outputMapping);
        return generateUniqueName;
    }

    private void addTaskInputParameter(final ObjectType netObject, final String varName, final DecompositionFactsType taskDecomposition) {
        InputParameterFactsType inputParam = YAWL_FACTORY.createInputParameterFactsType();
        inputParam.setName(varName);
        if (netObject instanceof SoftType) {
            inputParam.setType(((SoftType) netObject).getType());
        } else {
            // Object is a Document
            inputParam.setType("YDocumentType");
        }
        inputParam.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
        taskDecomposition.getInputParam().add(inputParam);
    }

    private String addInputMapping(final ObjectType netObject, final NetFactsType net, final ExternalTaskFactsType task, final DecompositionFactsType d) {
        VarMappingFactsType inputMapping = YAWL_FACTORY.createVarMappingFactsType();
        ExpressionType yawlExpression = YAWL_FACTORY.createExpressionType();
        String yawlXQuery = ExpressionUtils.createYAWLInputExpression(netObject, net);
        yawlExpression.setQuery(yawlXQuery);
        inputMapping.setExpression(yawlExpression);
        String generateUniqueName = ConversionUtils.generateUniqueName(netObject.getName(), getNameSet(d.getInputParam()));
        inputMapping.setMapsTo(generateUniqueName);
        getOrCreateStartingMappings(task).getMapping().add(inputMapping);
        return generateUniqueName;

    }

    private Set<String> getNameSet(final List<? extends VariableBaseType> varList) {
        Set<String> nameSet = new HashSet<>();
        for (VariableBaseType varMapping : varList) {
            nameSet.add(varMapping.getName());
        }
        return nameSet;
    }

    private VarMappingSetType getOrCreateStartingMappings(final ExternalTaskFactsType task) {
        if (task.getStartingMappings() == null) {
            task.setStartingMappings(YAWL_FACTORY.createVarMappingSetType());
        }
        return task.getStartingMappings();
    }

    private VarMappingSetType getOrCreateCompletedMappings(final ExternalTaskFactsType task) {
        if (task.getCompletedMappings() == null) {
            task.setCompletedMappings(YAWL_FACTORY.createVarMappingSetType());
        }
        return task.getCompletedMappings();
    }

}
