package org.apromore.service.helper;

import org.apromore.dao.model.Content;
import org.apromore.graph.canonical.Canonical;

import java.util.Map;

/**
 * Graph Pocket Mapper Interface.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface GraphPocketMapper {

    /**
     * Maps each pocket in f to its corresponding pocket in an existing content.
     * A pocket is mapped, only if its counterpart in the content can be
     * accurately identified.
     * @param f       Fragment with pockets
     * @param g       Process Model Graph
     * @param content content
     * @return Mapping from pockets in f to pockets in the content. null if pockets cannot be mapped.
     */
    Map<String, String> mapPockets(Canonical f, Canonical g, Content content);

}
