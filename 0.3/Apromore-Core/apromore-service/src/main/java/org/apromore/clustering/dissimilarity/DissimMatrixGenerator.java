package org.apromore.clustering.dissimilarity;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.TreeMultiset;
import nl.tue.tm.is.epc.Connector;
import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.epc.Node;
import nl.tue.tm.is.graph.SimpleGraph;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.clustering.containment.ContainmentRelation;
import org.apromore.clustering.dissimilarity.measure.GEDDissimCalc;

public class DissimMatrixGenerator implements DissimilarityMatrix {

    private MultiKeyMap dissimmap;
    private ContainmentRelation crel;
    private double dissThreshold;
    private File dir;
    private Map<String, SimpleGraph> models = new HashMap<String, SimpleGraph>();
    private List<DissimilarityCalc> chain = new LinkedList<DissimilarityCalc>();
    private PrintStream out;

    public DissimMatrixGenerator(File dir, ContainmentRelation crel) {
        this(dir, crel, null);
    }

    public DissimMatrixGenerator(File dir, ContainmentRelation crel, PrintStream out) {
        this.dir = dir;
        this.crel = crel;
        this.out = out;
    }

    public void setDissThreshold(double dissThreshold) {
        this.dissThreshold = dissThreshold;
    }

    public void computeDissimilarity() {
        long startedTime = System.currentTimeMillis();
        if (dissimmap == null) {
            dissimmap = new MultiKeyMap();

            int nfrag = crel.getNumberOfFragments();
            int totalPairs = nfrag * (nfrag + 1) / 2;
            int reportingInterval = 0;
            int processedPairs = 0;

            for (int i = 0; i < nfrag - 1; i++) {
                for (int j = i + 1; j < nfrag; j++) {

                    if (crel.getFragmentId(i).equals(287) && crel.getFragmentId(j).equals(756)) {
                        System.out.println("FOUND");
                    }

                    if (crel.getFragmentId(i).equals(756) && crel.getFragmentId(j).equals(287)) {
                        System.out.println("FOUND");
                    }

                    if (!crel.areInContainmentRelation(i, j)) {
                        double dissim = compute(crel.getFragmentId(i), crel.getFragmentId(j));

                        if (dissim <= dissThreshold) {
                            dissimmap.put(i, j, dissim);
                            if (out != null) {
                                out.printf("%d,%d,%f\n", crel.getFragmentId(i), crel.getFragmentId(j), dissim);
                            }
                        }
                    }

                    reportingInterval++;
                    processedPairs++;

                    if (reportingInterval == 1000) {
                        long duration = (System.currentTimeMillis() - startedTime) / 1000;
                        reportingInterval = 0;
                        double percentage = (double) processedPairs * 100 / totalPairs;
                        percentage = (double) Math.round((percentage * 1000)) / 1000d;
                        System.out.println(processedPairs + " processed out of " + totalPairs + " | " + percentage + " % completed. | Elapsed time: " + duration + " s");
                    }
                }
            }
        }
    }

    public Double getDissimilarity(Integer frag1, Integer frag2) {
        Double result = (Double) dissimmap.get(frag1, frag2);
        if (result == null) {
            result = (Double) dissimmap.get(frag2, frag1);
        }
        return result;
    }

    public void addDissimCalc(DissimilarityCalc calc) {
        chain.add(calc);
    }

    public double compute(String frag1, String frag2) {
        SimpleGraph g1 = getSimpleGraph(frag1);
        SimpleGraph g2 = getSimpleGraph(frag2);
        double disim = 1.0;

        for (DissimilarityCalc calc : chain) {
            disim = calc.compute(g1, g2);
            if (calc instanceof GEDDissimCalc) {
                if (((GEDDissimCalc) calc).isDeterministicGED() == false && !calc.isAboveThreshold(disim)) {
                    System.out.printf(">> %d,%d -- incurs in at least one non-deterministic mapping (cf. Greedy algorithm)\n", frag1, frag2);
                }
            }
            if (calc.isAboveThreshold(disim)) {
                disim = 1.0;
                break;
            }
        }
        return disim;
    }


    private SimpleGraph getSimpleGraph(String frag) {
        SimpleGraph graph = models.get(frag);

        if (graph == null) {
            String fname = String.format(dir.getAbsolutePath() + "/Fragment_%d.epml", frag);
            EPC epc = EPC.loadEPML(fname);
            formatConnectorLabel(epc);
            graph = new SimpleGraph(epc);

            // NOTE: this was commented out in the svn version
            if (graph.getEdges().size() < 100) {
                models.put(frag, graph);
            }
        }

        return graph;
    }

    private void formatConnectorLabel(EPC epc) {
        Map<Connector, String> labels = new HashMap<Connector, String>();

        for (Connector c : epc.getConnectors()) {
            String label = c.getName();
            TreeMultiset<String> mset = TreeMultiset.create();

            for (Node n : epc.getPre(c)) {
                if (n != null && n.getName() != null) {
                    mset.add(n.getName());
                }
            }
            label += mset.toString();
            mset.clear();

            for (Node n : epc.getPost(c)) {
                if (n != null && n.getName() != null) {
                    mset.add(n.getName());
                }
            }
            label += mset.toString();

            labels.put(c, label);
        }

        for (Connector c : labels.keySet()) {
            c.setName(labels.get(c));
        }
    }

}
