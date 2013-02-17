package org.apromore.toolbox.similaritySearch.algorithms;

import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;

public class MergeModels {

    public Canonical mergeModels(Canonical g1, Canonical g2, IdGeneratorHelper idGenerator, boolean removeEnt, String algortithm,
            double... param) {
//        AssingmentProblem assingmentProblem = new AssingmentProblem();
//        HashMap<String, String> objectresourceIDMap = new HashMap<String, String>();
//
//        Canonical merged = new Canonical();
//        long startTime = System.currentTimeMillis();
//
//        merged.addNodes(g1.getNodes());
//        merged.addEdges(g1.getEdges());
//        merged.addNodes(g2.getNodes());
//        merged.addEdges(g2.getEdges());
//
//        // add all resources from the first models
//        merged.setResources(g1.getResources());
//        mergeResources(g2.getResources(), objectresourceIDMap, merged);
//        merged.setObjects(g1.getObjects());
//        mergeObjects(g2.getObjects(), objectresourceIDMap, merged);
//
//        Set<NodePair> mapping = new HashSet<NodePair>(0);
//
//        if (algortithm.equals("Greedy")) {
//            GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
//            Object weights[] = {"ledcutoff", param[0], "cedcutoff", param[1], "vweight", param[2], "sweight", param[3], "eweight", param[4]};
//
//            gedepc.setWeight(weights);
//
//            for (TwoVertices pair : gedepc.compute(g1, g2)) {
//                CPFNode v1 = g1.getNodeMap().get(pair.v1);
//                CPFNode v2 = g2.getNodeMap().get(pair.v2);
//                if (v1.getNodeType().equals(v2.getNodeType())) {
//                    mapping.add(new NodePair(v1, v2, pair.weight));
//                }
//            }
//        } else if (algortithm.equals("Hungarian")) {
//            mapping = assingmentProblem.getMappingsNodesUsingNodeMapping(g1, g2, param[0], param[1]);
//        }
//
//        // clean mappings from mappings that conflict
//        // TODO uncomment
////      removeNonDominanceMappings(mapping);
//
//        if (removeEnt) {
//            g1.populateDominanceRelations();
//            g2.populateDominanceRelations();
//            removeNonDominanceMappings2(mapping);
//        }
//
//        MappingRegions mappingRegions = findMaximumCommonRegions(g1, g2, mapping);
//        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
//            for (NodePair vp : region) {
//                LinkedList<CPFNode> nodesToProcess = new LinkedList<CPFNode>();
//                for (CPFNode c : vp.getRight().getGraph().getAllSuccessors(vp.getRight())) {
//                    // the child is also part of the mapping remove the edge from the merged modelass
//                    if (containsNode(region, c)) {
//                        nodesToProcess.add(c);
//                    }
//                }
//                for (CPFNode c : nodesToProcess) {
//                    HashSet<String> labels = merged.removeEdge(vp.getRight().getId(), c.getId());
//
//                    vp.getRight().removeChild(c.getId());
//                    c.removeParent(vp.getRight().getId());
//
//                    CPFNode cLeft = getMappingPair(mapping, c);
//                    Edge e = merged.containsEdge(vp.getLeft().getId(), cLeft.getId());
//                    if (e != null) {
//                        e.addLabels(labels);
//                    }
//                }
//            }
//            // add annotations for the labels
//            for (NodePair vp : region) {
//                CPFNode mappingRight = vp.getRight();
//                vp.getLeft().addAnnotations(mappingRight.getAnnotationMap());
//
//                // merge object references
//                for (CPFNodeObjectRef o : mappingRight.objectRefs) {
//                    boolean mergedO = false;
//                    for (CPFNodeObjectRef vo : vp.getLeft().objectRefs) {
//                        if ((vo.getObjectID().equals(o.getObjectID()) ||
//                                objectresourceIDMap.get(o.getObjectID()) != null &&
//                                        objectresourceIDMap.get(o.getObjectID()).equals(vo.getObjectID())) &&
//                                o.canMerge(vo)) {
//                            vo.addModels(o.getModels());
//                            mergedO = true;
//                            break;
//                        }
//                    }
//                    if (!mergedO) {
//                        vp.getLeft().objectRefs.add(o);
//                    }
//                }
//
//                // merge resource references
//                for (CPFNodeResourceRef o : mappingRight.resourceRefs) {
//                    boolean mergedO = false;
//                    for (CPFNodeResourceRef vo : vp.getLeft().resourceRefs) {
//                        if ((vo.getresourceID().equals(o.getresourceID()) ||
//                                objectresourceIDMap.get(o.getresourceID()) != null &&
//                                        objectresourceIDMap.get(o.getresourceID()).equals(vo.getresourceID())) &&
//                                o.canMerge(vo)) {
//                            vo.addModels(o.getModels());
//                            mergedO = true;
//                            break;
//                        }
//                    }
//                    if (!mergedO) {
//                        vp.getLeft().resourceRefs.add(o);
//                    }
//                }
//            }
//        }
//
//        LinkedList<CPFNode> toRemove = new LinkedList<CPFNode>();
//        // check if some vertices must be removed
//        for (CPFNode v : merged.getVertices()) {
//            if (v.getGraph().getAllSuccessors(v).size() == 0 && v.getGraph().getAllPredecessors(v).size() == 0) {
//                toRemove.add(v);
//            }
//        }
//
//        for (CPFNode v : toRemove) {
//            merged.removeNode(v);
//        }
//
//        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
//            for (NodePair vp : region) {
//                boolean addgw = true;
//                boolean addgwr = true;
//
//                for (CPFNode p : vp.getLeft().getGraph().getAllSuccessors(vp.getLeft())) {
//                    if (containsNode(region, p)) {
//                        addgw = false;
//                        break;
//                    }
//                }
//                // check parents from second modelass
//                // maybe the nodes are concurrent in one modelass but not in the other
//                for (CPFNode p : vp.getRight().getGraph().getAllSuccessors(vp.getRight())) {
//                    if (containsNode(region, p)) {
//                        addgwr = false;
//                        break;
//                    }
//                }
//                if ((addgw || addgwr) && vp.getLeft().getGraph().getAllSuccessors(vp.getLeft()).size() == 1 &&
//                        vp.getRight().getGraph().getAllSuccessors(vp.getRight()).size() == 1) {
//                    CPFNode newGw = new CPFNode();
//                    newGw.setNodeType(NodeTypeEnum.ORJOIN);
//                    newGw.setConfigurable(true);
//                    merged.addNode(newGw);
//
//                    CPFNode v1 = vp.getLeft().getParents().get(0);
//                    HashSet<String> s1 = merged.removeEdge(v1.getId(), vp.getLeft().getId());
//                    v1.removeChild(vp.getLeft().getId());
//                    vp.getLeft().removeParent(v1.getId());
//                    merged.connectVertices(v1, newGw, s1);
//
//                    CPFNode v2 = vp.getRight().getParents().get(0);
//                    HashSet<String> s2 = merged.removeEdge(v2.getId(), vp.getRight().getId());
//                    v2.removeChild(vp.getRight().getId());
//                    vp.getRight().removeParent(v2.getId());
//                    merged.connectVertices(v2, newGw, s2);
//
//                    HashSet<String> s3 = new HashSet<String>(s1);
//                    s3.addAll(s2);
//                    merged.connectVertices(newGw, vp.getLeft(), s3);
//                    newGw.addAnnotationsForGw(s3);
//                }
//            }
//        }
//
//        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
//            for (NodePair vp : region) {
//                boolean addgw = true;
//                boolean addgwr = true;
//                for (CPFNode ch : vp.getLeft().getChildren()) {
//                    if (containsNode(region, ch)) {
//                        addgw = false;
//                        break;
//                    }
//                }
//
//                // check parents from second modelass
//                // maybe the nodes are concurrent in one modelass but not in the other
//                for (CPFNode ch : vp.getRight().getChildren()) {
//                    if (containsNode(region, ch)) {
//                        addgwr = false;
//                        break;
//                    }
//                }
//                if ((addgw || addgwr) && vp.getLeft().getChildren().size() == 1 &&
//                        vp.getRight().getChildren().size() == 1) {
//                    CPFNode newGw = new CPFNode(GWType.xor, idGenerator.getNextId());
//                    newGw.setConfigurable(true);
//                    merged.addNode(newGw);
//
//                    CPFNode v1 = vp.getLeft().getChildren().get(0);
//                    HashSet<String> s1 = merged.removeEdge(vp.getLeft().getId(), v1.getId());
//                    vp.getLeft().removeChild(v1.getId());
//                    v1.removeParent(vp.getLeft().getId());
//                    merged.connectVertices(newGw, v1, s1);
//
//                    CPFNode v2 = vp.getRight().getChildren().get(0);
//                    HashSet<String> s2 = merged.removeEdge(vp.getRight().getId(), v2.getId());
//                    vp.getRight().removeChild(v2.getId());
//                    v2.removeParent(vp.getRight().getId());
//                    merged.connectVertices(newGw, v2, s2);
//
//                    HashSet<String> s3 = new HashSet<String>(s1);
//                    s3.addAll(s2);
//                    merged.connectVertices(vp.getLeft(), newGw, s3);
//                    newGw.addAnnotationsForGw(s3);
//                }
//            }
//        }
//
//        mergeConnectors(mappingRegions, merged, mapping);
//
//        toRemove = new LinkedList<CPFNode>();
//        // check if some vertices must be removed
//        for (CPFNode v : merged.getVertices()) {
//            if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
//                toRemove.add(v);
//            }
//        }
//        for (CPFNode v : toRemove) {
//            merged.removeNode(v);
//        }
//
//        //int[] gwInf = merged.getNrOfConfigGWs();
//
//        long mergeTime = System.currentTimeMillis();
//        merged.cleanGraph();
//
//        //gwInf = merged.getNrOfConfigGWs();
//
//        // labels for all edges should be added to the modelass
//        for (CPFEdge e : merged.getEdges()) {
//            e.addLabelToModel();
//        }
//
//        long cleanTime = System.currentTimeMillis();
//
//        merged.mergetime = mergeTime - startTime;
//        merged.cleanTime = cleanTime - startTime;
//
//        merged.name = "";
//        for (String l : merged.getEdgeLabels()) {
//            merged.name += l + ",";
//        }
//        merged.name = merged.name.substring(0, merged.name.length() - 1);
//
//        return merged;
        return null;
    }


//    /* Merge the Resources of two Model. */
//    private static void mergeResources(Collection<ICPFResource> existing, HashMap<String, String> objectResourceIdMap, Canonical merged) {
//        // add resources and objects
//        for (ICPFResource v : existing) {
//            boolean mergedResource = false;
//            for (ICPFResource mv : merged.getResources()) {
//                if (mv.canMerge(v)) {
//                    objectResourceIdMap.put(v.getId(), mv.getId());
//
//                    if (v.isConfigurable()) {
//                        mv.setConfigurable(true);
//                    }
//                    mergedResource = true;
//                    break;
//                }
//            }
//            // this resource must be added
//            if (!mergedResource) {
//                merged.addResource(v);
//            }
//        }
//    }
//
//    /* Merge the Objects of two Model. */
//    private static void mergeObjects(Collection<ICPFObject> existing, HashMap<String, String> objectresourceIDMap, Canonical merged) {
//        // add resources and objects
//        for (ICPFObject v : existing) {
//            boolean mergedResource = false;
//            for (ICPFObject mv : merged.getObjects()) {
//                if (mv.canMerge(v)) {
//                    objectresourceIDMap.put(v.getId(), mv.getId());
//
//                    if (v.isConfigurable()) {
//                        mv.setConfigurable(true);
//                    }
//                    mergedResource = true;
//                    break;
//                }
//            }
//            // this resource must be added
//            if (!mergedResource) {
//                merged.addObject(v);
//            }
//        }
//    }
//
//    @SuppressWarnings("unused")
//    private void removeNonDominanceMappings(LinkedList<NodePair> mapping) {
//        LinkedList<NodePair> removeList = new LinkedList<NodePair>();
//        int i = 0;
//
//        for (NodePair vp : mapping) {
//            i++;
//            // the mapping is already in removed list
//            if (removeList.contains(vp)) {
//                continue;
//            }
//
//            for (int j = i; j < mapping.size(); j++) {
//                NodePair vp1 = mapping.get(j);
//                if (vp.getLeft().getId() == vp1.getLeft().getId() || vp.getRight().getId() == vp1.getRight().getId()) {
//                    continue;
//                }
//                boolean dominanceInG1 = containsInDownwardsPath(vp.getLeft(), vp1.getLeft());
//                boolean dominanceInG2 = containsInDownwardsPath(vp.getRight(), vp1.getRight());
//
//                // dominance rule is broken
//                if (dominanceInG1 && !dominanceInG2 || !dominanceInG1 && dominanceInG2) {
//                    // remove 2 pairs from the pairs list and start with the new pair
//                    removeList.add(vp);
//                    removeList.add(vp1);
//                    break;
//                }
//            }
//        }
//
//        // remove conflicting mappings
//        for (NodePair vp : removeList) {
//            mapping.remove(vp);
//        }
//    }
//
//    @SuppressWarnings("unused")
//    private void removeNonDominanceMappings1(LinkedList<NodePair> mapping) {
//        LinkedList<NodePair> removeList = new LinkedList<NodePair>();
//        int i = 0;
//
//        for (NodePair vp : mapping) {
//            i++;
//            // the mapping is already in removed list
//            if (removeList.contains(vp)) {
//                continue;
//            }
//
//            // TODO - if there exists path where A dominances B, then this dominances B
//            // even when this is a cycle
//            for (int j = i; j < mapping.size(); j++) {
//                NodePair vp1 = mapping.get(j);
//                if (vp.getLeft().getId() == vp1.getLeft().getId() || vp.getRight().getId() == vp1.getRight().getId()) {
//                    continue;
//                }
//
//                // dominance rule is broken
//                if (vp.getLeft().dominance.contains(vp1.getLeft().getId())
//                        && vp1.getRight().dominance.contains(vp.getRight().getId())
//                        || vp1.getLeft().dominance.contains(vp.getLeft().getId())
//                        && vp.getRight().dominance.contains(vp1.getRight().getId())) {
//                    // remove 2 pairs from the pairs list and start with the new pair
//                    removeList.add(vp);
//                    removeList.add(vp1);
//                    break;
//                }
//            }
//        }
//
//        // remove conflicting mappings
//        for (NodePair vp : removeList) {
//            mapping.remove(vp);
//        }
//    }
//
//    // implementation of Marlon new dominance mapping relation
//    private static void removeNonDominanceMappings2(Set<NodePair> mapping) {
//        LinkedList<NodePair> removeList = new LinkedList<NodePair>();
//        int j;
//        int i = 0;
//
//        for (NodePair vp : mapping) {
//            i++;
//
//            // the mapping is already in removed list
//            if (removeList.contains(vp)) {
//                continue;
//            }
//
//            //for (int j = i; j < mapping.size(); j++) {
//            //    NodePair vp1 = mapping.get(j);
//
//            j = 0;
//            for (NodePair vp1 : mapping) {
//                j++;
//                // the mapping is already in removed list
//                if (j < i || removeList.contains(vp1)) {
//                    continue;
//                }
//
//                // same starting or ending point of models
//                if (vp.getLeft().getId() == vp1.getLeft().getId() || vp.getRight().getId() == vp1.getRight().getId()) {
//                    continue;
//                }
//
//                // dominance rule is broken
//                if ((vp.getLeft().dominance.contains(vp1.getLeft().getId())
//                        && vp1.getRight().dominance.contains(vp.getRight().getId())
//                        && !(vp1.getLeft().dominance.contains(vp.getLeft().getId())
//                        || vp.getRight().dominance.contains(vp1.getRight().getId())))
//                        || (vp1.getLeft().dominance.contains(vp.getLeft().getId())
//                        && vp.getRight().dominance.contains(vp1.getRight().getId())
//                        && !(vp.getLeft().dominance.contains(vp1.getLeft().getId())
//                        || vp1.getRight().dominance.contains(vp.getRight().getId())))) {
//                    // remove 2 pairs from the pairs list and start with the new pair
//                    removeList.add(vp);
//                    removeList.add(vp1);
//                    break;
//                }
//            }
//        }
//
//        // remove conflicting mappings
//        for (NodePair vp : removeList) {
//            mapping.remove(vp);
//        }
//    }
//
//    private boolean containsInDownwardsPath(CPFNode v1, CPFNode v2) {
//
//        LinkedList<CPFNode> toProcess = new LinkedList<CPFNode>();
//        toProcess.addAll(v1.getChildren());
//
//        while (toProcess.size() > 0) {
//            CPFNode process = toProcess.removeFirst();
//            if (process.getId() == v2.getId()) {
//                return true;
//            }
//            toProcess.addAll(process.getChildren());
//        }
//        return false;
//    }
//
//    private static void mergeConnectors(MappingRegions mappingRegions, Graph merged, LinkedList<NodePair> mapping) {
//        for (LinkedList<NodePair> region : mappingRegions.getRegions()) {
//            for (NodePair vp : region) {
//                if (vp.getLeft().getNodeType().equals(NodeTypeEnum.ROUTING)) {
//                    boolean makeConf = false;
//                    LinkedList<CPFNode> toProcess = new LinkedList<CPFNode>();
//                    for (CPFNode p : vp.getRight().getParents()) {
//                        if (!containsNode(region, p)) {
//                            toProcess.add(p);
//                        }
//                    }
//
//                    for (CPFNode p : toProcess) {
//                        makeConf = true;
//                        HashSet<String> l = merged.removeEdge(p.getId(), vp.getRight().getId());
//                        p.removeChild(vp.getRight().getId());
//                        vp.getRight().removeParent(p.getId());
//                        merged.connectVertices(p, vp.getLeft(), l);
//                    }
//                    toProcess = new LinkedList<CPFNode>();
//
//                    for (CPFNode p : vp.getRight().getChildren()) {
//                        if (!containsNode(region, p)) {
//                            toProcess.add(p);
//                        }
//                    }
//
//                    for (CPFNode p : toProcess) {
//                        makeConf = true;
//                        HashSet<String> l = merged.removeEdge(vp.getRight().getId(), p.getId());
//                        p.removeParent(vp.getRight().getId());
//                        vp.getRight().removeChild(p.getId());
//                        merged.connectVertices(vp.getLeft(), p, l);
//                    }
//                    if (makeConf) {
//                        vp.getLeft().setConfigurable(true);
//                    }
//                    if (!vp.getLeft().getNodeType().equals(vp.getRight().getNodeType())) {
//                        vp.getLeft().setNodeType(NodeTypeEnum.ORJOIN);
//                    }
//                }
//            }
//        }
//    }
//
//
//    private static NodePair findNextNodeToProcess(LinkedList<NodePair> mapping, LinkedList<NodePair> visited) {
//        for (NodePair vp : mapping) {
//            NodePair process = containsMapping(visited, vp.getLeft(), vp.getRight());
//            if (process == null) {
//                return vp;
//            }
//        }
//        return null;
//    }
//
//    private static NodePair containsMapping(LinkedList<NodePair> mapping, CPFNode left, CPFNode right) {
//        for (NodePair vp : mapping) {
//            if (vp.getLeft().getId().equals(left.getId()) && vp.getRight().getId().equals(right.getId())) {
//                return vp;
//            }
//        }
//        return null;
//    }
//
//    @SuppressWarnings("unused")
//    private static boolean containsMapping(LinkedList<NodePair> mapping, NodePair v) {
//        for (NodePair vp : mapping) {
//            if (vp.getLeft().getId().equals(v.getLeft().getId()) && vp.getRight().getId().equals(v.getRight().getId())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static MappingRegions findMaximumCommonRegions(Canonical g1, Canonical g2, LinkedList<NodePair> mapping) {
//        MappingRegions map = new MappingRegions();
//        LinkedList<NodePair> visited = new LinkedList<NodePair>();
//
//        while (true) {
//            NodePair c = findNextNodeToProcess(mapping, visited);
//            if (c == null) {
//                break;
//            }
//            LinkedList<NodePair> toVisit = new LinkedList<NodePair>();
//            LinkedList<NodePair> mapRegion = new LinkedList<NodePair>();
//
//            toVisit.add(c);
//            while (toVisit.size() > 0) {
//                c = toVisit.removeFirst();
//                mapRegion.add(c);
//
//                visited.add(c);
//                for (CPFNode pLeft : c.getLeft().getGraph().getAllPredecessors(c.getLeft())) {
//                    for (CPFNode pRight : c.getRight().getGraph().getAllPredecessors(c.getRight())) {
//                        NodePair pairMap = containsMapping(mapping, pLeft, pRight);
//                        NodePair containsMap = containsMapping(visited, pLeft, pRight);
//                        NodePair containsMap1 = containsMapping(toVisit, pLeft, pRight);
//                        if (pairMap != null && containsMap == null && containsMap1 == null) {
//                            toVisit.add(pairMap);
//                        }
//                    }
//                }
//
//                for (CPFNode pLeft : c.getLeft().getGraph().getAllSuccessors(c.getLeft())) {
//                    for (CPFNode pRight : c.getRight().getGraph().getAllSuccessors(c.getRight())) {
//                        NodePair pairMap = containsMapping(mapping, pLeft, pRight);
//                        NodePair containsMap = containsMapping(visited, pLeft, pRight);
//                        NodePair containsMap1 = containsMapping(toVisit, pLeft, pRight);
//                        if (pairMap != null && containsMap == null && containsMap1 == null) {
//                            toVisit.add(pairMap);
//                        }
//                    }
//                }
//
//            }
//            if (mapRegion.size() > 0) {
//                map.addRegion(mapRegion);
//            }
//        }
//
//        return map;
//    }
//
//    public static boolean containsNode(LinkedList<NodePair> mapping, CPFNode v) {
//        for (NodePair vp : mapping) {
//            if (vp.getLeft().getId().equals(v.getId()) || vp.getRight().getId().equals(v.getId())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static CPFNode getMappingPair(LinkedList<NodePair> mapping, CPFNode v) {
//        for (NodePair vp : mapping) {
//            if (vp.getLeft().getId().equals(v.getId())) {
//                return vp.getRight();
//            } else if (vp.getRight().getId().equals(v.getId())) {
//                return vp.getLeft();
//            }
//        }
//        return null;
//    }
}
