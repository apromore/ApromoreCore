package org.apromore.dao;

import org.apromore.dao.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Node.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Node
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {

    /**
     * Find the Node by It's Uri.
     * @param uri the uri to search for.
     * @return the found Node.
     */
    @Query("SELECT n FROM Node n JOIN n.nodeMappings nm JOIN nm.fragmentVersion f WHERE n.uri = ?1 and f.id = ?2")
    Node findNodeByUriAndFragmentVersion(String uri, Integer fragmentId);

    /**
     * Find Nodes by using the Fragment URI.
     * @param fragmentURI the fragment uri.
     * @return the list of found nodes.
     */
    @Query("SELECT n FROM Node n JOIN n.nodeMappings nm JOIN nm.fragmentVersion f WHERE f.uri = ?1")
    List<Node> getNodesByFragmentURI(String fragmentURI);

}
