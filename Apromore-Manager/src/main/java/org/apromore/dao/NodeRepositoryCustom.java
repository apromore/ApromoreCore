package org.apromore.dao;

import java.util.List;

import org.apromore.dao.dataObject.ContentDO;
import org.apromore.dao.dataObject.NodeDO;

/**
 * Interface domain model Data access object Node Custom Methods.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Node
 */
public interface NodeRepositoryCustom {

    /**
     * ** SPECIAL method for fast access to Content Records direct to the DB.
     *
     * @param contentId the first fragment version id
     * @return list of nodes attached to this content id, (not attached to DB).
     */
    List<NodeDO> getNodeDOsByContent(final Integer contentId);

}
