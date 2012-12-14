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


}
