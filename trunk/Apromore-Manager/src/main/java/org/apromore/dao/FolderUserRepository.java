package org.apromore.dao;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderUser;
import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object FolderUser.
 *
 * @see org.apromore.dao.model.FolderUser
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface FolderUserRepository extends JpaRepository<FolderUser, Integer> {

    /**
     * Find the Folder and User combination.
     * @param folder the folder we are looking in
     * @param user and the user that has access
     * @return the permissions for that user in the FolderUser.
     */
    FolderUser findByFolderAndUser(final Folder folder, final User user);

    /**
     * Returns a list of all users for the specified folder.
     * @param folder the folder Id
     * @return the list of folder user relationship records
     */
    List<FolderUser> findByFolder(final Folder folder);

    /**
     * Returns a list of Folder Users for the folder and user combination.
     * @param parentFolderId the parent folder Id
     * @param userGuid the Users Row Globally unique Id
     * @return the list of found records
     */
    @Query("SELECT fu FROM FolderUser fu JOIN fu.folder f JOIN fu.user u LEFT JOIN f.parentFolder f1 " +
            "WHERE ((?1 = 0 AND f1 IS NULL) OR (f1.id = ?1)) AND (u.rowGuid = ?2) order by f1.name asc")
    List<FolderUser> findByParentFolderAndUser(final Integer parentFolderId, final String userGuid);
}
