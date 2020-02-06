package org.processmining.models.graphbased.directedhypergraph;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.processmining.framework.util.Cast;
import org.processmining.models.graphbased.AbstractGraphEdge;
import org.processmining.models.graphbased.AttributeMap;

public abstract class AbstractDirectedOutgoingHyperedge<S extends AbstractDirectedHypergraphNode, T extends AbstractDirectedHypergraphNode>
		extends AbstractGraphEdge<S, Set<T>> implements DirectedOutgoingHyperedge<S, T> {

	private final AbstractDirectedHypergraph<?, ?, ?> graph;

	public AbstractDirectedOutgoingHyperedge(S source, Set<T> targets, String label) {
		super(source, targets);
		// DO NOT uncomment the next line. It is not valid,
		// but the idea is clear.
		// assert(for (source:sources) {source.getGraph() ==
		// target.getGraph()});
		assert (source.getGraph() instanceof AbstractDirectedHypergraph<?, ?, ?>);
		this.graph = Cast.<AbstractDirectedHypergraph<?, ?, ?>>cast(source.getGraph());
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public AbstractDirectedHypergraph<?, ?, ?> getGraph() {
		return graph;
	}

	public int compareTo(AbstractGraphEdge<S, Set<T>> edge) {
		int c;
		c = getClass().getName().compareTo(edge.getClass().getName());
		if (c != 0) {
			return c;
		}
		// Check for same source
		c = source.compareTo(edge.getSource());
		if (c != 0) {
			return c;
		}
		// Check for same sources size
		c = target.size() - edge.getTarget().size();
		if ((c != 0) || (target.size() == 0)) {
			return c;
		}
		SortedSet<T> targets = new TreeSet<T>(target);
		SortedSet<T> edgeTargets = new TreeSet<T>(edge.getTarget());
		Iterator<T> it = targets.iterator();
		Iterator<T> it2 = edgeTargets.iterator();
		do {
			c = (it.next().compareTo(it2.next()));
		} while ((c == 0) && it.hasNext());
		return c;
	}

	public Set<T> getTargets() {
		return getTarget();
	}
}
