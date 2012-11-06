package org.apromore.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apromore.dao.ContentDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.PocketMappingException;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.TreeVisitor;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.service.ContentService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.BondContentHandler;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.PocketMapper;
import org.apromore.service.helper.extraction.Extractor;
import org.apromore.service.model.RFragment2;
import org.apromore.service.utils.MutableTreeConstructor;
import org.apromore.util.FragmentUtil;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
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

    @Autowired
    @Qualifier("ContentDao")
    private ContentDao cDao;

    @Autowired
    @Qualifier("ContentService")
    private ContentService cSrv;
    @Autowired
    @Qualifier("FragmentService")
    private FragmentService fSrv;

    @Autowired
    @Qualifier("BondContentHandler")
    private BondContentHandler bcHandler;
    @Autowired
    @Qualifier("PocketMapper")
    private PocketMapper pMapper;


    /**
     * Decompose the Process Model Graph and save the fragments to the Repository.
     * Why it this here, it should build a list of objects and return to the Repository Service for Persistence?
     *
     * @param graph       the process model graph
     * @param fragmentIds empty list ready to be populated? why?
     * @return the Root Id.
     * @throws org.apromore.exception.RepositoryException
     *          if something fails while populating the Repository
     */
    @Override
    @SuppressWarnings("unchecked")
    public FragmentVersion decompose(final Canonical graph, final List<String> fragmentIds) throws RepositoryException {
        TreeVisitor visitor = new TreeVisitor();
        OperationContext op = new OperationContext();
        op.setGraph(graph);
        op.setTreeVisitor(visitor);

        try {
            RPST<Edge, Node> rpst = new RPST(graph);
            RFragment2 rf = MutableTreeConstructor.construct(rpst);
            FragmentVersion rootFV = decompose(rf, op, fragmentIds);
            fragmentIds.add(rootFV.getId().toString());
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
     * @param f           The Refined Process Structure Tree Node
     * @param op          Operation Context
     * @param fragmentIds the list of unpopulated Fragment Id's
     * @return the root fragment Id
     * @throws RepositoryException if something fails while populating the Repository
     */
    @SuppressWarnings("unchecked")
    public FragmentVersion decompose(final RFragment2 f, final OperationContext op, final List<String> fragmentIds)
            throws RepositoryException {
        String keywords = "";
         int fragmentSize = f.getVertices().size();
        String nodeType = FragmentUtil.getFragmentType(f);

        Collection<RFragment2> cs = f.getChildren();
        Map<String, String> childMappings = mapPocketChildId(f, op, fragmentIds, cs);

        String hash =  UUID.randomUUID().toString();// HashUtil.computeHash(f, f.getType(), op); //"";
        Content matchingContent = null;
        if (matchingContent == null) {
            return addFragmentVersion(f, hash, childMappings, fragmentSize, nodeType, keywords, op);
        }

        Map<String, String> newChildMappings;
        if (f.getType().equals(TCType.BOND)) {
            newChildMappings = new HashMap<String, String>();
            String matchingBondFragmentId = bcHandler.matchFragment(f, matchingContent, childMappings, newChildMappings);
            if (matchingBondFragmentId != null) {
                return fSrv.getFragmentVersion(Integer.valueOf(matchingBondFragmentId));
            }
        } else {
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
            FragmentVersion matchingFV = fSrv.getMatchingFragmentVersionId(matchingContent.getId(), newChildMappings);
            if (matchingFV != null) {
                return matchingFV;
            }
        }

        return addFragmentVersion(matchingContent, newChildMappings, null, 0, 0, fragmentSize, nodeType, keywords, op);
    }

    /**
     * Deconstructs a Fragment.
     *
     * @param graph       the process model graph
     * @param fragmentIds the fragment Ids
     * @return the root fragment version id
     * @throws org.apromore.exception.RepositoryException
     *
     */
    @Override
    public String decomposeFragment(final Canonical graph, final List<String> fragmentIds) throws RepositoryException {
        TreeVisitor visitor = new TreeVisitor();
        OperationContext op = new OperationContext();
        op.setGraph(graph);
        op.setTreeVisitor(visitor);

        try {
            Node entry = FragmentUtil.getFirstVertex(graph.getSourceNodes());
            Node exit = FragmentUtil.getFirstVertex(graph.getSinkNodes());
            if (graph.getVertices().size() > 2) {
                RPST<Edge, Node> rpst = new RPST<Edge, Node>(graph);
                RFragment2 rFragment2 = MutableTreeConstructor.construct(rpst);
                FragmentVersion rootFV = decompose(rFragment2, op, fragmentIds);
                fragmentIds.add(rootFV.getId().toString());
                return rootFV.getId().toString();
            } else {
                FragmentVersion rootFV = decomposeSimpleStandaloneFragment(graph, entry, exit, op);
                fragmentIds.add(rootFV.getId().toString());
                return rootFV.getId().toString();
            }
        } catch (Exception e) {
            String msg = "Failed to add root fragment version.";
            LOGGER.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /**
     * Decomposing a single Standalone Fragment
     *
     * @param g     the RPST graph
     * @param entry the entry node
     * @param exit  the exit node
     * @param op    the OperationConext
     * @return the new fragment
     * @throws ExceptionDao        the DAO Exception
     * @throws RepositoryException the Repository Exception
     */
    @SuppressWarnings("unchecked")
    public FragmentVersion decomposeSimpleStandaloneFragment(final Canonical g, final Node entry, final Node exit,
                                                             final OperationContext op) throws ExceptionDao, RepositoryException {
        String keywords = "";
        int fragmentSize = g.getVertices().size();
        String nodeType = "P";

        Map<String, String> childMappings = new HashMap<String, String>(0);

        Set<Edge> edges = new HashSet<Edge>(g.getEdges());
        String hash = op.getTreeVisitor().visitSNode(g, edges, entry);
        Integer matchingContentId = cSrv.getMatchingContentId(hash);
        if (matchingContentId == null) {
            return addFragmentVersion(g, hash, childMappings, fragmentSize, nodeType, keywords, op);
        }

        FragmentVersion matchingFV = fSrv.getMatchingFragmentVersionId(matchingContentId, childMappings);
        if (matchingFV != null) {
            return matchingFV;
        }

        Content cnt = cDao.findContent(matchingContentId);
        return addFragmentVersion(cnt, childMappings, null, 0, 0, fragmentSize, nodeType, keywords, op);
    }


    /* mapping pocketId -> childId */
    private Map<String, String> mapPocketChildId(final RFragment2 f, final OperationContext op,
                                                 final List<String> fragmentIds, final Collection<RFragment2> cs) throws RepositoryException {
        Map<String, String> childMappings = new HashMap<String, String>();
        for (RFragment2 c : cs) {
            if (TCType.TRIVIAL.equals(c.getType())) {
                continue;
            }

            Node pocket = Extractor.extractChildFragment(f, c, op.getGraph());
            FragmentUtil.cleanFragment(f);
            FragmentVersion child = decompose(c, op, fragmentIds);
            fragmentIds.add(child.getUri());
            childMappings.put(pocket.getId(), child.getUri());
        }
        return childMappings;
    }

    /* Adds a fragment version */
    private FragmentVersion addFragmentVersion(final Canonical f, final String hash, final Map<String, String> childMappings,
                                               final int fragmentSize, final String fragmentType, final String keywords, final OperationContext op)
            throws RepositoryException {
        // mappings (UUIDs generated for pocket Ids -> Pocket Ids assigned to pockets when they are persisted in the database)
        Map<String, String> pocketIdMappings = new HashMap<String, String>();
        Content content = cSrv.addContent(f, hash, op.getGraph(), pocketIdMappings);

        // rewrite child mapping with new pocket Ids
        Map<String, String> refinedChildMappings = new HashMap<String, String>();
        for (String oldPocketId : childMappings.keySet()) {
            String childId = childMappings.get(oldPocketId);
            String newPocketId = pocketIdMappings.get(oldPocketId);
            refinedChildMappings.put(newPocketId, childId);
        }

        return addFragmentVersion(content, refinedChildMappings, null, 0, 0, fragmentSize, fragmentType, keywords, op);
    }

    /* Adds a fragment version */
    private FragmentVersion addFragmentVersion(final Content cid, final Map<String, String> childMappings, final String derivedFrom,
                                               final int lockStatus, final int lockCount, final int originalSize, final String fragmentType, final String keywords, final OperationContext op)
            throws RepositoryException {
        op.addProcessedFragmentType(fragmentType);
        return fSrv.addFragmentVersion(cid, childMappings, derivedFrom, lockStatus, lockCount, originalSize, fragmentType);
    }


    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     *
     * @param cntDAOJpa the content Dao.
     */
    public void setContentDao(final ContentDao cntDAOJpa) {
        cDao = cntDAOJpa;
    }

    /**
     * Set the Content Service object for this class. Mainly for spring tests.
     *
     * @param cService the Content Service.
     */
    public void setContentService(final ContentService cService) {
        cSrv = cService;
    }

    /**
     * Set the Fragment Service object for this class. Mainly for spring tests.
     *
     * @param fService the Fragment Service.
     */
    public void setFragmentService(final FragmentService fService) {
        fSrv = fService;
    }
}
