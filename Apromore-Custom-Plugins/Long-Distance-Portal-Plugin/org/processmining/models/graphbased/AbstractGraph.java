package org.processmining.models.graphbased;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class AbstractGraph extends AbstractGraphElement {

	protected final NodeID id = new NodeID();

	public AbstractGraph() {
		super();
	}

	public boolean equals(Object o) {
		if (!(o instanceof AbstractGraph)) {
			return false;
		}
		AbstractGraph net = (AbstractGraph) o;
		return net.id.equals(id);
	}

	protected synchronized <T> T removeNodeFromCollection(Collection<T> collection, T object) {
		for (T toRemove : collection) {
			if (toRemove.equals(object)) {
				collection.remove(toRemove);
				graphElementRemoved(object);
				return toRemove;
			}
		}
		return null;
	}

	/**
	 * Returns the edges from source to target, contained in the given
	 * collection
	 * 
	 * @param <T>
	 *            The type of edges
	 * @param source
	 *            the source node
	 * @param target
	 *            the target node
	 * @param collection
	 *            the collection of edges to search through
	 * @return
	 */
	protected <T extends AbstractGraphEdge<?, ?>> Collection<T> getEdges(AbstractGraphNode source,
			AbstractGraphNode target, Collection<T> collection) {
		Collection<T> s2t = new HashSet<T>();
		for (T a : collection) {
			if (a.getSource().equals(source) && a.getTarget().equals(target)) {
				s2t.add(a);
			}
		}
		return Collections.unmodifiableCollection(s2t);

	}

	protected synchronized <T extends AbstractGraphEdge<?, ?>> T removeFromEdges(AbstractGraphNode source,
			AbstractGraphNode target, Collection<T> collection) {
		for (T a : collection) {
			if (a.getSource().equals(source) && a.getTarget().equals(target)) {
				collection.remove(a);
				graphElementRemoved(a);
				return a;
			}
		}
		return null;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public synchronized void graphElementAdded(Object element) {
		//		isLayedOut = false;
		//		elementsAdded.add(element);
	}

	public synchronized void graphElementRemoved(Object element) {
		//		elementsRemoved.add(element);
	}

	public synchronized void graphElementChanged(Object element) {
		//		elementsChanged.add(element);
	}

}
