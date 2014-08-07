/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExpressionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.VariableFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Converts a NetType to a YAWL NetFactsType
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class NetTypeHandler extends DecompositionHandler<NetType, YAWLSpecificationFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetTypeHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        final NetFactsType netFactsType = YAWL_FACTORY.createNetFactsType();

        netFactsType.setId(generateUUID(getObject().getId()));
        if (getObject().getOriginalID() != null && !getObject().getOriginalID().isEmpty()) {
            netFactsType.setName(getObject().getOriginalID());
        }

        if (isRootNet()) {
            LOGGER.debug("Setting Net {} as root net", getObject().getId());
            netFactsType.setIsRootNet(true);
        }

        // Remember our net so that composite tasks, which use it are able to find it
        getContext().getControlFlowContext().addConvertedDecompositon(getObject().getId(), netFactsType);
        // Update all composite tasks that already have been converted
        updateCompositeTasks(netFactsType);

        convertDataObjects(netFactsType);

        final ProcessControlElements processControlElements = YAWL_FACTORY.createNetFactsTypeProcessControlElements();
        netFactsType.setProcessControlElements(processControlElements);

        for (final NodeType node : getObject().getNode()) {
            getContext().createHandler(node, netFactsType, getObject()).convert();
        }

        for (final EdgeType egde : getObject().getEdge()) {
            getContext().createHandler(egde, netFactsType, getObject()).convert();
        }

        LOGGER.debug("Added Net {}", netFactsType.getName());
        getConvertedParent().getDecomposition().add(netFactsType);
    }

    private void convertDataObjects(final NetFactsType netFactsType) throws CanoniserException {

        // Both sets are used to keep YAWL variable names unique, which is not enforced by CPF
        final Set<String> inputParamNameSet = new HashSet<String>();
        final Set<String> outputParamNameSet = new HashSet<String>();
        final Set<String> localParamNameSet = new HashSet<String>();

        if (isRootNet()) {
            int i = 0;
            // Convert all our objects as local variables, as CPF does not support "INPUT" or "OUTPUT" parameters to a case
            for (final ObjectType obj : getObject().getObject()) {
                if (obj instanceof SoftType) {
                    List<TypeAttribute> objectExt = obj.getAttribute();
                    SoftType softObject = (SoftType) obj;
                    if (ExtensionUtils.hasExtension(objectExt, ExtensionUtils.INPUT_VARIABLE) && ExtensionUtils.hasExtension(objectExt, ExtensionUtils.OUTPUT_VARIABLE)) {
                        addInputParameter(netFactsType, inputParamNameSet, i, obj, softObject);
                        addOutputParameter(netFactsType, outputParamNameSet, i, obj, softObject);
                    } else if (ExtensionUtils.hasExtension(objectExt, ExtensionUtils.INPUT_VARIABLE)) {
                        addInputParameter(netFactsType, inputParamNameSet, i, obj, softObject);
                    } else if (ExtensionUtils.hasExtension(objectExt, ExtensionUtils.OUTPUT_VARIABLE)) {
                        addOutputParameter(netFactsType, outputParamNameSet, i, obj, softObject);
                    } else {
                        addLocalParameter(netFactsType, localParamNameSet, i, obj, softObject);
                    }
                }
                i++;
            }
        } else {

            int i = 0;
            for (final ObjectType obj : getObject().getObject()) {
                final SoftType softObject = (SoftType) obj;
                if (getContext().isInputObjectForNet(obj.getId(), getObject().getId())
                        && getContext().isOutputObjectOfNet(obj.getId(), getObject().getId())) {
                    addInputParameter(netFactsType, inputParamNameSet, i, obj, softObject);
                    addOutputParameter(netFactsType, outputParamNameSet, i, obj, softObject);
                } else if (getContext().isOutputObjectOfNet(obj.getId(), getObject().getId())) {
                    addOutputParameter(netFactsType, outputParamNameSet, i, obj, softObject);
                } else if (getContext().isInputObjectForNet(obj.getId(), getObject().getId())) {
                    addInputParameter(netFactsType, inputParamNameSet, i, obj, softObject);
                } else {
                    // Local
                    addLocalParameter(netFactsType, localParamNameSet, i, obj, softObject);
                }
                i++;
            }
        }
    }

    private void addLocalParameter(final NetFactsType netFactsType, final Set<String> localParamNameSet, final int i, final ObjectType obj,
            final SoftType softObject) {
        final VariableFactsType var = convertLocalNetObject(softObject, i, localParamNameSet);
        getContext().addConvertedParameter(obj.getName(), getObject().getId(), var);
        LOGGER.debug("Added local variable {} to Net decomposition {}", var.getName(), netFactsType.getName());
        netFactsType.getLocalVariable().add(var);
    }

    private void addOutputParameter(final NetFactsType netFactsType, final Set<String> outputParamNameSet, final int i, final ObjectType obj,
            final SoftType softObject) {
        final OutputParameterFactsType outputParam = convertOutputParameterObject(softObject, i, outputParamNameSet);
        getContext().addConvertedParameter(obj.getName(), getObject().getId(), outputParam);
        LOGGER.debug("Added output parameter {} to Net decomposition {}", outputParam.getName(), netFactsType.getName());
        netFactsType.getOutputParam().add(outputParam);
    }

    private void addInputParameter(final NetFactsType netFactsType, final Set<String> inputParamNameSet, final int i, final ObjectType obj,
            final SoftType softObject) {
        final InputParameterFactsType inputParam = convertInputParameterObject(softObject, i, inputParamNameSet);
        getContext().addConvertedParameter(obj.getName(), getObject().getId(), inputParam);
        LOGGER.debug("Added input parameter {} to Net decomposition {}", inputParam.getName(), netFactsType.getName());
        netFactsType.getInputParam().add(inputParam);
    }

    private VariableFactsType convertLocalNetObject(final SoftType obj, final int index, final Set<String> nameSet) {
        final VariableFactsType defaultLocalVar = YAWL_FACTORY.createVariableFactsType();
        VariableFactsType localVar = ExtensionUtils.getFromExtension(obj.getAttribute(), ExtensionUtils.LOCAL_VARIABLE, VariableFactsType.class, defaultLocalVar);
        if (nameSet.contains(obj.getName())) {
            localVar.setName(ConversionUtils.generateUniqueName(obj.getName(), nameSet));
        } else {
            localVar.setName(obj.getName());
        }
        nameSet.add(localVar.getName());
        localVar.setType(obj.getType());
        localVar.setIndex(BigInteger.valueOf(index));
        localVar.setNamespace(ExpressionUtils.DEFAULT_TYPE_NAMESPACE);
        return localVar;
    }

    private void updateCompositeTasks(final NetFactsType netFactsType) {
        final Collection<ExternalTaskFactsType> compositeTasks = getContext().getControlFlowContext().getCompositeTasks(getObject().getId());
        for (final ExternalTaskFactsType task : compositeTasks) {
            DecompositionType dRef = YAWL_FACTORY.createDecompositionType();
            dRef.setId(netFactsType.getId());
            task.setDecomposesTo(dRef);
        }
    }

    /**
     * @return if the Net is a Root Net
     * @throws CanoniserException
     */
    private boolean isRootNet() throws CanoniserException {
        final CanonicalProcessType cProcess = (CanonicalProcessType) getOriginalParent();
        if (cProcess.getRootIds() != null && !cProcess.getRootIds().isEmpty()) {
            // TODO handle multiple root nets
            for (final String netId : cProcess.getRootIds()) {
                if (netId.equals(getObject().getId())) {
                    return true;
                }
            }
            return false;
        } else {
            // There should be only one Net otherwise CPF is faulty
            if (cProcess.getNet().size() == 1) {
                return true;
            } else {
                throw new CanoniserException("Can not determine the root net, rootId is null and there are multiple nets available!");
            }
        }
    }

}
