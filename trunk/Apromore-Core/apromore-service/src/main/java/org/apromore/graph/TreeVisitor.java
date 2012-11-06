package org.apromore.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Pattern;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.Node;
import org.apromore.graph.util.SortedListPermutationGenerator;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;

public class TreeVisitor {

    private static final Pattern COMPILE = Pattern.compile("\\s+");


    public TreeVisitor() {
    }


    private String getBestLabelHash(Canonical graph, Set<Edge> edges, Set<Node> vertices,
            INode entry, INode exit, LinkedList<QueueEntry> sortedEntries) {
        String lexSmallest = null;
        SortedListPermutationGenerator gen = new SortedListPermutationGenerator(sortedEntries);

        while (gen.hasMoreConbinations()) {
            LinkedList<QueueEntry> perm = gen.getNextCombination();

            StringBuffer buffer = new StringBuffer(0);
            Map<INode, Integer> idmap = new HashMap<INode, Integer>(0);
            for (QueueEntry ent : perm) {
                idmap.put(ent.getVertex(), idmap.size());
                buffer.append(ent.getLabel());
            }

            char matrix[][] = new char[idmap.size()][idmap.size()];

            for (int i = 0; i < idmap.size(); i++) {
                for (int j = 0; j < idmap.size(); j++) {
                    if (i != j) {
                        matrix[i][j] = '0';
                    } else if (i == 0) {
                        matrix[i][j] = entry.getName().charAt(0);
                    } else if (i == idmap.size() - 1) {
                        matrix[i][j] = exit.getName().charAt(0);
                    }else {
                        matrix[i][j] = Character.forDigit(i, 10);
                    }
                }
            }

            for (AbstractDirectedEdge e : edges) {
                // TODO: There are some weird edges
                if (idmap.containsKey(e.getSource()) && idmap.containsKey(e.getTarget())) {
                    if (vertices.contains(e.getSource()) && vertices.contains(e.getTarget())) {
                        int i = idmap.get(e.getSource());
                        int j = idmap.get(e.getTarget());
                        matrix[i][j] = '1';
                    }
                }
            }

            for (char[] aMatrix : matrix) {
                buffer.append(aMatrix);
            }

            String hash = buffer.toString();

            if (lexSmallest == null || hash.compareTo(lexSmallest) < 0) {
                lexSmallest = hash;
            }
        }
        return lexSmallest == null ? "" : lexSmallest;
    }

    /**
     * Generates a hash code for a Rigid Component.
     * <p/>
     * At this moment it only grabs labels of the children (either tasks or
     * folded components), sort them and concatenate into a single string.
     * -- Requires the generation of permutations a their corresponding
     * -- adjacency matrices.
     */
    public String visitRNode(Canonical graph, Set<Edge> edges, Set<Node> vertices, INode entry, INode exit) {
        // Grab children labels. Skip gateway/connector.
        PriorityQueue<QueueEntry> entries = new PriorityQueue<QueueEntry>();
        for (Node v : vertices) {
            entries.add(new QueueEntry(v, v.getName().replaceAll("\\s+", "")));
        }

        LinkedList<QueueEntry> sortedLabels = new LinkedList<QueueEntry>();
        while (!entries.isEmpty()) {
            sortedLabels.add(entries.poll());
        }

        return getBestLabelHash(graph, edges, vertices, entry, exit, sortedLabels);
    }

    public String visitSNode(Canonical graph, Collection<Edge> edges, Node entry) {
        StringBuilder buffer = new StringBuilder(0);
        Node v = entry;
        do {
            if (!Constants.CONNECTOR.equals(graph.getNodeProperty(v.getId(), Constants.TYPE))) {
                if (v.getName() != null) {
                    buffer.append(v.getName().replaceAll("\\s+", ""));
                }
            }

            List<Node> postset = FragmentUtil.getPostset(v, edges);
            if (postset.size() > 0) {
                v = postset.get(0);
            } else {
                v = null;
            }
        } while (v != null);

        return buffer.toString();
    }

}
