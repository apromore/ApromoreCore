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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.LayoutDimensionType;
import org.yawlfoundation.yawlschema.LayoutFactsType;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutLocaleType;
import org.yawlfoundation.yawlschema.NetFactsType;

/**
 * Converts all Annotations from ANF to a YAWL layout. It will also guess defaults for elements that don't have any layout.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public class AnnotationsTypeHandler extends CanonicalElementHandler<AnnotationsType, Object> {

    public class BFSInfo {
        public BFSInfo(final int distance) {
            this.depth = distance;
        }

        public int depth;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationsTypeHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // Initalise layout element
        final LayoutFactsType layoutFacts = getContext().getYawlObjectFactory().createLayoutFactsType();
        layoutFacts.setLocale(convertLocale());
        getContext().getYAWLSpecificationSet().setLayout(layoutFacts);

        // Initalise layout for this specification
        final Specification specLayout = getContext().getYawlObjectFactory().createLayoutFactsTypeSpecification();
        specLayout.setId(getContext().getYAWLRootSpecification().getUri());
        convertSize(specLayout);
        layoutFacts.getSpecification().add(specLayout);

        // First convert Net and their Elements Annotations
        for (final Entry<String, NetFactsType> netEntry : getContext().getConvertedNets()) {
            final Collection<AnnotationType> netAnnotations = getContext().getAnnotations(netEntry.getKey());

            boolean graphicsConverted = false;
            for (final AnnotationType ann : netAnnotations) {
                LOGGER.debug("Trying to convert {} for CPF net: {}", ann.getClass().getSimpleName(), ann.getCpfId());
                getContext().getHandlerFactory().createHandler(ann, specLayout, getObject()).convert();
                if (ann instanceof GraphicsType) {
                    graphicsConverted = true;
                }
            }

            if (!graphicsConverted) {
                final GraphicsType fakeGraphic = new org.apromore.anf.ObjectFactory().createGraphicsType();
                fakeGraphic.setCpfId(netEntry.getKey());
                LOGGER.debug("Guessing graphics for CPF net: {}", netEntry.getKey());
                getContext().getHandlerFactory().createHandler(fakeGraphic, specLayout, getObject()).convert();
            }

            // Second convert Element Annotations
            convertNetElementsInBreadthFirstOrder(specLayout, getContext().getNetById(netEntry.getKey()));
        }

        // Third convert all Edge Annotations
        for (final Entry<String, FlowsIntoType> edgeEntry : getContext().getConvertedFlows()) {
            final Collection<AnnotationType> elementAnnotations = getContext().getAnnotations(edgeEntry.getKey());

            boolean graphicsConverted = false;
            for (final AnnotationType ann : elementAnnotations) {
                LOGGER.debug("Trying to convert {} for CPF edge: {}", ann.getClass().getSimpleName(), ann.getCpfId());
                getContext().getHandlerFactory().createHandler(ann, specLayout, getObject()).convert();
                if (ann instanceof GraphicsType) {
                    graphicsConverted = true;
                }
            }

            if (!graphicsConverted) {
                final GraphicsType fakeGraphic = new org.apromore.anf.ObjectFactory().createGraphicsType();
                fakeGraphic.setCpfId(edgeEntry.getKey());
                LOGGER.debug("Guessing graphics for CPF edge: {}", edgeEntry.getKey());
                getContext().getHandlerFactory().createHandler(fakeGraphic, specLayout, getObject()).convert();
            }
        }

    }

    private void convertNetElementsInBreadthFirstOrder(final Specification specLayout, final NetType net) throws CanoniserException {

        final Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
        if (sourceNodes.size() != 1) {
            throw new CanoniserException("YAWL Net contains more than one source Node. Invalid!");
        }

        final NodeType sourceNode = sourceNodes.iterator().next();

        getContext().getAutoLayoutInfo().setMaxBreadth(determineMaxBreadth(sourceNode));
        getContext().getAutoLayoutInfo().setCurrentDistance(0);
        getContext().getAutoLayoutInfo().setCurrentBreadth(1);

        LOGGER.debug("Maximal Breadth of YAWL Net {}", getContext().getAutoLayoutInfo().getMaxBreadth());

        final Queue<NodeType> bfsQueue = new LinkedList<NodeType>();
        bfsQueue.add(sourceNode);

        final Map<String, BFSInfo> markedNodes = new HashMap<String, BFSInfo>();
        markedNodes.put(sourceNode.getId(), new BFSInfo(getContext().getAutoLayoutInfo().getCurrentDistance()));

        int lastDepth = 0;

        while (!bfsQueue.isEmpty()) {
            final NodeType node = bfsQueue.peek();
            final BFSInfo currentNodeInfo = markedNodes.get(node.getId());

            if (currentNodeInfo.depth > lastDepth) {
                lastDepth = currentNodeInfo.depth;
                getContext().getAutoLayoutInfo().setCurrentBreadth(bfsQueue.size());
            }

            getContext().getAutoLayoutInfo().setCurrentDistance(currentNodeInfo.depth);

            convertNode(specLayout, node);

            final List<NodeType> postSet = getContext().getPostSet(node.getId());

            for (final NodeType nextNode : postSet) {
                if (!markedNodes.containsKey(nextNode.getId())) {
                    bfsQueue.add(nextNode);
                    final BFSInfo newNodeInfo = new BFSInfo(currentNodeInfo.depth + 1);
                    markedNodes.put(nextNode.getId(), newNodeInfo);
                }
            }

            getContext().getAutoLayoutInfo().setLastElementDistance(currentNodeInfo.depth);
            bfsQueue.poll();
        }

    }

    private int determineMaxBreadth(final NodeType sourceNode) throws CanoniserException {
        final Queue<NodeType> bfsQueue = new LinkedList<NodeType>();
        bfsQueue.add(sourceNode);

        final Map<String, Integer> markedNodes = new HashMap<String, Integer>();
        markedNodes.put(sourceNode.getId(), 1);

        int currentBreadth = 1;
        int maxBreadth = 1;
        int lastDepth = 1;

        while (!bfsQueue.isEmpty()) {
            final NodeType node = bfsQueue.peek();

            if (markedNodes.get(node.getId()) > lastDepth) {
                lastDepth = markedNodes.get(node.getId());
                currentBreadth = bfsQueue.size();
            }

            final List<NodeType> postSet = getContext().getPostSet(node.getId());

            for (final NodeType nextNode : postSet) {
                if (!markedNodes.containsKey(nextNode.getId())) {
                    bfsQueue.add(nextNode);
                    markedNodes.put(nextNode.getId(), lastDepth + 1);
                }
            }
            maxBreadth = Math.max(currentBreadth, maxBreadth);

            bfsQueue.poll();
        }

        return maxBreadth;
    }

    private void convertNode(final Specification specLayout, final NodeType node) throws CanoniserException {
        final Collection<AnnotationType> elementAnnotations = getContext().getAnnotations(node.getId());

        boolean graphicsConverted = false;
        for (final AnnotationType ann : elementAnnotations) {
            LOGGER.debug("Trying to convert {} for CPF element: {}", ann.getClass().getSimpleName(), ann.getCpfId());
            getContext().getHandlerFactory().createHandler(ann, specLayout, getObject()).convert();
            if (ann instanceof GraphicsType) {
                graphicsConverted = true;
            }
        }

        if (!graphicsConverted) {
            final GraphicsType fakeGraphic = new org.apromore.anf.ObjectFactory().createGraphicsType();
            fakeGraphic.setCpfId(node.getId());
            LOGGER.debug("Guessing graphics for CPF element: {}", node.getId());
            getContext().getHandlerFactory().createHandler(fakeGraphic, specLayout, getObject()).convert();
        }

    }

    private void convertSize(final Specification specLayout) {
        final LayoutDimensionType specDimension = getContext().getYawlObjectFactory().createLayoutDimensionType();
        // TODO what is the size of an specification?
        specDimension.setH(BigInteger.valueOf(100));
        specDimension.setW(BigInteger.valueOf(100));
        specLayout.setSize(specDimension);
    }

    private LayoutLocaleType convertLocale() {
        final LayoutLocaleType layoutLocale = getContext().getYawlObjectFactory().createLayoutLocaleType();
        layoutLocale.setCountry(getContext().getYawlLocale().getCountry());
        layoutLocale.setLanguage(getContext().getYawlLocale().getLanguage());
        return layoutLocale;
    }

}
