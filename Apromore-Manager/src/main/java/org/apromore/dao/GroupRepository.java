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
