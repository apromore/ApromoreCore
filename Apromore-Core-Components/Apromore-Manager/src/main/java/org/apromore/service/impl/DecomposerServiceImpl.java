/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.EdgeMappingRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NodeMappingRepository;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.EdgeMapping;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeMapping;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ContentService;
import org.apromore.service.DecomposerService;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.MutableTreeConstructor;
import org.jbpt.algo.tree.rpst.RPST;
import static org.jbpt.algo.tree.tctree.TCType.TRIVIAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class DecomposerServiceImpl implements DecomposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecomposerServiceImpl.class);

    @Inject
    private ContentService cService;
    @Inject
    private FragmentVersionRepository fvRepository;
    @Inject
    private FragmentVersionDagRepository fvdRepository;
    @Inject
    private NodeMappingRepository nmRepository;
    @Inject
    private EdgeMappingRepository emRepository;


    /**
     * @see DecomposerService#decompose(org.apromore.graph.canonical.Canonical, ProcessModelVersion)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public OperationContext decompose(Canonical graph, ProcessModelVersion modelVersion) throws RepositoryException {
        try {
            OperationContext op = new OperationContext();
            op.setGraph(graph);

            RPST<CPFEdge, CPFNode> rpst = new RPST(graph);
            FragmentNode rf = new MutableTreeConstructor().construct(rpst);
            if (rf != null) {
                op = decompose(modelVersion, rf, op);
            }

            op.addAllCpfNodes(graph.getNodes());
            cService.updateCancelNodes(op);  // decompose() had to populate op before we could call this

            return op;
        } catch (Exception e) {
            String msg = "Failed to add root fragment version of the process model.";
            LOGGER.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /* Doing all the work of decomposing into the DB structure. */
    @SuppressWarnings("unchecked")
    private OperationContext decompose(ProcessModelVersion pmv, final FragmentNode root, OperationContext op) throws RepositoryException {
        Queue<FragmentNode> q = new LinkedList<>();
        q.add(root);

        FragmentVersion rootfv = saveFragment(pmv, root);
        op.setCurrentFragment(rootfv);
        addElements(root, rootfv, pmv, op);

        while (!q.isEmpty()) {
            // add fragment -> element Id mappings to all non-root fragments
            FragmentNode f = q.poll();
            FragmentNode parent = f.getParent();

            if (parent != null) {
                // we don't have to save the root again, or to save parent relationship of root
                preprocessFragment(f);
                FragmentVersion fv = saveFragment(pmv, f);
                addElementMappings(f, fv, op);

                FragmentVersion parentfv = fvRepository.findFragmentVersionByUri(parent.getUri());
                FragmentVersionDag dag = new FragmentVersionDag();
                dag.setFragmentVersion(parentfv);
                dag.setChildFragmentVersion(fv);
                fvdRepository.save(dag);
            }

            Collection<FragmentNode> children = f.getChildren();
            for (FragmentNode child : children) {
                if (!child.getType().equals(TRIVIAL)) {
                    q.add(child);
                }
            }
        }

        return op;
    }

    private void addElementMappings(FragmentNode f, FragmentVersion fv, OperationContext op) {
        Set<CPFNode> nodes = f.getNodes();
        for (CPFNode node : nodes) {
            Node pNode = op.getPersistedNodes().get(node.getId());

            NodeMapping nodeMapping = new NodeMapping();
            nodeMapping.setFragmentVersion(fv);
            nodeMapping.setNode(pNode);
            nmRepository.save(nodeMapping);
        }

        Set<CPFEdge> edges = f.getEdges();
        for (CPFEdge edge : edges) {
            Edge pEdge = op.getPersistedEdges().get(edge.getId());

            EdgeMapping edgeMapping = new EdgeMapping();
            edgeMapping.setFragmentVersion(fv);
            edgeMapping.setEdge(pEdge);
            emRepository.save(edgeMapping);
        }

        fvRepository.save(fv);
    }


    public void addElements(FragmentNode f, FragmentVersion fv, ProcessModelVersion pmv, OperationContext op) {
        Set<CPFNode> nodes = f.getNodes();
        for (CPFNode node : nodes) {
            String type = op.getGraph().getNodeProperty(node.getId(), Constants.TYPE);

            Node pNode = cService.addNode(node, type, pmv.getObjects(), pmv.getResources());
            op.addPersistedNode(pNode.getUri(), pNode);

            NodeMapping nodeMapping = new NodeMapping();
            nodeMapping.setFragmentVersion(fv);
            nodeMapping.setNode(pNode);
            nmRepository.save(nodeMapping);
        }

        Set<CPFEdge> edges = f.getEdges();
        for (CPFEdge edge : edges) {
            Edge pEdge = cService.addEdge(edge, fv, op);
            op.addPersistedEdge(pEdge.getUri(), pEdge);

            EdgeMapping edgeMapping = new EdgeMapping();
            edgeMapping.setFragmentVersion(fv);
            edgeMapping.setEdge(pEdge);
            emRepository.save(edgeMapping);
        }

        fvRepository.save(fv);
    }


    private void preprocessFragment(FragmentNode f) {
        // if a polygon starts (or ends) with a connector AND if that connector has a single outgoing (or incoming) edge,
        // we can remove that connector from the fragment (OR should we keep that and have polygons with connectors as boundaries?)
    }


    private FragmentVersion saveFragment(ProcessModelVersion pmv, FragmentNode f) {
        FragmentVersion fv = new FragmentVersion();
        fv.setUri(f.getUri());
        fv.setFragmentType(f.getType().toString());
        fv.setFragmentSize(f.getNodes().size());

        pmv.addFragmentVersion(fv);
        fv.addProcessModelVersion(pmv);

        return fvRepository.save(fv);
    }

}
