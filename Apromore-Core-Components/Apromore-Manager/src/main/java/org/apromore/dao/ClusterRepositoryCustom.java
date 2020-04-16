/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

/**
 *
 */
package org.apromore.dao;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Cluster;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public interface ClusterRepositoryCustom {

    /**
     * Get the clustering Summary.
     * @return the clustering summary.
     */
    List<Object[]> getClusteringSummary();

    /**
     * the fragments contained in a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    List<Integer> getFragmentIds(Integer clusterId);

    /**
     * Returns A list of clusters from the Cluster Filter.
     * @param filter the filter criteria.
     * @return the list of clusters
     */
    List<Cluster> getFilteredClusters(ClusterFilter filter);

    /**
     * return the distance stored for two fragments.
     * @param fragmentId1 fragment one id
     * @param fragmentId2 fragment two id
     * @return the found distance
     */
    double getDistance(final Integer fragmentId1, final Integer fragmentId2);

    /**
     * returns the map of fragment pair and distances for all distances.
     * @param threshold the distance threshold. anything under this threshold is ignored.
     * @return the map of fragment pairs and their distance.
     */
    Map<FragmentPair, Double> getDistances(final double threshold);

}
