package org.apromore.toolbox.similaritySearch.planarGraphMathing;

import org.apromore.toolbox.similaritySearch.common.NodePair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class PlanarGraphMathing {

    Set<NodePair> nodesToVisit = new HashSet<NodePair>();
    static Set<NodePair> mappings = new HashSet<NodePair>();

//    /**
//     * Finds matching regions. Also adds gateways, if in one modelass, the gateway exist and in other modelass does not
//     * exist. Also matches gateways of different types.
//     *
//     * @param g1
//     * @param g2
//     * @param threshold
//     * @param stemmer
//     * @return
//     */
//    public static MappingRegions findMatchWithGWAdding(Graph g1, Graph g2, double threshold, SnowballStemmer stemmer) {
//        LinkedList<NodePair> process = new LinkedList<NodePair>();
//        LinkedList<NodePair> processed = new LinkedList<NodePair>();
//        AssingmentProblem assingmentProblem = new AssingmentProblem();
//
//        MappingRegions map = new MappingRegions();
//
//        if (stemmer == null) {
//            stemmer = Settings.getEnglishStemmer();
//        }
//
//        LinkedList<NodePair> mappings = assingmentProblem.getMappingsGraph(g1, g2, threshold, stemmer);
//
//        if (mappings == null || mappings.size() == 0) {
//            return map;
//        }
//
//        NodePair v = mappings.removeFirst();
//
//        process.clear();
//        processed.clear();
//
//        process.add(v);
//
//        while (true) {
//            LinkedList<NodePair> mapRegion = new LinkedList<NodePair>();
//            if (process.size() == 0) {
//                break;
//            }
//
//            // map parents
//            while (true) {
//                if (process.size() == 0) {
//                    break;
//                }
//
//                NodePair toProcess = process.getFirst();
//
//                // match parents
//                LinkedList<Vertex> leftParents = removeVertices((LinkedList<Vertex>) toProcess.getLeft().getParentsList());
//                LinkedList<Vertex> rightParents = removeVertices((LinkedList<Vertex>) toProcess.getRight().getParentsList());
//
//                LinkedList<NodePair> nodeMappings = AssingmentProblem.getMappingsVetrex(leftParents, rightParents, threshold, stemmer, 1);
//                for (NodePair vp : nodeMappings) {
//                    if (!hasProcessed(processed, vp) && !hasProcessed(process, vp)) {
//                        process.add(vp);
//                    }
//                }
//
//                // match children
//                LinkedList<Vertex> leftChildren = removeVertices((LinkedList<Vertex>) toProcess.getLeft().getChildrenList());
//                LinkedList<Vertex> rightChildren = removeVertices((LinkedList<Vertex>) toProcess.getRight().getChildrenList());
//
//                nodeMappings = AssingmentProblem.getMappingsVetrex(leftChildren, rightChildren, threshold, stemmer, 2);
//                for (NodePair vp : nodeMappings) {
//                    if (!hasProcessed(processed, vp) && !hasProcessed(process, vp)) {
//                        process.add(vp);
//                    }
//                }
//                process.remove(toProcess);
//                processed.add(toProcess);
//                mapRegion.add(toProcess);
//            }
//
//            if (mapRegion.size() > 0) {
//                map.addRegion(mapRegion);
//            }
//
//            LinkedList<NodePair> mappingsCopy = new LinkedList<NodePair>(mappings);
//
//            for (NodePair v1 : mappingsCopy) {
//                // this has already processed
//                if (hasProcessed(processed, v1)) {
//                    mappings.remove(v1);
//                } else {
//                    process.add(v1);
//                    break;
//                }
//            }
//        }
//        return map;
//    }
//
//
//    static LinkedList<Vertex> removeVertices(LinkedList<Vertex> vList) {
//        LinkedList<Vertex> toReturn = new LinkedList<Vertex>();
//
//        if (vList == null) {
//            return toReturn;
//        }
//
//        for (Vertex v : vList) {
//            if ((Settings.considerGateways || (!Settings.considerGateways && v.getType() != Type.gateway))
//                    && (Settings.considerEvents || (!Settings.considerEvents && v.getType() != Type.event))) {
//                toReturn.add(v);
//            }
//        }
//
//        return toReturn;
//    }
//
//    static double calculateWeight(LinkedList<NodePair> processedVertices) {
//        double result = 0;
//
//        for (NodePair vp : processedVertices) {
//            result += vp.getWeight();
//        }
//        return result;
//    }
//
//    static boolean hasProcessed(LinkedList<NodePair> processedVertices, NodePair vp) {
//
//        for (NodePair processed : processedVertices) {
//            if (processed.getLeft().getID() == vp.getLeft().getID() || processed.getRight().getID() == vp.getRight().getID()) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static class MappingRegions {

        private LinkedList<LinkedList<NodePair>> regions = new LinkedList<LinkedList<NodePair>>();

        public void addRegion(LinkedList<NodePair> region) {
            regions.add(region);
        }

        public LinkedList<LinkedList<NodePair>> getRegions() {
            return regions;
        }
    }

}
