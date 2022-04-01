/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.dao;

import java.util.List;
import java.util.Set;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.User;
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
     * @param name  the name of an existing group
     * @return the group with the specified <var>name</var>
     */
    Group findByName(String name);

    /**
     * Search for groups by name.
     *
     * @param searchString the username of the user we are searching for.
     * @return groups with similar names to <var>searchString</var>
     */
    List<Group> findByNameLike(String searchString);

    /**
     * @param user  arbitrary, but non-null
     * @return all groups containing the specified <var>user</var>
     */
    @Query("SELECT g FROM User u JOIN u.groups g WHERE u = ?1")
    Set<Group> findByUser(User user);

    /**
     * @param user  an arbitrary, existing user
     * @return all non-elective groups; this should always be exactly the public group and the <var>user</var>'s singleton group
     */
    @Query("SELECT g FROM User u JOIN u.groups g WHERE u = ?1 AND g.type != 'GROUP'")
    Set<Group> findCompulsoryGroups(User user);

    /**
     * @return all groups
     */
    @Query("SELECT g FROM Group g")
    List<Group> findAllGroups();

    /**
     * @return all elective groups
     */
    @Query("SELECT g FROM Group g WHERE (g.type = 'GROUP')")
    List<Group> findElectiveGroups();

    /**
     * Find the public group.
     */
    @Query("SELECT g FROM Group g WHERE (g.type = 'PUBLIC')")
    Group findPublicGroup();
}
