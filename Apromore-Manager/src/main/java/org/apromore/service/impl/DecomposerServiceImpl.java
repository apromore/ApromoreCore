package org.apromore.service.impl;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.TreeVisitor;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ContentService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.extraction.Extractor;
import org.apromore.service.model.FragmentNode;
import org.apromore.service.utils.MutableTreeConstructor;
import org.apromore.util.FragmentUtil;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional
public class DecomposerServiceImpl implements DecomposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecomposerServiceImpl.class);

    private ContentService cService;
    private FragmentService fService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param contentService Content Service.
     * @param fragmentService Fragment Service.
     */
    @Inject
    public DecomposerServiceImpl(final ContentService contentService, final FragmentService fragmentService) {
        cService = contentService;
        fService = fragmentService;
    }


    /**
     * @see DecomposerService#decompose(org.apromore.graph.canonical.Canonical, ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public OperationContext decompose(Canonical graph, ProcessModelVersion modelVersion) throws RepositoryException {
        try {
            TreeVisitor visitor = new TreeVisitor();
            OperationContext op = new OperationContext();
            op.setGraph(graph);
            op.setTreeVisitor(visitor);

            RPST<CPFEdge, CPFNode> rpst = new RPST(graph);
            FragmentNode rf = new MutableTreeConstructor().construct(rpst);
            op = decompose(modelVersion, rf, op);

            cService.updateCancelNodes(op);

            return op;
        } catch (Exception e) {
            String msg = "Failed to add root fragment version of the process model.";
            LOGGER.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /* Doing all the work of decomposing into the DB structure. */
    @SuppressWarnings("unchecked")
    private OperationContext decompose(ProcessModelVersion modelVersion, final FragmentNode f, OperationContext op)
            throws RepositoryException {
        String keywords = "";
        int fragmentSize = f.getNodes().size();
        String nodeType = FragmentUtil.getFragmentType(f);

        Collection<FragmentNode> cs = f.getChildren();
        Map<String, String> childMappings = mapPocketChildId(modelVersion, f, op, cs);

        String hash = UUID.randomUUID().toString();
        return addFragmentVersion(modelVersion, f, hash, childMappings, fragmentSize, nodeType, keywords, op);
    }

    /* mapping pocketId -> childId */
    private Map<String, String> mapPocketChildId(ProcessModelVersion modelVersion, final FragmentNode f, OperationContext op,
            final Collection<FragmentNode> cs) throws RepositoryException {
        Map<String, String> childMappings = new HashMap<String, String>();
        for (FragmentNode c : cs) {
            if (TCType.TRIVIAL.equals(c.getType())) {
                continue;
            }

            CPFNode pocket = Extractor.extractChildFragment(f, c, op.getGraph());
            FragmentUtil.cleanFragment(f);
            OperationContext child = decompose(modelVersion, c, op);
            childMappings.put(pocket.getId(), child.getCurrentFragment().getUri());
        }
        return childMappings;
    }

    /* Adds the decomposed fragment to the process model version. */
    private OperationContext addFragmentVersion(ProcessModelVersion modelVersion, final FragmentNode f, final String hash,
            final Map<String, String> childMappings, final int fragmentSize, final String fragmentType, final String keywords,
            OperationContext op) throws RepositoryException {
        LOGGER.info("Adding Fragment Version: " + modelVersion.getVersionName());

        // mappings (UUIDs generated for pocket Ids -> Pocket Ids assigned to pockets when they are persisted in the database)
        Map<String, String> pocketIdMappings = new HashMap<String, String>();
        Content content = cService.addContent(modelVersion, f, hash, op, pocketIdMappings);

        // rewrite child mapping with new pocket Ids
        Map<String, String> refinedChildMappings = new HashMap<String, String>();
        for (String oldPocketId : childMappings.keySet()) {
            String childId = childMappings.get(oldPocketId);
            String newPocketId = pocketIdMappings.get(oldPocketId);
            refinedChildMappings.put(newPocketId, childId);
        }

        return addFragmentVersion(modelVersion, content, refinedChildMappings, null, 0, 0, fragmentSize, fragmentType, keywords, op);
    }

    /* Adds a fragment version */
    private OperationContext addFragmentVersion(ProcessModelVersion modelVersion, Content content, final Map<String, String> childMappings,
            final String derivedFrom, final int lockStatus, final int lockCount, final int originalSize, final String fragmentType,
            final String keywords, OperationContext op) throws RepositoryException {
        FragmentVersion fragmentVersion = fService.addFragmentVersion(modelVersion, content, childMappings, derivedFrom, lockStatus, lockCount, originalSize, fragmentType);

        op.addProcessedFragmentType(fragmentType);
        op.setCurrentFragment(fragmentVersion);
        op.addAllNodes(content.getNodes());
        op.addAllEdges(content.getEdges());

        return op;
    }
}
