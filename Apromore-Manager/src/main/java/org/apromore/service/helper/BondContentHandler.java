package org.apromore.service.helper;

import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.service.GraphService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional
public class BondContentHandler implements ContentHandler {

    private FragmentVersionRepository fragmentVersionRepository;
    private FragmentVersionDagRepository fragmentVersionDagRepository;
    private GraphService graphService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param fragmentVersionRepository Fragment Version Repository
     * @param fragmentVersionDagRepository Fragment Version Dag Repository
     * @param graphService Graphing Service
     */
    @Inject
    public BondContentHandler(final FragmentVersionRepository fragmentVersionRepository,
            final FragmentVersionDagRepository fragmentVersionDagRepository, final GraphService graphService) {
        this.fragmentVersionRepository = fragmentVersionRepository;
        this.fragmentVersionDagRepository = fragmentVersionDagRepository;
        this.graphService = graphService;
    }



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
    public String matchFragment(Canonical f, Content matchingContent, Map<String, String> childMappings,
            Map<String, String> newChildMappings) {
        // find forward and reverse pocket ids of the given fragment
        String fragmentEntryId = f.getEntry().getId();
        String fragmentExitId = f.getExit().getId();
        List<String> forwardFragmentPocketIds = new ArrayList<String>();
        List<String> reverseFragmentPocketIds = new ArrayList<String>();
        Collection<CPFEdge> fragmentEdges = f.getEdges();
        for (CPFEdge fragmentEdge : fragmentEdges) {
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
        Canonical content = graphService.getGraph(matchingContent.getId());
        String contentEntryId = content.getSourceNodes().iterator().next().getId();
        String contentExitId = content.getSinkNodes().iterator().next().getId();
        Collection<CPFEdge> contentEdges = content.getEdges();
        for (CPFEdge contentEdge : contentEdges) {
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

        String matchingFragmentId = null;
        List<FragmentChildMapping> candidateChildMappings = getCandidateChildMappings(matchingContent.getId());
        for (FragmentChildMapping fragmentChildMapping : candidateChildMappings) {
            List<FragmentVersionDag> candidateMapping = fragmentChildMapping.getChildMapping();

            List<String> forwardCandidateChildIds = new ArrayList<String>();
            List<String> reverseCandidateChildIds = new ArrayList<String>();

            for (FragmentVersionDag candidatePocket : candidateMapping) {
                if (forwardContentPocketIds.contains(candidatePocket.getPocketId())) {
                    forwardCandidateChildIds.add(candidatePocket.getChildFragmentVersion().getId().toString());
                }
                if (reverseContentPocketIds.contains(candidatePocket.getPocketId())) {
                    reverseCandidateChildIds.add(candidatePocket.getChildFragmentVersion().getId().toString());
                }
            }
            if (forwardFragmentChildIds.containsAll(forwardCandidateChildIds) &&
                    reverseFragmentChildIds.containsAll(reverseCandidateChildIds)) {
                matchingFragmentId = fragmentChildMapping.getFragmentId().toString();
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
        List<FragmentVersion> candidateFragmentIds = fragmentVersionRepository.getUsedFragments(matchingContentId);
        for (FragmentVersion candidateFragmentId : candidateFragmentIds) {
            List<FragmentVersionDag> childMapping = fragmentVersionDagRepository.getChildMappings(candidateFragmentId.getId());
            FragmentChildMapping fragmentChildMapping = new FragmentChildMapping();
            fragmentChildMapping.setFragmentId(candidateFragmentId.getId());
            fragmentChildMapping.setChildMapping(childMapping);
            candidateChildMappings.add(fragmentChildMapping);
        }
        return candidateChildMappings;
    }


}
