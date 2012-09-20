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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow;

import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.data.InputVarMappingHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.data.OutputVarMappingHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.data.TaskVariableHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CancellationSetType;
import org.apromore.cpf.ControlFlowRef;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.yawlfoundation.yawlschema.ConfigurationType;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.RemovesTokensFromFlowType;
import org.yawlfoundation.yawlschema.ResourcingExternalInteractionType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

/**
 * Base class for converting a YAWL task
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 * @param <T>
 */
public abstract class BaseTaskHandler extends ExternalNetElementHandler<ExternalTaskFactsType> {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        convertAnnotations();
    }

    /**
     * Return a converted TaskType that is already added to its parent Net
     * 
     * @param task
     * @return the CPF task
     * @throws CanoniserException
     */
    protected TaskType createTask(final ExternalTaskFactsType task) throws CanoniserException {
        final TaskType taskNode = getContext().getCanonicalOF().createTaskType();
        taskNode.setId(generateUUID(CONTROLFLOW_ID_PREFIX, task.getId()));
        taskNode.setOriginalID(task.getId());
        taskNode.setName(task.getName());

        // Need to add the Task to a Net before converting the Decomposition,
        // as we may need to reference Objects from this Net
        getConvertedParent().getNode().add(taskNode);

        if (task.getDecomposesTo() != null) {
            final DecompositionType d = getContext().getDecompositionByID(task.getDecomposesTo().getId());
            if (d != null) {
                convertDecomposition(taskNode, d);
            } else {
                throw new CanoniserException("Could not find decomposition with ID " + task.getDecomposesTo().getId() + " for Task " + task.getId());
            }
        }
        // Need to do this after the variables are converted
        convertDataMappings(taskNode, task);
        convertConfiguration(taskNode, task);
        convertResources(taskNode, task);
        convertCancellation(taskNode, task);
        return taskNode;
    }

    protected void linkToSucessors(final NodeType taskExitNode) throws CanoniserException {
        // Create SPLIT routings if necessary
        if (checkSingleExit(getObject())) {
            // This is safe as we're SESE
            connectToSuccessors(taskExitNode, getObject().getFlowsInto());
        } else {
            final RoutingType splitRouting = createSplit();
            // Connect ourself with the introduced routing node
            createSimpleEdge(taskExitNode, splitRouting);
            connectToSuccessors(splitRouting, getObject().getFlowsInto());
        }
    }

    protected void linkToPredecessors(final NodeType taskEntryNode) throws CanoniserException {
        if (hasIncomingQueue(getObject()) && !checkSingleEntry(getObject())) {
            // Create routing if we are non single entry
            final RoutingType routingNode = createJoin();
            // Connect ourself with the state node
            createSimpleEdge(routingNode, taskEntryNode);
            connectFromPredecessors(getObject(), routingNode);
        } else if (hasIncomingQueue(getObject())) {
            connectFromPredecessors(getObject(), taskEntryNode);
        }
    }

    /**
     * Create a Join Routing Node
     * 
     * @return
     * @throws CanoniserException
     */
    protected RoutingType createJoin() throws CanoniserException {
        final ControlTypeType split = getObject().getJoin();
        RoutingType routing;
        switch (split.getCode()) {
        case AND:
            routing = createANDJoin();
            break;
        case OR:
            routing = createORJoin();
            break;
        case XOR:
            routing = createXORJoin();
            break;
        default:
            throw new CanoniserException("Unkown RoutingType " + split.getCode().name());
        }
        return routing;
    }

    protected XORJoinType createXORJoin() {
        final XORJoinType xorJoin = getContext().getCanonicalOF().createXORJoinType();
        xorJoin.setId(generateUUID());
        getConvertedParent().getNode().add(xorJoin);
        return xorJoin;
    }

    protected ORJoinType createORJoin() {
        final ORJoinType orJoin = getContext().getCanonicalOF().createORJoinType();
        orJoin.setId(generateUUID());
        getConvertedParent().getNode().add(orJoin);
        return orJoin;
    }

    protected ANDJoinType createANDJoin() {
        final ANDJoinType andJoin = getContext().getCanonicalOF().createANDJoinType();
        andJoin.setId(generateUUID());
        getConvertedParent().getNode().add(andJoin);
        return andJoin;
    }

    /**
     * Create a Split Routing Node
     * 
     * @return
     * @throws CanoniserException
     */
    protected RoutingType createSplit() throws CanoniserException {
        final ControlTypeType split = getObject().getSplit();
        RoutingType routing;
        switch (split.getCode()) {
        case AND:
            routing = createANDSplit();
            break;
        case OR:
            routing = createORSplit();
            break;
        case XOR:
            routing = createXORSplit();
            break;
        default:
            throw new CanoniserException("Unkown RoutingType " + split.getCode().name());
        }
        return routing;
    }

    protected XORSplitType createXORSplit() {
        final XORSplitType xorSplit = getContext().getCanonicalOF().createXORSplitType();
        xorSplit.setId(generateUUID());
        getConvertedParent().getNode().add(xorSplit);
        return xorSplit;
    }

    protected ORSplitType createORSplit() {
        final ORSplitType orSplit = getContext().getCanonicalOF().createORSplitType();
        orSplit.setId(generateUUID());
        getConvertedParent().getNode().add(orSplit);
        return orSplit;
    }

    protected ANDSplitType createANDSplit() {
        final ANDSplitType andSplit = getContext().getCanonicalOF().createANDSplitType();
        andSplit.setId(generateUUID());
        getConvertedParent().getNode().add(andSplit);
        return andSplit;
    }

    /**
     * Convert the Annotations of the currently to be converted Object and adds them to the ANF.
     * 
     * @throws CanoniserException
     *             in case the Annotations can not be converted
     */
    protected void convertAnnotations() throws CanoniserException {
        // Annotation
        createDocumentation(getObject()).getDocumentation().add(getObject().getDocumentation());
        createGraphics(getObject());
    }

    private void convertDecomposition(final TaskType taskNode, final DecompositionType decomposition) throws CanoniserException {
        if (decomposition instanceof NetFactsType) {
            final NetFactsType netDecomposition = (NetFactsType) decomposition;
            taskNode.setSubnetId(generateUUID(NET_ID_PREFIX, netDecomposition.getId()));
        } else if (decomposition instanceof WebServiceGatewayFactsType) {
            final WebServiceGatewayFactsType taskDecomposition = (WebServiceGatewayFactsType) decomposition;
            if (taskDecomposition.getInputParam() != null) {
                for (final InputParameterFactsType param : taskDecomposition.getInputParam()) {
                    getContext().getHandlerFactory().createHandler(param, taskNode, decomposition, TaskVariableHandler.class).convert();
                }
            }
            if (taskDecomposition.getOutputParam() != null) {
                for (final OutputParameterFactsType param : taskDecomposition.getOutputParam()) {
                    getContext().getHandlerFactory().createHandler(param, taskNode, decomposition, TaskVariableHandler.class).convert();
                }
            }
        }
    }

    private void convertDataMappings(final TaskType taskNode, final ExternalTaskFactsType task) throws CanoniserException {
        if (task.getCompletedMappings() != null) {
            for (final VarMappingFactsType mapping : task.getCompletedMappings().getMapping()) {
                getContext().getHandlerFactory().createHandler(mapping, taskNode, task, OutputVarMappingHandler.class).convert();
            }
        }
        if (task.getStartingMappings() != null) {
            for (final VarMappingFactsType mapping : task.getStartingMappings().getMapping()) {
                getContext().getHandlerFactory().createHandler(mapping, taskNode, task, InputVarMappingHandler.class).convert();
            }
        }
    }

    private void convertResources(final TaskType taskNode, final ExternalTaskFactsType task) throws CanoniserException {
        if (task.getResourcing() != null) {
            getContext().getHandlerFactory().createHandler(task.getResourcing(), taskNode, task).convert();
        }
    }

    private void convertConfiguration(final TaskType taskNode, final ExternalTaskFactsType task) {
        if (task.getConfiguration() != null) {
            taskNode.setConfigurable(true);
            addToExtension(ConversionUtils.marshalYAWLFragment("configuration", task.getConfiguration(), ConfigurationType.class), taskNode.getId());
        }
    }

    private void convertCancellation(final TaskType taskNode, final ExternalTaskFactsType task) {
        if (!task.getRemovesTokens().isEmpty() || !task.getRemovesTokensFromFlow().isEmpty()) {

            final CancellationSetType cancellationSet = getContext().getCanonicalOF().createCancellationSetType();
            final List<ControlFlowRef> cancellationList = cancellationSet.getControlFlowRef();

            // Add cancelled Nodes
            for (final ExternalNetElementType element : task.getRemovesTokens()) {
                final ControlFlowRef ref = getContext().getCanonicalOF().createControlFlowRef();
                ref.setControlFlowRefId(generateUUID(CONTROLFLOW_ID_PREFIX, element.getId()));
                cancellationList.add(ref);
            }

            // Add cancelled Edges
            for (final RemovesTokensFromFlowType flow : task.getRemovesTokensFromFlow()) {
                final String sourceId = flow.getFlowSource().getId();
                final String targetId = flow.getFlowDestination().getId();
                final ControlFlowRef ref = getContext().getCanonicalOF().createControlFlowRef();
                ref.setControlFlowRefId(generateEdgeId(sourceId, targetId));
                cancellationList.add(ref);
            }

            taskNode.setCancellationSet(cancellationSet);
        }
    }

    protected boolean isAutomaticTask(final ExternalTaskFactsType task) {
        return task.getDecomposesTo() != null
                && ((WebServiceGatewayFactsType) getContext().getDecompositionByID(task.getDecomposesTo().getId())).getExternalInteraction().equals(
                        ResourcingExternalInteractionType.AUTOMATED);
    }

}
