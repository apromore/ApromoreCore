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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.SoftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(NetTypeHandler.class.getName());

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        final NetFactsType netFactsType = getContext().getYawlObjectFactory().createNetFactsType();

        netFactsType.setId(generateUUID(getObject().getId()));
        if (getObject().getOriginalID() != null) {
            netFactsType.setName(getObject().getOriginalID());
        }

        if (isRootNet()) {
            LOGGER.debug("Setting Net {} as root net", getObject().getId());
            netFactsType.setIsRootNet(true);
        }

        // Remember our net so that composite tasks, which use it are able to find it
        getContext().addConvertedNet(getObject().getId(), netFactsType);
        // Update all composite tasks that already have been converted
        updateCompositeTasks(netFactsType);

        convertDataObjects(netFactsType);

        final ProcessControlElements processControlElements = getContext().getYawlObjectFactory().createNetFactsTypeProcessControlElements();
        netFactsType.setProcessControlElements(processControlElements);

        for (final NodeType node : getObject().getNode()) {
            getContext().getHandlerFactory().createHandler(node, netFactsType, getObject()).convert();
        }

        for (final EdgeType egde : getObject().getEdge()) {
            getContext().getHandlerFactory().createHandler(egde, netFactsType, getObject()).convert();
        }

        fixMissingInputCondition(netFactsType);
        fixMissingOutputCondition(netFactsType);

        LOGGER.debug("Added Net {}", netFactsType.getName());
        getConvertedParent().getDecomposition().add(netFactsType);
    }

    private void fixMissingOutputCondition(final NetFactsType netFactsType) {
        if (netFactsType.getProcessControlElements().getOutputCondition() == null) {
            EventType node = new EventType();
            node.setId(generateUUID());
            netFactsType.getProcessControlElements().setOutputCondition(createOutputCondition(node));
        }

    }

    private void fixMissingInputCondition(final NetFactsType netFactsType) {
        if (netFactsType.getProcessControlElements().getInputCondition() == null) {
            EventType node = new EventType();
            node.setId(generateUUID());
            netFactsType.getProcessControlElements().setInputCondition(createCondition(node));
        }
    }

    private void convertDataObjects(final NetFactsType netFactsType) throws CanoniserException {

        if (isRootNet()) {
            final Set<String> localParamNameSet = new HashSet<String>();
            int i = 0;
            // Convert all our objects as local variables, as CPF does not support "INPUT" or "OUTPUT" parameters to a case
            for (final ObjectType obj : getObject().getObject()) {
                if (obj instanceof SoftType) {
                    final VariableFactsType var = convertLocalNetObject((SoftType) obj, i, localParamNameSet);
                    getContext().addConvertedParameter(obj.getId(), var);
                    LOGGER.debug("Added local variable {} to Root-Net decomposition {}", var.getName(), netFactsType.getName());
                    netFactsType.getLocalVariable().add(var);
                }
                i++;
            }
        } else {

            // Both sets are used to keep YAWL variable names unique, which is not enforced by CPF
            final Set<String> inputParamNameSet = new HashSet<String>();
            final Set<String> outputParamNameSet = new HashSet<String>();
            final Set<String> localParamNameSet = new HashSet<String>();

            int i = 0;
            for (final ObjectType obj : getObject().getObject()) {
                final SoftType softObject = (SoftType) obj;
                if (getContext().isInputObjectForNet(obj.getId(), getObject().getId())
                        && getContext().isOutputObjectOfNet(obj.getId(), getObject().getId())) {
                    final InputParameterFactsType inputParam = convertInputParameterObject(softObject, i, inputParamNameSet);
                    getContext().addConvertedParameter(obj.getId(), inputParam);
                    LOGGER.debug("Added input parameter {} to Net decomposition {}", inputParam.getName(), netFactsType.getName());
                    netFactsType.getInputParam().add(inputParam);
                    final OutputParameterFactsType outputParam = convertOutputParameterObject(softObject, i, outputParamNameSet);
                    getContext().addConvertedParameter(obj.getId(), outputParam);
                    LOGGER.debug("Added output parameter {} to Net decomposition {}", outputParam.getName(), netFactsType.getName());
                    netFactsType.getOutputParam().add(outputParam);
                } else if (getContext().isOutputObjectOfNet(obj.getId(), getObject().getId())) {
                    final OutputParameterFactsType outputParam = convertOutputParameterObject(softObject, i, outputParamNameSet);
                    getContext().addConvertedParameter(obj.getId(), outputParam);
                    LOGGER.debug("Added output parameter {} to Net decomposition {}", outputParam.getName(), netFactsType.getName());
                    netFactsType.getOutputParam().add(outputParam);
                } else if (getContext().isInputObjectForNet(obj.getId(), getObject().getId())) {
                    final InputParameterFactsType inputParam = convertInputParameterObject(softObject, i, inputParamNameSet);
                    LOGGER.debug("Added input parameter {} to Net decomposition {}", inputParam.getName(), netFactsType.getName());
                    getContext().addConvertedParameter(obj.getId(), inputParam);
                    netFactsType.getInputParam().add(inputParam);
                } else {
                    // Local
                    final VariableFactsType var = convertLocalNetObject(softObject, i, localParamNameSet);
                    getContext().addConvertedParameter(obj.getId(), var);
                    LOGGER.debug("Added local variable {} to Net decomposition {}", var.getName(), netFactsType.getName());
                    netFactsType.getLocalVariable().add(var);
                }
                i++;
            }
        }
    }

    private VariableFactsType convertLocalNetObject(final SoftType obj, final int index, final Set<String> nameSet) {
        final VariableFactsType localVar = getContext().getYawlObjectFactory().createVariableFactsType();
        if (nameSet.contains(obj.getName())) {
            localVar.setName(ConversionUtils.generateUniqueName(obj.getName(), nameSet));
        } else {
            localVar.setName(obj.getName());
        }
        nameSet.add(localVar.getName());
        localVar.setType(obj.getType());
        localVar.setIndex(BigInteger.valueOf(index));
        // localVar.setInitialValue("");
        return localVar;
    }

    private void updateCompositeTasks(final NetFactsType netFactsType) {
        final Collection<ExternalTaskFactsType> compositeTasks = getContext().getCompositeTasks(getObject().getId());
        for (final ExternalTaskFactsType task : compositeTasks) {
            task.setDecomposesTo(netFactsType);
        }
    }

    /**
     * @return if the Net is a Root Net
     * @throws CanoniserException
     */
    private boolean isRootNet() throws CanoniserException {
        final CanonicalProcessType cProcess = (CanonicalProcessType) getOriginalParent();
        if (cProcess.getRootIds() != null && !cProcess.getRootIds().isEmpty()) {
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
