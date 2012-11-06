package org.apromore.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.dao.ContentDao;
import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.NodeDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.PocketMappingException;
import org.apromore.graph.canonical.AndJoin;
import org.apromore.graph.canonical.AndSplit;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.graph.canonical.OrJoin;
import org.apromore.graph.canonical.OrSplit;
import org.apromore.graph.canonical.XOrJoin;
import org.apromore.graph.canonical.XOrSplit;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.apromore.service.helper.OperationContext;
import org.apromore.util.FragmentUtil;
import org.apromore.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service("ComposerService")
@Transactional(propagation = Propagation.REQUIRED)
public class ComposerServiceImpl implements ComposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComposerServiceImpl.class);

    @Autowired @Qualifier("ContentDao")
    private ContentDao cDao;
    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fvdDao;
    @Autowired @Qualifier("NodeDao")
    private NodeDao nDao;

    @Autowired @Qualifier("GraphService")
    private GraphService gSrv;

    /**
     * Compose a process Model graph from the DB.
     * @param fragmentVersionUri the fragment version Id we are looking to construct from.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    public Canonical compose(final String fragmentVersionUri) throws ExceptionDao {
        OperationContext op = new OperationContext();
        Canonical g = new Canonical();
        op.setGraph(g);
        composeFragment(op, fvDao.findFragmentVersionByURI(fragmentVersionUri), null);
        return g;
    }



    private void composeFragment(final OperationContext op, final FragmentVersion fragVersion, final String pocketId) throws ExceptionDao {
        Content content = cDao.getContentByFragmentVersion(fragVersion.getId());

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
        gSrv.fillNodes(g, contentDO.getId());
        gSrv.fillEdges(g, contentDO.getId());

        Collection<Node> nodesToBeRemoved = new HashSet<Node>();
        if (pocketId != null) {
            Collection<Edge> edges = g.getEdges();
            for (Edge edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(getNodeIdByUri(pocketId))) {
                    Node boundaryS = g.getNode(getNodeIdByUri(contentDO.getBoundaryS()));
                    Node parentT1 = edge.getSource();
                    if (canCombineSplit(parentT1, boundaryS)) {
                        Collection<Node> childTs = g.getDirectSuccessors(boundaryS);
                        for (Node ct : childTs) {
                            g.addEdge(parentT1, ct);
                        }
                        nodesToBeRemoved.add(boundaryS);
                    } else {
                        edge.setTarget(g.getNode(getNodeIdByUri(contentDO.getBoundaryS())));
                    }
                }

                if (edge.getSource() != null && edge.getSource().getId().equals(getNodeIdByUri(pocketId))) {
                    Node boundaryE = g.getNode(getNodeIdByUri(contentDO.getBoundaryE()));
                    Node parentT2 = edge.getTarget();
                    if (canCombineJoin(parentT2, boundaryE)) {
                        Collection<Node> childTs = g.getDirectPredecessors(boundaryE);
                        for (Node ct : childTs) {
                            g.addEdge(ct, parentT2);
                        }
                        nodesToBeRemoved.add(boundaryE);

                    } else {
                        edge.setSource(g.getNode(getNodeIdByUri(contentDO.getBoundaryE())));
                    }
                }
            }
            g.removeVertex(g.getNode(getNodeIdByUri(pocketId)));
            g.removeVertices(nodesToBeRemoved);
        }

        List<FragmentVersionDag> childMappings = fvdDao.getChildMappingsByURI(fragmentVersionUri);
        for (FragmentVersionDag fvd : childMappings) {
            composeFragment(op, fvd.getChildFragmentVersionId(), fvd.getPocketId());
        }
    }

    private String getNodeIdByUri(final String uri) {
        //TODO FM, 22.09.2012 just a workaround to get things working
        return String.valueOf(nDao.findNodeByUri(uri).getId());
    }

    private boolean canCombineSplit(final Node p1, final Node bS) {
        if (p1 == null || bS == null) {
            return false;
        } else if ((p1 instanceof XOrSplit) && (bS instanceof XOrSplit)) {
            return true;
        } else if ((p1 instanceof AndSplit) && (bS instanceof AndSplit)) {
            return true;
        } else if ((p1 instanceof OrSplit) && (bS instanceof OrSplit)) {
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

    private boolean canCombineJoin(final Node p2, final Node bE) {
        if (p2 == null || bE == null) {
            return false;
        } else if ((p2 instanceof XOrJoin) && (bE instanceof XOrJoin)) {
            return true;
        } else if ((p2 instanceof AndJoin) && (bE instanceof AndJoin)) {
            return true;
        } else if ((p2 instanceof OrJoin) && (bE instanceof OrJoin)) {
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

    private void composeNewContentOld(final OperationContext op, final String fragmentVersionUri, final String pocketId, final Content contentDO)
            throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        Canonical g = op.getGraph();
        gSrv.fillNodes(g, contentDO.getId());
        gSrv.fillEdges(g, contentDO.getId());

        if (pocketId != null) {
            Collection<Edge> edges = g.getEdges();
            for (Edge edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
                    edge.setTarget(g.getNode(contentDO.getBoundaryS()));
                }
                if (edge.getSource() != null && edge.getSource().getId().equals(pocketId)) {
                    edge.setSource(g.getNode(contentDO.getBoundaryE()));
                }
            }
            g.removeVertex(g.getNode(pocketId));
        }

        List<FragmentVersionDag> childMappings = fvdDao.getChildMappingsByURI(fragmentVersionUri);
        for (FragmentVersionDag fvd : childMappings) {
            composeFragment(op, fvd.getChildFragmentVersionId(), fvd.getPocketId());
        }
    }

    private void composeDuplicateContent(final OperationContext op, final String fragmentVersionUri, final String pocketId, final Content contentDO)
            throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        Canonical g = op.getGraph();
        Canonical contentGraph = gSrv.getGraph(contentDO.getId());
        Canonical duplicateGraph = new Canonical();
        Map<String, String> vMap = GraphUtil.copyContentGraph(contentGraph, duplicateGraph);
        GraphUtil.fillGraph(g, duplicateGraph);
        fillOriginalNodeMappings(vMap, g);

        Collection<Node> nodesToBeRemoved = new HashSet<Node>();
        if (pocketId != null) {
            Collection<Edge> edges = g.getEdges();
            for (Edge edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(getNodeIdByUri(pocketId))) {
                    Node boundaryS = g.getNode(vMap.get(getNodeIdByUri(contentDO.getBoundaryS())));
                    Node parentT1 = edge.getSource();
                    if (canCombineSplit(parentT1, boundaryS)) {
                        Collection<Node> childTs = g.getDirectSuccessors(boundaryS);
                        for (Node ct : childTs) {
                            g.addEdge(parentT1, ct);
                        }
                        nodesToBeRemoved.add(boundaryS);

                    } else {
                        edge.setTarget(g.getNode(vMap.get(getNodeIdByUri(contentDO.getBoundaryS()))));
                    }
                }
                if (edge.getSource() !=null && edge.getSource().getId().equals(getNodeIdByUri(pocketId))) {
                    Node boundaryE = g.getNode(vMap.get(getNodeIdByUri(contentDO.getBoundaryE())));
                    Node parentT2 = edge.getTarget();
                    if (canCombineJoin(parentT2, boundaryE)) {
                        Collection<Node> childTs = g.getDirectPredecessors(boundaryE);
                        for (Node ct : childTs) {
                            g.addEdge(ct, parentT2);
                        }
                        nodesToBeRemoved.add(boundaryE);

                    } else {
                        edge.setSource(g.getNode(vMap.get(getNodeIdByUri(contentDO.getBoundaryE()))));
                    }
                }
            }
            g.removeVertex(g.getNode(getNodeIdByUri(pocketId)));
            g.removeVertices(nodesToBeRemoved);
        }

        List<FragmentVersionDag> childMappings = fvdDao.getChildMappingsByURI(fragmentVersionUri);
        Map<String, String> newChildMapping = null;
        try {
            newChildMapping = FragmentUtil.remapChildren(childMappings, vMap);
        } catch (PocketMappingException e) {
            String msg = "Failed a pocked mapping of fragment " + fragmentVersionUri;
            LOGGER.error(msg, e);
        }
        Set<String> pids = newChildMapping.keySet();
        for (String pid: pids) {
            FragmentVersion childId = fvDao.findFragmentVersionByURI(newChildMapping.get(pid));
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





    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     * @param cntDAOJpa the content Dao.
     */
    public void setContentDao(final ContentDao cntDAOJpa) {
        cDao = cntDAOJpa;
    }

    /**
     * Set the Fragment Version DAO object for this class. Mainly for spring tests.
     * @param fvDAOJpa the Fragment Version Dao.
     */
    public void setFragmentVersionDao(final FragmentVersionDao fvDAOJpa) {
        fvDao = fvDAOJpa;
    }

    /**
     * Set the Fragment Version Dag DAO object for this class. Mainly for spring tests.
     * @param fvdDAOJpa the Fragment Version Dag Dao.
     */
    public void setFragmentVersionDagDao(final FragmentVersionDagDao fvdDAOJpa) {
        fvdDao = fvdDAOJpa;
    }

    /**
     * Set the Graph Service object for this class. Mainly for spring tests.
     * @param gService the Graph Service.
     */
    public void setGraphService(final GraphService gService) {
        gSrv = gService;
    }
}
