package org.apromore.service;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.graph.canonical.Canonical;

import java.util.Map;

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
    Integer getMatchingContentId(String hash);

    /**
     * Add new Content?
     * @param modelVersion the process model version
     * @param c    RPSTNode
     * @param hash the contents Hash code
     * @param g    the Process Model Graph
     * @param pocketIdMappings the pocket mappings
     */
    Content addContent(ProcessModelVersion modelVersion, Canonical c, String hash, Canonical g, Map<String, String> pocketIdMappings);

    /**
     * Delete the Content and all it's extra elements.
     * @param contentId the content to remove.
     */
    void deleteContent(Integer contentId);

}
