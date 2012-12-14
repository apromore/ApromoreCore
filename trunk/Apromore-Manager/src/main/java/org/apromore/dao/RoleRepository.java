package org.apromore.dao;

import org.apromore.dao.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Role.
 *
 * @see org.apromore.dao.model.Role
 * @author <a href="mailto:igor.goldobin@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Gets specified Role in the System.
     * @param name the name of the role we are searching for.
     * @return the name of the role we are searching for.
     */
    Role findByName(String name);

}
