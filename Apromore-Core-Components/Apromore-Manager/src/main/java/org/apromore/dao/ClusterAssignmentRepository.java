/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

/**
 *
 */
package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.ClusterAssignment;
import org.apromore.dao.model.FragmentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Clustering.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 * @version 2.0
 * @see org.apromore.dao.model.ClusterAssignment
 */
@Repository
public interface ClusterAssignmentRepository extends JpaRepository<ClusterAssignment, Integer> {

    /**
     * find a fragments of a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    @Query("SELECT fv FROM ClusterAssignment ca JOIN ca.fragment fv JOIN ca.cluster c WHERE c.id = ?1")
    List<FragmentVersion> findFragmentVersionByClusterId(Integer clusterId);

}
