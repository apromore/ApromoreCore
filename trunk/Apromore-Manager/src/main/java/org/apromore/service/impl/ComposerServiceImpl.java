package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NodeRepository;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.PocketMappingException;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.apromore.service.helper.OperationContext;
import org.apromore.util.FragmentUtil;
import org.apromore.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional
public class ComposerServiceImpl implements ComposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComposerServiceImpl.class);

    private ContentRepository cRepository;
    private FragmentVersionRepository fvRepository;
    private FragmentVersionDagRepository fvdRepository;
    private NodeRepository nRepository;
    private GraphService gService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param contentRepository Content Repository.
     * @param fragmentVersionRepository Fragment Version Repository.
     * @param fragmentVersionDagRepository Fragment Version Dag Repository.
     * @param nodeRepository Node Repository.
     * @param graphService Graphing Services.
     */
    @Inject
    public ComposerServiceImpl(final ContentRepository contentRepository, final FragmentVersionRepository fragmentVersionRepository,
            final FragmentVersionDagRepository fragmentVersionDagRepository, final NodeRepository nodeRepository,
            final GraphService graphService) {
        cRepository = contentRepository;
        fvRepository = fragmentVersionRepository;
        fvdRepository = fragmentVersionDagRepository;
        nRepository = nodeRepository;
        gService = graphService;
    }



    /**
     * Compose a process Model graph from the DB.
     * @param rootFragment the root Fragment we are going to build this model from.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    public Canonical compose(final FragmentVersion rootFragment) throws ExceptionDao {
        OperationContext op = new OperationContext();
        Canonical g = new Canonical();
        op.setGraph(g);
        composeFragment(op, rootFragment, null);
        return g;
    }



    private void composeFragment(final OperationContext op, final FragmentVersion fragVersion, final String pocketId) throws ExceptionDao {
        Content content = cRepository.getContentByFragmentVersion(fragVersion.getId());

        if (op.getContentUsage(content.getId()) == 0) {
            composeNewContent(op, fragVersion.getUri(), pocketId, content);
        } else {
            composeDuplicateContent(op, fragVersion.getUri(), pocketId, content);
        }
    }

    private void composeNewContent(final OperationContext op, final String fragmentVersionUri, final String pocketId,
            final Content contentDO) throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        Canonical g = op.getGraph();
        gService.fillNodes(g, contentDO.getId());
        gService.fillEdges(g, contentDO.getId());

        Collection<CPFNode> nodesToBeRemoved = new HashSet<CPFNode>();
        if (pocketId != null) {
            Collection<CPFEdge> edges = g.getEdges();
            for (CPFEdge edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
                    CPFNode boundaryS = g.getNode(contentDO.getBoundaryS().getUri());
                    CPFNode parentT1 = edge.getSource();
                    if (canCombineSplit(parentT1, boundaryS)) {
                        Collection<CPFNode> childTs = g.getDirectSuccessors(boundaryS);
                        for (CPFNode ct : childTs) {
                            g.addEdge(parentT1, ct);
                        }
                        nodesToBeRemoved.add(boundaryS);
                    } else {
                        edge.setTarget(boundaryS);
                    }
                }

                if (edge.getSource() != null && edge.getSource().getId().equals(pocketId)) {
                    CPFNode boundaryE = g.getNode(contentDO.getBoundaryE().getUri());
                    CPFNode parentT2 = edge.getTarget();
                    if (canCombineJoin(parentT2, boundaryE)) {
                        Collection<CPFNode> childTs = g.getDirectPredecessors(boundaryE);
                        for (CPFNode ct : childTs) {
                            g.addEdge(ct, parentT2);
                        }
                        nodesToBeRemoved.add(boundaryE);
                    } else {
                        edge.setSource(boundaryE);
                    }
                }
            }
            g.removeVertex(g.getNode(pocketId));
            g.removeVertices(nodesToBeRemoved);
        }

        List<FragmentVersionDag> childMappings = fvdRepository.getChildMappingsByURI(fragmentVersionUri);
        for (FragmentVersionDag fvd : childMappings) {
            composeFragment(op, fvd.getChildFragmentVersion(), fvd.getPocketId());
        }
    }

    private boolean canCombineSplit(final CPFNode p1, final CPFNode bS) {
        if (p1 == null || bS == null) {
            return false;
        } else if ((p1.getNodeType().equals(NodeTypeEnum.XORSPLIT)) && (bS.getNodeType().equals(NodeTypeEnum.XORSPLIT))) {
            return true;
        } else if ((p1.getNodeType().equals(NodeTypeEnum.ANDSPLIT)) && (bS.getNodeType().equals(NodeTypeEnum.ANDSPLIT))) {
            return true;
        } else if ((p1.getNodeType().equals(NodeTypeEnum.ORSPLIT)) && (bS.getNodeType().equals(NodeTypeEnum.ORSPLIT))) {
            return true;
        } else if (("XOR".equals(p1.getName())) && ("XOR".equals(bS.getName()))) {
            return true;
        } else if (("AND".equals(p1.getName())) && ("AND".equals(bS.getName()))) {
            return true;
        } else if (("OR".equals(p1.getName())) && ("OR".equals(bS.getName()))) {
            return true;
        }
        return false;
    }

    private boolean canCombineJoin(final CPFNode p2, final CPFNode bE) {
        if (p2 == null || bE == null) {
            return false;
        } else if ((p2.getNodeType().equals(NodeTypeEnum.XORJOIN)) && (bE.getNodeType().equals(NodeTypeEnum.XORJOIN))) {
            return true;
        } else if ((p2.getNodeType().equals(NodeTypeEnum.ANDJOIN)) && (bE.getNodeType().equals(NodeTypeEnum.ANDJOIN))) {
            return true;
        } else if ((p2.getNodeType().equals(NodeTypeEnum.ORJOIN)) && (bE.getNodeType().equals(NodeTypeEnum.ORJOIN))) {
            return true;
        } else if (("XOR".equals(p2.getName())) && ("XOR".equals(bE.getName()))) {
            return true;
        } else if (("AND".equals(p2.getName())) && ("AND".equals(bE.getName()))) {
            return true;
        } else if (("OR".equals(p2.getName())) && ("OR".equals(bE.getName()))) {
            return true;
        }
        return false;
    }

    private void composeDuplicateContent(final OperationContext op, final String fragmentVersionUri, final String pocketId, final Content contentDO)
            throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        Canonical g = op.getGraph();
        Canonical contentGraph = gService.getGraph(contentDO.getId());
        Canonical duplicateGraph = new Canonical();
        Map<String, String> vMap = GraphUtil.copyContentGraph(contentGraph, duplicateGraph);
        GraphUtil.fillGraph(g, duplicateGraph);
        fillOriginalNodeMappings(vMap, g);

        Collection<CPFNode> nodesToBeRemoved = new HashSet<CPFNode>();
        if (pocketId != null) {
            Collection<CPFEdge> edges = g.getEdges();
            for (CPFEdge edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(getNodeIdByUri(pocketId))) {
                    CPFNode boundaryS = g.getNode(vMap.get(contentDO.getBoundaryS().getUri()));
                    CPFNode parentT1 = edge.getSource();
                    if (canCombineSplit(parentT1, boundaryS)) {
                        Collection<CPFNode> childTs = g.getDirectSuccessors(boundaryS);
                        for (CPFNode ct : childTs) {
                            g.addEdge(parentT1, ct);
                        }
                        nodesToBeRemoved.add(boundaryS);
                    } else {
                        edge.setTarget(g.getNode(vMap.get(contentDO.getBoundaryS().getUri())));
                    }
                }
                if (edge.getSource() !=null && edge.getSource().getId().equals(getNodeIdByUri(pocketId))) {
                    CPFNode boundaryE = g.getNode(vMap.get(contentDO.getBoundaryE().getUri()));
                    CPFNode parentT2 = edge.getTarget();
                    if (canCombineJoin(parentT2, boundaryE)) {
                        Collection<CPFNode> childTs = g.getDirectPredecessors(boundaryE);
                        for (CPFNode ct : childTs) {
                            g.addEdge(ct, parentT2);
                        }
                        nodesToBeRemoved.add(boundaryE);
                    } else {
                        edge.setSource(g.getNode(vMap.get(contentDO.getBoundaryE().getUri())));
                    }
                }
            }
            g.removeVertex(g.getNode(getNodeIdByUri(pocketId)));
            g.removeVertices(nodesToBeRemoved);
        }

        List<FragmentVersionDag> childMappings = fvdRepository.getChildMappingsByURI(fragmentVersionUri);
        Map<String, String> newChildMapping = null;
        try {
            newChildMapping = FragmentUtil.remapChildren(childMappings, vMap);
        } catch (PocketMappingException e) {
            String msg = "Failed a pocked mapping of fragment " + fragmentVersionUri;
            LOGGER.error(msg, e);
        }
        Set<String> pids = newChildMapping.keySet();
        for (String pid: pids) {
            FragmentVersion childId = fvRepository.findFragmentVersionByUri(newChildMapping.get(pid));
            composeFragment(op, childId, pid);
        }
    }

    private void fillOriginalNodeMappings(final Map<String, String> vMap, final Canonical g) {
        for (String originalNode : vMap.keySet()) {
            String duplicateNode = vMap.get(originalNode);
            if (!g.getNodeProperty(duplicateNode, Constants.TYPE).equals(Constants.POCKET)) {
                g.addOriginalNodeMapping(duplicateNode, originalNode);
            }
        }
    }

    /* Convenience method to find the NodeId using the ID. */
    private String getNodeIdByUri(final String uri) {
        return nRepository.findNodeByUri(uri).getId().toString();
    }


}
