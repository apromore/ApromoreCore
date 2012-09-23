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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
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
import org.yawlfoundation.yawlschema.VarMappingSetType;
import org.yawlfoundation.yawlschema.VariableBaseType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType.YawlService;

/**
 * Converts a TaskType and also directly pre-/succeeding routing Nodes.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
/**
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class TaskTypeHandler extends DecompositionHandler<TaskType, NetFactsType> {
    // TODO refactor this class

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTypeHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final ExternalTaskFactsType taskFacts = createTask(getObject());

        boolean isAutomaticTask = false;

        if (isCompositeTask()) {
            final NetFactsType yawlNet = getContext().getConvertedNet(getObject().getSubnetId());
            if (yawlNet != null) {
                taskFacts.setDecomposesTo(yawlNet);
            } else {
                // Remember Task points to a Net. Can not convert now, as Net not have been converted yet.
                getContext().addCompositeTask(getObject().getSubnetId(), taskFacts);
            }
        } else {
            final WebServiceGatewayFactsType d = createDecomposition(getObject());
            taskFacts.setDecomposesTo(d);

            if (hasResources(getObject()) && !isAutomatic(getObject())) {
                taskFacts.setResourcing(convertResourceing(d));
            } else {
                isAutomaticTask = true;
                d.setCodelet(convertCodelet(getObject()));
                d.setYawlService(convertYAWLService(getObject()));
            }

            convertDataObjects(d);
        }

        if (hasOutputReferences(getObject())) {
            taskFacts.setCompletedMappings(convertOutputReferences(getObject(), taskFacts));
        }

        if (hasInputReferences(getObject())) {
            taskFacts.setStartingMappings(convertInputReferences(getObject(), taskFacts));
        }

        taskFacts.setJoin(convertJoinType(getObject(), taskFacts));
        taskFacts.setSplit(convertSplitType(getObject(), taskFacts));
        taskFacts.setTimer(convertTimer(getObject(), taskFacts, isAutomaticTask));

        LOGGER.debug("Added Task {} to Net {}", taskFacts.getName(), getConvertedParent().getName());
        getConvertedParent().getProcessControlElements().getTaskOrCondition().add(taskFacts);
    }

    private YawlService convertYAWLService(final TaskType object) {
        // TODO Auto-generated method stub
        return null;
    }

    private String convertCodelet(final TaskType task) {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean isAutomatic(final TaskType task) {
        // TODO Auto-generated method stub
        return false;
    }

    private TimerType convertTimer(final TaskType task, final ExternalTaskFactsType taskFacts, final boolean isAutomatic) throws CanoniserException {
        final TimerType timer = getContext().getElementInfo(task.getId()).timer;
        if (timer != null) {
            return timer;
        }

        final NodeType predecessor = getContext().getFirstPredecessor(task.getId());
        if (predecessor instanceof org.apromore.cpf.TimerType && isAutomatic) {
            // Remember that we removed the timer node
            getContext().setElement(predecessor.getId(), taskFacts);
            // Create Task or attach existing
            final TimerType yawlTimer = createTimer(task);
            LOGGER.debug("Added Timer (onEnablement) to Task {}", taskFacts.getName());
            return yawlTimer;
        }

        return null;
    }

    /********************* CONTROL FLOW ***************/

    private boolean isCompositeTask() {
        return getObject().getSubnetId() != null;
    }

    private ControlTypeType convertJoinType(final TaskType task, final ExternalTaskFactsType taskFacts) throws CanoniserException {
        final ControlTypeType joinType = getContext().getElementInfo(task.getId()).joinType;
        if (joinType != null) {
            getContext().setJoinRouting(taskFacts.getId());
            LOGGER.debug("Added JOIN decorator of type {} to Task {}", joinType.getCode(), taskFacts.getName());
            return joinType;
        } else {
            return getDefaultJoinType();
        }
    }

    private ControlTypeType convertSplitType(final TaskType task, final ExternalTaskFactsType taskFacts) throws CanoniserException {
        final ControlTypeType splitType = getContext().getElementInfo(task.getId()).splitType;
        if (splitType != null) {
            getContext().setSplitRouting(taskFacts.getId());
            LOGGER.debug("Added SPLIT decorator of type {} to Task {}", splitType.getCode(), taskFacts.getName());
            return splitType;
        } else {
            return getDefaultSplitType();
        }
    }

    /************* RESOURCES **************/

    private boolean hasResources(final TaskType task) {
        return task.getResourceTypeRef() != null && !task.getResourceTypeRef().isEmpty();
    }

    private ResourcingFactsType convertResourceing(final WebServiceGatewayFactsType decompositionType) {
        final ResourcingFactsType resourceing = getContext().getYawlObjectFactory().createResourcingFactsType();

        final ResourcingOfferFactsType offer = getContext().getYawlObjectFactory().createResourcingOfferFactsType();
        offer.setInitiator(ResourcingInitiatorType.SYSTEM);
        final ResourcingDistributionSetFactsType distributionSet = getContext().getYawlObjectFactory().createResourcingDistributionSetFactsType();
        distributionSet.setInitialSet(convertInitialDistributionSet());
        offer.setDistributionSet(distributionSet);
        resourceing.setOffer(offer);

        final ResourcingStartFactsType start = getContext().getYawlObjectFactory().createResourcingStartFactsType();
        start.setInitiator(ResourcingInitiatorType.USER);
        resourceing.setStart(offer);

        final ResourcingAllocateFactsType allocate = getContext().getYawlObjectFactory().createResourcingAllocateFactsType();
        allocate.setInitiator(ResourcingInitiatorType.USER);
        resourceing.setAllocate(allocate);

        // TODO Secondary Resources
        // resourceing.setSecondary();

        return resourceing;
    }

    private InitialSet convertInitialDistributionSet() {
        final InitialSet initialDistributionSet = getContext().getYawlObjectFactory().createResourcingDistributionSetFactsTypeInitialSet();
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
        return resourceType.getDistributionSet() != null && resourceType.getDistributionSet().getResourceTypeRef() != null
                && !resourceType.getDistributionSet().getResourceTypeRef().isEmpty();
    }

    /************* DATA **************/

    private void convertDataObjects(final WebServiceGatewayFactsType taskDecomposition) throws CanoniserException {

        // Both sets are used to keep YAWL variable names unique, which is not enforced by CPF
        final Set<String> inputParamNameSet = new HashSet<String>();
        final Set<String> outputParamNameSet = new HashSet<String>();

        // int i = 0;
        // for (final ObjectType obj : getObject().getObject()) {
        // if (isInput(obj.getId()) && isOutput(obj.getId())) {
        // final InputParameterFactsType inputParam = convertInputParameterObject((SoftType) obj, i, inputParamNameSet);
        // getContext().addConvertedParameter(obj.getId(), inputParam);
        // LOGGER.debug("Added input parameter {} to Task decomposition {}", inputParam.getName(), taskDecomposition.getName());
        // taskDecomposition.getInputParam().add(inputParam);
        //
        // final OutputParameterFactsType outputParam = convertOutputParameterObject((SoftType) obj, i, outputParamNameSet);
        // getContext().addConvertedParameter(obj.getId(), outputParam);
        // LOGGER.debug("Added output parameter {} to Task decomposition {}", outputParam.getName(), taskDecomposition.getName());
        // taskDecomposition.getOutputParam().add(outputParam);
        // } else if (isOutput(obj.getId())) {
        // final OutputParameterFactsType outputParam = convertOutputParameterObject((SoftType) obj, i, outputParamNameSet);
        // getContext().addConvertedParameter(obj.getId(), outputParam);
        // LOGGER.debug("Added output parameter {} to Task decomposition {}", outputParam.getName(), taskDecomposition.getName());
        // taskDecomposition.getOutputParam().add(outputParam);
        // } else if (isInput(obj.getId())) {
        // final InputParameterFactsType inputParam = convertInputParameterObject((SoftType) obj, i, inputParamNameSet);
        // LOGGER.debug("Added input parameter {} to Task decomposition {}", inputParam.getName(), taskDecomposition.getName());
        // getContext().addConvertedParameter(obj.getId(), inputParam);
        // taskDecomposition.getInputParam().add(inputParam);
        // }
        // i++;
        // }
    }

    // private boolean isOutput(final String objectId) {
    // // TODO optimize
    // for (final ObjectRefType ref : getObject().getObjectRef()) {
    // if (ref.getMapsToObjectId().equals(objectId)) {
    // if (ref.getType() == InputOutputType.OUTPUT) {
    // return true;
    // }
    // }
    // }
    // return false;
    // }
    //
    // private boolean isInput(final String objectId) {
    // // TODO optimize
    // for (final ObjectRefType ref : getObject().getObjectRef()) {
    // if (ref.getMapsToObjectId().equals(objectId)) {
    // if (ref.getType() == InputOutputType.INPUT) {
    // return true;
    // }
    // }
    // }
    // return false;
    // }

    private boolean hasInputReferences(final TaskType task) {
        for (final ObjectRefType ref : task.getObjectRef()) {
            if (ref.getType().equals(InputOutputType.INPUT)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasOutputReferences(final TaskType task) {
        for (final ObjectRefType ref : task.getObjectRef()) {
            if (ref.getType().equals(InputOutputType.OUTPUT)) {
                return true;
            }
        }
        return false;
    }

    private VarMappingSetType convertInputReferences(final TaskType taskNode, final ExternalTaskFactsType yawlTask) throws CanoniserException {

        return null;

        // // There may be more than one mapping to the same Task/Net variable.
        // // We could either try to merge both or just ignore the second.
        // // As merging arbitrary types is not feasible we ignore!
        // final Set<String> mapsToSet = new HashSet<String>();
        //
        // final VarMappingSetType inputMapping = getContext().getYawlObjectFactory().createVarMappingSetType();
        // for (final ObjectRefType ref : taskNode.getObjectRef()) {
        // if (ref.getType() == InputOutputType.INPUT) {
        // if (!hasMapping(mapsToSet, isCompositeTask(), ref.getMapsToObjectId())) {
        // final VarMappingFactsType varMapping = getContext().getYawlObjectFactory().createVarMappingFactsType();
        //
        // String varName = null;
        //
        // // Look if there is information about to which Object the reference is mapped
        // if (ref.getMapsToObjectId() != null) {
        // // Find task variable in case of an atomic task or net variable in case of a composite task
        // if (isCompositeTask()) {
        // final ObjectType unconvertedObject = getContext().getObjectTypeById(ref.getMapsToObjectId());
        // if (unconvertedObject != null) {
        // varMapping.setMapsTo(unconvertedObject.getName());
        // } else {
        // throw new CanoniserException("Task is referencing non-exsting Object with ID " + ref.getMapsToObjectId());
        // }
        // // Remember we already added a reference to this Object
        // mapsToSet.add("C" + ref.getMapsToObjectId());
        // } else {
        // final VariableBaseType param = getContext().getConvertedParameter(ref.getMapsToObjectId());
        // if (param != null) {
        // varName = param.getName();
        // varMapping.setMapsTo(varName);
        // } else {
        // // Invalid CPF or programming error
        // throw new CanoniserException("Task is referencing Object with ID " + ref.getMapsToObjectId()
        // + ", but the YAWL variable (INPUT) is missing.");
        // }
        // // Remember we already added a reference to this Object
        // mapsToSet.add("T" + ref.getMapsToObjectId());
        // }
        //
        // } else {
        // // No information about mapping target -> Create new Task or Net variable
        // if (isCompositeTask()) {
        // if (yawlTask.getDecomposesTo() != null) {
        // varName = createArtificialVariable(ref, (DecompositionFactsType) yawlTask.getDecomposesTo()).getName();
        // varMapping.setMapsTo(varName);
        // } else {
        // // Remember we've to add an artificial variable to our decomposition later on
        // getContext().addIntroducedVariable(getObject().getSubnetId(), createArtificialVariable(ref, null));
        // }
        // } else {
        // varName = createArtificialVariable(ref, (DecompositionFactsType) yawlTask.getDecomposesTo()).getName();
        // varMapping.setMapsTo(varName);
        // }
        // }
        //
        // final ExpressionType expr = getContext().getYawlObjectFactory().createExpressionType();
        // if (hasYAWLExpression(ref)) {
        // // Use original YAWL expression
        // expr.setQuery(getYAWLExpression(ref));
        // } else {
        // // Build our own expression
        // expr.setQuery(buildInputQuery(varName));
        // }
        // varMapping.setExpression(expr);
        //
        // inputMapping.getMapping().add(varMapping);
        // }
        // }
        // }
        // return inputMapping;
    }

    private boolean hasMapping(final Set<String> mapsToSet, final boolean compositeTask, final String mapsToObjectId) {
        if (compositeTask) {
            return mapsToSet.contains("C" + mapsToObjectId);
        } else {
            return mapsToSet.contains("T" + mapsToObjectId);
        }
    }

    private VariableBaseType createArtificialVariable(final ObjectRefType ref, final DecompositionFactsType d) {

        final SoftType obj = (SoftType) getContext().getObjectTypeById(ref.getObjectId());

        if (ref.getType() == InputOutputType.INPUT) {
            final InputParameterFactsType param = getContext().getYawlObjectFactory().createInputParameterFactsType();
            param.setName(obj.getName());
            param.setType(obj.getType());
            if (d != null) {
                param.setIndex(BigInteger.valueOf(calculateMaxIndex(d.getInputParam()) + 1));
            }
            return param;
        } else {
            final OutputParameterFactsType param = getContext().getYawlObjectFactory().createOutputParameterFactsType();
            param.setName(obj.getName());
            param.setType(obj.getType());
            if (d != null) {
                param.setIndex(BigInteger.valueOf(calculateMaxIndex(d.getOutputParam()) + 1));
            }
            return param;
        }
    }

    private int calculateMaxIndex(final List<? extends VariableBaseType> varList) {
        int maxIndex = 0;
        for (final VariableBaseType var : varList) {
            maxIndex = Math.max(maxIndex, var.getIndex().intValue());
        }
        return maxIndex;
    }

    private boolean hasYAWLExpression(final ObjectRefType ref) {
        return getYAWLExpression(ref) != null;
    }

    private String getYAWLExpression(final ObjectRefType ref) {
        return null;
        // for (final TypeAttribute attr : ref.getAttribute()) {
        // if (attr.getTypeRef().equals("yawlOriginalExpression")) {
        // return attr.getValue();
        // }
        // }
        // return null;
    }

    private VarMappingSetType convertOutputReferences(final TaskType taskNode, final ExternalTaskFactsType yawlTask) throws CanoniserException {
        return null;
        // final VarMappingSetType inputMapping = getContext().getYawlObjectFactory().createVarMappingSetType();
        // for (final ObjectRefType ref : taskNode.getObjectRef()) {
        // if (ref.getType() == InputOutputType.OUTPUT) {
        // final VarMappingFactsType varMapping = getContext().getYawlObjectFactory().createVarMappingFactsType();
        //
        // // Find net variable that we're refering to
        // final VariableBaseType param = getContext().getConvertedParameter(ref.getObjectId());
        // if (param != null) {
        // varMapping.setMapsTo(param.getName());
        // } else {
        // // Invalid CPF or programming error
        // throw new CanoniserException("Task is referencing Object with ID " + ref.getMapsToObjectId()
        // + ", but the YAWL variable (OUTPUT) is missing.");
        // }
        //
        // final ExpressionType expr = getContext().getYawlObjectFactory().createExpressionType();
        // if (hasYAWLExpression(ref)) {
        // // Use original YAWL expression
        // expr.setQuery(getYAWLExpression(ref));
        // } else {
        // // Build our own expression
        // expr.setQuery(buildOutputQuery(param.getName()));
        // }
        // varMapping.setExpression(expr);
        //
        // inputMapping.getMapping().add(varMapping);
        // }
        // }
        // return inputMapping;
    }

    private String buildInputQuery(final String variableName) {
        return "/" + getConvertedParent().getId() + "/" + variableName;
    }

    private String buildOutputQuery(final String variableName) {
        return "/" + getObject().getId() + "/" + variableName;
    }

}
