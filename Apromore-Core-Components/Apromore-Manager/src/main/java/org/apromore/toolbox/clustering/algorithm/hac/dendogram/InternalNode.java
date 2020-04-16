/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.toolbox.clustering.algorithm.hac.dendogram;

import java.util.LinkedList;

public class InternalNode extends LeafNode {

    private Node first, second;
    private double proximity;

    public InternalNode(int index, Node first, Node second, double proximity) {
        super(index);
        this.first = first;
        this.second = second;
        this.proximity = proximity;
        this.children = new LinkedList<Integer>(first.getChildren());
        this.children.addAll(second.getChildren());
    }

    public Node getFirst() {
        return first;
    }

    public Node getSecond() {
        return second;
    }

    public double getProximity() {
        return proximity;
    }

    public String toString() {
        return String.format("Internal: %f ", proximity) + children;
    }
}
