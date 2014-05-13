package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link org.apromore.dao.model.Group}/{@link org.apromore.dao.model.Process}  instance pairs.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Repository
public interface GroupProcessRepository extends JpaRepository<GroupProcess, Integer> {

    /**
     * Find a row by its natural primary key (group and process).
     *
     * @param group
     * @param process
     * @return the identified process
     */
    GroupProcess findByGroupAndProcess(final Group group, final Process process);

    /**
     * @param processId
     * @return all groups containing the process identified by <var>processId</var>
     */
    @Query("SELECT gp FROM GroupProcess gp WHERE (gp.process.id = ?1)")
    List<GroupProcess> findByProcessId(final Integer processId);

    /**
     * Search for processes to which a particular user has access
     *
     * @param userRowGuid the rowGuid of a user
     * @return processes to which the user has access
     */
    @Query("SELECT gp FROM GroupProcess gp JOIN gp.process p JOIN gp.group g1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE (p.folder IS NULL) AND (u.rowGuid = ?1) AND (g1 = g2)")
    List<GroupProcess> findRootProcessesByUser(String userRowGuid);

    /**
     * Finds all the Processes in a Folder for a User
     *
     * @param folderId The folder we are looking in
     * @param userRowGuid the user we are looking for
     * @return the list of processUser records
     */
    @Query("SELECT gp FROM GroupProcess gp JOIN gp.process p JOIN p.folder f JOIN gp.group g1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE (f.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2)")
    List<GroupProcess> findAllProcessesInFolderForUser(final Integer folderId, final String userRowGuid);
}
