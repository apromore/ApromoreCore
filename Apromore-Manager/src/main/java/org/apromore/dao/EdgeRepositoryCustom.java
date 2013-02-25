package org.apromore.dao;

import java.util.List;

import org.apromore.dao.dataObject.EdgeDO;

/**
 * Interface domain model Data access object Node Custom Methods.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Node
 */
public interface EdgeRepositoryCustom {

    /**
     * ** SPECIAL method for fast access to Edge Records direct to the DB.
     *
     * @param contentId the contentId we are using to look for edges with
     * @return list of edges attached to this content id, (not attached to DB).
     */
    List<EdgeDO> getEdgeDOsByContent(final Integer contentId);

}
