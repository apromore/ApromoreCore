package org.apromore.dao;

import org.apromore.dao.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Workspace.
 *
 * @see org.apromore.dao.model.Workspace
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {

    /**
     * Finds a workspace in the System by it's name.
     * @param name the name of the workspace we are searching for.
     * @return the name of the workspace we are searching for.
     */
    Workspace findByName(String name);

}
