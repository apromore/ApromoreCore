package org.apromore.dao;

import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object User.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a User in the System.
     * @param username the username of the user we are searching for.
     * @return the username of the user we are searching for.
     */
    @Query("SELECT usr FROM User usr WHERE usr.username = ?1")
    User findUserByLogin(String username);

}
