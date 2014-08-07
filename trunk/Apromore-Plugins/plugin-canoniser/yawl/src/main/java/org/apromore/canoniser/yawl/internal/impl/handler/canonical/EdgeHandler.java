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
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.PredicateType;

/**
 * Converts an EdgeType to YAWL 'flowsInto'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class EdgeHandler extends CanonicalElementHandler<EdgeType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // Find Source of Edge
        ExternalNetElementFactsType sourceElement;

        if (getContext().getControlFlowContext().getElementInfo(getObject().getSourceId()).getElement() != null) {
            // Source will always be ExternalNetElementFactsType, as OutputCondition has no successor
            sourceElement = (ExternalNetElementFactsType) getContext().getControlFlowContext().getElementInfo(getObject().getSourceId()).getElement();
        } else {
            LOGGER.warn("Could not find source element {} for Edge {}.", getObject().getSourceId(), getObject().getId());
            return; // Ignore Edge
        }

        // Find Target of Edge
        ExternalNetElementType targetElement = getContext().getControlFlowContext().getElementInfo(getObject().getTargetId()).getElement();
        if (targetElement == null) {
            final OutputConditionFactsType outputCondition = getConvertedParent().getProcessControlElements().getOutputCondition();
            if (outputCondition != null) {
                if (outputCondition.getId().equals(generateUUID(getObject().getTargetId()))) {
                    targetElement = outputCondition;
                }
            } else {
                LOGGER.warn("Missing OutputCondition in YAWL Net", getConvertedParent().getId());
            }

        }
        if (targetElement == null) {
            LOGGER.warn("Could not find target element {} for Edge {}.", getObject().getTargetId(), getObject().getId());
            return; // Ignore Edge
        }

        if (sourceElement.getId().equals(targetElement.getId())) {
            // Invalid edge pointing at the same element,
            // probably because we merged split or join nodes into a single task
            LOGGER.debug("Ignoring Edge from {} to {}", getObject().getSourceId(), getObject().getTargetId());
            return;
        }

        final FlowsIntoType flowsIntoType = YAWL_FACTORY.createFlowsIntoType();
        final ExternalNetElementType netElementType = YAWL_FACTORY.createExternalNetElementType();
        netElementType.setId(targetElement.getId());
        flowsIntoType.setNextElementRef(netElementType);
        if (getObject().getConditionExpr() != null) {
            final PredicateType predicate = YAWL_FACTORY.createPredicateType();
            predicate.setValue(convertCanonicalExpression(getObject().getConditionExpr()));
            final List<NodeType> postSet = getContext().getPostSet(getObject().getSourceId());
            if (isXorSplit(sourceElement)) {
                if (postSet.size() > 1) {
                    BigInteger targetIndex = determineTargetIndex(postSet);
                    predicate.setOrdering(targetIndex);
                } else {
                    predicate.setOrdering(BigInteger.valueOf(1));
                }
            }
            if (getObject().isDefault()) {
                flowsIntoType.setIsDefaultFlow("");
            }
            flowsIntoType.setPredicate(predicate);
            LOGGER.debug("Adding Flow from {} to {} with condition {}",
                    new String[] { sourceElement.getName(), targetElement.getId(), predicate.getValue() });
        } else {
            decideDefaultFlowWithOutCondition(sourceElement, targetElement, flowsIntoType);
        }

        sourceElement.getFlowsInto().add(flowsIntoType);

        getContext().getControlFlowContext().addConvertedFlow(getObject().getId(), flowsIntoType);
    }

    private void decideDefaultFlowWithOutCondition(final ExternalNetElementFactsType sourceElement, final ExternalNetElementType targetElement,
            final FlowsIntoType flowsIntoType) throws CanoniserException {
        final List<NodeType> postSet = getContext().getPostSet(getObject().getSourceId());
        if (postSet.size() > 1) {
            if (!isAndSplit(sourceElement)) {
                //TODO infer default flow if not correctly set
                flowsIntoType.setIsDefaultFlow("");
                LOGGER.debug("Adding Flow from {} to {} as default flow", sourceElement.getName(), targetElement.getId());
            }
        } else {
            LOGGER.debug("Adding Flow from {} to {}", sourceElement.getName(), targetElement.getId());
        }
    }

    /**
     * Returns true if the element has an AND split attached to it.
     *
     * @param element
     * @return
     */
    private boolean isAndSplit(final ExternalNetElementFactsType element) {
        if (element instanceof ExternalTaskFactsType) {
            ExternalTaskFactsType task = (ExternalTaskFactsType) element;
            return task.getSplit().getCode().equals(ControlTypeCodeType.AND);
        }
        return false;
    }

    /**
     * Returns true if the element has an XOR split attached to it.
     *
     * @param element
     * @return
     */
    private boolean isXorSplit(final ExternalNetElementFactsType element) {
        if (element instanceof ExternalTaskFactsType) {
            ExternalTaskFactsType task = (ExternalTaskFactsType) element;
            return task.getSplit().getCode().equals(ControlTypeCodeType.XOR);
        }
        return false;
    }

    private String convertCanonicalExpression(final ConditionExpressionType conditionExpr) {
        // TODO check and convert XPath
        if (conditionExpr.getLanguage() != null && conditionExpr.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XPATH)) {
            if (conditionExpr.getExpression() != null) {
                return conditionExpr.getExpression();
            }
        } else {
            if (conditionExpr.getDescription() != null) {
                return conditionExpr.getDescription();
            }
        }
        return "Missing Condition";
    }

    private BigInteger determineTargetIndex(final List<NodeType> postSet) throws CanoniserException {
        final NodeType targetNode = getContext().getNodeById(getObject().getTargetId());
        if (postSet.contains(targetNode)) {
            return BigInteger.valueOf(postSet.indexOf(targetNode));
        } else {
            throw new CanoniserException("Invalid Edge! Target Node is not part of the postset of source Node! Edge-ID: " + getObject().getId());
        }
    }

}
