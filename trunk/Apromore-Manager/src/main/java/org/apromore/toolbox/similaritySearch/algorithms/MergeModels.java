package org.apromore.toolbox.similaritySearch.algorithms;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.ICPFObject;
import org.apromore.graph.canonical.ICPFObjectReference;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.toolbox.similaritySearch.common.NodePair;
import org.apromore.toolbox.similaritySearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.toolbox.similaritySearch.common.algos.TwoVertices;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.planarGraphMathing.PlanarGraphMathing.MappingRegions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MergeModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeModels.class);


    public Canonical mergeModels(Canonical g1, Canonical g2, boolean removeEnt, String algortithm, double... param) {
        AssingmentProblem assingmentProblem = new AssingmentProblem();
        HashMap<String, String> objectresourceIDMap = new HashMap<String, String>();

        Canonical merged = new Canonical();

        merged.addVertices(g1.getVertices());
        merged.addEdges(g1.getEdges());
        merged.addVertices(g2.getVertices());
        merged.addEdges(g2.getEdges());

        // add all resources from the first models
        merged.getResources().addAll(g1.getResources());
        mergeResources(g2.getResources(), objectresourceIDMap, merged);
        merged.getObjects().addAll(g1.getObjects());
        mergeObjects(g2.getObjects(), objectresourceIDMap, merged);

        LinkedList<NodePair> mapping = new LinkedList<NodePair>();

        if (algortithm.equals("Greedy")) {
            GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
            Object weights[] = {"ledcutoff", param[0], "cedcutoff", param[1], "vweight", param[2], "sweight", param[3], "eweight", param[4]};

            gedepc.setWeight(weights);

            for (TwoVertices pair : gedepc.compute(g1, g2)) {
                CPFNode v1 = g1.getNodeMap().get(pair.v1);
                CPFNode v2 = g2.getNodeMap().get(pair.v2);
                if (v1.getNodeType().equals(v2.getNodeType())) {
                    mapping.add(new NodePair(v1, v2, pair.weight));
                }
            }
        } else if (algortithm.equals("Hungarian")) {
            mapping = assingmentProblem.getMappingsNodesUsingNodeMapping(g1, g2, param[0], param[1]);
        }

        // clean mappings from mappings that conflict
        // TODO uncomment
//        removeNonDominanceMappings(mapping);

        if (removeEnt) {
            g1.populateDominantRelationships();
            g2.populateDominantRelationships();
            removeNonDominanceMappings2(mapping);
        }

        MappingRegions mappingRegions = findMaximumCommonRegions(g1, g2, mapping);
        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
            for (NodePair vp : region) {
                LinkedList<CPFNode> nodesToProcess = new LinkedList<CPFNode>();
                for (CPFNode c : vp.getRight().getChildren()) {
                    // the child is also part of the mapping remove the edge from the merged model
                    if (containsNode(region, c)) {
                        nodesToProcess.add(c);
                    }
                }
                for (CPFNode c : nodesToProcess) {
                    merged.removeEdge(merged.getEdge(vp.getRight(), c));

                    vp.getRight().removeChild(c);
                    c.removeParent(vp.getRight());

                    CPFNode cLeft = getMappingPair(mapping, c);
                    CPFEdge e = merged.getEdge(vp.getLeft(), cLeft);
                    if (e != null) {
                        //addLabels
                        LOGGER.debug("Not sure what happened here...");
                    }
                }
            }

            // add annotations for the labels
            for (NodePair vp : region) {
                CPFNode mappingRight = vp.getRight();

                // merge object references
                for (ICPFObjectReference o : mappingRight.getObjectReferences()) {
                    boolean mergedO = false;
                    for (ICPFObjectReference vo : vp.getLeft().getObjectReferences()) {
                        if ((vo.getObjectId().equals(o.getObjectId()) || objectresourceIDMap.get(o.getObjectId()) != null &&
                                objectresourceIDMap.get(o.getObjectId()).equals(vo.getObjectId())) &&
                                canMergeObjectReference(o, vo)) {
                            mergedO = true;
                            break;
                        }
                    }
                    if (!mergedO) {
                        vp.getLeft().getObjectReferences().add(o);
                    }
                }

                // merge resource references
                for (ICPFResourceReference o : mappingRight.getResourceReferences()) {
                    boolean mergedO = false;
                    for (ICPFResourceReference vo : vp.getLeft().getResourceReferences()) {
                        if ((vo.getResourceId().equals(o.getResourceId()) || objectresourceIDMap.get(o.getResourceId()) != null &&
                                objectresourceIDMap.get(o.getResourceId()).equals(vo.getResourceId())) &&
                                canMergeResourceReference(o, vo)) {
                            mergedO = true;
                            break;
                        }
                    }
                    if (!mergedO) {
                        vp.getLeft().getResourceReferences().add(o);
                    }
                }
            }
        }

        // check if some vertices must be removed
        LinkedList<CPFNode> toRemove = new LinkedList<CPFNode>();
        for (CPFNode v : merged.getNodes()) {
            if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
                toRemove.add(v);
            }
        }
        merged.removeNodes(toRemove);

        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
            for (NodePair vp : region) {
                boolean addgw = true;
                boolean addgwr = true;

                for (CPFNode p : vp.getLeft().getParents()) {
                    if (containsNode(region, p)) {
                        addgw = false;
                        break;
                    }
                }

                // check parents from second modelass. maybe the nodes are concurrent in one modelass but not in the other
                for (CPFNode p : vp.getRight().getParents()) {
                    if (containsNode(region, p)) {
                        addgwr = false;
                        break;
                    }
                }

                if ((addgw || addgwr) && vp.getLeft().getParents().size() == 1 && vp.getRight().getParents().size() == 1) {
                    CPFNode newGw = new CPFNode();
                    newGw.setNodeType(NodeTypeEnum.XORSPLIT);
                    newGw.setConfigurable(true);
                    merged.addNode(newGw);

                    CPFNode v1 = vp.getLeft().getParents().iterator().next();
                    merged.removeEdge(merged.getEdge(v1, vp.getLeft()));
                    v1.removeChild(vp.getLeft());
                    vp.getLeft().removeParent(v1);
                    merged.addEdge(v1, newGw);

                    CPFNode v2 = vp.getRight().getParents().iterator().next();
                    merged.removeEdge(merged.getEdge(v2, vp.getRight()));
                    v2.removeChild(vp.getRight());
                    vp.getRight().removeParent(v2);
                    merged.addEdge(v2, newGw);

                    merged.addEdge(newGw, vp.getLeft());
                }
            }
        }

        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
            for (NodePair vp : region) {
                boolean addgw = true;
                boolean addgwr = true;
                for (CPFNode ch : vp.getLeft().getChildren()) {
                    if (containsNode(region, ch)) {
                        addgw = false;
                        break;
                    }
                }

                // check parents from second modelass maybe the nodes are concurrent in one modelass but not in the other
                for (CPFNode ch : vp.getRight().getChildren()) {
                    if (containsNode(region, ch)) {
                        addgwr = false;
                        break;
                    }
                }
                if ((addgw || addgwr) && vp.getLeft().getChildren().size() == 1 && vp.getRight().getChildren().size() == 1) {
                    CPFNode newGw = new CPFNode();
                    newGw.setNodeType(NodeTypeEnum.XORSPLIT);
                    newGw.setConfigurable(true);
                    merged.addVertex(newGw);

                    CPFNode v1 = vp.getLeft().getChildren().iterator().next();
                    merged.removeEdge(merged.getEdge(vp.getLeft(), v1));
                    vp.getLeft().removeChild(v1);
                    v1.removeParent(vp.getLeft());
                    merged.addEdge(newGw, v1);

                    CPFNode v2 = vp.getRight().getChildren().iterator().next();
                    merged.removeEdge(merged.getEdge(vp.getRight(), v2));
                    vp.getRight().removeChild(v2);
                    v2.removeParent(vp.getRight());
                    merged.addEdge(newGw, v2);

                    merged.addEdge(vp.getLeft(), newGw);
                }
            }
        }

        mergeConnectors(mappingRegions, merged, mapping);

        toRemove = new LinkedList<CPFNode>();
        // check if some vertices must be removed
        for (CPFNode v : merged.getVertices()) {
            if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
                toRemove.add(v);
            }
        }
        merged.removeNodes(toRemove);

//        // labels for all edges should be added to the modelass
//        for (CPFEdge e : merged.getEdges()) {
//            e.addLabelToModel();
//        }
//
//        String name = "";
//        for (String l : merged.getEdgeLabels()) {
//            name += l + ",";
//        }
//        merged.setName(name.substring(0, name.length() - 1));

        return merged;
    }

    private boolean canMergeObjectReference(ICPFObjectReference o, ICPFObjectReference vo) {
        return o.isConsumed() == vo.isConsumed() &&  o.isOptional() == vo.isOptional() &&
                (o.getObjectRefType().equals(vo.getObjectRefType()));
    }

    private boolean canMergeResourceReference(ICPFResourceReference o, ICPFResourceReference vo) {
        return o.getQualifier() != null && vo.getQualifier() != null && o.getQualifier().equals(vo.getQualifier());
    }

    /* Merge the Resources of two Model. */
    private static void mergeResources(Collection<ICPFResource> existing, HashMap<String, String> objectResourceIdMap, Canonical merged) {
        // add resources and objects
        for (ICPFResource v : existing) {
            boolean mergedResource = false;
            for (ICPFResource mv : merged.getResources()) {
                if (mv.canMerge(v)) {
                    objectResourceIdMap.put(v.getId(), mv.getId());

                    if (v.isConfigurable()) {
                        mv.setConfigurable(true);
                    }
                    mergedResource = true;
                    break;
                }
            }
            // this resource must be added
            if (!mergedResource) {
                merged.addResource(v);
            }
        }
    }

    /* Merge the Objects of two Model. */
    private static void mergeObjects(Collection<ICPFObject> existing, HashMap<String, String> objectresourceIDMap, Canonical merged) {
        // add resources and objects
        for (ICPFObject v : existing) {
            boolean mergedResource = false;
            for (ICPFObject mv : merged.getObjects()) {
                if (mv.canMerge(v)) {
                    objectresourceIDMap.put(v.getId(), mv.getId());

                    if (v.isConfigurable()) {
                        mv.setConfigurable(true);
                    }
                    mergedResource = true;
                    break;
                }
            }
            // this resource must be added
            if (!mergedResource) {
                merged.addObject(v);
            }
        }
    }

    @SuppressWarnings("unused")
    private void removeNonDominanceMappings(LinkedList<NodePair> mapping) {
        LinkedList<NodePair> removeList = new LinkedList<NodePair>();
        int i = 0;

        for (NodePair vp : mapping) {
            i++;
            // the mapping is already in removed list
            if (removeList.contains(vp)) {
                continue;
            }

            for (int j = i; j < mapping.size(); j++) {
                NodePair vp1 = mapping.get(j);
                if (vp.getLeft().getId().equals(vp1.getLeft().getId()) || vp.getRight().getId().equals(vp1.getRight().getId())) {
                    continue;
                }
                boolean dominanceInG1 = containsInDownwardsPath(vp.getLeft(), vp1.getLeft());
                boolean dominanceInG2 = containsInDownwardsPath(vp.getRight(), vp1.getRight());

                // dominance rule is broken
                if (dominanceInG1 && !dominanceInG2 || !dominanceInG1 && dominanceInG2) {
                    // remove 2 pairs from the pairs list and start with the new pair
                    removeList.add(vp);
                    removeList.add(vp1);
                    break;
                }
            }
        }

        // remove conflicting mappings
        for (NodePair vp : removeList) {
            mapping.remove(vp);
        }
    }

    @SuppressWarnings("unused")
    private void removeNonDominanceMappings1(LinkedList<NodePair> mapping) {

        LinkedList<NodePair> removeList = new LinkedList<NodePair>();
        int i = 0;

        for (NodePair vp : mapping) {
            i++;
            // the mapping is already in removed list
            if (removeList.contains(vp)) {
                continue;
            }

            // TODO - if there exists path where A dominances B, then this dominances B
            // even when this is a cycle
            for (int j = i; j < mapping.size(); j++) {
                NodePair vp1 = mapping.get(j);
                if (vp.getLeft().getId().equals(vp1.getLeft().getId()) || vp.getRight().getId().equals(vp1.getRight().getId())) {
                    continue;
                }

                // dominance rule is broken
                if (vp.getLeft().dominance.contains(vp1.getLeft().getId())
                        && vp1.getRight().dominance.contains(vp.getRight().getId())
                        || vp1.getLeft().dominance.contains(vp.getLeft().getId())
                        && vp.getRight().dominance.contains(vp1.getRight().getId())) {
                    // remove 2 pairs from the pairs list and start with the new pair
                    removeList.add(vp);
                    removeList.add(vp1);
                    break;
                }
            }
        }

        // remove conflicting mappings
        for (NodePair vp : removeList) {
            mapping.remove(vp);
        }
    }

    // implementation of Marlon new dominance mapping relation
    private static void removeNonDominanceMappings2(LinkedList<NodePair> mapping) {

        LinkedList<NodePair> removeList = new LinkedList<NodePair>();
        int i = 0;

        for (NodePair vp : mapping) {
            i++;
            // the mapping is already in removed list
            if (removeList.contains(vp)) {
                continue;
            }

            for (int j = i; j < mapping.size(); j++) {

                NodePair vp1 = mapping.get(j);

                // the mapping is already in removed list
                if (removeList.contains(vp1)) {
                    continue;
                }

                // same starting or ending point of models
                if (vp.getLeft().getId().equals(vp1.getLeft().getId()) || vp.getRight().getId().equals(vp1.getRight().getId())) {
                    continue;
                }

                // dominance rule is broken
                if ((vp.getLeft().dominance.contains(vp1.getLeft().getId())
                        && vp1.getRight().dominance.contains(vp.getRight().getId())
                        && !(vp1.getLeft().dominance.contains(vp.getLeft().getId())
                        || vp.getRight().dominance.contains(vp1.getRight().getId())))
                        || (vp1.getLeft().dominance.contains(vp.getLeft().getId())
                        && vp.getRight().dominance.contains(vp1.getRight().getId())
                        && !(vp.getLeft().dominance.contains(vp1.getLeft().getId())
                        || vp1.getRight().dominance.contains(vp.getRight().getId())))) {
                    // remove 2 pairs from the pairs list and start with the new pair
                    removeList.add(vp);
                    removeList.add(vp1);
                    break;
                }
            }
        }

        // remove conflicting mappings
        for (NodePair vp : removeList) {
            mapping.remove(vp);
        }
    }

    private boolean containsInDownwardsPath(CPFNode v1, CPFNode v2) {
        LinkedList<CPFNode> toProcess = new LinkedList<CPFNode>();
        toProcess.addAll(v1.getChildren());

        while (toProcess.size() > 0) {
            CPFNode process = toProcess.removeFirst();
            if (process.getId().equals(v2.getId())) {
                return true;
            }
            toProcess.addAll(process.getChildren());
        }
        return false;
    }

    private static void mergeConnectors(MappingRegions mappingRegions, Canonical merged, LinkedList<NodePair> mapping) {
        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
            for (NodePair vp : region) {
                if (vp.getLeft().getNodeType().equals(NodeTypeEnum.ROUTING)) {
                    boolean makeConf = false;
                    LinkedList<CPFNode> toProcess = new LinkedList<CPFNode>();
                    for (CPFNode p : vp.getRight().getParents()) {
                        if (!containsNode(region, p)) {
                            toProcess.add(p);
                        }
                    }

                    for (CPFNode p : toProcess) {
                        makeConf = true;
                        merged.removeEdge(merged.getEdge(p, vp.getRight()));
                        p.removeChild(vp.getRight());
                        vp.getRight().removeParent(p);
                        merged.addEdge(p, vp.getLeft());
                    }
                    toProcess = new LinkedList<CPFNode>();

                    for (CPFNode p : vp.getRight().getChildren()) {
                        if (!containsNode(region, p)) {
                            toProcess.add(p);
                        }
                    }

                    for (CPFNode p : toProcess) {
                        makeConf = true;
                        merged.removeEdge(merged.getEdge(vp.getRight(), p));
                        p.removeParent(vp.getRight());
                        vp.getRight().removeChild(p);
                        merged.addEdge(vp.getLeft(), p);
                    }
                    if (makeConf) {
                        vp.getLeft().setConfigurable(true);
                    }
                    if (!vp.getLeft().getNodeType().equals(vp.getRight().getNodeType())) {
                        vp.getLeft().setNodeType(NodeTypeEnum.ORSPLIT);
                    }
                }
            }
        }
    }


    private static NodePair findNextNodeToProcess(LinkedList<NodePair> mapping, LinkedList<NodePair> visited) {
        for (NodePair vp : mapping) {
            NodePair process = containsMapping(visited, vp.getLeft(), vp.getRight());
            if (process == null) {
                return vp;
            }
        }
        return null;
    }

    private static NodePair containsMapping(LinkedList<NodePair> mapping, CPFNode left, CPFNode right) {
        for (NodePair vp : mapping) {
            if (vp.getLeft().getId().equals(left.getId()) && vp.getRight().getId().equals(right.getId())) {
                return vp;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static boolean containsMapping(LinkedList<NodePair> mapping, NodePair v) {
        for (NodePair vp : mapping) {
            if (vp.getLeft().getId().equals(v.getLeft().getId()) && vp.getRight().getId().equals(v.getRight().getId())) {
                return true;
            }
        }
        return false;
    }

    public static MappingRegions findMaximumCommonRegions(Canonical g1, Canonical g2, LinkedList<NodePair> mapping) {
        MappingRegions map = new MappingRegions();
        LinkedList<NodePair> visited = new LinkedList<NodePair>();

        while (true) {
            NodePair c = findNextNodeToProcess(mapping, visited);
            if (c == null) {
                break;
            }
            LinkedList<NodePair> toVisit = new LinkedList<NodePair>();
            LinkedList<NodePair> mapRegion = new LinkedList<NodePair>();

            toVisit.add(c);
            while (toVisit.size() > 0) {
                c = toVisit.removeFirst();
                mapRegion.add(c);

                visited.add(c);
                for (CPFNode pLeft : c.getLeft().getParents()) {
                    for (CPFNode pRight : c.getRight().getParents()) {
                        NodePair pairMap = containsMapping(mapping, pLeft, pRight);
                        NodePair containsMap = containsMapping(visited, pLeft, pRight);
                        NodePair containsMap1 = containsMapping(toVisit, pLeft, pRight);
                        if (pairMap != null && containsMap == null && containsMap1 == null) {
                            toVisit.add(pairMap);
                        }
                    }
                }

                for (CPFNode pLeft : c.getLeft().getChildren()) {
                    for (CPFNode pRight : c.getRight().getChildren()) {
                        NodePair pairMap = containsMapping(mapping, pLeft, pRight);
                        NodePair containsMap = containsMapping(visited, pLeft, pRight);
                        NodePair containsMap1 = containsMapping(toVisit, pLeft, pRight);
                        if (pairMap != null && containsMap == null && containsMap1 == null) {
                            toVisit.add(pairMap);
                        }
                    }
                }

            }
            if (mapRegion.size() > 0) {
                map.addRegion(mapRegion);
            }
        }

        return map;
    }

    public static boolean containsNode(LinkedList<NodePair> mapping, CPFNode v) {
        for (NodePair vp : mapping) {
            if (vp.getLeft().getId().equals(v.getId()) || vp.getRight().getId().equals(v.getId())) {

                return true;
            }
        }
        return false;
    }

    public static CPFNode getMappingPair(LinkedList<NodePair> mapping, CPFNode v) {
        for (NodePair vp : mapping) {
            if (vp.getLeft().getId().equals(v.getId())) {
                return vp.getRight();
            } else if (vp.getRight().getId().equals(v.getId())) {
                return vp.getLeft();
            }
        }
        return null;
    }
}
