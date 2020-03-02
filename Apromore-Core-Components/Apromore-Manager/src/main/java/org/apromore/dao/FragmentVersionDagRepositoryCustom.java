/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
 */

package org.apromore.dao;

import java.util.List;
import java.util.Map;

import org.apromore.dao.dataObject.FragmentVersionDagDO;

/**
 * Interface domain model Data access object FragmentVersionDag.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentVersionDag
 */
public interface FragmentVersionDagRepositoryCustom {


    /* ************************** JPA Methods here ******************************* */

    /**
     * Returns all parent-child mappings between all fragments
     * @return mappings fragment Id -> list of child Ids for all fragments that has at least one child
     */
    Map<Integer, List<Integer>> getAllParentChildMappings();

    /**
     * Returns all child-parent mappings between all fragments
     * @return mappings fragment Id -> list of parent Ids for all non-root fragments
     */
    Map<Integer, List<Integer>> getAllChildParentMappings();



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    /**
     * Finds all the DAG entries by size.
     * @param minimumChildFragmentSize the min size we are interested in
     * @return the list of fragment Version DAG entries
     */
    List<FragmentVersionDagDO> getAllDAGEntriesBySize(int minimumChildFragmentSize);
}
