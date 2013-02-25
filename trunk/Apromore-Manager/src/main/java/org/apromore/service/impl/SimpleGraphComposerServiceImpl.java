package org.apromore.service.impl;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.common.Constants;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.EdgeRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NodeRepository;
import org.apromore.dao.dataObject.ContentDO;
import org.apromore.dao.dataObject.EdgeDO;
import org.apromore.dao.dataObject.NodeDO;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class SimpleGraphComposerServiceImpl implements ComposerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGraphComposerServiceImpl.class);

    private ContentRepository contentRepository;
    private FragmentVersionRepository fragmentVersionRepository;
    private FragmentVersionDagRepository fragmentVersionDagRepository;
    private EdgeRepository edgeRepository;
    private NodeRepository nodeRepository;
    private GraphService graphService;

    private Map<String, SimpleGraph> cache = new HashMap<String, SimpleGraph>();
    private int minCachableFragmentSize = 2;
    private int maxCachableFragmentSize = 100;

    private Map<Integer, List<NodeDO>> nodeCache = new HashMap<Integer, List<NodeDO>>();
    private Map<Integer, List<EdgeDO>> edgeCache = new HashMap<Integer, List<EdgeDO>>();
	private Map<Integer, ContentDO> contentCache = new HashMap<Integer, ContentDO>();
	private Map<Integer, List<FragmentVersionDag>> childMappingsCache = new HashMap<Integer, List<FragmentVersionDag>>();



    @Inject
    public SimpleGraphComposerServiceImpl(final ContentRepository cRepo, final FragmentVersionRepository fragRepo,
            final FragmentVersionDagRepository fragDagRepo, final EdgeRepository eRepo, final NodeRepository nRepo,
            final GraphService gSrv) {
        contentRepository = cRepo;
        fragmentVersionRepository = fragRepo;
        fragmentVersionDagRepository = fragDagRepo;
        edgeRepository = eRepo;
        nodeRepository = nRepo;
        graphService = gSrv;
    }


    /**
     * Compose a process Model graph from the DB.
     * @param fragmentVersion the fragment version Id we are looking to construct from.
     * @return the process model graph
     * @throws org.apromore.exception.ExceptionDao if something fails.
     */
    @Override
	public Canonical compose(FragmentVersion fragmentVersion) throws ExceptionDao {
		OperationContext op = new OperationContext();
        Canonical g = new Canonical();
		op.setGraph(g);
		composeFragment(op, fragmentVersion, null);
		return g;
	}

    @Override
    public void clearCache(List<Integer> fids) {
        for (Integer fid : fids) {
            clearCache(fid);
        }
    }




	private ContentDO getContent(Integer fragmentId) {
		ContentDO contentDO = contentCache.get(fragmentId);
		if (contentDO == null) {
			contentDO = contentRepository.getContentDOByFragmentVersion(fragmentId);
			contentCache.put(fragmentId, contentDO);
		}
		return contentDO;
	}

	private void composeFragment(OperationContext op, FragmentVersion fragmentVersion, String pocketId) throws ExceptionDao {
		ContentDO content = getContent(fragmentVersion.getId());

		if (op.getContentUsage(content.getId()) == 0) {
			composeNewContent(op, fragmentVersion.getId(), pocketId, content);
		} else {
			composeDuplicateContent(op, fragmentVersion.getId(), pocketId, content);
		}
	}

	private void composeNewContent(OperationContext op, Integer fragmentVersionId, String pocketId, ContentDO contentDO)
            throws ExceptionDao {
		op.incrementContentUsage(contentDO.getId());

        Canonical g = op.getGraph();
        fillNodes(g, contentDO.getId());
        fillEdges(g, contentDO.getId());

        Collection<CPFNode> nodesToBeRemoved = new HashSet<CPFNode>();
        Collection<CPFEdge> edgesToBeRemoved = new HashSet<CPFEdge>();
		if (pocketId != null) {
			Collection<CPFEdge> edges = g.getEdges();
			for (CPFEdge edge: edges) {
				if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
					CPFNode boundaryS = g.getNode(contentDO.getBoundaryS());
					CPFNode parentT1 = edge.getSource();
					if (canCombineSplit(parentT1, boundaryS)) {
						Collection<CPFNode> childTs = g.getDirectSuccessors(boundaryS);
						for (CPFNode ct : childTs) {
							g.addEdge(parentT1, ct);
						}
						nodesToBeRemoved.add(boundaryS);

					} else {
						edge.setTarget(g.getNode(contentDO.getBoundaryS()));
					}
				}

				if (edge.getSource() != null && edge.getSource().getId().equals(pocketId)) {
					CPFNode boundaryE = g.getNode(contentDO.getBoundaryE());
					CPFNode parentT2 = edge.getTarget();
					if (canCombineJoin(parentT2, boundaryE)) {
						Collection<CPFNode> childTs = g.getDirectPredecessors(boundaryE);
						for (CPFNode ct : childTs) {
							g.addEdge(ct, parentT2);
						}
						nodesToBeRemoved.add(boundaryE);

					} else {
						edge.setSource(g.getNode(contentDO.getBoundaryE()));
					}
				}
			}
			g.removeNode(g.getNode(pocketId));
			g.removeNodes(nodesToBeRemoved);
		}

		List<FragmentVersionDag> childMappings = getChildMappings(fragmentVersionId);
        for (FragmentVersionDag fvd : childMappings) {
            composeFragment(op, fvd.getChildFragmentVersion(), fvd.getPocketId());
        }
	}

    private boolean canCombineSplit(final CPFNode p1, final CPFNode bS) {
        if (p1 == null || bS == null) {
            return false;
        } else if (("XOrSplit".equals(p1.getName())) && ("XOrSplit".equals(bS.getName()))) {
            return true;
        } else if (("AndSplit".equals(p1.getName())) && ("AndSplit".equals(bS.getName()))) {
            return true;
        } else if (("OrSplit".equals(p1.getName())) && ("OrSplit".equals(bS.getName()))) {
            return true;
        } else if (p1.getNodeType() != null && bS.getNodeType() != null) {
            if ((p1.getNodeType().equals(NodeTypeEnum.XORSPLIT)) && (bS.getNodeType().equals(NodeTypeEnum.XORSPLIT))) {
                return true;
            } else if ((p1.getNodeType().equals(NodeTypeEnum.ANDSPLIT)) && (bS.getNodeType().equals(NodeTypeEnum.ANDSPLIT))) {
                return true;
            } else if ((p1.getNodeType().equals(NodeTypeEnum.ORSPLIT)) && (bS.getNodeType().equals(NodeTypeEnum.ORSPLIT))) {
                return true;
            }
        }
        return false;
    }

    private boolean canCombineJoin(final CPFNode p2, final CPFNode bE) {
        if (p2 == null || bE == null) {
            return false;
        } else if (("XOrJoin".equals(p2.getName())) && ("XOrJoin".equals(bE.getName()))) {
            return true;
        } else if (("AndJoin".equals(p2.getName())) && ("AndJoin".equals(bE.getName()))) {
            return true;
        } else if (("OrJoin".equals(p2.getName())) && ("OrJoin".equals(bE.getName()))) {
            return true;
        } else if (p2.getNodeType() != null && bE.getNodeType() != null) {
            if ((p2.getNodeType().equals(NodeTypeEnum.XORJOIN)) && (bE.getNodeType().equals(NodeTypeEnum.XORJOIN))) {
                return true;
            } else if ((p2.getNodeType().equals(NodeTypeEnum.ANDJOIN)) && (bE.getNodeType().equals(NodeTypeEnum.ANDJOIN))) {
                return true;
            } else if ((p2.getNodeType().equals(NodeTypeEnum.ORJOIN)) && (bE.getNodeType().equals(NodeTypeEnum.ORJOIN))) {
                return true;
            }
        }
        return false;
    }
    
    
	private void composeDuplicateContent(OperationContext op, Integer fragmentVersionId, String pocketId,
            ContentDO contentDO) throws ExceptionDao {
		op.incrementContentUsage(contentDO.getId());

        Canonical g = op.getGraph();
        Canonical contentGraph = getGraph(contentDO.getId());
        Canonical duplicateGraph = new Canonical();
		Map<String, String> vMap = GraphUtil.copyContentGraph(contentGraph, duplicateGraph);
		GraphUtil.fillGraph(g, duplicateGraph);
		fillOriginalNodeMappings(vMap, g);

		Collection<CPFNode> nodesToBeRemoved = new HashSet<CPFNode>();
		if (pocketId != null) {
			Collection<CPFEdge> edges = g.getEdges();
			for (CPFEdge edge: edges) {
				if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
					CPFNode boundaryS = g.getNode(vMap.get(contentDO.getBoundaryS()));
					CPFNode parentT1 = edge.getSource();
					if (canCombineSplit(parentT1, boundaryS)) {
						Collection<CPFNode> childTs = g.getDirectSuccessors(boundaryS);
						for (CPFNode ct : childTs) {
							g.addEdge(parentT1, ct);
						}
						nodesToBeRemoved.add(boundaryS);

					} else {
						edge.setTarget(g.getNode(vMap.get(contentDO.getBoundaryS())));
					}
				}
				if (edge.getSource().getId().equals(pocketId)) {
					CPFNode boundaryE = g.getNode(vMap.get(contentDO.getBoundaryE()));
					CPFNode parentT2 = edge.getTarget();
					if (canCombineJoin(parentT2, boundaryE)) {
						Collection<CPFNode> childTs = g.getDirectPredecessors(boundaryE);
						for (CPFNode ct : childTs) {
							g.addEdge(ct, parentT2);
						}
						nodesToBeRemoved.add(boundaryE);

					} else {
						edge.setSource(g.getNode(vMap.get(contentDO.getBoundaryE())));
					}
				}
			}
			g.removeVertex(g.getNode(pocketId));
			g.removeVertices(nodesToBeRemoved);
		}

        List<FragmentVersionDag> childMappings = getChildMappings(fragmentVersionId);
		Map<String, String> newChildMapping = null;
		try {
			newChildMapping = FragmentUtil.remapChildren(childMappings, vMap);
		} catch (PocketMappingException e) {
			String msg = "Failed a pocked mapping of fragment " + fragmentVersionId;
			LOGGER.error(msg, e);
		}
		Set<String> pids = newChildMapping.keySet();
		for (String pid: pids) {
            FragmentVersion childId = fragmentVersionRepository.findFragmentVersionByUri(newChildMapping.get(pid));
			composeFragment(op, childId, pid);
		}
	}

	private void fillOriginalNodeMappings(Map<String, String> vMap, Canonical g) {
		for (String originalNode : vMap.keySet()) {
			String duplicateNode = vMap.get(originalNode);
			if (!g.getNodeProperty(duplicateNode, Constants.TYPE).equals(Constants.POCKET)) {
				g.addOriginalNodeMapping(duplicateNode, originalNode);
			}
		}
	}





	public void clearCache(Integer fragmentId) {
		ContentDO contentDO = contentCache.get(fragmentId);
		if (contentDO != null) {
			nodeCache.remove(contentDO.getId());
			edgeCache.remove(contentDO.getId());
			contentCache.remove(fragmentId);
			childMappingsCache.remove(fragmentId);
		}
	}

    @Transactional(readOnly = true)
    public void fillNodes(Canonical procModelGraph, Integer contentID) {
        CPFNode v;
        List<NodeDO> nodes = nodeCache.get(contentID);
        if (nodes == null) {
        	nodes =	nodeRepository.getNodeDOsByContent(contentID);
        	nodeCache.put(contentID, nodes);
        }

        for (NodeDO node : nodes) {
            v = buildNodeByType(node);
            procModelGraph.addVertex(v);
            procModelGraph.setNodeProperty(String.valueOf(node.getId()), Constants.TYPE, node.getNodeType());
        }
    }

    @Transactional(readOnly = true)
    public void fillEdges(Canonical procModelGraph, Integer contentID) {
    	List<EdgeDO> edges = edgeCache.get(contentID);
    	if (edges == null) {
    		edges = edgeRepository.getEdgeDOsByContent(contentID);
    		edgeCache.put(contentID, edges);
    	}

        for (EdgeDO edge : edges) {
            CPFNode v1 = procModelGraph.getNode(String.valueOf(edge.getSourceId()));
            CPFNode v2 = procModelGraph.getNode(String.valueOf(edge.getTargetId()));

            if (v1 != null && v2 != null) {
            	procModelGraph.addEdge(v1, v2);
            } else {
            	if (v1 == null && v2 != null) {
            		LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in content " + contentID);
            	}

            	if (v2 == null && v1 != null) {
            		LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in content " + contentID);
            	}

            	if (v1 == null && v2 == null) {
            		LOGGER.info("Null source and target nodes found for an edge in content " + contentID);
            	}
            }
        }
    }

    private List<FragmentVersionDag> getChildMappings(Integer fragmentId) {
    	List<FragmentVersionDag> childMappings = childMappingsCache.get(fragmentId);
    	if (childMappings == null) {
			childMappings = fragmentVersionDagRepository.getChildMappings(fragmentId);
			childMappingsCache.put(fragmentId, childMappings);
    	}
    	return childMappings;
    }

    @Transactional(readOnly = true)
    public Canonical getGraph(Integer contentID){
        Canonical g = new Canonical();
        fillNodes(g, contentID);
        fillEdges(g, contentID);
        return g;
    }

	/* Build the correct type of Node so we don't loss Information */
    private CPFNode buildNodeByType(NodeDO node) {
        CPFNode result = new CPFNode();
        if (node != null) {
            result.setId(node.getId().toString());
            result.setNodeType(NodeTypeEnum.fromValue(node.getNodeType()));
        }
        return result;
    }



    public void setCache(Map<String, SimpleGraph> cache) {
        this.cache = cache;
    }

    public void setMinCachableFragmentSize(int minCachableFragmentSize) {
        this.minCachableFragmentSize = minCachableFragmentSize;
    }

    public void setMaxCachableFragmentSize(int maxCachableFragmentSize) {
        this.maxCachableFragmentSize = maxCachableFragmentSize;
    }

}
