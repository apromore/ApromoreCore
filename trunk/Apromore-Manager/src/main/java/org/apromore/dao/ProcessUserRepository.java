package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessUser;
import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object ProcessUser.
 *
 * @see org.apromore.dao.model.ProcessUser
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface ProcessUserRepository extends JpaRepository<ProcessUser, Integer> {

    /**
     * Find the Process and User combination.
     * @param process the process we are looking in
     * @param user and the user that has access
     * @return the permissions for that user in the ProcessUser.
     */
    ProcessUser findByProcessAndUser(final Process process, final User user);

    /**
     * Returns a list of all users for the specified process.
     * @param process the process Id
     * @return the list of process user relationship records
     */
    List<ProcessUser> findByProcess(final Process process);

    /**
     * Find the Root Process User for a user.
     * @param userId the userid we are searching for.
     * @return the list of ProcessUser records
     */
    @Query("SELECT pu FROM ProcessUser pu JOIN pu.process p JOIN pu.user u " +
            "WHERE (p.folder IS NULL) AND (u.rowGuid = ?1)")
    List<ProcessUser> findRootProcessesByUser(final String userId);

    /**
     * Finds all the Processes in a Folder for a User
     * @param folderId The folder we are looking in
     * @param userId the user we are looking for
     * @return the list of processUser records
     */
    @Query("SELECT pu FROM ProcessUser pu JOIN pu.process p JOIN pu.user u JOIN p.folder f " +
            "WHERE (f.id = ?1) AND (u.rowGuid = ?2)")
    List<ProcessUser> findAllProcessesInFolderForUser(final Integer folderId, final String userId);
}
