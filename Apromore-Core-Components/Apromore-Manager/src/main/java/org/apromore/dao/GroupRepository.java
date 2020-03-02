/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

import org.apromore.dao.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link org.apromore.dao.model.Group} instances.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    /**
     * Gets specified Group in the System.
     * @param rowGuid the id of the group we are searching for.
     * @return the id of the group we are searching for.
     */
    Group findByRowGuid(String rowGuid);

    /**
     * Search for groups by name.
     *
     * @param searchString the username of the user we are searching for.
     * @return groups with similar names to <var>searchString</var>
     */
    List<Group> findByNameLike(String searchString);

    /**
     * Find the public group.
     */
    @Query("SELECT g FROM Group g WHERE (g.type = org.apromore.dao.model.Group.Type.PUBLIC)")
    Group findPublicGroup();
}
