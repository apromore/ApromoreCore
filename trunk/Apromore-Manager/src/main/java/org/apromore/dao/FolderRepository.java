package org.apromore.dao;

import org.apromore.dao.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Folder.
 *
 * @see org.apromore.dao.model.Folder
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer>, FolderRepositoryCustom {

}
