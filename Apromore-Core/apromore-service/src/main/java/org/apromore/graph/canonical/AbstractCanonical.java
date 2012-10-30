package org.apromore.graph.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * An implementation of ICanonical interface.
 *
 * @author Cameron James
 */
public abstract class AbstractCanonical<E extends IEdge<N>, N extends INode, F extends IEvent, T extends ITask, M extends IMessage,
        I extends ITimer, S extends IState, P extends ISplit, J extends IJoin> extends AbstractDirectedGraph<E, N>
        implements ICanonical<E, N, F, T, M, I, S, P, J> {

    /**
     * Empty constructor.
     */
    public AbstractCanonical() { }



    @Override
    public N addNode(N node) {
        return this.addVertex(node);
    }

    @Override
    public Collection<N> addNodes(Collection<N> nodes) {
        Collection<N> result = this.addVertices(nodes);
        return result == null ? new ArrayList<N>() : result;
    }

    @Override
    public F addEvent(F event) {
        return this.addNode((N) event) == null ? null : event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<F> addEvents(Collection<F> events) {
        Collection<F> result = new ArrayList<F>();
        if (events == null) {
            return result;
        }

        for (F event : events) {
            if (this.addNode((N) event) != null) {
                result.add(event);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T addTask(final T task) {
        return this.addNode((N) task) == null ? null : task;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<T> addTasks(final Collection<T> tasks) {
        Collection<T> result = new ArrayList<T>();
        if (tasks == null) {
            return result;
        }

        for (T task : tasks) {
            if (this.addNode((N) task) != null) {
                result.add(task);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public M addMessage(final M transition) {
        return this.addNode((N) transition) == null ? null : transition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<M> addMessages(final Collection<M> messages) {
        Collection<M> result = new ArrayList<M>();
        if (messages == null) {
            return result;
        }

        for (M msg : messages) {
            if (this.addNode((N) msg) != null) {
                result.add(msg);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public I addTimer(final I timer) {
        return this.addNode((N) timer) == null ? null : timer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<I> addTimers(final Collection<I> timers) {
        Collection<I> result = new ArrayList<I>();
        if (timers == null) {
            return result;
        }

        for (I timer : timers) {
            if (this.addNode((N) timer) != null) {
                result.add(timer);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public S addState(final S state) {
        return this.addNode((N) state) == null ? null : state;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<S> addStates(final Collection<S> states) {
        Collection<S> result = new ArrayList<S>();
        if (states == null) {
            return result;
        }

        for (S state : states) {
            if (this.addNode((N) state) != null) {
                result.add(state);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public P addSplit(final P split) {
        return this.addNode((N) split) == null ? null : split;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<P> addSplits(final Collection<P> splits) {
        Collection<P> result = new ArrayList<P>();
        if (splits == null) {
            return result;
        }

        for (P split : splits) {
            if (this.addNode((N) split) != null) {
                result.add(split);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public J addJoin(final J join) {
        return this.addNode((N) join) == null ? null : join;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<J> addJoins(final Collection<J> joins) {
        Collection<J> result = new ArrayList<J>();
        if (joins == null) {
            return result;
        }

        for (J join : joins) {
            if (this.addNode((N) join) != null) {
                result.add(join);
            }
        }

        return result;
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

    @SuppressWarnings("unchecked")
    @Override
    public F removeEvent(F event) {
        return this.removeNode((N) event) == null ? null : event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<F> removeEvents(Collection<F> events) {
        Collection<F> result = new ArrayList<F>();
        if (events == null) {
            return result;
        }

        for (F event : events) {
            if (this.removeNode((N) event) != null) {
                result.add(event);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T removeTask(T task) {
        return this.removeNode((N) task) == null ? null : task;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<T> removeTasks(Collection<T> tasks) {
        Collection<T> result = new ArrayList<T>();
        if (tasks == null) {
            return result;
        }

        for (T task : tasks) {
            if (this.removeNode((N) task) != null) {
                result.add(task);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public M removeMessage(M message) {
        return this.removeNode((N) message) == null ? null : message;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<M> removeMessages(Collection<M> messages) {
        Collection<M> result = new ArrayList<M>();
        if (messages == null) {
            return result;
        }

        for (M message : messages) {
            if (this.removeNode((N) message) != null) {
                result.add(message);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public I removeTimer(I timer) {
        return this.removeNode((N) timer) == null ? null : timer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<I> removeTimers(Collection<I> timers) {
        Collection<I> result = new ArrayList<I>();
        if (timers == null) {
            return result;
        }

        for (I timer : timers) {
            if (this.removeNode((N) timer) != null) {
                result.add(timer);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public S removeState(S state) {
        return this.removeNode((N) state) == null ? null : state;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<S> removeStates(Collection<S> states) {
        Collection<S> result = new ArrayList<S>();
        if (states == null) {
            return result;
        }

        for (S state : states) {
            if (this.removeNode((N) state) != null) {
                result.add(state);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public P removeSplit(P split) {
        return this.removeNode((N) split) == null ? null : split;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<P> removeSplits(Collection<P> splits) {
        Collection<P> result = new ArrayList<P>();
        if (splits == null) {
            return result;
        }

        for (P split : splits) {
            if (this.removeNode((N) split) != null) {
                result.add(split);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public J removeJoin(J join) {
        return this.removeNode((N) join) == null ? null : join;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<J> removeJoins(Collection<J> joins) {
        Collection<J> result = new ArrayList<J>();
        if (joins == null) {
            return result;
        }

        for (J join : joins) {
            if (this.removeNode((N) join) != null) {
                result.add(join);
            }
        }

        return result;
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
        // TODO this.getVertices() must return set.
        return new HashSet<N>(super.getVertices());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<F> getEvents() {
        Set<F> result = new HashSet<F>();

        for (N node : this.getNodes()) {
            if (node instanceof IEvent) {
                result.add((F) node);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<T> getTasks() {
        Set<T> result = new HashSet<T>();

        for (N node : this.getNodes()) {
            if (node instanceof ITask) {
                result.add((T) node);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<M> getMessages() {
        Set<M> result = new HashSet<M>();

        for (N node : this.getNodes()) {
            if (node instanceof IMessage) {
                result.add((M) node);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<I> getTimers() {
        Set<I> result = new HashSet<I>();

        for (N node : this.getNodes()) {
            if (node instanceof ITimer) {
                result.add((I) node);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<S> getStates() {
        Set<S> result = new HashSet<S>();

        for (N node : this.getNodes()) {
            if (node instanceof IState) {
                result.add((S) node);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<P> getSplits() {
        Set<P> result = new HashSet<P>();

        for (N node : this.getNodes()) {
            if (node instanceof ISplit) {
                result.add((P) node);
            }
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Set<J> getJoins() {
        Set<J> result = new HashSet<J>();

        for (N node : this.getNodes()) {
            if (node instanceof IJoin) {
                result.add((J) node);
            }
        }

        return result;
    }

    @Override
    public Set<E> getEdges() {
        return new HashSet<E>(super.getEdges());
    }


    @Override
    public Set<N> getPostset(N node) {
        // TODO this.getDirectSuccessors(node) must return set.
        return new HashSet<N>(this.getDirectSuccessors(node));
    }

    @Override
    public Set<N> getPostset(Collection<N> nodes) {
        // TODO this.getDirectSuccessors(nodes) must return set.
        return new HashSet<N>(this.getDirectSuccessors(nodes));
    }

    @Override
    public Set<N> getPreset(N node) {
        // TODO this.getDirectPredecessors(node) must return set
        return new HashSet<N>(this.getDirectPredecessors(node));
    }

    @Override
    public Set<N> getPreset(Collection<N> nodes) {
        // TODO this.getDirectPredecessors(nodes) must return set
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

    @SuppressWarnings("unchecked")
    @Override
    public AbstractCanonical<E, N, F, T, M, I, S, P, J> clone() {
        AbstractCanonical<E, N, F, T, M, I, S, P, J> clone = (AbstractCanonical<E, N, F, T, M, I, S, P, J>) super.clone();

        return this.cloneHelper(clone, new HashMap<N, N>());
    }

    @SuppressWarnings("unchecked")
    private AbstractCanonical<E, N, F, T, M, I, S, P, J> cloneHelper(AbstractCanonical<E, N, F, T, M, I, S, P, J> clone, Map<N, N> nodeMapping) {
        clone.clearMembers();

        for (N n : this.getNodes()) {
            N cn = (N) n.clone();
            clone.addVertex(cn);
            nodeMapping.put(n, cn);
        }

        for (E f : this.getEdges()) {
            E cf = clone.addEdge(nodeMapping.get(f.getSource()), nodeMapping.get(f.getTarget()));

            if (f.getName() != null) {
                cf.setName(f.getName());
            }
            if (f.getDescription() != null) {
                cf.setDescription(f.getDescription());
            }
        }

        return clone;
    }

    @SuppressWarnings("unchecked")
    public AbstractCanonical<E, N, F, T, M, I, S, P, J> clone(Map<N, N> nodeMapping) {
        AbstractCanonical<E, N, F, T, M, I, S, P, J> clone = (AbstractCanonical<E, N, F, T, M, I, S, P, J>) super.clone();
        return cloneHelper(clone, nodeMapping);
    }


    @Override
    public void clear() {
        this.removeVertices(this.getVertices());
    }
}
