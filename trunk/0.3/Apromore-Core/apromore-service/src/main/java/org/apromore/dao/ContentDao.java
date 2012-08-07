package org.apromore.dao;

import org.apromore.dao.model.Content;

/**
 * Interface domain model Data access object Content.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Content
 */
public interface ContentDao {

    /**
     * Returns a single Content based on the primary Key.
     * @param contentId the content Id
     * @return the found content
     */
    Content findContent(String contentId);


    /**
     * Finds the Content item from the fragment Version.
     * @param fragVersionId the fragment version
     * @return the found content record or null
     */
    Content getContentByFragmentVersion(String fragVersionId);

    /**
     * Finds the Content record by the has code.
     * @param code the fragment version
     * @return the found content record or null
     */
    Content getContentByCode(String code);


    /**
     * Save the content.
     * @param content the content to persist
     */
    void save(Content content);

    /**
     * Update the content.
     * @param content the content to update
     * @return the merged object.
     */
    Content update(Content content);

    /**
     * Remove the content.
     * @param content the content to remove
     */
    void delete(Content content);

}
