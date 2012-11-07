package org.apromore.graph.canonical;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jbpt.graph.abs.IDirectedGraph;

/**
 * Interface to the Canonical Process Format.
 *
 * @author Cameron James
 */
public interface ICanonical<E extends IEdge<N>, N extends INode, F extends IEvent, T extends ITask, M extends IMessage, I extends ITimer,
        S extends IState, P extends ISplit, J extends IJoin> extends IDirectedGraph<E, N> {

    /**
     * returns the graphs URI.
     * @return the Graphs URI
     */
    String getUri();

    /**
     * Sets the URI for the graph.
     * @param newUri the graphs URI.
     */
    void setUri(final String newUri);

    /**
     * returns the version of the graph
     * @return the version of the graph
     */
    String getVersion();

    /**
     * Sets the version for the graph.
     * @param newVersion the version URI.
     */
    void setVersion(final String newVersion);

    /**
     * returns the author of the graph
     * @return the author of the graph
     */
    String getAuthor();

    /**
     * Sets the author for the graph.
     * @param newAuthor the author URI.
     */
    void setAuthor(final String newAuthor);

    /**
     * returns the creation date of the graph
     * @return the creation date of the graph
     */
    String getCreationDate();

    /**
     * Sets the creation date for the graph.
     * @param newCreationDate the creation date URI.
     */
    void setCreationDate(final String newCreationDate);

    /**
     * returns the modified date of the graph
     * @return the modified date of the graph
     */
    String getModifiedDate();

    /**
     * Sets the modified date for the graph.
     * @param newModifiedDate the modified date URI.
     */
    void setModifiedDate(final String newModifiedDate);


    /**
     * Returns the entry Node.
     * @return the entry node.
     */
    Node getEntry();

    /**
     * Set the entry Node.
     * @param entry the entry Node.
     */
    void setEntry(final Node entry);

    /**
     * Returns the Exit Node.
     * @return the exit node.
     */
    Node getExit();

    /**
     * Set the Exit Node.
     * @param exit the exit Node.
     */
    void setExit(Node exit);

    /**
     * Adds a specific Edge to the the Graph, we need specific details that the edge holds.
     * @param edge the edge to add.
     * @return the edge we added.
     */
    public E addEdge(E edge);


    /**
     * Return all {@link Node} which precede the given {@link Node} in the {@link Edge}.
     * @param fn {@link Node} to start from
     * @return {@link Collection} containing all predecessors of the given {@link Node}
     */
    public Collection<Node> getAllPredecessors(Node fn);

    /**
     * Return all {@link Node} which succeed the given {@link Node} in the {@link Edge}.
     * @param fn {@link Node} to start from
     * @return {@link Collection} containing all successors of the given {@link Node}
     */
    public Collection<Node> getAllSuccessors(Node fn);



    /**
     * sets a Node Property.
     * @param nodeId the node Id
     * @param propertyName the property name
     * @param propertyValue the property value
     */
    void setNodeProperty(final String nodeId, final String propertyName, final String propertyValue);

    /**
     * Set the Node Properties.
     * @param nodeId the Node Id
     * @param propertyName the property Name
     * @return the Node Property we found, or null.
     */
    String getNodeProperty(String nodeId, String propertyName);

    /**
     * @param properties the properties
     */
    void setProperties(Map<String, IAttribute> properties);

    /**
     * return the properties
     * @return the map of properties
     */
    Map<String, IAttribute> getProperties();

    /**
     * return a property.
     * @param name the name of the property
     * @return the value of the property we are searching for.
     */
    IAttribute getProperty(String name);

    /**
     * Sets a property.
     * @param name  the name of the property
     * @param value the simple value text value of the property
     * @param any the complex XML value of the property
     */
    void setProperty(String name, String value, java.lang.Object any);

    /**
     * Sets a property only the simple text based value.
     * @param name  the name of the property
     * @param value the simple value text value of the property
     */
    void setProperty(String name, String value);



    /**
     * Add flow to this net.
     * This method ensures net stays bipartite.
     * @param from Source node.
     * @param to   Target node.
     * @return Edge added to this net; <tt>null</tt> if no flow was added.
     */
    public E addEdge(N from, N to);

    /**
     * Add node to this net.
     * @param node Node to add.
     * @return Node added to this net; <tt>null</tt> if no node was added.
     */
    public N addNode(N node);

    /**
     * Add nodes to this net.
     * @param nodes Nodes to add.
     * @return Nodes added to this net.
     */
    public Collection<N> addNodes(Collection<N> nodes);

    /**
     * Add event to this net.
     * @param event Event to add.
     * @return Event added to this net; <tt>null</tt> if no event was added.
     */
    public F addEvent(F event);

    /**
     * Add events to this net.
     * @param events Events to add.
     * @return Events added to this net.
     */
    public Collection<F> addEvents(Collection<F> events);

    /**
     * Add Task to this net.
     * @param task Task to add.
     * @return Task added to this net; <tt>null</tt> if no task was added.
     */
    public T addTask(T task);

    /**
     * Add tasks to this net.
     * @param tasks Tasks to add.
     * @return Tasks added to this net.
     */
    public Collection<T> addTasks(Collection<T> tasks);

    /**
     * Add message to this net.
     * @param message Message to add.
     * @return Message added to this net; <tt>null</tt> if no message was added.
     */
    public M addMessage(M message);

    /**
     * Add messages to this net.
     * @param messages Messages to add.
     * @return messages added to this net.
     */
    public Collection<M> addMessages(Collection<M> messages);

    /**
     * Add timer to this net.
     * @param timer Timer to add.
     * @return Timer added to this net; <tt>null</tt> if no timer was added.
     */
    public I addTimer(I timer);

    /**
     * Add timers to this net.
     * @param timers Timers to add.
     * @return Timers added to this net.
     */
    public Collection<I> addTimers(Collection<I> timers);

    /**
     * Add state to this net.
     * @param state State to add.
     * @return State added to this net; <tt>null</tt> if no state was added.
     */
    public S addState(S state);

    /**
     * Add states to this net.
     * @param states States to add.
     * @return States added to this net.
     */
    public Collection<S> addStates(Collection<S> states);

    /**
     * Add join to this net.
     * @param join Join to add.
     * @return Join added to this net; <tt>null</tt> if no join was added.
     */
    public J addJoin(J join);

    /**
     * Add joins to this net.
     * @param joins Joins to add.
     * @return Joins added to this net.
     */
    public Collection<J> addJoins(Collection<J> joins);

    /**
     * Add split to this net.
     * @param split Splits to add.
     * @return Splits added to this net; <tt>null</tt> if no split was added.
     */
    public P addSplit(P split);

    /**
     * Add splits to this net.
     * @param splits Splits to add.
     * @return SplitS added to this net.
     */
    public Collection<P> addSplits(Collection<P> splits);

    /**
     * Remove node from this net.
     * @param node Node to remove.
     * @return Node removed from this net; <tt>null</tt> if node was not removed.
     */
    public N removeNode(N node);

    /**
     * Remove nodes from this net.
     * @param nodes Nodes to remove.
     * @return Nodes removed from this net.
     */
    public Collection<N> removeNodes(Collection<N> nodes);

    /**
     * Remove event from this net.
     * @param event Event to remove.
     * @return Event removed from this net; <tt>null</tt> if event was not removed.
     */
    public F removeEvent(F event);

    /**
     * Remove events from this net.
     * @param events Events to remove.
     * @return Events removed from this net.
     */
    public Collection<F> removeEvents(Collection<F> events);

    /**
     * Remove task from this net.
     * @param task Task to remove.
     * @return Task removed from this net; <tt>null</tt> if task was not removed.
     */
    public T removeTask(T task);

    /**
     * Remove tasks from this net.
     * @param tasks Tasks to remove.
     * @return Tasks removed from this net.
     */
    public Collection<T> removeTasks(Collection<T> tasks);

    /**
     * Remove message from this net.
     * @param message Message to remove.
     * @return Message removed from this net; <tt>null</tt> if message was not removed.
     */
    public M removeMessage(M message);

    /**
     * @param messages Messages to remove.
     * @return Messages removed from this net.
     */
    public Collection<M> removeMessages(Collection<M> messages);

    /**
     * Remove timer from this net.
     * @param timer Timer to remove.
     * @return Timer removed from this net; <tt>null</tt> if timer was not removed.
     */
    public I removeTimer(I timer);

    /**
     * @param timers Timers to remove.
     * @return Timers removed from this net.
     */
    public Collection<I> removeTimers(Collection<I> timers);

    /**
     * Remove state from this net.
     * @param state State to remove.
     * @return State removed from this net; <tt>null</tt> if state was not removed.
     */
    public S removeState(S state);

    /**
     * @param states State to remove.
     * @return State removed from this net.
     */
    public Collection<S> removeStates(Collection<S> states);

    /**
     * Remove split from this net.
     * @param split Split to remove.
     * @return Split removed from this net; <tt>null</tt> if split was not removed.
     */
    public P removeSplit(P split);

    /**
     * @param splits Splits to remove.
     * @return Splits removed from this net.
     */
    public Collection<P> removeSplits(Collection<P> splits);

    /**
     * Remove join from this net.
     * @param join Join to remove.
     * @return Join removed from this net; <tt>null</tt> if join was not removed.
     */
    public J removeJoin(J join);

    /**
     * @param joins Joins to remove.
     * @return Joins removed from this net.
     */
    public Collection<J> removeJoins(Collection<J> joins);

    /**
     * Remove flow from this net.
     * @param edge Edge to remove.
     * @return Edge removed from this net; <tt>null</tt> if no flow was removed.
     */
    public E removeFlow(E edge);

    /**
     * Remove flow from this net.
     * @param edge Edge to remove.
     * @return Edge removed from this net.
     */
    public Collection<E> removeFlows(Collection<E> edge);


    /**
     * Get node by it's Id.
     * @return Node of this net.
     */
    public N getNode(String id);

    /**
     * Get nodes of this net.
     * @return Nodes of this net.
     */
    public Set<N> getNodes();


    /**
     * Get events of this net.
     * @return Events of this net.
     */
    public Set<F> getEvents();

    /**
     * Get tasks of this net.
     * @return Tasks of this net.
     */
    public Set<T> getTasks();

    /**
     * Get messages of this net.
     * @return Messages of this net.
     */
    public Set<M> getMessages();

    /**
     * Get timers of this net.
     * @return Timers of this net.
     */
    public Set<I> getTimers();

    /**
     * Get state of this net.
     * @return State of this net.
     */
    public Set<S> getStates();

    /**
     * Get split of this net.
     * @return Split of this net.
     */
    public Set<P> getSplits();

    /**
     * Get join of this net.
     * @return Join of this net.
     */
    public Set<J> getJoins();

    /**
     * Get flow relation of this net.
     * @return Edge relation of this net.
     */
    public Set<E> getEdges();

    /**
     * Get postset of a given node.
     * @param node Node.
     * @return Postset of the given node.
     */
    public Set<N> getPostset(N node);

    /**
     * Get postset of given nodes.
     * @param nodes Nodes.
     * @return Postset of given nodes.
     */
    public Set<N> getPostset(Collection<N> nodes);

    /**
     * Get preset of a given node.
     * @param node Node.
     * @return Preset of the given node.
     */
    public Set<N> getPreset(N node);

    /**
     * Get preset of the given nodes.
     *
     * @param nodes Nodes.
     * @return Preset of the given nodes.
     */
    public Set<N> getPreset(Collection<N> nodes);

    /**
     * Get source nodes of this net.
     * A node is a source node if it has empty preset.
     *
     * @return Source nodes of this net.
     */
    public Set<N> getSourceNodes();

    /**
     * Get sink nodes of this net.
     * A node is a sink node if it has empty postset.
     *
     * @return Sink nodes of this net.
     */
    public Set<N> getSinkNodes();

    /**
     * Get minimal nodes of this net (alias of {@link ICanonical#getSourceNodes()}).
     * @return Minimal nodes of this net.
     */
    public Set<N> getMin();

    /**
     * Get maximal nodes of this net (alias of {@link ICanonical#getSinkNodes()}).
     *
     * @return Maximal nodes of this net.
     */
    public Set<N> getMax();

    /**
     * Clear this net.
     */
    public void clear();
}