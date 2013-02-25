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
public interface NodeRepository extends JpaRepository<Node, Integer>, NodeRepositoryCustom {

    /**
     * Find the Node by It's Uri.
     * @param uri the uri to search for.
     * @return the found Node.
     */
    @Query("SELECT n FROM Node n WHERE n.uri = ?1")
    Node findNodeByUri(String uri);

    /**
     * Returns all the Content Id's from Node table.
     * @return the list of content id's from the Node records
     */
    @Query("SELECT distinct n.content.id FROM Node n")
    List<String> getContentIDs();

    /**
     * Returns the Node records for the ContentId.
     * @param contentID the content id
     * @return the list of Node or null.
     */
    @Query("SELECT n FROM Node n WHERE n.content.id = ?1")
    List<Node> getNodesByContent(Integer contentID);

    /**
     * Get Node by It's Fragment id.
     * @param fragmentID the fragment Id
     * @return the Node
     */
    @Query("SELECT n FROM Node n, FragmentVersion fv WHERE n.content.id = fv.content.id AND fv.id = ?1")
    List<Node> getNodesByFragment(Integer fragmentID);

}
