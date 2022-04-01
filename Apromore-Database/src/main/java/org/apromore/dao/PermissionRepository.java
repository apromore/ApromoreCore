/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

import org.apromore.dao.model.Permission;
import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Permission.
 *
 * @see org.apromore.dao.model.Permission
 * @author <a href="mailto:igor.goldobin@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    /**
     * Gets specified permission in the System.
     * @param name the name of the permission we are searching for.
     * @return the name of the permission we are searching for.
     */
    Permission findByName(String name);

    /**
     * Find the permission for a User.
     * @param userGuid the user we are looking for.
     * @return the list of Permissions.
     */
    @Query("SELECT DISTINCT p FROM User u JOIN u.roles r JOIN r.permissions p WHERE u.rowGuid = ?1")
    List<Permission> findByUser(String userGuid);

    /**
     * Find the permission for a Role.
     * @param name the name of the role whose permissions we are searching for.
     * @return the list of Permissions.
     */
    @Query("SELECT DISTINCT p FROM Role r JOIN r.permissions p WHERE r.name = ?1")
    List<Permission> findByRole(String name);


}
