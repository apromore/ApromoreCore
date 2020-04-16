/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
        return new HashSet<>(super.getVertices());
    }


    @Override
    public Set<E> getEdges() {
        return new HashSet<>(super.getEdges());
    }


    @Override
    public Set<N> getPostset(N node) {
        return new HashSet<>(this.getDirectSuccessors(node));
    }

    @Override
    public Set<N> getPostset(Collection<N> nodes) {
        return new HashSet<>(this.getDirectSuccessors(nodes));
    }

    @Override
    public Set<N> getPreset(N node) {
        return new HashSet<>(this.getDirectPredecessors(node));
    }

    @Override
    public Set<N> getPreset(Collection<N> nodes) {
        return new HashSet<>(this.getDirectPredecessors(nodes));
    }

    @Override
    public Set<N> getMin() {
        return this.getSourceNodes();
    }

    @Override
    public Set<N> getMax() {
        return this.getSinkNodes();
    }

    @Override
    public void clear() {
        this.removeVertices(this.getVertices());
    }
}
