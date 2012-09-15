package org.apromore.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.jpa.FragmentVersionDagDaoJpa;
import org.apromore.dao.jpa.FragmentVersionDaoJpa;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.GraphService;
import org.apromore.service.impl.GraphServiceImpl;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service("BondContentHandler")
@Transactional(propagation = Propagation.REQUIRED)
public class BondContentHandler {

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fvdDao;

    @Autowired @Qualifier("GraphService")
    private GraphService gSrv;


    /**
     * Tries to match an existing B fragment to the given B fragment f. f should be a post-extraction fragment.
     * This tries map a forward children and reverse children (i.e. loop) separately, as the given B fragment can
     * contain loops. If there are no matching fragments, this will fill the given new child mapping to suit
     * the matching content.
     *
     * @param f                B Fragment to be matched.
     * @param matchingContent  Matching content.
     * @param childMappings    Original child mapping of the fragment f.
     * @param newChildMappings New child mapping to the matching content. Filled only if there is not matching
     *                         fragment. i.e. return value is null.
     * @return Matching fragment id. Null if there is no matching fragment.
     */
    public Integer matchFragment(RPSTNode f, Content matchingContent, Map<String, String> childMappings,
            Map<String, String> newChildMappings) {
        // find forward and reverse pocket ids of the given fragment
        String fragmentEntryId = f.getEntry().getId();
        String fragmentExitId = f.getExit().getId();
        List<String> forwardFragmentPocketIds = new ArrayList<String>();
        List<String> reverseFragmentPocketIds = new ArrayList<String>();
        Collection<AbstractDirectedEdge> fragmentEdges = f.getFragmentEdges();
        for (AbstractDirectedEdge fragmentEdge : fragmentEdges) {
            if (fragmentEdge.getSource().getId().equals(fragmentEntryId)) {
                String forwardNodeId = fragmentEdge.getTarget().getId();
                if (!forwardNodeId.equals(fragmentExitId)) {
                    forwardFragmentPocketIds.add(fragmentEdge.getTarget().getId());
                }
            }

            if (fragmentEdge.getSource().getId().equals(fragmentExitId)) {
                String reverseNodeId = fragmentEdge.getTarget().getId();
                if (!reverseNodeId.equals(fragmentEntryId)) {
                    reverseFragmentPocketIds.add(fragmentEdge.getTarget().getId());
                }
            }
        }

        // find forward and reverse child ids of the given fragment
        List<String> forwardFragmentChildIds = new ArrayList<String>();
        List<String> reverseFragmentChildIds = new ArrayList<String>();
        Set<String> fragmentPockets = childMappings.keySet();
        for (String fragmentPocketId : fragmentPockets) {
            if (forwardFragmentPocketIds.contains(fragmentPocketId)) {
                forwardFragmentChildIds.add(childMappings.get(fragmentPocketId));
            }

            if (reverseFragmentPocketIds.contains(fragmentPocketId)) {
                reverseFragmentChildIds.add(childMappings.get(fragmentPocketId));
            }
        }

        // find forward and reverse pocket Ids of the matching content
        List<String> forwardContentPocketIds = new ArrayList<String>();
        List<String> reverseContentPocketIds = new ArrayList<String>();
        CPF content = gSrv.getGraph(matchingContent.getId());
        String contentEntryId = content.getSourceVertices().get(0).getId();
        String contentExitId = content.getSinkVertices().get(0).getId();
        Collection<ControlFlow<FlowNode>> contentEdges = content.getEdges();
        for (ControlFlow<FlowNode> contentEdge : contentEdges) {
            if (contentEdge.getSource().getId().equals(contentEntryId)) {
                String forwardNodeId = contentEdge.getTarget().getId();
                if (!forwardNodeId.equals(contentExitId)) {
                    forwardContentPocketIds.add(contentEdge.getTarget().getId());
                }
            }
            if (contentEdge.getSource().getId().equals(contentExitId)) {
                String reverseNodeId = contentEdge.getTarget().getId();
                if (!reverseNodeId.equals(contentEntryId)) {
                    reverseContentPocketIds.add(contentEdge.getTarget().getId());
                }
            }
        }

        Integer matchingFragmentId = null;
        List<FragmentChildMapping> candidateChildMappings = getCandidateChildMappings(matchingContent.getId());
        for (FragmentChildMapping fragmentChildMapping : candidateChildMappings) {
            List<FragmentVersionDag> candidateMapping = fragmentChildMapping.getChildMapping();

            List<String> forwardCandidateChildIds = new ArrayList<String>();
            List<String> reverseCandidateChildIds = new ArrayList<String>();
            for (FragmentVersionDag candidatePocket : candidateMapping) {
                if (forwardContentPocketIds.contains(candidatePocket.getPocketId())) {
                    forwardCandidateChildIds.add(candidatePocket.getChildFragmentVersionId().getUri());
                }
                if (reverseContentPocketIds.contains(candidatePocket.getPocketId())) {
                    reverseCandidateChildIds.add(candidatePocket.getChildFragmentVersionId().getUri());
                }
            }
            if (forwardFragmentChildIds.containsAll(forwardCandidateChildIds) &&
                    reverseFragmentChildIds.containsAll(reverseCandidateChildIds)) {
                matchingFragmentId = fragmentChildMapping.getFragmentId();
                break;
            }
        }

        if (matchingFragmentId == null) {
            // there is no matching fragment Id. we have to map fragment children to content pockets.
            for (int i = 0; i < forwardFragmentChildIds.size(); i++) {
                String forwardFragmentChildId = forwardFragmentChildIds.get(i);
                String forwardContentPocketId = forwardContentPocketIds.get(i);
                newChildMappings.put(forwardContentPocketId, forwardFragmentChildId);
            }
            for (int k = 0; k < reverseFragmentChildIds.size(); k++) {
                String reverseFragmentChildId = reverseFragmentChildIds.get(k);
                String reverseContentPocketId = reverseContentPocketIds.get(k);
                newChildMappings.put(reverseContentPocketId, reverseFragmentChildId);
            }
        }

        return matchingFragmentId;
    }

    private List<FragmentChildMapping> getCandidateChildMappings(Integer matchingContentId) {
        List<FragmentChildMapping> candidateChildMappings = new ArrayList<FragmentChildMapping>();
        List<Integer> candidateFragmentIds = fvDao.getUsedFragmentIds(matchingContentId);
        for (Integer candidateFragmentId : candidateFragmentIds) {
            List<FragmentVersionDag> childMapping = fvdDao.getChildMappings(candidateFragmentId);
            FragmentChildMapping fragmentChildMapping = new FragmentChildMapping();
            fragmentChildMapping.setFragmentId(candidateFragmentId);
            fragmentChildMapping.setChildMapping(childMapping);
            candidateChildMappings.add(fragmentChildMapping);
        }
        return candidateChildMappings;
    }


    /**
     * Set the Fragment Version DAO object for this class. Mainly for spring tests.
     * @param fvDAOJpa the Fragment Version Dao.
     */
    public void setFragmentVersionDao(FragmentVersionDaoJpa fvDAOJpa) {
        fvDao = fvDAOJpa;
    }

    /**
     * Set the Fragment Version Dag Dao object for this class. Mainly for spring tests.
     * @param fvdDAOJpa the Fragment Version Dag Dao.
     */
    public void setFragmentVersionDagDao(FragmentVersionDagDaoJpa fvdDAOJpa) {
        fvdDao = fvdDAOJpa;
    }

    /**
     * Set the Graph Service object for this class. Mainly for spring tests.
     * @param gService the Graph Service.
     */
    public void setGraphService(GraphServiceImpl gService) {
        gSrv = gService;
    }
}
