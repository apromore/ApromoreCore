package org.apromore.graph.canonical;

import org.jbpt.graph.abs.AbstractDirectedGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of ICanonical interface.
 *
 * @author Cameron James
 */
public abstract class AbstractCanonical<E extends IEdge<N>, N extends INode> extends AbstractDirectedGraph<E, N>
        implements ICanonical<E, N> {

    /**
     * Empty constructor.
     */
    public AbstractCanonical() { }



    @Override
    public N addNode(N node) {
        return this.addVertex(node);
    }

    @Override
    public N removeNode(final N node) {
        return this.removeVertex(node);
    }

    @Override
    public Collection<N> removeNodes(final Collection<N> nodes) {
        Collection<N> result = this.removeVertices(nodes);
        return result == null ? new ArrayList<N>() : result;
    }

    @Override
    public E removeFlow(E edge) {
        return this.removeEdge(edge);
    }

    @Override
    public Collection<E> removeFlows(Collection<E> edge) {
        Collection<E> result = this.removeEdges(edge);
        return result == null ? new ArrayList<E>() : result;
    }


    @Override
    public N getNode(String id) {
        N result = null;

        for (N node : this.getNodes()) {
            if (node.getId().equals(id)) {
                result = node;
                break;
            }
        }

        return result;
    }

    @Override
    public Set<N> getNodes() {
        return new HashSet<N>(super.getVertices());
    }


    @Override
    public Set<E> getEdges() {
        return new HashSet<E>(super.getEdges());
    }


    @Override
    public Set<N> getPostset(N node) {
        return new HashSet<N>(this.getDirectSuccessors(node));
    }

    @Override
    public Set<N> getPostset(Collection<N> nodes) {
        return new HashSet<N>(this.getDirectSuccessors(nodes));
    }

    @Override
    public Set<N> getPreset(N node) {
        return new HashSet<N>(this.getDirectPredecessors(node));
    }

    @Override
    public Set<N> getPreset(Collection<N> nodes) {
        return new HashSet<N>(this.getDirectPredecessors(nodes));
    }

    @Override
    public Set<N> getMin() {
        return this.getSourceNodes();
    }

    @Override
    public Set<N> getMax() {
        return this.getSinkNodes();
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public AbstractCanonical<E, N> clone() {
//        AbstractCanonical<E, N> clone = (AbstractCanonical<E, N>) super.clone();
//
//        return this.cloneHelper(clone, new HashMap<N, N>());
//    }
//
//    @SuppressWarnings("unchecked")
//    private AbstractCanonical<E, N> cloneHelper(AbstractCanonical<E, N> clone, Map<N, N> nodeMapping) {
//        clone.clearMembers();
//
//        for (N n : this.getNodes()) {
//            N cn = (N) n.clone();
//            clone.addVertex(cn);
//            nodeMapping.put(n, cn);
//        }
//
//        for (E f : this.getEdges()) {
//            E cf = clone.addEdge(nodeMapping.get(f.getSource()), nodeMapping.get(f.getTarget()));
//
//            if (f.getId() != null) {
//                cf.setId(f.getId());
//            }
//            if (f.getName() != null) {
//                cf.setName(f.getName());
//            }
//            if (f.getDescription() != null) {
//                cf.setDescription(f.getDescription());
//            }
//            if (f.getOriginalId() != null) {
//                cf.setOriginalId(f.getOriginalId());
//            }
//        }
//
//        return clone;
//    }
//
//    @SuppressWarnings("unchecked")
//    public AbstractCanonical<E, N> clone(Map<N, N> nodeMapping) {
//        AbstractCanonical<E, N> clone = (AbstractCanonical<E, N>) super.clone();
//        return cloneHelper(clone, nodeMapping);
//    }
//

    @Override
    public void clear() {
        this.removeVertices(this.getVertices());
    }
}
