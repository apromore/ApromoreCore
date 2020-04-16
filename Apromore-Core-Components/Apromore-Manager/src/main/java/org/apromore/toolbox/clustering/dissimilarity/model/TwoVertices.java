/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.toolbox.clustering.dissimilarity.model;

public class TwoVertices implements Comparable<TwoVertices> {
    public Integer v1;
    public Integer v2;

    public TwoVertices(Integer v1, Integer v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public String toString() {
        return "(" + v1 + "," + v2 + ")";
    }

    public boolean equals(Object pair2) {
        return pair2 instanceof TwoVertices && (v1.equals(((TwoVertices) pair2).v1) && v2.equals(((TwoVertices) pair2).v2));
    }

    public int hashCode() {
        return v1.hashCode() + v2.hashCode();
    }

    public int compareTo(TwoVertices o) {
        return equals(o) ? 0 : 1;
    }
}
