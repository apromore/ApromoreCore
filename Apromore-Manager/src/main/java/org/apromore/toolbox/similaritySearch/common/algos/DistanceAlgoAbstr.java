package org.apromore.toolbox.similaritySearch.common.algos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DistanceAlgoAbstr implements DistanceAlgo {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistanceAlgoAbstr.class);

    //public final static int EPSILON = -1; //means: 'no mapping'
    public final static double VERTEX_INSERTION_COST = 0.1; //Only for reproducing Luciano's results
    public final static double VERTEX_DELETION_COST = 0.9; //Only for reproducing Luciano's results

    protected Canonical sg1;
    protected Canonical sg2;
    protected int totalNrVertices;
    protected int totalNrEdges;

    double weightGroupedVertex;
    double weightSkippedVertex;
    double weightSkippedEdge;
    double weightSubstitutedVertex;
    double ledcutoff;
    double cedcutoff;
    boolean usepuredistance;
    int prunewhen;
    int pruneto;
    boolean useepsilon;
    boolean dogrouping;

    protected void init(Canonical sg1, Canonical sg2) {
        this.sg1 = sg1;
        this.sg2 = sg2;
        totalNrVertices = sg1.getVertices().size() + sg2.getVertices().size();
        totalNrEdges = sg1.getEdges().size() + sg2.getEdges().size();
    }

    /**
     * Sets the weights for:
     * - skipping vertices (vweight)
     * - substituting vertices (sweight)
     * - skipping edges (eweight)
     * - string edit similarity cutoff (ledcutoff)
     * - use pure edit distance/ use weighted average distance (usepuredistance)
     * Ad usepuredistance: weight is a boolean. If 1.0: uses the pure edit distance, if 0.0: uses weighted average of the fractions of skipped vertices, skipped edges and substitution score.
     * - prune when recursion reaches this depth, 0.0 means no pruning (prunewhen)
     * - prune to recursion of this depth (pruneto)
     * <p/>
     * The argument is an array of objects, interchangably a String ("vweight", "sweight", or "eweight")
     * and a 0.0 <= Double <= 1.0 that is the value that should be set for the given weight.
     * All other weights are set to 0.0.
     *
     * @param weights Pre: for i mod 2 = 0: weights[i] instanceof String /\ weights[i] \in {"vweight", "sweight", or "eweight"}
     *                for i mod 2 = 1: weights[i] instanceof Double /\ 0.0 <= weights[i] <= 1.0
     *                for i: if i < weights.length(), then i+1 < weights.length()
     *                Post: weight identified by weights[i] is set to weights[i+1]
     *                all other weights are set to 0.0
     */
    public void setWeight(Object weights[]) {
        this.weightGroupedVertex = 0.0;
        this.weightSkippedVertex = 0.0;
        this.weightSubstitutedVertex = 0.0;
        this.weightSkippedEdge = 0.0;
        this.ledcutoff = 0.0;
        this.cedcutoff = 0.0;
        this.usepuredistance = false;
        this.prunewhen = 0;
        this.pruneto = 0;
        this.useepsilon = false;
        this.dogrouping = false;

        for (int i = 0; i < weights.length; i = i + 2) {
            String wname = (String) weights[i];
            Double wvalue = (Double) weights[i + 1];
            switch (wname) {
                case "vweight":
                    this.weightSkippedVertex = wvalue;
                    break;
                case "sweight":
                    this.weightSubstitutedVertex = wvalue;
                    break;
                case "gweight":
                    this.weightGroupedVertex = wvalue;
                    break;
                case "eweight":
                    this.weightSkippedEdge = wvalue;
                    break;
                case "ledcutoff":
                    this.ledcutoff = wvalue;
                    break;
                case "cedcutoff":
                    this.cedcutoff = wvalue;
                    break;
                case "usepuredistance":
                    this.usepuredistance = wvalue != 0.0;
                    break;
                case "useepsilon":
                    this.useepsilon = wvalue != 0.0;
                    break;
                case "dogrouping":
                    this.dogrouping = wvalue != 0.0;
                    break;
                case "prunewhen":
                    this.prunewhen = wvalue.intValue();
                    break;
                case "pruneto":
                    this.pruneto = wvalue.intValue();
                    break;
                default:
                    System.err.println("ERROR: Invalid weight identifier: " + wname);
                    break;
            }
        }
    }


    protected double computeScore(double skippedEdges, double skippedVertices, double substitutedVertices, double insertedVertices, double deletedVertices) {
        if (usepuredistance) {
            if (useepsilon) {
                return weightSkippedVertex * (VERTEX_DELETION_COST * deletedVertices + VERTEX_INSERTION_COST * insertedVertices) + weightSkippedEdge * skippedEdges + weightSubstitutedVertex * 2.0 * substitutedVertices;
            } else {
                return weightSkippedVertex * skippedVertices + weightSkippedEdge * skippedEdges + weightSubstitutedVertex * 2.0 * substitutedVertices;
            }
        } else {
            //Return the total edit distance. Multiply each element with its weight.
            double vskip = skippedVertices / (1.0 * totalNrVertices);
            double vsubs = (2.0 * substitutedVertices) / (1.0 * totalNrVertices - skippedVertices);
            double editDistance;
            if (totalNrEdges == 0) {
                editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs)) / (weightSkippedVertex + weightSubstitutedVertex);
            } else {
                double eskip = (skippedEdges / (1.0 * totalNrEdges));
                editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip)) / (weightSkippedVertex + weightSubstitutedVertex + weightSkippedEdge);
            }
            return editDistance;
        }
    }

    protected double editDistance(BestMapping bestMapping, TwoVertices addedPair) {
        // Substituted vertices are vertices that >are< mapped.
        // Their distance is 1.0 - string-edit similarity of their labels.
        double substitutedVertices = bestMapping.substitutedVerticesCost + addedPair.weight;

        int addedbyMapping = bestMapping.nrMappedEdges + findNrVerticesByPair(bestMapping, addedPair);

        double skippedEdges = sg1.getEdges().size() + sg2.getEdges().size() - (2 * addedbyMapping);
        double skippedVertices = sg1.getVertices().size() + sg2.getVertices().size() - (2 * (bestMapping.size() + 1));

        return computeScore(skippedEdges, skippedVertices, substitutedVertices, 0.0, 0.0);
    }


    private int findNrVerticesByPair(BestMapping bestMapping, TwoVertices addedPair) {
        int addedbyMapping = 0;
        CPFNode left = sg1.getNodeMap().get(addedPair.v1);
        CPFNode right = sg2.getNodeMap().get(addedPair.v2);
        if (bestMapping.size() > 0) { // best mapping contains some vertices already
            for (CPFNode p : sg1.getAllSuccessors(left)) {
                String mappingRight = bestMapping.mappingRight.get(p.getId());
                // the parent is also mapped and is parent of mapped node
                if (mappingRight != null && sg2.getAllSuccessors(right).contains(sg2.getNodeMap().get(mappingRight))) {
                    addedbyMapping++;
                }
            }
            for (CPFNode ch : sg1.getAllSuccessors(left)) {
                String mappingRight = bestMapping.mappingRight.get(ch.getId());
                // the parent is also mapped and is parent of mapped node
                if (mappingRight != null && sg2.getAllPredecessors(right).contains(sg2.getNodeMap().get(mappingRight))) {
                    addedbyMapping++;
                }
            }
        }
        return addedbyMapping;
    }



    public class BestMapping {
        public Set<TwoVertices> mapping = new HashSet<TwoVertices>();
        HashMap<String, String> mappingRight = new HashMap<String, String>();
        double substitutedVerticesCost = 0;
        int nrMappedEdges = 0;

        public void addPair(TwoVertices pair) {
            mappingRight.put(pair.v1, pair.v2);
            mapping.add(pair);
            substitutedVerticesCost += pair.weight;
            nrMappedEdges += findNrVerticesByPair(this, pair);
        }

        public Set<TwoVertices> getMapping() {
            return mapping;
        }

        public int size() {
            return mapping.size();
        }
    }
}
