package org.apromore.service;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.helper.OperationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for the Content Service. Defines all the methods that will do the majority of the work for
 * adding new content into the repository.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ContentService {


    /**
     * Get all the matching Content record with the same Hash.
     * @param hash the hash to search for.
     * @return any matching content records
     */
    List<Content> getContentByCode(String hash);

    /**
     * Add new Content?
     * @param modelVersion the process model version
     * @param c    RPSTNode
     * @param hash the contents Hash code
     * @param op   The operation Context of this graph
     * @param pocketIdMappings the pocket mappings
     */
    Content addContent(ProcessModelVersion modelVersion, Canonical c, String hash, OperationContext op, Map<String, String> pocketIdMappings);

    /**
     * Updates the Cancel Nodes and Edges with the correct Information.
     * @param operationContext the current Operational Context.
     */
    public void updateCancelNodes(OperationContext operationContext);

    /**
     * Delete the Content and all it's extra elements.
     * @param contentId the content to remove.
     */
    void deleteContent(Integer contentId);

}
