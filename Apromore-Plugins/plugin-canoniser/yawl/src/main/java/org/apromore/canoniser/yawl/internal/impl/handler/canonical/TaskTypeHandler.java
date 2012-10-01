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
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ConfigurationType;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.ResourcingAllocateFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.InitialSet;
import org.yawlfoundation.yawlschema.ResourcingFactsType;
import org.yawlfoundation.yawlschema.ResourcingInitiatorType;
import org.yawlfoundation.yawlschema.ResourcingOfferFactsType;
import org.yawlfoundation.yawlschema.ResourcingStartFactsType;
import org.yawlfoundation.yawlschema.TimerType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType.YawlService;

/**
 * Converts a TaskType to a YAWL Task (Atomic/Composite)
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class TaskTypeHandler extends DecompositionHandler<TaskType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTypeHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final ExternalTaskFactsType taskFacts = createTask(getObject());

        if (ConversionUtils.isCompositeTask(getObject())) {
            final NetFactsType yawlNet = (NetFactsType) getContext().getConvertedDecomposition(getObject().getSubnetId());
            if (yawlNet != null) {
                DecompositionType refD = YAWL_FACTORY.createDecompositionType();
                refD.setId(yawlNet.getId());
                taskFacts.setDecomposesTo(refD);
            } else {
                // Remember Task points to a Net. Can not convert now, as Net not have been converted yet.
                getContext().addCompositeTask(getObject().getSubnetId(), taskFacts);
            }
        } else {
            final WebServiceGatewayFactsType d = createDecomposition(getObject());

            DecompositionType refD = YAWL_FACTORY.createDecompositionType();
            refD.setId(d.getId());

            taskFacts.setDecomposesTo(refD);

            if (hasResources(getObject()) && !isAutomatic(getObject())) {
                taskFacts.setResourcing(convertResourceing());
            } else {
                convertCodelet(d, getObject());
                convertYAWLService(d, getObject());
            }
        }

        taskFacts.setJoin(convertJoinType(getObject(), taskFacts));
        taskFacts.setSplit(convertSplitType(getObject(), taskFacts));

        convertDataObjects(taskFacts, getObject());
        convertTimer(getObject(), taskFacts);
        convertConfiguration(getObject(), taskFacts);

        LOGGER.debug("Added Task {} to Net {}", taskFacts.getName(), getConvertedParent().getName());
        getConvertedParent().getProcessControlElements().getTaskOrCondition().add(taskFacts);
    }

    private void convertConfiguration(final TaskType task, final ExternalTaskFactsType taskFacts) {
        taskFacts.setConfiguration(ExtensionUtils.getFromNodeExtension(task, ExtensionUtils.CONFIGURATION, ConfigurationType.class, null));
    }

    private void convertTimer(final TaskType task, final ExternalTaskFactsType taskFacts) {
        TimerType timer = getContext().getElementInfo(task.getId()).getTimer();
        if (timer != null) {
            taskFacts.setTimer(timer);
        }
    }

    private void convertYAWLService(final WebServiceGatewayFactsType d, final TaskType task) {
        d.setYawlService(ExtensionUtils.getFromNodeExtension(task, ExtensionUtils.YAWL_SERVICE, YawlService.class, null));
    }

    private void convertCodelet(final WebServiceGatewayFactsType d, final TaskType task) {
        d.setCodelet(ExtensionUtils.getFromNodeExtension(task, ExtensionUtils.CODELET, String.class, null));
    }

    private ControlTypeType convertJoinType(final TaskType task, final ExternalTaskFactsType taskFacts) throws CanoniserException {
        final ControlTypeType joinType = getContext().getElementInfo(task.getId()).getJoinType();
        if (joinType != null) {
            getContext().setJoinRouting(taskFacts.getId());
            LOGGER.debug("Added JOIN decorator of type {} to Task {}", joinType.getCode(), taskFacts.getName());
            return joinType;
        } else {
            return getDefaultJoinType();
        }
    }

    private ControlTypeType convertSplitType(final TaskType task, final ExternalTaskFactsType taskFacts) throws CanoniserException {
        final ControlTypeType splitType = getContext().getElementInfo(task.getId()).getSplitType();
        if (splitType != null) {
            getContext().setSplitRouting(taskFacts.getId());
            LOGGER.debug("Added SPLIT decorator of type {} to Task {}", splitType.getCode(), taskFacts.getName());
            return splitType;
        } else {
            return getDefaultSplitType();
        }
    }

    private boolean isAutomatic(final TaskType task) {
        boolean isAutomatic = false;
        for (ResourceTypeRefType ref : task.getResourceTypeRef()) {
            ResourceTypeType resource = getContext().getResourceTypeById(ref.getResourceTypeId());
            isAutomatic = isAutomatic || resource instanceof NonhumanType;
        }
        return isAutomatic;
    }

    private boolean hasResources(final TaskType task) {
        return task.getResourceTypeRef() != null && !task.getResourceTypeRef().isEmpty();
    }

    private ResourcingFactsType convertResourceing() {
        final ResourcingFactsType resourceing = YAWL_FACTORY.createResourcingFactsType();

        final ResourcingOfferFactsType offer = YAWL_FACTORY.createResourcingOfferFactsType();
        offer.setInitiator(ResourcingInitiatorType.SYSTEM);
        final ResourcingDistributionSetFactsType distributionSet = YAWL_FACTORY.createResourcingDistributionSetFactsType();
        distributionSet.setInitialSet(convertInitialDistributionSet());
        offer.setDistributionSet(distributionSet);
        resourceing.setOffer(offer);

        final ResourcingStartFactsType start = YAWL_FACTORY.createResourcingStartFactsType();
        start.setInitiator(ResourcingInitiatorType.USER);
        resourceing.setStart(offer);

        final ResourcingAllocateFactsType allocate = YAWL_FACTORY.createResourcingAllocateFactsType();
        allocate.setInitiator(ResourcingInitiatorType.USER);
        resourceing.setAllocate(allocate);

        // TODO Secondary Resources
        // resourceing.setSecondary();

        return resourceing;
    }

    private InitialSet convertInitialDistributionSet() {
        final InitialSet initialDistributionSet = YAWL_FACTORY.createResourcingDistributionSetFactsTypeInitialSet();
        final List<ResourceTypeRefType> resourceRefList = getObject().getResourceTypeRef();
        if (resourceRefList.size() > 1) {
            // Not supported by YAWL
            LOGGER.warn("Can not convert resource information of Task {}, as YAWL does not support teamwork!", getObject().getName());
        } else if (resourceRefList.size() == 1) {
            // Either single Resource or DistributionSet
            final ResourceTypeRefType resourceReference = resourceRefList.get(0);
            final ResourceTypeType resourceType = getContext().getResourceTypeById(resourceReference.getResourceTypeId());
            if (resourceType != null) {
                if (hasDistributionSet(resourceType)) {
                    // Distribution Set
                    LOGGER.debug("Would convert Distribution Set");
                } else {
                    // Single Role or Participant
                    LOGGER.debug("Would convert single Role or Participant");
                }
            } else {
                LOGGER.warn("Could not find ResourceType with ID {}! Invalid CPF!", resourceReference.getResourceTypeId());
            }
        }
        return initialDistributionSet;
    }

    private boolean hasDistributionSet(final ResourceTypeType resourceType) {
        return false;
//        return resourceType.getDistributionSet() != null && resourceType.getDistributionSet().getResourceTypeRef() != null
//                && !resourceType.getDistributionSet().getResourceTypeRef().isEmpty();
    }

    private void convertDataObjects(final ExternalTaskFactsType taskFacts, final TaskType task) throws CanoniserException {

        if (hasExpressions(task)) {
            convertDataExpressions(taskFacts, task);
        } else {
            convertObjectReferences(taskFacts, task);
        }

        if (taskFacts.getDecomposesTo() != null) {
            if (ConversionUtils.isCompositeTask(task)) {
                updateParamIndexes(getContext().getConvertedDecomposition(task.getSubnetId()));
            } else {
                updateParamIndexes(getContext().getConvertedDecomposition(task.getId()));
            }
        }
    }

    private void updateParamIndexes(final DecompositionFactsType d) {
        BigInteger i = BigInteger.ONE;
        for (InputParameterFactsType input: d.getInputParam()) {
            input.setIndex(i);
            i = i.add(BigInteger.ONE);
        }
        BigInteger j = BigInteger.ONE;
        for (OutputParameterFactsType output: d.getOutputParam()) {
            output.setIndex(j);
            j = j.add(BigInteger.ONE);
        }
    }

    private void convertObjectReferences(final ExternalTaskFactsType taskFacts, final TaskType task) throws CanoniserException {
        for (ObjectRefType objectRef: task.getObjectRef()) {
            getContext().createHandler(objectRef, taskFacts, task).convert();
        }
    }

    private void convertDataExpressions(final ExternalTaskFactsType taskFacts, final TaskType task) throws CanoniserException {
        for (InputExpressionType inputExpr: task.getInputExpr()) {
            getContext().createHandler(inputExpr, taskFacts, task).convert();
        }
        for (OutputExpressionType outputExpr: task.getOutputExpr()) {
            getContext().createHandler(outputExpr, taskFacts, task).convert();
        }
    }

    private boolean hasExpressions(final TaskType task) {
        return (task.getInputExpr() != null && task.getInputExpr().size() > 1)
                || (task.getOutputExpr() != null && task.getOutputExpr().size() > 1);
    }

}
