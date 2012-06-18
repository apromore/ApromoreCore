package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.ContentDao;
import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.PocketMappingException;
import org.apromore.graph.JBPT.CPF;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chathura Ekanayake
 */
@Service("Composer")
@Transactional(propagation = Propagation.REQUIRED)
public class Composer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Composer.class);

    @Autowired @Qualifier("ContentDao")
    private ContentDao cDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fvdDao;

    @Autowired @Qualifier("GraphService")
    private GraphService gSrv;

    /**
     * Compose a process Model graph from the DB.
     * @param fragmentVersionId the fragment version Id we are looking to construct from.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
	public CPF compose(String fragmentVersionId) throws ExceptionDao {
		OperationContext op = new OperationContext();
        CPF g = new CPF();
		op.setGraph(g);
		composeFragment(op, fragmentVersionId, null);
		return g;
	}



	private void composeFragment(OperationContext op, String fragmentVersionId, String pocketId) throws ExceptionDao {
		Content content = cDao.getContentByFragmentVersion(fragmentVersionId);
		
		if (op.getContentUsage(content.getContentId()) == 0) {
			composeNewContent(op, fragmentVersionId, pocketId, content);
		} else {
			composeDuplicateContent(op, fragmentVersionId, pocketId, content);
		}
	}
	
	private void composeNewContent(OperationContext op, String fragmentVersionId, String pocketId, Content contentDO)
            throws ExceptionDao {
		op.incrementContentUsage(contentDO.getContentId());

        CPF g = op.getGraph();
        gSrv.fillNodes(g, contentDO.getContentId());
        gSrv.fillEdges(g, contentDO.getContentId());

		if (pocketId != null) {
			Collection<ControlFlow<FlowNode>> edges = g.getEdges();
			for (ControlFlow<FlowNode> edge: edges) {
				if (edge.getTarget().getId().equals(pocketId)) {
					edge.setTarget(g.getVertex(contentDO.getBoundaryS()));
				}
				if (edge.getSource().getId().equals(pocketId)) {
					edge.setSource(g.getVertex(contentDO.getBoundaryE()));
				}
			}
			g.removeVertex(g.getVertex(pocketId));
		}
		
		List<FragmentVersionDag> childMappings = fvdDao.getChildMappings(fragmentVersionId);
        for (FragmentVersionDag fvd : childMappings) {
            composeFragment(op, fvd.getId().getChildFragmentVersionId(), fvd.getId().getPocketId());
        }
	}
	
	private void composeDuplicateContent(OperationContext op, String fragmentVersionId, String pocketId,
            Content contentDO) throws ExceptionDao {
		op.incrementContentUsage(contentDO.getContentId());

		CPF g = op.getGraph();
        CPF contentGraph = gSrv.getGraph(contentDO.getContentId());
        CPF duplicateGraph = new CPF();
		Map<String, String> vMap = GraphUtil.copyContentGraph(contentGraph, duplicateGraph);
		GraphUtil.fillGraph(g, duplicateGraph);
		fillOriginalNodeMappings(vMap, g);
		
		if (pocketId != null) {
			Collection<ControlFlow<FlowNode>> edges = g.getEdges();
			for (ControlFlow<FlowNode> edge: edges) {
				if (edge.getTarget() != null && edge.getTarget().getId().equals(pocketId)) {
					edge.setTarget(g.getVertex(vMap.get(contentDO.getBoundaryS())));
				}
				if (edge.getSource().getId().equals(pocketId)) {
					edge.setSource(g.getVertex(vMap.get(contentDO.getBoundaryE())));
				}
			}
			g.removeVertex(g.getVertex(pocketId));
		}

        List<FragmentVersionDag> childMappings = fvdDao.getChildMappings(fragmentVersionId);
		Map<String, String> newChildMapping = null;
		try {
			newChildMapping = FragmentUtil.remapChildren(childMappings, vMap);
		} catch (PocketMappingException e) {
			String msg = "Failed a pocked mapping of fragment " + fragmentVersionId;
			LOGGER.error(msg, e);
		}
		Set<String> pids = newChildMapping.keySet();
		for (String pid: pids) {
			String childId = newChildMapping.get(pid);
			composeFragment(op, childId, pid);
		}
	}
	
	private void fillOriginalNodeMappings(Map<String, String> vMap, CPF g) {
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
    public void setContentDao(ContentDao cntDAOJpa) {
        cDao = cntDAOJpa;
    }

    /**
     * Set the Fragment Version Dag DAO object for this class. Mainly for spring tests.
     * @param fvdDAOJpa the Fragment Version Dag Dao.
     */
    public void setFragmentVersionDagDao(FragmentVersionDagDao fvdDAOJpa) {
        fvdDao = fvdDAOJpa;
    }

    /**
     * Set the Graph Service object for this class. Mainly for spring tests.
     * @param gService the Graph Service.
     */
    public void setGraphService(GraphService gService) {
        gSrv = gService;
    }
}
