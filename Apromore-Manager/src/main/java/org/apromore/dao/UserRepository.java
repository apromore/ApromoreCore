package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object User.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {

    /**
     * Gets specified User in the System.
     * @param username the username of the user we are searching for.
     * @return the username of the user we are searching for.
     */
    User findByUsername(String username);

    /**
     * Gets specified User in the System.
     * @param rowGuid the id of the user we are searching for.
     * @return the id of the user we are searching for.
     */
    User findByRowGuid(String rowGuid);

    /**
     * Searches user by username.
     * @param searchString the username of the user we are searching for.
     * @return the username of the user we are searching for.
     */
    List<User> findByUsernameLike(String searchString);
}
