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

package org.apromore.toolbox.clustering.dissimilarity.algorithm;

import org.apromore.graph.canonical.Canonical;

/**
 * Interface for GED Distance algorithms.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanonicalDistanceAlgorithm {

    /**
     * Given two graphs, returns a value by which graphs can be sorted for relevance,
     * lowest value first. E.g. the value can be:
     * - an edit distance (lower edit distance means better match between graphs)
     * - 1.0 - similarity score (lower value means higher similarity score, means better match between graphs)
     *
     * @param sg1 A graph.
     * @param sg2 A graph.
     * @return A value, where a lower value represents a more relevant match between graphs.
     */
    public double compute(Canonical sg1, Canonical sg2);

}