package org.apromore.service.impl;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.PocketMappingException;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.TreeVisitor;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ContentService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.ContentHandler;
import org.apromore.service.helper.GraphPocketMapper;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.extraction.Extractor;
import org.apromore.service.model.FragmentNode;
import org.apromore.service.utils.MutableTreeConstructor;
import org.apromore.util.FragmentUtil;
import org.apromore.util.HashUtil;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class DecomposerServiceImpl implements DecomposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecomposerServiceImpl.class);

    private ContentService cService;
    private FragmentService fService;
    private GraphPocketMapper pMapper;
    private ContentHandler bcHandler;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param contentService Content Service.
     * @param fragmentService Fragment Service.
     */
    @Inject
    public DecomposerServiceImpl(final ContentService contentService, final FragmentService fragmentService,
            final GraphPocketMapper pocketMapper, final @Qualifier("bondContentHandler") ContentHandler bondContentHandler) {
        cService = contentService;
        fService = fragmentService;
        pMapper = pocketMapper;
        bcHandler = bondContentHandler;
    }


    /**
     * @see DecomposerService#decompose(org.apromore.graph.canonical.Canonical, ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    public OperationContext decompose(Canonical graph, ProcessModelVersion modelVersion) throws RepositoryException {
        try {
            TreeVisitor visitor = new TreeVisitor();
            OperationContext op = new OperationContext();
            op.setGraph(graph);
            op.setTreeVisitor(visitor);

            RPST<CPFEdge, CPFNode> rpst = new RPST(graph);
            FragmentNode rf = new MutableTreeConstructor().construct(rpst);

            //IOUtils.toFile("outputCPF.dot", graph.toDOT());
            //IOUtils.invokeDOT("/Users/cameron/Development/logs", "outputCPF.png", graph.toDOT());
            //IOUtils.toFile("outputRPST.dot", rpst.toDOT());
            //IOUtils.invokeDOT("/Users/cameron/Development/logs", "outputRPST.png", rpst.toDOT());

            if (rf != null) {
                op = decompose(modelVersion, rf, op);
                cService.updateCancelNodes(op);
            }

            return op;
        } catch (Exception e) {
            String msg = "Failed to add root fragment version of the process model.";
            LOGGER.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /* Doing all the work of decomposing into the DB structure. */
    @SuppressWarnings("unchecked")
    private OperationContext decompose(ProcessModelVersion modelVersion, final FragmentNode parent, OperationContext op)
            throws RepositoryException {
        String keywords = "";
        int fragmentSize = parent.getNodes().size();
        String nodeType = FragmentUtil.getFragmentType(parent);

        Collection<FragmentNode> childFragments = parent.getChildren();
        Map<String, String> childMappings = mapPocketChildId(modelVersion, parent, op, childFragments);

        String hash = HashUtil.computeHash(parent, parent.getType(), op);
        if (hash == null) {
            return addFragmentVersion(modelVersion, parent, hash, childMappings, fragmentSize, nodeType, keywords, op);
        }

        List<Content> matchingContents = cService.getContentByCode(hash);
        if (matchingContents == null || matchingContents.isEmpty()) {
            return addFragmentVersion(modelVersion, parent, hash, childMappings, fragmentSize, nodeType, keywords, op);
        }

        Content matchingContent = matchingContents.get(0);

        Map<String, String> newChildMappings = null;
        if (parent.getType().equals(TCType.BOND)) {
            newChildMappings = new HashMap<String, String>();
            int matchingBondFragmentId = bcHandler.matchFragment(parent, matchingContent, childMappings, newChildMappings);
            if (matchingBondFragmentId != -1) {
                FragmentVersion existingFragment = fService.getFragmentVersion(matchingBondFragmentId);
                op.addFragmentVersion(existingFragment);
                op.setCurrentFragment(existingFragment);
                return op;
            }
        } else {
            Map<String, String> pocketMappings = pMapper.mapPockets(parent, op.getGraph(), matchingContent);
            if (pocketMappings == null) {
                LOGGER.info("Could not map pockets of fragment with its matching fragment " + childMappings);
                return addFragmentVersion(modelVersion, parent, hash, childMappings, fragmentSize, nodeType, keywords, op);
            }
            try {
                newChildMappings = FragmentUtil.remapChildren(childMappings, pocketMappings);
            } catch (PocketMappingException e) {
                String msg = "Failed to remap pockets of the structure " + matchingContent + " to new child fragments.";
                LOGGER.error(msg, e);
                return addFragmentVersion(modelVersion, parent, hash, childMappings, fragmentSize, nodeType, keywords, op);
            }
            FragmentVersion matchingFV = fService.getMatchingFragmentVersionId(matchingContent.getId(), newChildMappings);
            if (matchingFV != null) {
                op.addFragmentVersion(matchingFV);
                op.setCurrentFragment(matchingFV);
                return op;
            }
        }

        return addFragmentVersion(modelVersion, parent, hash, childMappings, fragmentSize, nodeType, keywords, op);    }

    /* mapping pocketId -> childId */
    private Map<String, String> mapPocketChildId(ProcessModelVersion modelVersion, final FragmentNode parent, OperationContext op,
            final Collection<FragmentNode> childFragments) throws RepositoryException {
        Map<String, String> childMappings = new HashMap<String, String>();
        for (FragmentNode child : childFragments) {
            if (TCType.TRIVIAL.equals(child.getType())) {
                continue;
            }

            CPFNode pocket = Extractor.extractChildFragment(parent, child, op.getGraph());
            FragmentUtil.cleanFragment(parent);
            OperationContext fragment = decompose(modelVersion, child, op);
            childMappings.put(pocket.getId(), fragment.getCurrentFragment().getUri());
        }
        return childMappings;
    }

    /* Adds the decomposed fragment to the process model version. */
    private OperationContext addFragmentVersion(ProcessModelVersion modelVersion, final FragmentNode parent, final String hash,
            final Map<String, String> childMappings, final int fragmentSize, final String fragmentType, final String keywords,
            OperationContext op) throws RepositoryException {
        LOGGER.info("Adding Fragment Version: " + modelVersion.getProcessBranch().getBranchName() + " - " + modelVersion.getVersionNumber());

        // mappings (UUIDs generated for pocket Ids -> Pocket Ids assigned to pockets when they are persisted in the database)
        Map<String, String> pocketIdMappings = new HashMap<String, String>();
        Content content = cService.addContent(modelVersion, parent, hash, op, pocketIdMappings);

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
        op.addFragmentVersion(fragmentVersion);

        return op;
    }
}
