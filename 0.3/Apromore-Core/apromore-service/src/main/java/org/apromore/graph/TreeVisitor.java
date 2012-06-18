package org.apromore.graph;

import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.util.SortedListPermutationGenerator;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IFlowNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Pattern;

public class TreeVisitor {

    private static final Pattern COMPILE = Pattern.compile("\\s+");

    public TreeVisitor() { }

	
	private String getBestLabelHash(CPF graph, Set<AbstractDirectedEdge> edges, Set<FlowNode> vertices,
            IFlowNode entry, IFlowNode exit, LinkedList<QueueEntry> sortedEntries) {
        String lexSmallest = null;
        SortedListPermutationGenerator gen = new SortedListPermutationGenerator(sortedEntries);
		
		while (gen.hasMoreConbinations()) {
			LinkedList<QueueEntry> perm = gen.getNextCombination();
			
			StringBuffer buffer = new StringBuffer(0);
			Map<IFlowNode, Integer> idmap = new HashMap<IFlowNode, Integer>(0);
			for (QueueEntry ent : perm) {
				idmap.put((IFlowNode) ent.getVertex(), idmap.size());
				buffer.append(ent.getLabel());
			}

            char matrix[][] = new char[idmap.size()][idmap.size()];
			
			for (int i = 0; i < idmap.size(); i++) {
				for (int j = 0; j < idmap.size(); j++) {
					if (i != j)
						matrix[i][j] = '0';
					else if (i == 0)
						matrix[i][j] = entry.getName().charAt(0);
					else if (i == idmap.size()-1)
						matrix[i][j] = exit.getName().charAt(0);
					else
						matrix[i][j] = Character.forDigit(i, 10);
				}
			}
			
			for (AbstractDirectedEdge e: edges) {
				// TODO: There are some weird edges
				if (idmap.containsKey(e.getSource()) && idmap.containsKey(e.getTarget())) {
					if (vertices.contains(e.getSource()) && vertices.contains(e.getTarget())) {
						int i = idmap.get(e.getSource());
						int j = idmap.get(e.getTarget());
						matrix[i][j] = '1';
					}
				}
			}
			
			for (int i = 0; i < matrix.length; i++) {
				buffer.append(matrix[i]);
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
	 * 
	 * At this moment it only grabs labels of the children (either tasks or
	 * folded components), sort them and concatenate into a single string.
	 * -- Requires the generation of permutations a their corresponding
	 * -- adjacency matrices.
	 */
	public String visitRNode(CPF graph, Set<AbstractDirectedEdge> edges, Set<FlowNode> vertices, IFlowNode entry, IFlowNode exit) {
		// Grab children labels. Skip gateway/connector.
		PriorityQueue<QueueEntry> entries = new PriorityQueue<QueueEntry>();
		for (IFlowNode v: vertices) {
			entries.add(new QueueEntry(v, v.getName().replaceAll("\\s+", "")));
		}

		LinkedList<QueueEntry> sortedLabels = new LinkedList<QueueEntry>();
		while (!entries.isEmpty()) {
			sortedLabels.add(entries.poll());
		}

		return getBestLabelHash(graph, edges, vertices, entry, exit, sortedLabels);
	}

	public String visitSNode(CPF graph, Collection<AbstractDirectedEdge> edges, IFlowNode entry) {
		StringBuffer buffer = new StringBuffer(0);
		IFlowNode v = entry;
		do {
			if (!Constants.CONNECTOR.equals(graph.getVertexProperty(v.getId(), Constants.TYPE))) {
                if (v.getName() != null) {
                    buffer.append(v.getName().replaceAll("\\s+", ""));
                }
            }
			
			List<IFlowNode> postset = FragmentUtil.getPostset(v, edges);
			if (postset.size() > 0) {
				v = postset.get(0);
			} else {
				v = null;
			}
		} while (v != null);
		
		return buffer.toString();
	}

}
