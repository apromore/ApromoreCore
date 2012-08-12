package org.apromore.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.ContentDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.PocketMappingException;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.TreeVisitor;
import org.apromore.service.ContentService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.BondContentHandler;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.PocketMapper;
import org.apromore.service.helper.extraction.Extractor;
import org.apromore.util.FragmentUtil;
import org.apromore.util.GraphUtil;
import org.apromore.util.HashUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
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
@Service("DecomposerService")
@Transactional(propagation = Propagation.REQUIRED)
public class DecomposerServiceImpl implements DecomposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecomposerServiceImpl.class);

    @Autowired @Qualifier("ContentDao")
    private ContentDao cDao;

    @Autowired @Qualifier("ContentService")
    private ContentService cSrv;
    @Autowired @Qualifier("FragmentService")
    private FragmentService fSrv;

    @Autowired @Qualifier("BondContentHandler")
    private BondContentHandler bcHandler;
    @Autowired @Qualifier("PocketMapper")
    private PocketMapper pMapper;


    /**
     * Decompose the Process Model Graph and save the fragments to the Repository.
     * Why it this here, it should build a list of objects and return to the Repository Service for Persistence?
     * @param graph the process model graph
     * @param fragmentIds empty list ready to be populated? why?
     * @return the Root Id.
     * @throws org.apromore.exception.RepositoryException if something fails while populating the Repository
     */
    public FragmentVersion decompose(CPF graph, List<String> fragmentIds) throws RepositoryException {
        TreeVisitor visitor = new TreeVisitor();
        OperationContext op = new OperationContext();
        op.setGraph(graph);
        op.setTreeVisitor(visitor);

        try {
            RPST rpst = GraphUtil.normalizeGraph(graph);
            FragmentVersion rootFV = decompose(rpst, rpst.getRoot(), op, fragmentIds);
            fragmentIds.add(rootFV.getFragmentVersionId());
            return rootFV;
        } catch (Exception e) {
            String msg = "Failed to add root fragment version of the process model.";
            LOGGER.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /**
     * Decompose the Process Model Graph and save the fragments to the Repository.
     * Why it this here, it should build a list of objects and return to the Repository Service for Persistence?
     * @param rpst The Refined Process Structure Tree
     * @param f The Refined Process Structure Tree Node
     * @param op Operation Context
     * @param fragmentIds the list of unpopulated Fragment Id's
     * @return the root fragment Id
     * @throws RepositoryException if something fails while populating the Repository
     */
    @SuppressWarnings("unchecked")
    public FragmentVersion decompose(RPST rpst, RPSTNode f, OperationContext op, List<String> fragmentIds) throws RepositoryException {
        String keywords = "";
        int fragmentSize = f.getFragment().getVertices().size();
        String nodeType = FragmentUtil.getFragmentType(f);

        Collection<RPSTNode> cs = rpst.getChildren(f);

        Map<String, String> childMappings = mapPocketChildId(rpst, f, op, fragmentIds, cs);

        String hash = HashUtil.computeHash(f, op);
        Content matchingContent = cDao.getContentByCode(hash);
        if (matchingContent == null) {
            return addFragmentVersion(f, hash, childMappings, fragmentSize, nodeType, keywords, op);
        }

        Map<String, String> newChildMappings;
        if (f.getType().equals(TCType.B)) {
            newChildMappings = new HashMap<>();
            String matchingBondFragmentId = bcHandler.matchFragment(f, matchingContent, childMappings, newChildMappings);
            if (matchingBondFragmentId != null) {
                return fSrv.getFragmentVersion(matchingBondFragmentId);
            }
        }
        else {
            Map<String, String> pocketMappings = pMapper.mapPockets(f, op.getGraph(), matchingContent);
            if (pocketMappings == null) {
                LOGGER.info("Could not map pockets of fragment with its matching fragment " + childMappings);
                return addFragmentVersion(f, hash, childMappings, fragmentSize, nodeType, keywords, op);
            }
            try {
                newChildMappings = FragmentUtil.remapChildren(childMappings, pocketMappings);
            } catch (PocketMappingException e) {
                String msg = "Failed to remap pockets of the structure " + matchingContent + " to new child fragments.";
                LOGGER.error(msg, e);
                throw new RepositoryException(msg, e);
            }
            FragmentVersion matchingFV = fSrv.getMatchingFragmentVersionId(matchingContent.getContentId(), newChildMappings);
            if (matchingFV != null) {
                return matchingFV;
            }
        }

        return addFragmentVersion(matchingContent, newChildMappings, null, 0, 0, fragmentSize, nodeType, keywords, op);
    }

    /**
     * Deconstructs a Fragment.
     * @param graph the process model graph
     * @param fragmentIds the fragment Ids
     * @return the root fragment version id
     * @throws org.apromore.exception.RepositoryException
     */
    public String decomposeFragment(CPF graph, List<String> fragmentIds) throws RepositoryException {
        TreeVisitor visitor = new TreeVisitor();
        OperationContext op = new OperationContext();
        op.setGraph(graph);
        op.setTreeVisitor(visitor);

        try {
            FlowNode entry = FragmentUtil.getFirstVertex(graph.getSourceVertices());
            FlowNode exit = FragmentUtil.getFirstVertex(graph.getSinkVertices());
            if (graph.getVertices().size() > 2) {
                RPST<ControlFlow<FlowNode>, FlowNode> rpst = new RPST<ControlFlow<FlowNode>, FlowNode>(graph);
                RPSTNode rootFragment = rpst.getRoot();
                FragmentVersion rootFV = decompose(rpst, rootFragment, op, fragmentIds);
                fragmentIds.add(rootFV.getFragmentVersionId());
                return rootFV.getFragmentVersionId();
            }
            else {
                FragmentVersion rootFV = decomposeSimpleStandaloneFragment(graph, entry, exit, op);
                fragmentIds.add(rootFV.getFragmentVersionId());
                return rootFV.getFragmentVersionId();
            }
        } catch (Exception e) {
            String msg = "Failed to add root fragment version.";
            LOGGER.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /**
     * @param g
     * @param entry
     * @param exit
     * @param op
     * @return
     * @throws ExceptionDao
     * @throws RepositoryException
     */
    @SuppressWarnings("unchecked")
    public FragmentVersion decomposeSimpleStandaloneFragment(CPF g, FlowNode entry, FlowNode exit, OperationContext op) throws ExceptionDao,
            RepositoryException {
        String keywords = "";
        int fragmentSize = g.getVertices().size();
        String nodeType = "P";

        RPSTNode sNode = new RPSTNode();
        sNode.getFragment().getVertices().addAll(g.getVertices());
        sNode.getFragmentEdges().addAll(g.getEdges());
        sNode.setEntry(entry);
        sNode.setExit(exit);

        Map<String, String> childMappings = new HashMap<>(0);
        Set<AbstractDirectedEdge> edges = new HashSet<AbstractDirectedEdge>(g.getEdges());
        String hash = op.getTreeVisitor().visitSNode(g, edges, entry);
        String matchingContentId = cSrv.getMatchingContentId(hash);
        if (matchingContentId == null) {
            return addFragmentVersion(sNode, hash, childMappings, fragmentSize, nodeType, keywords, op);
        }

        FragmentVersion matchingFV = fSrv.getMatchingFragmentVersionId(matchingContentId, childMappings);
        if (matchingFV != null) {
            return matchingFV;
        }

        Content cnt = cDao.findContent(matchingContentId);
        return addFragmentVersion(cnt, childMappings, null, 0, 0, fragmentSize, nodeType, keywords, op);
    }


    /* mapping pocketId -> childId */
    private Map<String, String> mapPocketChildId(RPST rpst, RPSTNode f, OperationContext op, List<String> fragmentIds, Collection<RPSTNode> cs)
            throws RepositoryException {
        Map<String, String> childMappings = new HashMap<>(0);
        for (RPSTNode c : cs) {
            if (TCType.T.equals(c.getType())) {
                continue;
            }

            FlowNode pocket = Extractor.extractChildFragment(f, c, rpst, op.getGraph());
            FragmentUtil.cleanFragment(f);
            FragmentVersion child = decompose(rpst, c, op, fragmentIds);
            fragmentIds.add(child.getFragmentVersionId());
            childMappings.put(pocket.getId(), child.getFragmentVersionId());
        }
        return childMappings;
    }

    /* Adds a fragment version */
    private FragmentVersion addFragmentVersion(RPSTNode f, String hash, Map<String, String> childMappings, int fragmentSize, String fragmentType,
                                               String keywords, OperationContext op) throws RepositoryException {
        // mappings (UUIDs generated for pocket Ids -> Pocket Ids assigned to pockets when they are persisted in the database)
        Map<String, String> pocketIdMappings = new HashMap<>();
        Content content = cSrv.addContent(f, hash, op.getGraph(), pocketIdMappings);

        // rewrite child mapping with new pocket Ids
        Map<String, String> refinedChildMappings = new HashMap<>();
        for (String oldPocketId : childMappings.keySet()) {
            String childId = childMappings.get(oldPocketId);
            String newPocketId = pocketIdMappings.get(oldPocketId);
            refinedChildMappings.put(newPocketId, childId);
        }

        return addFragmentVersion(content, refinedChildMappings, null, 0, 0, fragmentSize, fragmentType, keywords, op);
    }

    /* Adds a fragment version */
    private FragmentVersion addFragmentVersion(Content cid, Map<String, String> childMappings, String derivedFrom, int lockStatus, int lockCount,
                                               int originalSize, String fragmentType, String keywords, OperationContext op) throws RepositoryException {
        op.addProcessedFragmentType(fragmentType);
        return fSrv.addFragmentVersion(cid, childMappings, derivedFrom, lockStatus, lockCount, originalSize, fragmentType);
    }

    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     * @param cntDAOJpa the content Dao.
     */
    public void setContentDao(ContentDao cntDAOJpa) {
        cDao = cntDAOJpa;
    }

    /**
     * Set the Content Service object for this class. Mainly for spring tests.
     * @param cService the Content Service.
     */
    public void setContentService(ContentService cService) {
        cSrv = cService;
    }

    /**
     * Set the Fragment Service object for this class. Mainly for spring tests.
     * @param fService the Fragment Service.
     */
    public void setFragmentService(FragmentService fService) {
        fSrv = fService;
    }
}
