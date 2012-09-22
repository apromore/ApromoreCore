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
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfAndGateway;
import org.apromore.graph.JBPT.CpfOrGateway;
import org.apromore.graph.JBPT.CpfXorGateway;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.apromore.service.helper.OperationContext;
import org.apromore.util.FragmentUtil;
import org.apromore.util.GraphUtil;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
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
    public CPF compose(final String fragmentVersionUri) throws ExceptionDao {
        OperationContext op = new OperationContext();
        CPF g = new CPF();
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

    private void composeNewContent(final OperationContext op, final String fragmentVersionUri, final String pocketId, final Content contentDO) throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        CPF g = op.getGraph();
        gSrv.fillNodes(g, contentDO.getId());
        gSrv.fillEdges(g, contentDO.getId());

        Collection<FlowNode> nodesToBeRemoved = new HashSet<FlowNode>();
        if (pocketId != null) {
            Collection<ControlFlow<FlowNode>> edges = g.getEdges();
            for (ControlFlow<FlowNode> edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
                    FlowNode boundaryS = g.getVertex(getNodeIdByUri(contentDO.getBoundaryS()));
                    FlowNode parentT1 = edge.getSource();
                    if (canCombineSplit(parentT1, boundaryS)) {
                        Collection<FlowNode> childTs = g.getDirectSuccessors(boundaryS);
                        for (FlowNode ct : childTs) {
                            g.addEdge(parentT1, ct);
                        }
                        nodesToBeRemoved.add(boundaryS);
                    } else {
                        edge.setTarget(g.getVertex(getNodeIdByUri(contentDO.getBoundaryS())));
                    }
                }

                if (edge.getSource() != null && edge.getSource().getId().equals(pocketId)) {
                    FlowNode boundaryE = g.getVertex(getNodeIdByUri(contentDO.getBoundaryE()));
                    FlowNode parentT2 = edge.getTarget();
                    if (canCombineJoin(parentT2, boundaryE)) {
                        Collection<FlowNode> childTs = g.getDirectPredecessors(boundaryE);
                        for (FlowNode ct : childTs) {
                            g.addEdge(ct, parentT2);
                        }
                        nodesToBeRemoved.add(boundaryE);

                    } else {
                        edge.setSource(g.getVertex(getNodeIdByUri(contentDO.getBoundaryE())));
                    }
                }
            }
            g.removeVertex(g.getVertex(pocketId));
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

    private boolean canCombineSplit(final FlowNode parentT1, final FlowNode boundaryS) {
        if (parentT1 == null || boundaryS == null) {
            return false;
        } else if ((parentT1 instanceof CpfXorGateway) && (boundaryS instanceof CpfXorGateway)) {
            return true;
        } else if ((parentT1 instanceof CpfAndGateway) && (boundaryS instanceof CpfAndGateway)) {
            return true;
        } else if ((parentT1 instanceof CpfOrGateway) && (boundaryS instanceof CpfOrGateway)) {
            return true;
        } else if (("XOR".equals(parentT1.getName())) && ("XOR".equals(boundaryS.getName()))) {
            return true;
        } else if (("AND".equals(parentT1.getName())) && ("AND".equals(boundaryS.getName()))) {
            return true;
        } else if (("OR".equals(parentT1.getName())) && ("OR".equals(boundaryS.getName()))) {
            return true;
        }
        return false;
    }

    private boolean canCombineJoin(final FlowNode parentT2, final FlowNode boundaryE) {
        if (parentT2 == null || boundaryE == null) {
            return false;
        } else if ((parentT2 instanceof CpfXorGateway) && (boundaryE instanceof CpfXorGateway)) {
            return true;
        } else if ((parentT2 instanceof CpfAndGateway) && (boundaryE instanceof CpfAndGateway)) {
            return true;
        } else if ((parentT2 instanceof CpfOrGateway) && (boundaryE instanceof CpfOrGateway)) {
            return true;
        } else if (("XOR".equals(parentT2.getName())) && ("XOR".equals(boundaryE.getName()))) {
            return true;
        } else if (("AND".equals(parentT2.getName())) && ("AND".equals(boundaryE.getName()))) {
            return true;
        } else if (("OR".equals(parentT2.getName())) && ("OR".equals(boundaryE.getName()))) {
            return true;
        }
        return false;
    }

    private void composeNewContentOld(final OperationContext op, final String fragmentVersionUri, final String pocketId, final Content contentDO)
            throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        CPF g = op.getGraph();
        gSrv.fillNodes(g, contentDO.getId());
        gSrv.fillEdges(g, contentDO.getId());

        if (pocketId != null) {
            Collection<ControlFlow<FlowNode>> edges = g.getEdges();
            for (ControlFlow<FlowNode> edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
                    edge.setTarget(g.getVertex(contentDO.getBoundaryS()));
                }
                if (edge.getSource() != null && edge.getSource().getId().equals(pocketId)) {
                    edge.setSource(g.getVertex(contentDO.getBoundaryE()));
                }
            }
            g.removeVertex(g.getVertex(pocketId));
        }

        List<FragmentVersionDag> childMappings = fvdDao.getChildMappingsByURI(fragmentVersionUri);
        for (FragmentVersionDag fvd : childMappings) {
            composeFragment(op, fvd.getChildFragmentVersionId(), fvd.getPocketId());
        }
    }

    private void composeDuplicateContent(final OperationContext op, final String fragmentVersionUri, final String pocketId, final Content contentDO)
            throws ExceptionDao {
        op.incrementContentUsage(contentDO.getId());

        CPF g = op.getGraph();
        CPF contentGraph = gSrv.getGraph(contentDO.getId());
        CPF duplicateGraph = new CPF();
        Map<String, String> vMap = GraphUtil.copyContentGraph(contentGraph, duplicateGraph);
        GraphUtil.fillGraph(g, duplicateGraph);
        fillOriginalNodeMappings(vMap, g);

        Collection<FlowNode> nodesToBeRemoved = new HashSet<FlowNode>();
        if (pocketId != null) {
            Collection<ControlFlow<FlowNode>> edges = g.getEdges();
            for (ControlFlow<FlowNode> edge: edges) {
                if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
                    FlowNode boundaryS = g.getVertex(vMap.get(getNodeIdByUri(contentDO.getBoundaryS())));
                    FlowNode parentT1 = edge.getSource();
                    if (canCombineSplit(parentT1, boundaryS)) {
                        Collection<FlowNode> childTs = g.getDirectSuccessors(boundaryS);
                        for (FlowNode ct : childTs) {
                            g.addEdge(parentT1, ct);
                        }
                        nodesToBeRemoved.add(boundaryS);

                    } else {
                        edge.setTarget(g.getVertex(vMap.get(getNodeIdByUri(contentDO.getBoundaryS()))));
                    }
                }
                if (edge.getSource().getId().equals(pocketId)) {
                    FlowNode boundaryE = g.getVertex(vMap.get(getNodeIdByUri(contentDO.getBoundaryE())));
                    FlowNode parentT2 = edge.getTarget();
                    if (canCombineJoin(parentT2, boundaryE)) {
                        Collection<FlowNode> childTs = g.getDirectPredecessors(boundaryE);
                        for (FlowNode ct : childTs) {
                            g.addEdge(ct, parentT2);
                        }
                        nodesToBeRemoved.add(boundaryE);

                    } else {
                        edge.setSource(g.getVertex(vMap.get(getNodeIdByUri(contentDO.getBoundaryE()))));
                    }
                }
            }
            g.removeVertex(g.getVertex(pocketId));
            g.removeVertices(nodesToBeRemoved);
        }

        List<FragmentVersionDag> childMappings = fvdDao.getChildMappingsByURI(fragmentVersionUri);
        Map<String, FragmentVersion> newChildMapping = null;
        try {
            newChildMapping = FragmentUtil.remapChildren(childMappings, vMap);
        } catch (PocketMappingException e) {
            String msg = "Failed a pocked mapping of fragment " + fragmentVersionUri;
            LOGGER.error(msg, e);
        }
        Set<String> pids = newChildMapping.keySet();
        for (String pid: pids) {
            FragmentVersion childId = newChildMapping.get(pid);
            composeFragment(op, childId, pid);
        }
    }

    private void fillOriginalNodeMappings(final Map<String, String> vMap, final CPF g) {
        for (String originalNode : vMap.keySet()) {
            String duplicateNode = vMap.get(originalNode);
            if (!g.getVertexProperty(duplicateNode, Constants.TYPE).equals(Constants.POCKET)) {
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
