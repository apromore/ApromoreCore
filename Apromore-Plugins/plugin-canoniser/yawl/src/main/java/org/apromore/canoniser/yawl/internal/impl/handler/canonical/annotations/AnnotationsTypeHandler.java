/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
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
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class AnnotationsTypeHandler extends CanonicalElementHandler<AnnotationsType, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationsTypeHandler.class);

    private static final int DEFAULT_SPEC_WIDTH = 100;
    private static final int DEFAULT_SPEC_HEIGHT = 100;

    private final class BFSInfo {
        public BFSInfo(final int distance) {
            this.setDepth(distance);
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(final int depth) {
            this.depth = depth;
        }

        private int depth;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // Initalise layout element
        final LayoutFactsType layoutFacts = YAWL_FACTORY.createLayoutFactsType();
        layoutFacts.setLocale(convertLocale());
        getContext().getYAWLSpecificationSet().setLayout(layoutFacts);
        // Remember our Locale for number conversions
        getContext().setYawlLocale(new Locale(layoutFacts.getLocale().getLanguage(), layoutFacts.getLocale().getCountry()));

        // Initalise layout for this specification
        final Specification specLayout = YAWL_FACTORY.createLayoutFactsTypeSpecification();
        specLayout.setId(getContext().getYAWLRootSpecification().getUri());
        convertSize(specLayout);
        layoutFacts.getSpecification().add(specLayout);

        // First convert Net and their Elements Annotations
        for (final Entry<String, NetFactsType> netEntry : getContext().getControlFlowContext().getConvertedNets()) {
            final Collection<AnnotationType> netAnnotations = getContext().getAnnotations(netEntry.getKey());

            boolean graphicsConverted = false;
            for (final AnnotationType ann : netAnnotations) {
                LOGGER.debug("Trying to convert {} for CPF net: {}", ann.getClass().getSimpleName(), ann.getCpfId());
                getContext().createHandler(ann, specLayout, getObject()).convert();
                if (ann instanceof GraphicsType) {
                    graphicsConverted = true;
                }
            }

            if (!graphicsConverted) {
                final GraphicsType fakeGraphic = new org.apromore.anf.ObjectFactory().createGraphicsType();
                fakeGraphic.setCpfId(netEntry.getKey());
                LOGGER.debug("Guessing graphics for CPF net: {}", netEntry.getKey());
                getContext().createHandler(fakeGraphic, specLayout, getObject()).convert();
            }

            // Second convert Element Annotations
            convertNetElementsInBreadthFirstOrder(specLayout, getContext().getNetById(netEntry.getKey()));
        }

        // Third convert all Edge Annotations
        for (final Entry<String, FlowsIntoType> edgeEntry : getContext().getControlFlowContext().getConvertedFlows()) {
            final Collection<AnnotationType> elementAnnotations = getContext().getAnnotations(edgeEntry.getKey());

            boolean graphicsConverted = false;
            for (final AnnotationType ann : elementAnnotations) {
                LOGGER.debug("Trying to convert {} for CPF edge: {}", ann.getClass().getSimpleName(), ann.getCpfId());
                getContext().createHandler(ann, specLayout, getObject()).convert();
                if (ann instanceof GraphicsType) {
                    graphicsConverted = true;
                }
            }

            if (!graphicsConverted) {
                final GraphicsType fakeGraphic = new org.apromore.anf.ObjectFactory().createGraphicsType();
                fakeGraphic.setCpfId(edgeEntry.getKey());
                LOGGER.debug("Guessing graphics for CPF edge: {}", edgeEntry.getKey());
                getContext().createHandler(fakeGraphic, specLayout, getObject()).convert();
            }
        }

    }

    private void convertNetElementsInBreadthFirstOrder(final Specification specLayout, final NetType net) throws CanoniserException {

        Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
        if (sourceNodes.size() > 1) {
            LOGGER.warn("YAWL Net {} contains more than one source Node {}", ConversionUtils.toString(net),
                    ConversionUtils.nodesToString(sourceNodes));
        } else if (sourceNodes.size() == 0) {
            LOGGER.warn("YAWL Net {} contains no source Node {}", ConversionUtils.toString(net), ConversionUtils.nodesToString(sourceNodes));
            // Just assume all nodes as valid start points
            sourceNodes = net.getNode();
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

            if (currentNodeInfo.getDepth() > lastDepth) {
                lastDepth = currentNodeInfo.getDepth();
                getContext().getAutoLayoutInfo().setCurrentBreadth(bfsQueue.size());
            }

            getContext().getAutoLayoutInfo().setCurrentDistance(currentNodeInfo.getDepth());

            convertNode(specLayout, node);

            final List<NodeType> postSet = getContext().getPostSet(node.getId());

            for (final NodeType nextNode : postSet) {
                if (!markedNodes.containsKey(nextNode.getId())) {
                    bfsQueue.add(nextNode);
                    final BFSInfo newNodeInfo = new BFSInfo(currentNodeInfo.getDepth() + 1);
                    markedNodes.put(nextNode.getId(), newNodeInfo);
                }
            }

            getContext().getAutoLayoutInfo().setLastElementDistance(currentNodeInfo.getDepth());
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
            getContext().createHandler(ann, specLayout, getObject()).convert();
            if (ann instanceof GraphicsType) {
                graphicsConverted = true;
            }
        }

        if (!graphicsConverted) {
            final GraphicsType fakeGraphic = new org.apromore.anf.ObjectFactory().createGraphicsType();
            fakeGraphic.setCpfId(node.getId());
            LOGGER.debug("Guessing graphics for CPF element: {}", node.getId());
            getContext().createHandler(fakeGraphic, specLayout, getObject()).convert();
        }

    }

    private void convertSize(final Specification specLayout) {
        final LayoutDimensionType specDimension = YAWL_FACTORY.createLayoutDimensionType();
        // TODO what is the size of an specification?
        specDimension.setH(BigInteger.valueOf(DEFAULT_SPEC_HEIGHT));
        specDimension.setW(BigInteger.valueOf(DEFAULT_SPEC_WIDTH));
        specLayout.setSize(specDimension);
    }

    private LayoutLocaleType convertLocale() {
        final LayoutLocaleType defaultLayoutLocale = YAWL_FACTORY.createLayoutLocaleType();
        defaultLayoutLocale.setCountry(getContext().getYawlLocale().getCountry());
        defaultLayoutLocale.setLanguage(getContext().getYawlLocale().getLanguage());
        return getContext().getExtensionFromAnnotations(null, ExtensionUtils.LOCALE, LayoutLocaleType.class, defaultLayoutLocale);
    }

}
